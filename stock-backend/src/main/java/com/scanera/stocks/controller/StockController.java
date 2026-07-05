package com.scanera.stocks.controller;

import com.scanera.stocks.dto.StockResponse;
import com.scanera.stocks.repository.StockRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*") // tighten via app.cors.allowed-origins / CorsConfig for production
public class StockController {

    private final StockRepository stockRepository;

    public StockController(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * GET /api/stocks
     * Returns the last 10 stocks, newest first - exactly what
     * scanera.html's loadSignals() fetches and renders.
     */
    @GetMapping("/api/stocks")
    public List<StockResponse> getLatestStocks() {
        return stockRepository.findTop10ByOrderByEntryDateDescIdDesc()
                .stream()
                .map(StockResponse::new)
                .collect(Collectors.toList());
    }
}
