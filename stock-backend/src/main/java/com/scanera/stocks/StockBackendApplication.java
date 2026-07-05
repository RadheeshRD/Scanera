package com.scanera.stocks;

import com.scanera.stocks.config.WalletExtractor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StockBackendApplication {
    public static void main(String[] args) {
        // Extract the bundled wallet (classpath:/wallet/*) to a real,
        // absolute temp directory BEFORE the Spring context (and its
        // DataSource bean) is built, and expose that path as a system
        // property so application.properties' ${WALLET_LOCATION:...}
        // placeholder picks it up automatically.
        // An explicit -DWALLET_LOCATION=/some/path or env var still wins
        // if you ever want to point at a different wallet on disk.
        if (System.getProperty("WALLET_LOCATION") == null
                && System.getenv("WALLET_LOCATION") == null) {
            String extractedPath = WalletExtractor.extractToTempDir();
            System.setProperty("WALLET_LOCATION", extractedPath);
        }

        SpringApplication.run(StockBackendApplication.class, args);
    }
}
