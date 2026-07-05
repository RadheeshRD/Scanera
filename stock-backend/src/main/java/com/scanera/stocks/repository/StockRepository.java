package com.scanera.stocks.repository;

import com.scanera.stocks.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {

    /**
     * "Last 10" = the 10 most recently added stocks, newest first.
     * Ordered by ENTRY_DATE desc, then ID desc as a tiebreaker
     * (matches the comment already in scanera.html's loadSignals()).
     *
     * Spring Data derives this query from the method name alone -
     * no JPQL/SQL needed.
     */
    List<Stock> findTop10ByOrderByEntryDateDescIdDesc();
}
