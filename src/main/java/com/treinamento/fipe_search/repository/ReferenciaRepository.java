package com.treinamento.fipe_search.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.treinamento.fipe_search.dto.ConsultaFipeDTO;
import com.treinamento.fipe_search.entity.ReferenciaEntity;

public interface ReferenciaRepository extends JpaRepository<ReferenciaEntity, Long> {

    @Query("""
            SELECT new com.treinamento.fipe_search.dto.ConsultaFipeDTO(
                ma.nome, mo.nome, r.anoModelo, r.preco, r.mesReferencia)
            FROM ReferenciaEntity r
                JOIN r.modelo mo
                JOIN mo.marca ma
            WHERE r.modelo.id = :modeloId AND r.anoModelo = :anoModelo
            ORDER BY r.mesReferencia DESC
            """)
    List<ConsultaFipeDTO> findReferencias(
            @Param("modeloId") Long modeloId,
            @Param("anoModelo") Integer anoModelo);

    @Query(value = "SELECT pg_sleep(0.05)", nativeQuery = true)
    void simulateDelay();
}