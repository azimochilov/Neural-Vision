package com.fotodetector.api;

import com.fotodetector.entity.ChatCompletionRequest;
import com.fotodetector.entity.ChatCompletionResponse;
import com.google.api.client.util.Value;
import com.theokanning.openai.audio.CreateTranscriptionRequest;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;
import java.io.File;
@RestController
@CrossOrigin
public class OpenAiController {

    @Autowired
    RestTemplate restTemplate;
    private String apiKey = "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";

    @PostMapping("/audio")
    public String audio(@RequestParam("file") MultipartFile file){

        String filePath = file.getOriginalFilename();
        String uploadDir = "D:\\audio\\" + filePath;
        System.out.println(filePath);
        OpenAiService service = new OpenAiService(apiKey);

        CreateTranscriptionRequest request = new CreateTranscriptionRequest();
        request.setModel("whisper-1");
        String transcription = service.createTranscription(request, uploadDir).getText();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(transcription, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "http://localhost:8081/hitOpenaiApi",
                HttpMethod.POST,
                requestEntity,
                String.class
        );



//        HttpEntity<String> requestEntity2 = new HttpEntity<>(finalReslut, headers);
//
//        ResponseEntity<String> responseEntity2 = restTemplate.exchange(
//                "http://localhost:8081//feedbackOpenaiApi",
//                HttpMethod.POST,
//                requestEntity2,
//                String.class
//        );

        return "\nSummary: " + responseEntity.getBody();
    }


    @PostMapping("/hitOpenaiApi")
    public String getOpenaiResponse(@RequestBody String prompt) {

        String requst = "Summarize Text: ";
        String lastRes = requst + prompt;

        ChatCompletionRequest chatCompletionRequest =
                new ChatCompletionRequest("gpt-3.5-turbo", lastRes);
        ChatCompletionResponse response =
                restTemplate.postForObject("https://api.openai.com/v1/chat/completions", chatCompletionRequest, ChatCompletionResponse.class);


        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            String result = response.getChoices().get(0).getMessage().getContent();
            return result;
        }


        return lastRes;
    }

    @PostMapping("/feedbackOpenaiApi")
    public String getFeedbackOpenaiResponse(@RequestBody String prompt) {

        String requst = "Give to that summary Feedback ";
        String lastRes = requst + prompt;

        ChatCompletionRequest chatCompletionRequest =
                new ChatCompletionRequest("gpt-3.5-turbo", lastRes);
        ChatCompletionResponse response =
                restTemplate.postForObject("https://api.openai.com/v1/chat/completions", chatCompletionRequest, ChatCompletionResponse.class);


        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            String result = response.getChoices().get(0).getMessage().getContent();
            return result;
        }

        return lastRes;
    }
}