# Installation and Setup

## Requirements

- Paper 26.3 server.
- Java 25 runtime on the server.
- Java 25 JDK and Maven 3.9+ to build locally, or use the included GitHub Actions workflow.

## Build the JAR

1. Download/extract the complete source project.
2. Open the `HorizenEconomy` folder in IntelliJ IDEA as a Maven project.
3. Set the Project SDK and Maven runner JDK to Java 25.
4. Run `mvn clean verify` (or IntelliJ Maven Lifecycle → `verify`).
5. Confirm `target/HorizenEconomy-1.0.0.jar` exists.

Alternatively, push the project to GitHub. The workflow `.github/workflows/build.yml` runs Maven on Java 25 and uploads the JAR as a build artifact.

## Install on the Paper server

1. Stop the server and make a full backup of the server folder.
2. Copy `HorizenEconomy-1.0.0.jar` into `plugins/`.
3. Start the server once. The plugin creates `plugins/HorizenEconomy/` and initializes `economy.db`.
4. Stop the server and edit the YAML files in `plugins/HorizenEconomy/` as needed.
5. Start the server again. Avoid editing YAML while the server is running.
6. Verify startup logs contain `HorizenEconomy enabled; SQLite WAL storage ready.`
7. Test `/balance`, `/bank`, `/shop`, `/order unlock`, and `/auction unlock` with a test account before enabling public use.

## Configuration files

- `config.yml`: currency, transaction limits, interest, Manager powers, unlock prices, auction duration, upgrades.
- `messages.yml`: message defaults (currently only the shared prefix is used by all command messages).
- `shop.yml`: player shop catalogue and buy/sell prices.
- `upgrades.yml`: descriptive upgrade defaults; active upgrade pricing is read from `config.yml`.
- `adminshop.yml`: manager-only admin shop catalogue.
- `orders.yml`, `auctions.yml`: market defaults; active runtime values are read from `config.yml`.

## Data and backups

SQLite data is stored in `plugins/HorizenEconomy/economy.db`. The database uses WAL mode. On clean shutdown, the plugin checkpoints and copies a timestamped backup into `plugins/HorizenEconomy/backups/`, retaining the newest seven backup files. Always stop the server before manually copying the live database.

## Manager rank permissions

Buying Custom Manager inserts a permanent ownership record and attaches the configured individual Manager permissions to that player's session. The rank does not call `setOp(true)`. Permission toggles under `manager.powers` still apply. Do not grant `horizeneconomy.admin` to Manager rank holders.

## Before public launch

- Build with Java 25 and run `mvn clean verify`.
- Test the exact JAR on a copy of the Paper 26.3 server.
- Test with at least two player accounts for pay, order fulfilment, bidding, refunds, expiry, and offline mailbox claims.
- Test server restart while orders and auctions are open.
- Load-test database latency and market concurrency. This version uses serialized synchronous SQLite calls; a high-population server should move DB work to a managed async executor with main-thread callbacks before launch.
