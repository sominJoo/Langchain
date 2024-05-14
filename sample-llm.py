import os
from langchain_openai import ChatOpenAI
os.environ["OPENAI_API_KEY"] = ""
llm = ChatOpenAI()

# 간단하게 API 텍스트 호출
llm.invoke("LangSmith로 Testing과 Evaluation을 어떻게 할 수 있어?")
