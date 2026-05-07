package com.camila.speaklist.controller;

import com.camila.speaklist.service.ComandoService;
import com.camila.speaklist.service.ListaService;
import com.camila.speaklist.service.SpeechService;
import com.camila.speaklist.service.TranscricaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/audio")
public class AudioController {

    @Autowired
    private TranscricaoService transcricaoService;

    @Autowired
    private ComandoService comandoService;

    @Autowired
    private ListaService listaService;

    @Autowired
    private SpeechService speechService;

    @PostMapping("/comando")
    public ResponseEntity<byte[]> processarComando(
            @RequestParam("audio") MultipartFile arquivo) throws Exception {

        // 1. Transcreve o áudio para texto
        String texto = transcricaoService.transcrever(arquivo);

        // 2. Interpreta o comando com LLaMA
        String jsonResposta = comandoService.interpretarComando(texto);

        // 3. Processa o JSON
        String acao = extrairValor(jsonResposta, "acao");
        String mensagemResposta;

        switch (acao) {
            case "adicionar" -> {
                String nome = extrairValor(jsonResposta, "nome");
                int quantidade = Integer.parseInt(extrairValor(jsonResposta, "quantidade"));
                String unidade = extrairValor(jsonResposta, "unidade");
                listaService.adicionarItem(nome, quantidade, unidade);
                mensagemResposta = quantidade + " " + unidade + " de " + nome + " adicionado na lista!";
            }
            case "remover" -> {
                String nome = extrairValor(jsonResposta, "nome");
                listaService.removerItem(nome);
                mensagemResposta = nome + " removido da lista!";
            }
            case "listar" -> {
                var itens = listaService.listarItens();
                if (itens.isEmpty()) {
                    mensagemResposta = "Sua lista de compras está vazia!";
                } else {
                    StringBuilder sb = new StringBuilder("Sua lista tem: ");
                    itens.forEach(item -> sb.append(item.getQuantidade())
                            .append(" ")
                            .append(item.getUnidade())
                            .append(" de ")
                            .append(item.getNome())
                            .append(", "));
                    mensagemResposta = sb.toString();
                }
            }
            case "somar" -> {
                String nome = extrairValor(jsonResposta, "nome");
                String qtdStr = extrairValor(jsonResposta, "quantidade");
                int quantidade = qtdStr.isEmpty() ? 1 : Integer.parseInt(qtdStr);
                listaService.somarQuantidade(nome, quantidade);
                mensagemResposta = quantidade + " adicionado à quantidade de " + nome + "!";
            }
            case "subtrair" -> {
                String nome = extrairValor(jsonResposta, "nome");
                String qtdStr = extrairValor(jsonResposta, "quantidade");
                int quantidade = qtdStr.isEmpty() ? 1 : Integer.parseInt(qtdStr);
                listaService.subtrairQuantidade(nome, quantidade);
                mensagemResposta = quantidade + " subtraído da quantidade de " + nome + "!";
            }
            case "atualizar" -> {
                String nome = extrairValor(jsonResposta, "nome");
                String qtdStr = extrairValor(jsonResposta, "quantidade");
                int quantidade = qtdStr.isEmpty() ? 0 : Integer.parseInt(qtdStr);
                listaService.atualizarQuantidade(nome, quantidade);
                mensagemResposta = nome + " atualizado para " + quantidade + "!";
            }
            default -> mensagemResposta = "Não entendi o comando. Tente novamente!";

        }

        // 4. Converte resposta em áudio
        byte[] audioResposta = speechService.converterParaAudio(mensagemResposta);

        // 5. Retorna o áudio
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=resposta.mp3")
                .contentType(MediaType.valueOf("audio/mpeg"))
                .body(audioResposta);
    }

    private String extrairValor(String json, String chave) {
        try {
            // Remove quebras de linha e espaços extras
            json = json.replaceAll("\\s+", " ");

            String busca = "\"" + chave + "\"";
            int index = json.indexOf(busca);
            if (index == -1) return "";

            int doisPontos = json.indexOf(":", index);
            String restante = json.substring(doisPontos + 1).trim();

            // Remove espaços iniciais
            restante = restante.stripLeading();

            // Se é null
            if (restante.startsWith("null")) return "";

            // Se começa com aspas é string
            if (restante.startsWith("\"")) {
                int inicio = 1;
                int fim = restante.indexOf("\"", inicio);
                if (fim == -1) return "";
                return restante.substring(inicio, fim).trim();
            }

            // Se começa com número
            if (Character.isDigit(restante.charAt(0))) {
                StringBuilder numero = new StringBuilder();
                for (char c : restante.toCharArray()) {
                    if (Character.isDigit(c)) numero.append(c);
                    else break;
                }
                return numero.toString();
            }

            return "";
        } catch (Exception e) {
            return "";
        }
    }
}