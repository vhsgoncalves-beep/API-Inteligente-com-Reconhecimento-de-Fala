package com.camila.speaklist.service;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class TranscricaoService {

    @Autowired
    private OpenAiAudioTranscriptionModel transcriptionModel;

    public String transcrever(MultipartFile arquivo) throws IOException {

        ByteArrayResource audioResource = new ByteArrayResource(arquivo.getBytes()) {
            @Override
            public String getFilename() {
                return arquivo.getOriginalFilename();
            }
        };

        OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
                .model("whisper-large-v3")
                .language("pt")
                .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .build();

        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioResource, options);
        return transcriptionModel.call(prompt).getResult().getOutput();
    }
}