package com.camila.speaklist.service;

import com.camila.speaklist.model.ItemLista;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ImagemService {

    @Autowired
    private ListaService listaService;

    public byte[] gerarImagemLista() throws IOException {
        List<ItemLista> itens = listaService.listarItens();

        int largura = 500;
        int alturaLinha = 40;
        int cabecalho = 80;
        int rodape = 40;
        int altura = cabecalho + (Math.max(itens.size(), 1) * alturaLinha) + rodape;

        BufferedImage imagem = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagem.createGraphics();

        // Fundo branco
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, largura, altura);

        // Cabeçalho azul
        g.setColor(new Color(70, 130, 180));
        g.fillRect(0, 0, largura, cabecalho);

        // Título
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("- Lista de Compras -", 20, 45);

        // Quantidade de itens
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString(itens.size() + " item(ns)", 20, 65);

        // Linha separadora
        g.setColor(new Color(200, 200, 200));
        g.drawLine(0, cabecalho, largura, cabecalho);

        if (itens.isEmpty()) {
            g.setColor(Color.GRAY);
            g.setFont(new Font("Arial", Font.ITALIC, 16));
            g.drawString("Lista vazia!", 20, cabecalho + 30);
        } else {
            for (int i = 0; i < itens.size(); i++) {
                ItemLista item = itens.get(i);

                // Fundo alternado
                if (i % 2 == 0) {
                    g.setColor(new Color(245, 245, 245));
                    g.fillRect(0, cabecalho + (i * alturaLinha),
                            largura, alturaLinha);
                }

                // Checkbox
                g.setColor(new Color(34, 139, 34));
                g.drawRect(15, cabecalho + (i * alturaLinha) + 10, 18, 18);

                // Nome do item
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 15));
                g.drawString(item.getNome(), 45, cabecalho + (i * alturaLinha) + 24);

                // Quantidade e unidade
                g.setColor(Color.GRAY);
                g.setFont(new Font("Arial", Font.PLAIN, 13));
                g.drawString(item.getQuantidade() + " " + item.getUnidade(),
                        350, cabecalho + (i * alturaLinha) + 24);
            }
        }

        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(imagem, "png", baos);
        return baos.toByteArray();
    }
}