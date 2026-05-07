package com.camila.speaklist.service; // Dica: mude o 'camila' para seu nome no IntelliJ depois!

import com.camila.speaklist.model.ItemLista;
import com.camila.speaklist.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService { // Mudado de ListaService para InventoryService

    @Autowired
    private ItemRepository repository;

    // Registra uma nova entrada no inventário
    public ItemLista registerEntry(String name, Integer quantity, String unit) {
        if (repository.existsByNomeIgnoreCase(name)) {
            throw new RuntimeException("Asset Error: '" + name + "' already exists in database!");
        }
        ItemLista item = new ItemLista();
        item.setNome(name);
        item.setQuantidade(quantity);
        item.setUnidade(unit);
        return repository.save(item);
    }

    // Define um novo valor absoluto para o estoque
    public ItemLista updateStockLevel(String name, Integer newQuantity) {
        ItemLista item = repository.findByNomeIgnoreCase(name)
                .orElseThrow(() -> new RuntimeException("Inquiry Error: '" + name + "' not found!"));
        item.setQuantidade(newQuantity);
        return repository.save(item);
    }

    // Incrementa o saldo atual do item
    public ItemLista increaseStock(String name, Integer amount) {
        ItemLista item = repository.findByNomeIgnoreCase(name)
                .orElseThrow(() -> new RuntimeException("Inquiry Error: '" + name + "' not found!"));
        item.setQuantidade(item.getQuantidade() + amount);
        return repository.save(item);
    }

    // Decrementa o saldo e valida se o item deve ser removido
    public ItemLista decreaseStock(String name, Integer amount) {
        ItemLista item = repository.findByNomeIgnoreCase(name)
                .orElseThrow(() -> new RuntimeException("Inquiry Error: '" + name + "' not found!"));
        
        int updatedQuantity = item.getQuantidade() - amount;
        
        if (updatedQuantity <= 0) {
            repository.delete(item);
            throw new RuntimeException("System Notification: '" + name + "' has been purged due to zero balance.");
        }
        
        item.setQuantidade(updatedQuantity);
        return repository.save(item);
    }

    // Remove o registro permanentemente
    public void deleteRecord(String name) {
        ItemLista item = repository.findByNomeIgnoreCase(name)
                .orElseThrow(() -> new RuntimeException("Action Error: Resource '" + name + "' not found!"));
        repository.delete(item);
    }

    // Gera o relatório de todos os registros
    public List<ItemLista> fetchAllRecords() {
        return repository.findAll();
    }
}