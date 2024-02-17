package com.fotodetector.api;


import com.fotodetector.entity.TranslationRequest;
import com.fotodetector.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TranslateController {

    @Autowired
    private TranslationService translationService;

    @PostMapping("/translate")
    public String translate(@RequestBody TranslationRequest request) {

        String translatedText = translationService.translateText(request.getText(), request.getTargetLanguage());
        return translatedText;
    }
}