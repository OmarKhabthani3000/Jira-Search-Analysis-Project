import time
from elasticsearch import Elasticsearch
from transformers import RobertaTokenizer, RobertaModel, GPTNeoForCausalLM, AutoTokenizer
import torch

ELASTIC_PASSWORD = Password
client = Elasticsearch(
    "https://localhost:9200",
    ca_certs=r"C:\ElasticStack\elasticsearch-8.14.1\config\certs\http_ca.crt",
    basic_auth=("elastic", ELASTIC_PASSWORD)
)

roberta_tokenizer = RobertaTokenizer.from_pretrained("roberta-base")
roberta_model = RobertaModel.from_pretrained("roberta-base")

gpt_neo_model = GPTNeoForCausalLM.from_pretrained("EleutherAI/gpt-neo-1.3B")
gpt_neo_tokenizer = AutoTokenizer.from_pretrained("EleutherAI/gpt-neo-1.3B")

def get_embedding(text):
    if not text:
        return None
    inputs = roberta_tokenizer(text, return_tensors="pt", truncation=True, padding=True, max_length=512)
    with torch.no_grad():
        outputs = roberta_model(**inputs)
    embedding = torch.mean(outputs.last_hidden_state, dim=1).squeeze().numpy()
    return embedding.tolist()

def fetch_comments_for_issue(issue_key):
    search_query_comments = {
        "query": {
            "bool": {
                "must": [
                    {"match_phrase": {"public_dw_comments_issue_key": issue_key}}
                ]
            }
        },
        "_source": ["public_dw_comments_comments"]
    }
    response_comments = client.search(index="pgindex", body=search_query_comments)
    return [hit["_source"]["public_dw_comments_comments"] for hit in response_comments["hits"]["hits"]]

def analyze_issue_with_gpt_neo(issue_details):
    issue_details_text = f"""
    Issue Key: {issue_details['Issue Key']}
    Summary: {issue_details['Summary']}
    Description: {issue_details['Description']}
    Priority: {issue_details['Priority']}
    Components: {issue_details['Components']}
    Status: {issue_details['Status']}
    Comments: {', '.join(issue_details['Comments']) if issue_details['Comments'] else 'No comments'}
    """
    
    prompt = f"You are an expert in analyzing IT service Jira issues. Provide actionable insights for the following issue:\n{issue_details_text}\n\nAnalysis:"
    inputs = gpt_neo_tokenizer(prompt, return_tensors="pt", max_length=1024, truncation=True)
    
    outputs = gpt_neo_model.generate(inputs.input_ids, max_length=1024, num_return_sequences=1, temperature=0.7)
    analysis = gpt_neo_tokenizer.decode(outputs[0], skip_special_tokens=True)
    
    return analysis.split("Analysis:")[-1].strip()

def search_and_analyze_issues(query_summary, query_description, project_key=None, num_results=5):
    summary_embedding = get_embedding(query_summary)
    description_embedding = get_embedding(query_description)
    
    if summary_embedding is None and description_embedding is None:
        print("No valid embeddings found for the input query.")
        return

    search_query = {
        "_source": [
            "public_dw_issues_summary", "public_dw_issues_description", "public_dw_issues_issue_key",
            "public_dw_issues_type", "public_dw_issues_status", "public_dw_issues_assignee",
            "public_dw_issues_reporter", "public_dw_issues_created", "public_dw_issues_updated",
            "public_dw_issues_priority", "public_dw_issues_components", "public_dw_issues_resolution",
            "public_dw_issues_fix_versions", "public_dw_issues_affects_version", "public_dw_issues_status_category"
        ],
        "size": num_results,
        "query": {
            "bool": {
                "must": [
                    {"match": {"public_dw_issues_project_key": project_key}}
                ] if project_key else [],
                "should": [
                    {"knn": {"field": "summary_embedding", "query_vector": summary_embedding, "num_candidates": 10000}},
                    {"knn": {"field": "description_embedding", "query_vector": description_embedding, "num_candidates": 10000}}
                ]
            }
        }
    }

    response = client.search(index="pgindex", body=search_query)
    hits = response['hits']['hits']

    for hit in hits:
        source = hit["_source"]
        issue_key = source['public_dw_issues_issue_key']
        comments = fetch_comments_for_issue(issue_key)
        
        issue_details = {
            "Issue Key": issue_key,
            "Status": source['public_dw_issues_status'],
            "Type": source['public_dw_issues_type'],
            "Assignee": source['public_dw_issues_assignee'],
            "Reporter": source['public_dw_issues_reporter'],
            "Creation Time": source['public_dw_issues_created'],
            "Last Update Time": source['public_dw_issues_updated'],
            "Priority": source['public_dw_issues_priority'],
            "Components": source['public_dw_issues_components'],
            "Resolution": source['public_dw_issues_resolution'],
            "Fix Versions": source['public_dw_issues_fix_versions'],
            "Affects Version": source['public_dw_issues_affects_version'],
            "Status Category": source['public_dw_issues_status_category'],
            "Summary": source['public_dw_issues_summary'],
            "Description": source['public_dw_issues_description'],
            "Comments": comments
        }
        
        analysis = analyze_issue_with_gpt_neo(issue_details)

        print("Issue Details:")
        for key, value in issue_details.items():
            print(f"{key}: {value}")
        print("\nAnalysis:")
        print(analysis)
        print("-" * 40)

query_summary = "Default NodeName value not set in HAN leading to NPE"
query_description = "The EntityMode.DOM4J cannot be used when using"
project_key = "ANN" 

search_and_analyze_issues(query_summary, query_description, project_key)
