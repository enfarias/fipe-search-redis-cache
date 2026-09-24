package com.treinamento.fipe_search.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.treinamento.fipe_search.dto.ConsultaFipeDTO;
import com.treinamento.fipe_search.repository.ReferenciaRepository;

@Service
public class FipeService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FipeService.class);

	private final ReferenciaRepository referenciaRepository;

	public FipeService(ReferenciaRepository referenciaRepository) {
		this.referenciaRepository = referenciaRepository;
	}

	@Cacheable(cacheNames = "${app.cache.name}")
	public ConsultaFipeDTO consultar(Long modeloId, Integer anoModelo) {
		LOGGER.debug("Cache MISS — consultando PostgreSQL: modelo={}, ano={}", modeloId, anoModelo);

		return referenciaRepository
				.findReferencias(modeloId, anoModelo)
				.stream()
				.findFirst()
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"Consulta FIPE não encontrada: modelo=%d, ano=%d"
						.formatted(modeloId, anoModelo)));
	}

	@CacheEvict(cacheNames = "${app.cache.name}")
	public void invalidar(Long modeloId, Integer anoModelo) {
		LOGGER.debug("Cache invalidado: modelo={}, ano={}", modeloId, anoModelo);
	}
}