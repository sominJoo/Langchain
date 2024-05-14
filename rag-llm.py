from langchain_community.document_loaders import WebBaseLoader
from langchain_openai import OpenAIEmbeddings
from langchain_community.vectorstores import FAISS
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain.chains.combine_documents import create_stuff_documents_chain
from langchain_core.prompts import ChatPromptTemplate
from langchain_openai import ChatOpenAI
from langchain.chains import create_retrieval_chain

api_key = ""

# Web 정보 파싱
loader = WebBaseLoader("https://www.datanet.co.kr/news/articleView.html?idxno=183958")
docs = loader.load()

# 파싱정보 분할 후 벡터 스토어에 임베딩
embeddings = OpenAIEmbeddings(model="text-embedding-3-large", api_key=api_key)
text_splitter = RecursiveCharacterTextSplitter()
documents = text_splitter.split_documents(docs)
vector = FAISS.from_documents(documents, embeddings)

# 답변 형식 지정(질문 형식도 지정 가능)
prompt = ChatPromptTemplate.from_template(
    """Answer the following question based only on the provided context:
        <context>
        {context}
        </context>
        Question: {input}"""
)

llm = ChatOpenAI(api_key=api_key)
document_chain = create_stuff_documents_chain(llm, prompt)

retriever = vector.as_retriever()
retrieval_chain = create_retrieval_chain(retriever, document_chain)

response = retrieval_chain.invoke({"input": "모비젠의 데이터 패브릭 기술에 대해 알려줘"})
print(response["answer"])