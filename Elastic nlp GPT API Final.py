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

tokenizer = RobertaTokenizer.from_pretrained(r"C:\Users\omark\Documents\GitHub\StagePFE\roberta-finetuned") 
model = RobertaModel.from_pretrained(r"C:\Users\omark\Documents\GitHub\StagePFE\roberta-finetuned") 

gpt_neo_model = GPTNeoForCausalLM.from_pretrained("EleutherAI/gpt-neo-1.3B")
gpt_neo_tokenizer = AutoTokenizer.from_pretrained("EleutherAI/gpt-neo-1.3B")
gpt_neo_tokenizer.pad_token = gpt_neo_tokenizer.eos_token
gpt_neo_tokenizer.padding_side = "left"

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
                    {"match_phrase": {"public_dw_comments_issue_key": issue_key}}
                ]
            }
        },
        "_source": ["public_dw_comments_comments"]
    }
    response_comments = client.search(index="pgindex", body=search_query_comments)
    return [hit["_source"]["public_dw_comments_comments"] for hit in response_comments["hits"]["hits"]]

def summarize_comments_with_gpt_neo(comments):
    if not comments:
        return "No comments to summarize."

    combined_comments = "\n".join(comments)
    prompt = (
        f"Summarize the following comments in one or two sentences:\n\n{combined_comments}\n\nSummary:"
    )
    
    inputs = gpt_neo_tokenizer(prompt, return_tensors="pt", max_length=1024, truncation=True, padding=True)
    
    outputs = gpt_neo_model.generate(
        inputs.input_ids,
        attention_mask=inputs.attention_mask,
        max_new_tokens=50, 
        num_beams=3,
        early_stopping=True,
        pad_token_id=gpt_neo_tokenizer.pad_token_id
    )
    summary = gpt_neo_tokenizer.decode(outputs[0], skip_special_tokens=True)

    return summary.split("Summary:")[-1].strip()

def search_and_analyze_issues(query_summary, query_description, project_key=None, num_results=5):
    combined_embedding = create_combined_embedding(query_summary, query_description)
    
    if combined_embedding is None:
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
                    {"knn": {"field": "combined_embedding", "query_vector": combined_embedding.tolist(), "num_candidates": 2000}}
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
        
        summary = summarize_comments_with_gpt_neo(comments)

        print("Issue Details:")
        for key, value in issue_details.items():
            print(f"{key}: {value}")
        print("\nGeneral Idea:")
        print(summary)
        print("-" * 40)

query_summary = input("Enter the Query Summary: ")
query_description = input("Enter the Query Description: ")
project_key = input("Enter the Project Key (leave blank if not applicable): ")

search_and_analyze_issues(query_summary, query_description, project_key)
