# Commands

## Economy

| Command | Description | Permission |
|---|---|---|
| `/balance` or `/bal` | Show wallet and bank balances | `horizeneconomy.balance` |
| `/pay <player> <amount>` | Transfer whole rupees to an online/offline known player | `horizeneconomy.pay` |
| `/baltop` | Top 10 balances; ties sort alphabetically | `horizeneconomy.balance` |
| `/eco give <player> <amount>` | Admin credit | `horizeneconomy.admin` |
| `/eco take <player> <amount>` | Admin debit | `horizeneconomy.admin` |
| `/eco set <player> <amount>` | Admin set balance | `horizeneconomy.admin` |
| `/eco audit <player>` | Inspect recent private wallet transaction log | `horizeneconomy.admin` |

All amounts are positive whole rupees. Decimal input is rejected.

## Bank and shop

| Command | Description |
|---|---|
| `/bank` | Open bank GUI |
| `/bank deposit <amount>` | Deposit wallet funds |
| `/bank withdraw <amount>` | Withdraw bank funds |
| `/bank history` | View recent bank transactions |
| `/shop` | Open category shop |
| `/sell hand` | Sell the plain item stack in your main hand |
| `/sell all` | Sell eligible plain shop items up to your configured sell limit |

Shift-click a shop item to buy up to 64 (subject to configured maximum). Right-click a shop item to sell one plain item. Expensive purchases require confirmation.

## Upgrades and homes

| Command | Description |
|---|---|
| `/upgrade` | Open permanent upgrades |
| `/sethome <name>` | Save a home; home capacity increases with Homes upgrades |
| `/home <name>` | Teleport to a saved home |

Ender Storage upgrades add 9 slots per level to the player's persistent virtual storage. Click the Ender Storage upgrade again after unlocking it to reopen the storage.

## Custom Manager

| Command | Description |
|---|---|
| `/opshop` or `/opshop status` | View rank ownership/price |
| `/opshop buy` | Purchase permanent Custom Manager |
| `/adminshop` | Open manager-only admin shop |
| `/hop help` | List available powers |
| `/hop fly` | Toggle flight |
| `/hop gm <survival|creative|adventure|spectator>` | Change own gamemode |
| `/hop tp <player>` | Teleport to online player |
| `/hop heal` | Heal self |
| `/hop feed` | Restore hunger |
| `/hop give <material> <amount>` | Give self up to 64 of a valid material |

Custom Manager is not Bukkit OP and does not grant console, permission-editing, world deletion, economy administration, or arbitrary command execution.

## Private Orders

| Command | Description |
|---|---|
| `/order` | Show help |
| `/order unlock` | Permanently unlock orders |
| `/order create <material> <amount> <unit-price>` | Create a buy order and escrow the total price |
| `/order browse` | Browse anonymous open orders |
| `/order accept <id> [amount]` | Fulfil all or part of an order with plain matching items |
| `/order cancel <id>` | Cancel your open order and refund remaining escrow |
| `/order history` | View your own private order history |

Order goods are placed in the buyer's private mailbox after fulfilment. Use `/auction claim` to claim delivered items. Expired open orders refund remaining escrow automatically.

## Anonymous Auctions

| Command | Description |
|---|---|
| `/auction` | Show help |
| `/auction unlock` | Permanently unlock auctions |
| `/auction create <item> <amount> <starting-price>` | Auction the specified item held in your main hand |
| `/auction browse` | Browse anonymous auctions |
| `/auction bid <id> <amount>` | Place a bid; the bid is escrowed |
| `/auction cancel <id>` | Cancel your auction only if it has no bids |
| `/auction history` | View your own recent bids |
| `/auction claim` | Claim offline auction wins, returned items, and order deliveries |

The seller and bidder identities are never included in public listing output. The highest bidder is refunded automatically when outbid. Auctions settle after expiry; winning items go to the winner's private mailbox.
