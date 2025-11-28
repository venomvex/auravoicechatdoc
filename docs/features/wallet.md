# Wallet, Coins & Diamonds

The dual-currency economy system in Aura Voice Chat.

## Overview

The app uses two currency types:
- **Coins** — Primary spendable currency
- **Diamonds** — Received currency from gifts

---

## Coins

### Earning Coins
- Daily login rewards
- Medal achievement rewards
- Referral program rewards
- Exchange from diamonds (30% rate)

### Spending Coins
- Store purchases (frames, effects, etc.)
- Gift sending
- CP formation fee
- Family creation fee

### Display Format
- UI: Compact (K/M format, e.g., 11.8M)
- Transactions: Exact numbers with separators

---

## Diamonds

### Earning Diamonds
- Receiving gifts from other users (1:1 coin value to diamonds)
- Baggage gifts when sent count as diamond value

### Converting Diamonds
Diamonds can be exchanged back to Coins at a **30% rate**:

```
Coins Received = floor(Diamonds × 0.30)
```

### Exchange Rules
- Preview shown before confirmation
- No additional fee beyond conversion rate
- Anti-spam protection: Debounce interval between exchanges
- No maximum limit on exchange amount

---

## Transfers

### Sending Coins
- Users cannot directly transfer coins to others
- Coins are sent via gifts only
- Gift recipients receive **Diamonds** (1:1 ratio)

### Sending Diamonds
- Direct diamond transfers not allowed
- Only through gift mechanism

---

## Wallet API

### Get Balances

```
GET /wallet/balances
```

Response:
```json
{
  "coins": 1500000,
  "diamonds": 250000,
  "lastUpdated": "2025-11-27T15:30:00Z"
}
```

### Exchange Diamonds to Coins

```
POST /wallet/exchange
```

Request:
```json
{
  "diamonds": 100000
}
```

Response:
```json
{
  "success": true,
  "diamondsUsed": 100000,
  "coinsReceived": 30000,
  "newBalance": {
    "coins": 1530000,
    "diamonds": 150000
  }
}
```

---

## Large Transaction Warnings

For significant transactions, confirmation dialogs appear:
- Gift sending above threshold (configurable, e.g., 1M coins)
- Large diamond exchanges

---

## Anti-Spam Protections

- Exchange debounce interval (2-10 seconds between requests)
- Rate limiting on wallet operations
- Audit logging for large transactions

---

## Telemetry Events

| Event                        | Properties                    |
|------------------------------|-------------------------------|
| `wallet_exchange_attempt`    | diamonds, expectedCoins       |
| `wallet_exchange_success`    | diamonds, coinsReceived       |
| `wallet_view`                | coinBalance, diamondBalance   |

---

## Related Documentation

- [Store & Items](./store.md)
- [Gift System](./gifts.md)
- [Referral Programs](./referrals.md)
- [Recharge Event](./recharge-event.md)
- [VIP Recharge Bonus](./vip-recharge-bonus.md)
- [Naming Glossary](../naming-glossary.md)
- [Product Specification](../../README.md)
