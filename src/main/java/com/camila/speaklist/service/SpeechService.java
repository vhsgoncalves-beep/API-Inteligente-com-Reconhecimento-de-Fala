package com.camila.speaklist.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class SpeechService {

    public byte[] converterParaAudio(String texto) throws IOException, InterruptedException {

        String textoEncoded = URLEncoder.encode(texto, StandardCharsets.UTF_8);
        String url = "https://translate.google.com/translate_tts?ie=UTF-8&q="
                + textoEncoded
                + "&tl=pt-BR&client=tw-ob";

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Mozilla/5.0")
                .GET()
                .build();

        HttpResponse<byte[]> response = client.send(request,
                HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Erro no TTS: " + response.statusCode());
        }

        return response.body();
    }
}