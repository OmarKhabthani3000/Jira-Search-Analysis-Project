from elasticsearch import Elasticsearch

ELASTIC_PASSWORD = Password

client = Elasticsearch(
    "https://localhost:9200",
    ca_certs=r"C:\ElasticStack\elasticsearch-8.14.1\config\certs\http_ca.crt",
    basic_auth=("elastic", ELASTIC_PASSWORD)
)

if client.ping():
    print("Connected to Elasticsearch")
else:
    print("Could not connect to Elasticsearch")

def get_user_input():
    project_key = input("Enter the project key: ")
    summary_text = input("Enter the summary text: ")
    issue_type = input("Enter the issue type: ")
    affects_version = input("Enter the affects version: ")
    components = input("Enter the components: ")
    
    return {
        "project_key": project_key,
        "summary_text": summary_text,
        "issue_type": issue_type,
        "affects_version": affects_version,
        "components": components
    }

search_fields = get_user_input()

search_query_issues = {
    "query": {
        "bool": {
            "must": [
                {
                    "match": {
                        "public_dw_issues_project_key": search_fields["project_key"]
                    }
                },
                {
                    "more_like_this": {
                        "fields": ["public_dw_issues_summary"],
                        "like": search_fields["summary_text"],
                        "min_term_freq": 1,
                        "max_query_terms": 12
                    }
                },
                {
                    "match": {
                        "public_dw_issues_type": search_fields["issue_type"]
                    }
                }
            ],
            "filter": [
                {
                    "bool": {
                        "should": [
                            {
                                "exists": {
                                    "field": "public_dw_issues_affects_version"
                                }
                            }
                        ],
                        "must": [
                            {
                                "match": {
                                    "public_dw_issues_affects_version": search_fields["affects_version"]
                                }
                            }
                        ]
                    }
                },
                {
                    "bool": {
                        "should": [
                            {
                                "exists": {
                                    "field": "public_dw_issues_components"
                                }
                            }
                        ],
                        "must": [
                            {
                                "match": {
                                    "public_dw_issues_components": search_fields["components"]
                                }
                            }
                        ]
                    }
                }
            ]
        }
    },
    "size": 5 
}

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
        response_comments = client.search(index=comments_index, body=search_query_comments)
        return [hit["_source"] for hit in response_comments["hits"]["hits"]]
    except Exception as e:
        print(f"Error searching comments for issue key {issue_key}: {e}")
        return []

issues_index = "pgindex"  
comments_index = "pgindex"  

try:
    response_issues = client.search(index=issues_index, body=search_query_issues)
    print("Search results (First 5 issues):")
    for hit in response_issues["hits"]["hits"]:
        source = hit["_source"]
        issue_key = source.get('public_dw_issues_issue_key')
        
        comments = fetch_comments_for_issue(issue_key)
        
        print("Issue Key:", issue_key)
        print("Issue Details:", source)
        
        if comments:
            print("Comments:")
            for comment in comments:
                print("Comment Body:", comment["public_dw_comments_comments"])
        else:
            print("No comments found for this issue.")
        
        print("-" * 40)  
except Exception as e:
    print(f"Error searching the index: {e}")