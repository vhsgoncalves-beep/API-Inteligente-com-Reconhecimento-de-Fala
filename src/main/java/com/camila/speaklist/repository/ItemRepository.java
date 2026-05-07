package com.camila.speaklist.repository;

import com.camila.speaklist.model.ItemLista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<ItemLista, Long> {

    Optional<ItemLista> findByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCase(String nome);
}
