from elasticsearch import Elasticsearch
from transformers import RobertaTokenizer, RobertaModel
import torch

ELASTIC_PASSWORD = Password
client = Elasticsearch(
    "https://localhost:9200",
    ca_certs=r"C:\ElasticStack\elasticsearch-8.14.1\config\certs\http_ca.crt",
    basic_auth=("elastic", ELASTIC_PASSWORD)
)

tokenizer = RobertaTokenizer.from_pretrained(r"C:\Users\omark\Documents\GitHub\StagePFE\roberta-finetuned")  
model = RobertaModel.from_pretrained(r"C:\Users\omark\Documents\GitHub\StagePFE\roberta-finetuned")  

def get_roberta_embedding(text):
    if not text:
        return None  
    inputs = tokenizer(text, return_tensors="pt", truncation=True, padding=True, max_length=512)
    with torch.no_grad():
        outputs = model(**inputs)
    embedding = torch.mean(outputs.last_hidden_state, dim=1).squeeze().numpy()
    return embedding

def create_combined_embedding(summary, description, summary_weight=0.2, description_weight=0.8):
    summary_embedding = get_roberta_embedding(summary) if summary else None
    description_embedding = get_roberta_embedding(description) if description else None
    
    if summary_embedding is not None and description_embedding is not None:
        combined_embedding = (summary_weight * summary_embedding) + (description_weight * description_embedding)
    elif summary_embedding is not None:
        combined_embedding = summary_embedding  
    elif description_embedding is not None:
        combined_embedding = description_embedding  
    else:
        combined_embedding = None  
    return combined_embedding

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
    combined_embedding = create_combined_embedding(query_summary, query_description)
    
    if combined_embedding is None:
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
                    {"knn": {"field": "combined_embedding", "query_vector": combined_embedding.tolist(), "num_candidates": 2000}}
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
            
            print("Issue Key:", issue_key)
            print(f"Status: {source['public_dw_issues_status']}")
            print(f"Type: {source['public_dw_issues_type']}")
            print(f"Assignee: {source['public_dw_issues_assignee']}")
            print(f"Reporter: {source['public_dw_issues_reporter']}")
            print(f"Creation time: {source['public_dw_issues_created']}")
            print(f"Last Update time: {source['public_dw_issues_updated']}")
            print(f"Priority: {source['public_dw_issues_priority']}")
            print(f"Components: {source['public_dw_issues_components']}")
            print(f"Resolution: {source['public_dw_issues_resolution']}")
            print(f"Fix versions: {source['public_dw_issues_fix_versions']}")
            print(f"Affects version: {source['public_dw_issues_affects_version']}")
            print(f"Status category: {source['public_dw_issues_status_category']}")
            print(f"Summary: {source['public_dw_issues_summary']}")
            print(f"Description: {source['public_dw_issues_description']}")
            
            if comments:
                print("Comments:")
                for comment in comments:
                    print("Comment Body:", comment["public_dw_comments_comments"])
            else:
                print("No comments found for this issue.")
            
            print("-" * 40)
    else:
        print("No results found.")

query_summary = input("Enter the Query Summary: ")
query_description = input("Enter the Query Description: ")
project_key = input("Enter the Project Key (leave blank if not applicable): ")
issue_type = input("Enter the Issue Type (optional, leave blank if not applicable): ")

search_issues_with_embedding(query_summary, query_description, project_key, issue_type)