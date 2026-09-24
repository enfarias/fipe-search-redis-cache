package com.treinamento.fipe_search.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.treinamento.fipe_search.dto.ConsultaFipeDTO;
import com.treinamento.fipe_search.service.FipeService;

@RestController
@RequestMapping("/fipe")
public class FipeController {

    private final FipeService fipeService;

    public FipeController(FipeService fipeService) {
        this.fipeService = fipeService;
    }

    @GetMapping("/{modeloId}/{anoModelo}")
    public ConsultaFipeDTO consultar(
            @PathVariable Long modeloId,
            @PathVariable Integer anoModelo) {
        return fipeService.consultar(modeloId, anoModelo);
    }

    @DeleteMapping("/{modeloId}/{anoModelo}/cache")
    public ResponseEntity<Void> invalidarCache(
            @PathVariable Long modeloId,
            @PathVariable Integer anoModelo) {
        fipeService.invalidar(modeloId, anoModelo);
        return ResponseEntity.noContent().build();
    }
}