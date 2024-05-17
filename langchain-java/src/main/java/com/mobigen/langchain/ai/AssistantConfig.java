package com.mobigen.langchain.ai;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.document.transformer.HtmlTextExtractor;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
@RequiredArgsConstructor
public class AssistantConfig {
	private static final String API_KEY = "";

	public static Assistant ragAssistantWithUrl(String strUrl) throws MalformedURLException {
		// 문서를 로드 및 분해
		Document document = loadDocument(strUrl);
		DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);
		
		// 임베딩 모델 선언
		EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
				.apiKey(API_KEY)
				.modelName("text-embedding-3-large")
				.dimensions(1536)
				.build();
		
		// 분할한 문서 모델에 임베딩
		// 예시이므로 인-메모리에 저장
		EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
		EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
				.documentSplitter(splitter)
				.embeddingModel(embeddingModel)
				.embeddingStore(embeddingStore)
				.build();
		ingestor.ingest(document);
		
		// 모델 선언
		ChatLanguageModel chatLanguageModel = OpenAiChatModel.builder()
				.apiKey(API_KEY)
				.modelName("gpt-3.5-turbo")
				.build();
		
		// 사용자의 쿼리와 이전 대화를 하나의 독립형 쿼리로 변환 -> 프로세스 품질 향상 가능
		QueryTransformer queryTransformer = new CompressingQueryTransformer(chatLanguageModel);
		
		// 콘텐츠 연관성 검색
		ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
				.embeddingStore(embeddingStore)
				.embeddingModel(embeddingModel)
				.maxResults(2)
				.minScore(0.6)
				.build();
		
		// RAG 흐름 진입점. RAG 동작을 사용자 정의 가능
		RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
				.queryTransformer(queryTransformer)
				.contentRetriever(contentRetriever)
				.build();
		
		return AiServices.builder(Assistant.class)
				.chatLanguageModel(chatLanguageModel)
				.retrievalAugmentor(retrievalAugmentor)
				.chatMemory(MessageWindowChatMemory.withMaxMessages(10))
				.build();
	}
	private static Document loadDocument(String strUrl) throws MalformedURLException {
		// Web 문서 로드
		URL url = new URL(strUrl);
		Document htmlDocument = UrlDocumentLoader.load(url, new TextDocumentParser());
		
		// HTML 텍스트를 추출해 문서로 변환
		HtmlTextExtractor transformer = new HtmlTextExtractor(null, null, true);
		
		return transformer.transform(htmlDocument);
	}
}
