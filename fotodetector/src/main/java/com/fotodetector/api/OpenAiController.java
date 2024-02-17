package com.fotodetector.api;

import com.fotodetector.entity.ChatCompletionRequest;
import com.fotodetector.entity.ChatCompletionResponse;
import com.google.api.client.util.Value;
import com.theokanning.openai.audio.CreateTranscriptionRequest;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;
import java.io.File;

@RestController
public class OpenAiController {

    @Autowired
    RestTemplate restTemplate;
    private String apiKey = "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";

    @PostMapping("/audio")
    public String audio(@RequestParam("file") MultipartFile file){

        String filePath = file.getOriginalFilename();
        String uploadDir = "C:\\Users\\azimo\\OneDrive\\Рабочий стол\\fotodetector\\audios\\"+filePath;
        System.out.println(filePath);
        OpenAiService service = new OpenAiService(apiKey);

        CreateTranscriptionRequest request = new CreateTranscriptionRequest();
        request.setModel("whisper-1");
        String transcription = service.createTranscription(request,uploadDir).getText();
        return transcription;
    }

    @PostMapping("/hitOpenaiApi")
    public String getOpenaiResponse(@RequestBody String prompt) {
        // Creates ChatCompletionRequest object with the model name and the user prompt
        ChatCompletionRequest chatCompletionRequest =
                new ChatCompletionRequest("gpt-3.5-turbo", prompt);
        ChatCompletionResponse response =
                restTemplate.postForObject("https://api.openai.com/v1/chat/completions", chatCompletionRequest, ChatCompletionResponse.class);


        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            String result = response.getChoices().get(0).getMessage().getContent();
            return result;
        }


        return prompt;
    }
}
