package com.mobigen.langchain.example;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;

@RestController
@RequestMapping("/api/langchain/question")
@RequiredArgsConstructor
public class ExampleController {
	private final ExampleService exampleService;
	
	/**
	 * 일반 Chat
	 * @param question
	 * @return
	 */
	@GetMapping("")
	public String getQuestion(@RequestParam String question) {
		return exampleService.getQuestion(question);
	}
	
	/**
	 * Prompt 사용 질의
	 * @param question
	 * @return
	 */
	@GetMapping("/prompt")
	public String getQuestionWithSystemMessage(@RequestParam String question) {
		return exampleService.getQuestionWithSystemMessage(question);
	}
	
	/**
	 * RAG - 문서를 파싱해 정보 출력
	 * @param question
	 * @return
	 * @throws MalformedURLException
	 */
	@GetMapping("/rag")
	public String getRag(@RequestParam String question) throws MalformedURLException {
		return exampleService.getRag(question);
	}
}
