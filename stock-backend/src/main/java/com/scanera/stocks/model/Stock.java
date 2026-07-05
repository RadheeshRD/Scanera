package com.scanera.stocks.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Maps to the STOCKS table in Oracle Autonomous DB.
 *
 * IMPORTANT: column names below are a best guess based on the
 * fields scanera.html already expects (stockName, sector, status,
 * changes, entryDate). If your actual table uses different column
 * names, just update the @Column values - nothing else needs to change.
 */
@Entity
@Table(name = "SIGNALS")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "STOCK_NAME")
    private String stockName;

    @Column(name = "SECTOR")
    private String sector;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "CHANGES")
    private String changes;

    @Column(name = "ENTRY_DATE")
    private LocalDate entryDate;

    public Stock() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
