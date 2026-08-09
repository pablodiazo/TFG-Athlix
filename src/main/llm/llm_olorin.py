import requests
 
# API URL
# Important: to be run in olorin, for the moment, you must log as eliseo.bao user to deploy the model on slurm
url = "https://irlab.org/inference-olorin/chat/completions"
 
# Headers
headers = {
    "Authorization": "Bearer sk-irlab-llm-ff0044f00601bf11c8f6bd77a111d9b6",
    "Content-Type": "application/json"
}
 
# JSON Payload
data = {
    "model": "llama3.2:3b",
    # "model": "llama3.1:8b",
    "messages": [
        {
            "role": "system",
            "content": "You are a helpful assistant."
        },
        {
            "role": "user",
            "content": "Say this is a test"
        }
    ]
}
 
# Send POST request
response = requests.post(url, headers=headers, json=data)
 
# Print response
print(response.status_code)
print(response.json())
 