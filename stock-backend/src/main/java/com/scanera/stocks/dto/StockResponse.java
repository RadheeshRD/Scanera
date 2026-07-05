package com.scanera.stocks.dto;

import com.scanera.stocks.model.Stock;
import java.time.LocalDate;

/**
 * Shape returned by /api/stocks - matches exactly what
 * buildSignalCard() in scanera.html expects.
 */
public class StockResponse {

    private String stockName;
    private String sector;
    private String status;
    private String changes;
    private LocalDate entryDate;

    public StockResponse() {
    }

    public StockResponse(Stock s) {
        this.stockName = s.getStockName();
        this.sector = s.getSector();
        this.status = s.getStatus();
        this.changes = s.getChanges();
        this.entryDate = s.getEntryDate();
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChanges() {
        return changes;
    }

    public void setChanges(String changes) {
        this.changes = changes;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }
}
