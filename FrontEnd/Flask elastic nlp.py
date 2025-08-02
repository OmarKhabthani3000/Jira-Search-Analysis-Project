from flask import Flask, request
from flask_cors import CORS # type: ignore
from elasticsearch import Elasticsearch
from sentence_transformers import SentenceTransformer
import torch
import io
import sys


app = Flask(__name__)
CORS(app)

ELASTIC_PASSWORD = "elastic"
client = Elasticsearch(
    "https://localhost:9200",
    ca_certs=r"C:\ElasticStack\elasticsearch-8.14.1\config\certs\http_ca.crt",
    basic_auth=("elastic", ELASTIC_PASSWORD)
)

model = SentenceTransformer(r"C:\Users\omark\Documents\GitHub\StagePFE\sbert-summary-description-model")  

def get_sbert_embedding(text):
    if not text:
        return None  
    embedding = model.encode(text, convert_to_numpy=True)
    return embedding.tolist()


def fetch_comments_for_issue(issue_key):
    search_query_comments = {
        "query": {
            "bool": {
                "must": [
                    {
                        "match_phrase": {
                            "public_dw_comments_issue_key": issue_key
                        }
                    }
                ]
            }
        },
        "_source": ["public_dw_comments_comments"]  
    }
    
    try:
        response_comments = client.search(index="pgindex", body=search_query_comments)
        return [hit["_source"] for hit in response_comments["hits"]["hits"]]
    except Exception as e:
        print(f"Error searching comments for issue key {issue_key}: {e}")
        return []

def search_issues_with_embedding(query_summary, query_description, project_key=None, issue_type=None, num_results=5):
    combined_text = f"{query_summary} {query_description}".strip()
    query_embedding = get_sbert_embedding(combined_text)
    
    if query_embedding is None:
        print("No valid embeddings found for the input query.")
        return

    search_query = {
        "_source": [
        "public_dw_issues_summary",
        "public_dw_issues_description",
        "public_dw_issues_issue_key",
        "public_dw_issues_type",
        "public_dw_issues_status",
        "public_dw_issues_assignee",
        "public_dw_issues_reporter",
        "public_dw_issues_created",
        "public_dw_issues_updated",
        "public_dw_issues_priority",
        "public_dw_issues_components",
        "public_dw_issues_resolution",
        "public_dw_issues_fix_versions",
        "public_dw_issues_affects_version",
        "public_dw_issues_status_category"], 
        "size": num_results,  
        "query": {
            "bool": {
                "must": [
                    {"match": {"public_dw_issues_project_key": project_key}}
                ] if project_key else [],
                "should": [
                    {"knn": {"field": "sbert_embedding", "query_vector": query_embedding, "num_candidates": 2000}}
                                                    ]
            }
        }        
    }

    if project_key:
        search_query["query"]["bool"]["must"].append({
            "match": {
                "public_dw_issues_project_key": project_key
            }
        })

    if issue_type:
        search_query["query"]["bool"]["should"].append({
            "match": {
                "public_dw_issues_type": issue_type
            }
        })

    response = client.search(index="pgindex", body=search_query)

    hits = response['hits']['hits']
    if hits:
        for hit in hits:
            source = hit["_source"]
            issue_key = source.get('public_dw_issues_issue_key')
            
            comments = fetch_comments_for_issue(issue_key)
            
            print("========== Détails de l'issue ==========")
            print(f"Clé de l'issue: {issue_key}")
            print(f"Statut: {source['public_dw_issues_status']}")
            print(f"Type: {source['public_dw_issues_type']}")
            print(f"Assigné: {source['public_dw_issues_assignee']}")
            print(f"Rapporteur: {source['public_dw_issues_reporter']}")
            print(f"Date de création: {source['public_dw_issues_created']}")
            print(f"Dernière mise à jour: {source['public_dw_issues_updated']}")
            print(f"Priorité: {source['public_dw_issues_priority']}")
            print("\n--- Informations complémentaires ---")
            print(f"Composants: {source['public_dw_issues_components']}")
            print(f"Résolution: {source['public_dw_issues_resolution']}")
            print(f"Versions corrigées: {source['public_dw_issues_fix_versions']}")
            print(f"Versions affectées: {source['public_dw_issues_affects_version']}")
            print(f"Catégorie de statut: {source['public_dw_issues_status_category']}")
            print("\n--- Résumé et Description ---")
            print(f"Résumé: {source['public_dw_issues_summary']}")
            print(f"Description:\n{source['public_dw_issues_description']}")
            
            if comments:
                print("\n--- Commentaires de l'issue ---")
                for comment in comments:
                    comment_content = comment["public_dw_comments_comments"]
                    if comment_content.lower() != "none":  
                        print(f"Contenu du commentaire:\n{comment_content.strip()}")
                if all(comment["public_dw_comments_comments"].lower() == "none" for comment in comments):
                    print("Pas de commentaire dans cette issue")
            else:
                print("Pas de commentaire dans cette issue")
            
            print("-" * 40)
    else:
        print("Aucun résultat trouvé.")


@app.route('/search', methods=['POST'])
def search_issues():
    data = request.json
    query_summary = data.get('query_summary', '')
    query_description = data.get('query_description', '')
    project_key = data.get('project_key', None)
    issue_type = data.get('issue_type', None)

    buffer = io.StringIO()
    sys.stdout = buffer

    try:
        search_issues_with_embedding(query_summary, query_description, project_key, issue_type)
    finally:
        sys.stdout = sys.__stdout__


    response_text = buffer.getvalue()


    return response_text, 200, {'Content-Type': 'text/plain'}

if __name__ == '__main__':
    app.run(debug=True)