package com.camila.speaklist.controller;

import com.camila.speaklist.model.ItemLista;
import com.camila.speaklist.service.ImagemService;
import com.camila.speaklist.service.InventoryService; // Import atualizado
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory") // Mudado de /api/lista para um padrão versionado
public class InventoryController { // Nome da classe atualizado

    @Autowired
    private InventoryService inventoryService; // Nome do serviço atualizado

    @Autowired
    private ImagemService imagemService;

    @PostMapping("/add-asset") // Mudado de /adicionar
    public ResponseEntity<ItemLista> registerAsset(
            @RequestParam String nome,
            @RequestParam Integer quantidade,
            @RequestParam String unidade) {
        ItemLista item = inventoryService.registerEntry(nome, quantidade, unidade);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/set-level") // Mudado de /atualizar
    public ResponseEntity<ItemLista> setStockLevel(
            @RequestParam String nome,
            @RequestParam Integer quantidade) {
        ItemLista item = inventoryService.updateStockLevel(nome, quantidade);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/increment") // Mudado de /somar
    public ResponseEntity<ItemLista> increaseAsset(
            @RequestParam String nome,
            @RequestParam Integer quantidade) {
        ItemLista item = inventoryService.increaseStock(nome, quantidade);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/decrement") // Mudado de /subtrair
    public ResponseEntity<String> decreaseAsset(
            @RequestParam String nome,
            @RequestParam Integer quantidade) {
        inventoryService.decreaseStock(nome, quantidade);
        return ResponseEntity.ok("Stock for '" + nome + "' has been updated!");
    }

    @DeleteMapping("/purge") // Mudado de /remover
    public ResponseEntity<String> deleteAsset(@RequestParam String nome) {
        inventoryService.deleteRecord(nome);
        return ResponseEntity.ok("Resource '" + nome + "' removed from database.");
    }

    @GetMapping("/report") // Mudado de @GetMapping padrão
    public ResponseEntity<List<ItemLista>> getFullReport() {
        List<ItemLista> items = inventoryService.fetchAllRecords();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/export-visual") // Mudado de /imagem
    public ResponseEntity<byte[]> exportInventoryImage() throws IOException {
        byte[] imagem = imagemService.gerarImagemLista();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=inventory_report.png")
                .contentType(MediaType.IMAGE_PNG)
                .body(imagem);
    }
}