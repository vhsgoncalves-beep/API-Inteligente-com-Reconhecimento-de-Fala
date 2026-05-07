package com.camila.speaklist.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComandoService {

    @Autowired
    private ChatModel chatModel;

    public String interpretarComando(String textoTranscrito) {
        String instrucao = """
                Você é um assistente de lista de compras.
                O usuário falou: "%s"
                
                Responda APENAS com um JSON nesse formato, sem explicações:
                {
                  "acao": "adicionar" ou "remover" ou "listar" ou "somar" ou "subtrair" ou "atualizar",
                  "nome": "nome do item ou null se for listar",
                  "quantidade": numero ou null,
                  "unidade": "unidade ou null"
                }
                
                Exemplos de comandos e ações:
                - "adiciona 2 litros de leite" -> adicionar
                - "remove o leite" -> remover
                - "quais itens tem na lista" -> listar
                - "adiciona mais 3 litros de leite" -> somar
                - "tira 1 litro de leite" -> subtrair
                - "muda o leite para 5 litros" -> atualizar
                """.formatted(textoTranscrito);

        Prompt prompt = new Prompt(instrucao);
        return chatModel.call(prompt).getResult().getOutput().getText();
    }
}