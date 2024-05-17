package com.mobigen.langchain.ai;

import dev.langchain4j.service.spring.AiService;

/**
 * @AiService는 프로그램의 컨텍스트에서 사용 가능한 모든 LangChain4j 구성요소를 사용하여 이 인터페이스를 구현하고 빈으로 등록
 * -> 필요한 곳에 자동 배선 가능
 */
@AiService
public interface Assistant {
	String chat(String userMessage);
}
