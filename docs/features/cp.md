# CP (Couple Partnership)

The couple partnership system allows two users to form a special bond with exclusive rewards.

## Overview

CP is a premium feature where two users can form a couple, earning rewards by sending coins to each other.

---

## Formation

### Requirements
- Both users must accept the partnership
- One-time formation fee: **3,000,000 coins** (3M)

### Payment Models (Options)
1. **Single Payer:** One user pays full fee, partner confirms
2. **Split Cost:** Each pays 50% (1.5M each)
3. **Staged Cost:** Initiator pays 25% upfront, partner pays 75%

*Final payment model to be decided.*

### Process
1. User A initiates CP request
2. Fee payment (per selected model)
3. User B receives notification
4. User B accepts (and pays if applicable)
5. CP partnership created

---

## CP EXP & Rewards

### Earning CP EXP
- **1 coin sent to partner = +1 CP EXP**
- Both mutual sending counts

### Reward Thresholds (Examples)

| CP Level | Cumulative Coins | Gift Reward    | Cosmetics              |
|----------|------------------|----------------|------------------------|
| 1        | 5,000,000        | 500,000        | CP Frame (basic)       |
| 2        | 10,000,000       | 1,000,000      | Mic skin              |
| 3        | 25,000,000       | 2,500,000      | Seat heart upgrade    |
| ...      | ...              | ...            | ...                   |
| 10       | 250,000,000      | 25,000,000     | Premium set (permanent)|

*Full threshold table to be provided via product screenshots.*

---

## CP Visuals

### CP Frame
- Special frame displayed on both partners' profiles
- Upgrades at certain CP levels

### Seat Heart Effect
- Enhanced heart animation when partners are seated side-by-side in a room
- Visible to all room participants

### Profile Display
- CP partner shown on profile
- CP level badge visible

---

## Baggage Gifts

Rewards earned through CP are stored in the Baggage system:
- Not convertible to coins
- Can be sent as gifts (zero coin cost to sender)
- Recipient receives diamond value of gift
- See [Gifts & Baggage](./gifts.md) for details

---

## Dissolution

*Dissolution rules pending final decision.*

### Options Under Consideration
- **Cooldown:** 3, 7, or 14 days before forming new CP
- **Refund:** None, 25% to initiator, or 50% split
- **CP progress:** Reset or retained for future partnerships

---

## API Endpoints

### Initiate CP

```
POST /cp/initiate
```

Request:
```json
{
  "partnerId": "user_123",
  "paymentModel": "single_payer"
}
```

### Accept CP

```
POST /cp/accept
```

### Get CP Status

```
GET /cp/status
```

Response:
```json
{
  "partnerId": "user_456",
  "partnerName": "CoolUser",
  "cpLevel": 3,
  "totalCoinsSent": 25000000,
  "nextLevelTarget": 50000000,
  "rewards": [
    {"level": 1, "status": "CLAIMED"},
    {"level": 2, "status": "CLAIMED"},
    {"level": 3, "status": "CLAIMABLE"}
  ]
}
```

### Dissolve CP

```
POST /cp/dissolve
```

---

## Telemetry Events

| Event           | Properties                      |
|-----------------|----------------------------------|
| `cp_initiate`   | partnerId, paymentModel          |
| `cp_accept`     | partnerId                        |
| `cp_level_up`   | newLevel, totalCoins             |
| `cp_dissolve`   | partnerId, level, reason         |

---

## Related Documentation

- [Gifts & Baggage](./gifts.md)
- [Wallet & Currency](./wallet.md)
- [Store & Items](./store.md)
- [Product Specification](../README.md)
