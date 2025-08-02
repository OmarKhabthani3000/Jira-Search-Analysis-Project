import openai

openai.api_key = APIKEY

usage = openai.Usage.retrieve()
print(usage)