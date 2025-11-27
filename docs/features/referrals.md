# Referral Programs

Aura Voice Chat offers two referral programs to reward users for inviting friends.

## Overview

| Program    | Currency | Purpose                          |
|------------|----------|----------------------------------|
| Get Coins  | Coins    | In-app rewards for referrals     |
| Get Cash   | USD      | Real money earnings              |

---

## Get Coins (Invite Friends)

### Reward Structure

| Action                           | Reward              |
|----------------------------------|---------------------|
| Invite 1 friend                  | 2,500,000 Coins     |
| Friend's recharge ≥ 500,000      | 2,500,000 Coins     |
| Friends send gifts               | 5% of gift Coins    |

### Layout

- **Tabs:** Get Coins / Get Cash (navigation)
- **Reward Rules:** Display of earning opportunities
- **Binding Section:** Enter referral code, shows inviter name/ID on success
- **Withdraw Panel:** Coins to withdraw + Withdraw button
- **Stats:** Number of invitations, total Coins rewarded
- **Records Table:** Nickname | ID | Recharge ≥ 500,000 | Send Gifts | Total Coins
- **Invite Button:** Sticky at bottom

### Policy

| Setting            | Value                    |
|--------------------|--------------------------|
| Minimum withdrawal | 100 Coins               |
| Cooldown           | None                    |
| Daily cap          | Unlimited               |
| Verification       | Not required            |
| Page size          | 5 (default), up to 10   |

### Share Message Example

> "Login and get a reward worth $400, including 50,000 coins and a frame. Come to Aura App for exciting chats. My Aura ID: {ID}"

---

## Get Cash

### Layout

- **Tabs:** Get Coins / Get Cash
- **Banner + Marquee:** Promotional headline and live earnings ticker
- **My Cash Balance:** Value, History, Withdraw buttons
- **Reward Rules:**
  - Countdown timer (Days | Hours | Minutes | Seconds)
  - Level tabs Lv.1 – Lv.10 with rewards
  - Level detail: USD reward + BD frame (7 days)
  - Progress line: "Friend Recharge: current / target"
- **Invite Record:**
  - Summary: Total Recharge Friends, Total Rewards (USD)
  - Date range (weekly), records table
  - Columns: Name | ID | Invited Time | Expired Time | This Week Recharge USD
- **Ranking:** Leaderboard with masked names and USD earned (20 per page)

### Level Rewards

| Level | Target Recharge | Reward    | Frame      |
|-------|-----------------|-----------|------------|
| Lv.1  | $1.00           | $0.20     | 7 days     |
| Lv.2  | $5.00           | $0.50     | 7 days     |
| ...   | ...             | ...       | ...        |
| Lv.10 | $1,000.00       | $100.00   | 7 days     |

*Full table to be provided by product.*

### Withdrawal Policy

| Destination | Minimum | Cooldown   | Notes                           |
|-------------|---------|------------|----------------------------------|
| Wallet      | $1      | 30 seconds | Unlimited per day               |
| External    | $10     | 30 seconds | 5-day clearance before release  |

**External Destinations:**
- Bank transfer
- Card
- PayPal
- Payoneer

### Campaign Behavior

- Countdown auto-starts new cycle when reaching zero
- Level claims are automatic upon reaching target
- Campaign history archived when cycle ends

---

## API Endpoints

### Get Coins

```
POST /referrals/bind                    — Bind to inviter
GET  /referrals/coins/summary           — Get coins summary
POST /referrals/coins/withdraw          — Withdraw coins
GET  /referrals/records?page=N          — Get referral records
```

### Get Cash

```
GET  /referrals/cash/summary                           — Get cash summary
POST /referrals/cash/withdraw                          — Withdraw cash
     { destination: "wallet" | "bank" | "card" | "paypal" | "payoneer" }
GET  /referrals/cash/invite-record?weekStart=YYYY-MM-DD&page=N
GET  /referrals/cash/ranking?page=N
```

---

## Data Models

### Get Coins Summary

```json
{
  "invitationsCount": 9,
  "totalCoinsRewarded": 11797449,
  "withdrawableCoins": 13000,
  "withdrawMin": 100,
  "cooldownSeconds": 0
}
```

### Get Cash Summary

```json
{
  "balanceUsd": 0.11,
  "minWithdrawalUsd": 1.00,
  "walletCooldownSeconds": 30,
  "externalAllowedMinUsd": 10.00,
  "externalClearanceDays": 5,
  "levels": [
    {"level": 1, "targetUsd": 1.00, "rewardUsd": 0.20, "frameDays": 7, "currentUsd": 0.00, "status": "ACTIVE"},
    {"level": 10, "targetUsd": 1000.00, "rewardUsd": 100.00, "frameDays": 7, "currentUsd": 0.00, "status": "LOCKED"}
  ],
  "rankingPageSize": 20,
  "campaignAutoNewCycle": true
}
```

---

## Anti-Abuse

- Bind rate limiting
- Fraudulent recharge detection
- Audit logging for all withdrawals
- No verification required for Get Coins (can be added later)
- KYC threshold for large Get Cash withdrawals (amount TBD)

---

## Telemetry Events

| Event                            | Properties                   |
|----------------------------------|------------------------------|
| `referrals_get_coins_view`       | userId                       |
| `referrals_coins_withdraw_success` | amount                     |
| `referrals_get_cash_view`        | userId                       |
| `cash_withdraw_success`          | amount, destination          |

---

## Related Documentation

- [Wallet & Currency](./wallet.md)
- [Product Specification](../README.md)
