# HorizenEconomy

A Paper 26.3 economy plugin project for Horizen SMP Season 2. Currency is Nepali Rupee (`रू`), with whole-rupee integer storage and SQLite persistence.

## Compatibility

- Paper API: Maven range `[26.3.build,)` from PaperMC's current project setup documentation.
- Java: 25 (Paper 26.3 runtime requirement).
- Build: Maven 3.9+.
- Database: SQLite via the shaded Xerial JDBC driver.
- No scoreboard, PlaceholderAPI, Vault, or external GUI dependency.

## Implemented features

- Wallet balances, `/balance` (`/bal`), `/pay`, `/baltop`, `/eco` give/take/set/audit.
- SQLite transactions, wallet constraints, transaction history, WAL mode, shutdown backup rotation (last 7 backups).
- Bank wallet transfer GUI, deposit/withdraw commands, bank history, optional scheduled interest (disabled by default).
- YAML-driven `/shop` categories and item pricing, buy/sell, `/sell hand`, `/sell all`, shift-click quantity buying, expensive-purchase confirmation, metadata rejection for sell orders.
- Persistent upgrade levels; Ender Storage is a real virtual inventory that expands by 9 slots per level and saves contents. Homes upgrades affect `/sethome` capacity. Sell Limit upgrades affect `/sell all` quantity limits.
- Permanent Custom Manager purchase for `100,000,000,000,000` rupees. It never grants Bukkit OP. Individual powers are config- and permission-gated; manager prefix appears in chat and tab.
- Manager-only `/adminshop` with configurable categories and configurable free access.
- Anonymous private `/order` marketplace with permanent unlock, reserved buyer funds, random IDs, partial fulfilment, expiry refunds, seller payment, and private mailbox delivery.
- Anonymous `/auction` with item escrow, bidder money escrow, outbid refunds, anti-sniping, cancellation restrictions, automatic settlement, and offline item claims.

## Important limitations before production use

This source has not been run against a live Paper 26.3 server in this environment. The current build container has Java 21, no Maven installation, and no network access, while Paper 26.3's API requires Java 25. Consequently **no compiled JAR is included or claimed as tested**. The included GitHub Actions workflow builds and runs the included unit tests using Java 25; use that build (or a local Java 25 + Maven environment) before installation.

SQLite operations are serialized and transaction-safe, but most command/database calls currently execute synchronously on the server thread. This is suitable for a small test server but should be load-tested and moved behind an async service queue before a high-concurrency public launch. A real Paper integration test is still required for GUI edge cases, shutdown/restart recovery, and concurrent market activity.

The uploaded EconomyShopGUI ZIP was not present in the available files. `shop.yml` therefore contains an editable starter catalogue for Farming, Commands, Gear, and Ores. It is not a reproduction of the missing ZIP. The manager admin shop's `SPAWNER` entry is a vanilla spawner item and does not encode a custom mob type.

The bank is a single bank/savings balance; interest applies to that balance when enabled. No external plugin integrations are required.

## Build

```bash
mvn -B clean verify
```

The shaded plugin JAR is written to `target/HorizenEconomy-1.0.0.jar`.

See [SETUP.md](SETUP.md) and [COMMANDS.md](COMMANDS.md).
