from pydantic import BaseModel, Field
from openai import OpenAI

client = OpenAI(base_url="https://irlab.org/inference-olorin", api_key="sk-irlab-llm-ff0044f00601bf11c8f6bd77a111d9b6")

# Define the schema for the response
class FriendInfo(BaseModel):
    name: str = Field(..., description="The name of the friend")
    age: int = Field(..., description="The age of the friend")
    is_available: bool = Field(..., description="Whether the friend is available to hang out")

    


class FriendList(BaseModel):
    friends: list[FriendInfo]

try:
    completion = client.beta.chat.completions.parse(
        temperature=0,
        model="llama3.2:3b",
        messages=[
            {"role": "system", "content": "You are a helpful assistant."},
            {"role": "user", "content": "I have two friends. The first is Ollama 22 years old busy saving the world, and the second is Alonso 23 years old and wants to hang out. Return a list of friends in JSON format"}
        ],
        response_format=FriendList,
    )

    friends_response = completion.choices[0].message
    if friends_response.parsed:
        print(friends_response.parsed)
    elif friends_response.refusal:
        print(friends_response.refusal)
except Exception as e:
    print(f"Error: {e}")