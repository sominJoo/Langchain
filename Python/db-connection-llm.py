from langchain_community.utilities.sql_database import SQLDatabase
from langchain_experimental.sql import SQLDatabaseChain
from langchain_openai import ChatOpenAI

# API 연결 키
OPENAI_API_KEY = ""

# 데이터베이스 연결 정보
username=''
password=''
host=''
port=''
database=''

# 데이터베이스 객체 생성
pg_uri = f"postgresql+psycopg2://{username}:{password}@{host}:{port}/{database}"
db = SQLDatabase.from_uri(pg_uri)

llm = ChatOpenAI(temperature=0, api_key=OPENAI_API_KEY, model_name='gpt-3.5-turbo')


db_chain = SQLDatabaseChain(database=db,llm= llm, verbose=True)

question = "회사 2023년 총 매출, A팀의 2023년도의 총 매출, 2023년도 A팀 총 매출/회사 총매출 퍼센트를 알려줘"
response = db_chain.invoke({"query": question})
print(response["result"])

