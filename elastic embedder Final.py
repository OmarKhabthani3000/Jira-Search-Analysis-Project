import warnings
import time
from elasticsearch import Elasticsearch, helpers
from sentence_transformers import SentenceTransformer

warnings.filterwarnings("ignore", category=UserWarning)

ELASTIC_PASSWORD = Password
client = Elasticsearch(
    "https://localhost:9200",
    ca_certs=r"C:\ElasticStack\elasticsearch-8.14.1\config\certs\http_ca.crt",
    basic_auth=("elastic", ELASTIC_PASSWORD)
)

index_name = "pgindex"

model = SentenceTransformer(r'C:\Users\omark\Documents\GitHub\StagePFE\sbert-summary-description-model') 

def get_sbert_embedding(text):
    if not text:
        return None  
    embedding = model.encode(text, convert_to_numpy=True)
    return embedding.tolist()

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

def index_issues_with_sbert_embeddings(issues):
    actions = []

    for issue in issues:
        source = issue['_source']
        issue_id = issue['_id']

        summary_text = source.get('public_dw_issues_summary', "")
        description_text = source.get('public_dw_issues_description', "")
        combined_text = f"{summary_text} {description_text}".strip()

        combined_embedding = get_sbert_embedding(combined_text)


        source['sbert_embedding'] = combined_embedding

        action = {
            "_op_type": "index",
            "_index": index_name,
            "_id": issue_id,  
            "_source": source
        }
        actions.append(action)

    helpers.bulk(client, actions)
    print(f"Indexed {len(actions)} documents with SBERT embeddings.")

issues = fetch_all_issues()
index_issues_with_sbert_embeddings(issues)
