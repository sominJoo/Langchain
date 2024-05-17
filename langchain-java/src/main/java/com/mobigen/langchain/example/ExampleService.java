package com.mobigen.langchain.example;

import com.mobigen.langchain.ai.Assistant;
import com.mobigen.langchain.ai.AssistantConfig;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;

@Service
@RequiredArgsConstructor
public class ExampleService {
	private final ChatLanguageModel chatLanguageModel;
	
	/**
	 * 일반 Chat
	 * @param question
	 * @return
	 */
	public String getQuestion(String question) {
		return chatLanguageModel.generate(UserMessage.from(question)).content().text();
	}
	
	/**
	 * Prompt를 사용해 조금 더 정확한 결과를 얻게 만든다.
	 * @param question
	 * @return
	 */
	public String getQuestionWithSystemMessage(String question) {
		// LLM에게 추가적인 명령 전달
		SystemMessage systemMessage = SystemMessage.from("query 질문은 postgresql로 대답해야해");
		
		return chatLanguageModel.generate(systemMessage, UserMessage.from(question)).content().text();
	}
	
	/**
	 * RAG - 문서를 파싱해 정보 출력
	 * @param question
	 * @return
	 * @throws MalformedURLException
	 */
	public String getRag(String question) throws MalformedURLException {
		String url = "https://www.datanet.co.kr/news/articleView.html?idxno=183958";
		Assistant assistant = AssistantConfig.ragAssistantWithUrl(url);
		return assistant.chat(question);
	}
}
