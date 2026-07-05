package com.scanera.stocks.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Extracts the wallet bundled under classpath:/wallet/* into a real,
 * absolute directory on disk and returns that directory's path.
 *
 * Why: Oracle's JDBC driver needs TNS_ADMIN to be a real filesystem
 * directory containing tnsnames.ora/cwallet.sso/etc. It can't read
 * those files straight out of a jar's classpath, and a *relative*
 * path like "./wallet" depends on whatever directory the JVM happens
 * to be launched from - which varies between an IDE, `mvn spring-boot:run`,
 * and `java -jar`. Extracting to an absolute temp dir up front removes
 * that whole class of "file does not exist" (ORA-12263) errors.
 */
public final class WalletExtractor {

    private static final String[] WALLET_FILES = {
            "cwallet.sso",
            "ewallet.p12",
            "ewallet.pem",
            "keystore.jks",
            "ojdbc.properties",
            "sqlnet.ora",
            "tnsnames.ora",
            "truststore.jks"
    };

    private WalletExtractor() {
    }

    /**
     * @return absolute path to the directory containing the extracted
     *         wallet files, suitable for use as TNS_ADMIN.
     */
    public static String extractToTempDir() {
        try {
            Path tempDir = Files.createTempDirectory("oracle-wallet-");
            ClassLoader cl = WalletExtractor.class.getClassLoader();

            for (String fileName : WALLET_FILES) {
                String resourcePath = "wallet/" + fileName;
                try (InputStream in = cl.getResourceAsStream(resourcePath)) {
                    if (in == null) {
                        // Some wallets don't include every optional file
                        // (e.g. keystore.jks/truststore.jks if you only use SSO) - skip silently.
                        continue;
                    }
                    Path target = tempDir.resolve(fileName);
                    Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                }
            }

            return tempDir.toAbsolutePath().toString().replace('\\', '/');
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to extract Oracle wallet from classpath:/wallet/ to a temp directory. "
                            + "Make sure src/main/resources/wallet/ contains tnsnames.ora, cwallet.sso, etc.",
                    e);
        }
    }
}
