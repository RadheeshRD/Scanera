# Scanera Stock Backend (Spring Boot + Oracle Autonomous DB)

This is a self-contained Spring Boot app that:
1. Connects to your Oracle Autonomous DB using the wallet you uploaded.
2. Exposes `GET /api/stocks` → the last 10 stocks, newest first.
3. Serves your `scanera.html` itself, at `http://localhost:8081/`, so
   starting the app and opening that URL shows the live website.

## 1. Project layout

```
stock-backend/
├── pom.xml
└── src/main/
    ├── java/com/scanera/stocks/
    │   ├── StockBackendApplication.java   <- extracts wallet on startup
    │   ├── config/WalletExtractor.java    <- copies classpath wallet to a real temp dir
    │   ├── model/Stock.java               <- maps to STOCKS table
    │   ├── repository/StockRepository.java
    │   ├── dto/StockResponse.java
    │   └── controller/StockController.java
    └── resources/
        ├── application.properties
        ├── wallet/                  <- your wallet, bundled into the jar's classpath
        │   ├── cwallet.sso
        │   ├── tnsnames.ora
        │   └── ... (sqlnet.ora, ojdbc.properties, etc.)
        └── static/
            ├── index.html       <- copy of scanera.html (served at "/")
            └── scanera.html
```

The wallet no longer lives as loose files next to the jar. It's bundled
under `src/main/resources/wallet/`, and on every startup
`WalletExtractor` copies those files into a fresh, **absolute** OS temp
directory and points `TNS_ADMIN` at it. This is what fixed the
`ORA-12263: Failed to access tnsnames.ora` error — that error happened
because `./wallet` is a *relative* path, and gets resolved against
whatever directory the JVM was launched from (which differs between an
IDE, `mvn spring-boot:run` from different folders, and a packaged jar).
An absolute, freshly-extracted path removes that ambiguity entirely.

## 2. Database table assumption — please check this

I don't know your real table/column names, so I mapped to a table called
`STOCKS` with columns that match what `scanera.html` already expects
(visible in its own JS comments):

| Column        | Type          | Used for          |
|---------------|---------------|-------------------|
| `ID`          | NUMBER (PK)   | tiebreaker sort   |
| `STOCK_NAME`  | VARCHAR2      | `stockName`       |
| `SECTOR`      | VARCHAR2      | `sector`          |
| `STATUS`      | VARCHAR2      | `status`          |
| `CHANGES`     | VARCHAR2      | `changes` (e.g. "+4.2%") |
| `ENTRY_DATE`  | DATE          | `entryDate`       |

If your real table/columns are named differently, you only need to edit
the `@Column(name = "...")` values in `Stock.java` — nothing else changes.

If the table doesn't exist yet, you can create it in your DB (via SQL
Developer / Database Actions) with something like:

```sql
CREATE TABLE STOCKS (
  ID         NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  STOCK_NAME VARCHAR2(100) NOT NULL,
  SECTOR     VARCHAR2(100),
  STATUS     VARCHAR2(50),
  CHANGES    VARCHAR2(20),
  ENTRY_DATE DATE DEFAULT SYSDATE
);
```

## 3. Run it

Requires Java 17+ and Maven.

```bash
cd stock-backend
mvn spring-boot:run
```

No wallet path setup needed — it's bundled in the jar and extracted
automatically on startup (see section 2 above). You can run this from
any directory; it no longer matters where your terminal's `cwd` is.

Once it starts, open:
- **http://localhost:8081/** → your website, live, querying the real DB
- **http://localhost:8081/api/stocks** → raw JSON, the last 10 stocks

There's no separate "start the page" step — Spring Boot serves the
HTML file itself from `src/main/resources/static/`, so the app starting
*is* the website becoming available on port 8081.

## 4. Security note (please read)

You shared your DB password in plain text in this chat. I've kept it as
the default in `application.properties` for convenience, but you should:
- **Rotate/change the ADMIN password** in OCI Console after testing, since
  it's now been typed into a chat log.
- For anything beyond local testing, set it via an environment variable
  instead of the properties file:
  ```bash
  export DB_PASSWORD=your_new_password
  ```
  (the properties file already reads `${DB_PASSWORD:Swathi12}`, so an env
  var will override the default automatically).
- Don't commit the wallet to git (it's now at `src/main/resources/wallet/`)
  or the real password — both should be kept out of any public/shared repo.

## 5. Troubleshooting — common cases

| Symptom | Likely cause / fix |
|---|---|
| `ORA-12263` (`Failed to access tnsnames.ora`) | Should no longer happen — wallet is bundled + auto-extracted to an absolute path on startup. If you still see it, check the startup logs for `WalletExtractor` errors (e.g. resource not found), meaning `src/main/resources/wallet/` is missing files. |
| `ORA-01017: invalid username/password` | Wrong `DB_USERNAME`/`DB_PASSWORD`, or the ADMIN password was rotated since the wallet was downloaded. |
| `ORA-00942: table or view does not exist` | The `STOCKS` table name/case doesn't match your real schema — update `@Table(name = ...)` in `Stock.java` (Oracle is case-sensitive if the table was created with quoted lowercase names). |
| App starts but `/api/stocks` returns `[]` | Table exists but is empty, or `ENTRY_DATE` is null for all rows (sort still works, just nothing to show). |
| Website loads but signal cards never appear, console shows a fetch/network error | Confirm nothing else is already using port 8081 (`lsof -i :8081`), and that you're opening `http://localhost:8081/`, not double-clicking the raw `scanera.html` file from disk. |
| Connection works from SQL Developer but not from this app | Double check you unzipped the *same* wallet used here, and that the TNS alias (`nnrkjjoq5vv5kf8w_medium` by default) hasn't been deprecated — try `_high` or `_low` instead via `TNS_ALIAS` env var. |
| Want it on a different port | Change `server.port` in `application.properties`. The frontend's `API_BASE_URL` in `static/index.html`/`scanera.html` also needs to match. |

## 6. Changing which 10 rows come back

`findTop10ByOrderByEntryDateDescIdDesc()` in `StockRepository.java` is a
Spring Data "derived query" — Spring builds the SQL from the method name.
If you want a different sort (e.g. by `ID` only, or ascending), rename
that method, e.g. `findTop10ByOrderByIdDesc()`.
