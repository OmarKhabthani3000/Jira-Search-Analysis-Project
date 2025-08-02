import warnings
import time
from elasticsearch import Elasticsearch, helpers
from transformers import RobertaTokenizer, RobertaModel
import torch

warnings.filterwarnings("ignore", category=UserWarning, module='transformers')

ELASTIC_PASSWORD = Password
client = Elasticsearch(
    "https://localhost:9200",
    ca_certs=r"C:\ElasticStack\elasticsearch-8.14.1\config\certs\http_ca.crt",
    basic_auth=("elastic", ELASTIC_PASSWORD)
)

index_name = "pgindex"

tokenizer = RobertaTokenizer.from_pretrained(r"C:\Users\omark\Documents\GitHub\StagePFE\roberta-finetuned") 
model = RobertaModel.from_pretrained(r"C:\Users\omark\Documents\GitHub\StagePFE\roberta-finetuned")  

def get_roberta_embedding(text):
    if not text:
        return None 
    inputs = tokenizer(text, return_tensors="pt", truncation=True, padding=True, max_length=512)
    with torch.no_grad():
        outputs = model(**inputs)
    embedding = torch.mean(outputs.last_hidden_state, dim=1).squeeze().numpy()
    return embedding.tolist()

def combine_embeddings(summary_embedding, description_embedding, summary_weight=0.2, description_weight=0.8):
    if summary_embedding is None and description_embedding is None:
        return None
    if description_embedding is None: 
        return summary_embedding
    if summary_embedding is None:
        return description_embedding
    combined_embedding = [
        summary_weight * s + description_weight * d
        for s, d in zip(summary_embedding, description_embedding)
    ]
    return combined_embedding

def fetch_all_issues(batch_size=100, retries=3, delay=5):
    search_query = {
        "query": {
            "match_all": {}
        }
    }
    
    for attempt in range(retries):
        try:
            return helpers.scan(client, query=search_query, index=index_name, size=batch_size, scroll="10m")  
        except Elasticsearch.NotFoundError:
            print(f"Scroll context lost. Retrying... Attempt {attempt+1}/{retries}")
            time.sleep(delay)
    raise Exception("Failed to retrieve issues after multiple retries.")

def index_issues_with_combined_embeddings(issues):
    actions = []

    for issue in issues:
        source = issue['_source']
        issue_id = issue['_id']

        summary_text = source.get('public_dw_issues_summary', "")
        description_text = source.get('public_dw_issues_description', "")

        summary_embedding = get_roberta_embedding(summary_text)
        description_embedding = get_roberta_embedding(description_text)

        combined_embedding = combine_embeddings(summary_embedding, description_embedding)

        source['summary_embedding'] = summary_embedding
        source['description_embedding'] = description_embedding
        source['combined_embedding'] = combined_embedding

        action = {
            "_op_type": "index",
            "_index": index_name,
            "_id": issue_id,  
            "_source": source
        }
        actions.append(action)

    helpers.bulk(client, actions)
    print(f"Indexed {len(actions)} documents with embeddings.")

issues = fetch_all_issues()
index_issues_with_combined_embeddings(issues)
