package com.smilebat.learntribe.openai.services;

import com.smilebat.learntribe.dataaccess.ProfileSummaryRepository;
import com.smilebat.learntribe.dataaccess.jpa.entity.ProfileSummary;
import com.smilebat.learntribe.kafka.KafkaProfileRequest;
import com.smilebat.learntribe.learntribeclients.openai.OpenAiService;
import com.smilebat.learntribe.openai.OpenAiRequest;
import com.smilebat.learntribe.openai.response.Choice;
import com.smilebat.learntribe.openai.response.OpenAiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
@Service

public class SummaryFactory {

    private final OpenAiService openAiService;

    private final ProfileSummaryRepository profileSummaryRepository;

    public void createSummaries(KafkaProfileRequest request){
        List<String> skills = request.getSkills();
        for (String skill : skills) {
            getOpenAiCompletions(skill, request.getRole());
        }
    }

    private void getOpenAiCompletions(String skill, String role ) {
//        String prompt = "Create 3 profile summaries for "+skill;
//        log.info(prompt);
//        OpenAiRequest request = new OpenAiRequest();
//        request.setPrompt(prompt);
//        final OpenAiResponse response = openAiService.getCompletions(request);
//        final List<Choice> choices = response.getChoices();
//        if (choices == null || choices.isEmpty()) {
//            log.info("Unable to create open ai completion text");
//            throw new IllegalArgumentException();
//        }
//        Choice choice = choices.get(0);
//        String completedText = choice.getText();
        String completedText = "1. Spring Boot is an open source Java-based framework used to create stand-alone, production-grade applications. It is lightweight, easy to use, and provides powerful features such as auto-configuration, dependency management, and embedded web servers. With Spring Boot, developers can quickly create robust, secure, and production-ready applications with minimal effort.\n"+
                "2. Spring Boot is a powerful and feature-rich framework that makes it easy to create Java-based applications. It offers an impressive suite of tools and features, such as auto-configuration, dependency management, and embedded web servers. It is lightweight, easy to use, and provides a powerful platform for building high-performance and secure applications.\n"+
                "3. Spring Boot is a modern, open source Java-based framework that enables developers to create stand-alone, production-grade applications. It provides powerful features, such as auto-configuration, dependency management, and embedded web servers. With Spring Boot, developers can quickly build secure, robust, and high-performance applications in no time.";
        List<String> summaries = parseSummaries(completedText);
        for (String summary : summaries){
            ProfileSummary profileSummary = new ProfileSummary();
            profileSummary.setSummary(summary);
            profileSummary.setRole(role);
            profileSummary.setSkill(skill);
            profileSummaryRepository.save(profileSummary);
        }
    }

    private List<String> parseSummaries(String completedText) {
        List<String> summaries = new ArrayList<String>();
        String[] arr = completedText.split("\n");
        for (String s : arr) {
            s = s.replaceAll("[0-9]. ", "");
            System.out.println(s);
            summaries.add(s);
        }
        return summaries;
    }
}
