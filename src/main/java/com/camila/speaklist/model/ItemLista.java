package com.camila.speaklist.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "itens_lista")
public class ItemLista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Integer quantidade;

    @Column
    private String unidade; // ex: kg, litros, unidades

    @Column(name = "adicionado_em")
    private LocalDateTime adicionadoEm;

    @PrePersist
    public void prePersist() {
        this.adicionadoEm = LocalDateTime.now();
    }
}