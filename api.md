# API Reference

This document provides the complete API reference for Aura Voice Chat backend services.

## Authentication

### Mechanism
- **OAuth2** for social login (Google, Facebook)
- **JWT** for session management
- **OTP** for mobile phone authentication

### Required Headers
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
Accept: application/json
X-Client-Version: <app_version>
X-Device-ID: <hashed_device_id>
```

---

## Authentication Endpoints

### Send OTP
```
POST /auth/otp/send
```

Request:
```json
{
  "phone": "+1234567890"
}
```

Response:
```json
{
  "success": true,
  "cooldownSeconds": 30,
  "attemptsRemaining": 4
}
```

### Verify OTP
```
POST /auth/otp/verify
```

Request:
```json
{
  "phone": "+1234567890",
  "otp": "1234"
}
```

Response:
```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "user": {
    "id": "user_123",
    "name": "User Name",
    "isNewUser": false
  }
}
```

---

## Daily Rewards Endpoints

### Get Daily Status
```
GET /rewards/daily/status
```

Response:
```json
{
  "currentDay": 6,
  "claimable": true,
  "cycle": [
    {"day": 1, "coins": 5000, "status": "CLAIMED"},
    {"day": 2, "coins": 10000, "status": "CLAIMED"},
    {"day": 3, "coins": 15000, "status": "CLAIMED"},
    {"day": 4, "coins": 20000, "status": "CLAIMED"},
    {"day": 5, "coins": 25000, "status": "CLAIMED"},
    {"day": 6, "coins": 30000, "status": "CLAIMABLE"},
    {"day": 7, "base": 35000, "bonus": 15000, "total": 50000, "status": "LOCKED"}
  ],
  "streak": 5,
  "nextResetUtc": "2025-11-27T00:00:00Z",
  "vipTier": "VIP5",
  "vipMultiplier": 2.0
}
```

### Claim Reward
```
POST /rewards/daily/claim
```

Response:
```json
{
  "success": true,
  "day": 6,
  "baseCoins": 30000,
  "vipMultiplier": 2.0,
  "totalCoins": 60000
}
```

---

## VIP Endpoints

### Get VIP Tier
```
GET /vip/tier
```

Response:
```json
{
  "tier": "VIP5",
  "multiplier": 2.0,
  "expBoost": 0.25,
  "expiry": "2025-12-27T00:00:00Z",
  "benefits": ["daily_multiplier", "exp_boost", "exclusive_items", "priority_join", "seat_frame"]
}
```

### Purchase VIP
```
POST /vip/purchase
```

Request:
```json
{
  "tier": "VIP5",
  "duration": "monthly"
}
```

---

## Medals Endpoints

### Get User's Medals
```
GET /profile/medals
```

### Update Display Order
```
POST /profile/medals/display
```

Request:
```json
{
  "displayedMedals": ["medal_001", "medal_002"],
  "hiddenMedals": ["medal_003"]
}
```

### View Other User's Medals
```
GET /users/{id}/medals
```

---

## Wallet Endpoints

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

## Referral Endpoints

### Get Coins — Bind
```
POST /referrals/bind
```

Request:
```json
{
  "code": "ABC123"
}
```

### Get Coins — Summary
```
GET /referrals/coins/summary
```

Response:
```json
{
  "invitationsCount": 9,
  "totalCoinsRewarded": 11797449,
  "withdrawableCoins": 13000,
  "withdrawMin": 100,
  "cooldownSeconds": 0
}
```

### Get Coins — Withdraw
```
POST /referrals/coins/withdraw
```

### Get Coins — Records
```
GET /referrals/records?page=1&pageSize=10
```

### Get Cash — Summary
```
GET /referrals/cash/summary
```

Response:
```json
{
  "balanceUsd": 0.11,
  "minWithdrawalUsd": 1.00,
  "walletCooldownSeconds": 30,
  "externalAllowedMinUsd": 10.00,
  "externalClearanceDays": 5,
  "levels": [...],
  "rankingPageSize": 20,
  "campaignAutoNewCycle": true
}
```

### Get Cash — Withdraw
```
POST /referrals/cash/withdraw
```

Request:
```json
{
  "destination": "wallet" | "bank" | "card" | "paypal" | "payoneer"
}
```

### Get Cash — Invite Records
```
GET /referrals/cash/invite-record?weekStart=2025-11-20&page=1
```

### Get Cash — Ranking
```
GET /referrals/cash/ranking?page=1
```

---

## Rooms Endpoints

### Video — Add to Playlist
```
POST /rooms/{id}/video/playlist
```

Request:
```json
{
  "url": "https://youtube.com/watch?v=..."
}
```

### Video — Exit
```
POST /rooms/{id}/video/exit
```

---

## Rocket System Endpoints

### Get Room Rocket State
```
GET /room/{roomId}/rocket/state
```

Response:
```json
{
  "roomId": "R112233",
  "region": "Pakistan",
  "currentRocket": 3,
  "currentTotalSent": 10453219,
  "nextMilestone": 15000000,
  "milestones": [
    { "rocket": 1, "trigger": 2500000, "launchedAtUtc": "2025-11-26T05:10:00Z" },
    { "rocket": 2, "trigger": 5000000, "launchedAtUtc": "2025-11-26T06:20:00Z" }
  ]
}
```

### Increment Contribution
```
POST /room/{roomId}/rocket/contribution
```

### Get Active Rockets by Region
```
GET /region/{region}/rockets/active
```

### Get Rocket Rewards
```
GET /room/{roomId}/rocket/{rocketNumber}/rewards
```

---

## Recharge Event Endpoints

### Get Recharge Status (Composite)
```
GET /programs/recharge/status
```

Response includes daily surge, monthly milestones, and weekly ranking status.

### Claim Daily Surge Reward
```
POST /programs/recharge/daily/claim
```

### Claim Milestone Bundle
```
POST /programs/recharge/milestone/claim
```

Request:
```json
{
  "threshold": 5000000
}
```

### Get Milestone Ladder
```
GET /programs/recharge/milestones
```

### Get Daily Surge Pools
```
GET /programs/recharge/daily-pools
```

### Get Weekly Leaderboard
```
GET /programs/recharge/weekly-leaderboard?page=1
```

### Get Weekly Rewards
```
GET /programs/recharge/weekly-rewards
```

### Get Recharge Logs
```
GET /programs/recharge/logs
```

---

## AuraPass Program Endpoints

### AuraPass Spin — Get Status
```
GET /programs/aurapass-spin/status
```

Response:
```json
{
  "ticketBalance": 5,
  "vipScore": 125000,
  "currentTier": "VIP4",
  "wheels": {
    "spark": { "ticketCost": 1, "available": true },
    "flare": { "ticketCost": 3, "available": true }
  }
}
```

### AuraPass Spin — Execute Spin
```
POST /programs/aurapass-spin/spin
```

Request:
```json
{
  "wheel": "spark",
  "count": 1
}
```

### AuraPass Spin — Get Records
```
GET /programs/aurapass-spin/records?cursor=xxx
```

### VIP Recharge Bonus — Get Status
```
GET /programs/vip-recharge-bonus/status
```

Response:
```json
{
  "currentTier": "VIP5",
  "bonusPercentage": 12,
  "caps": {
    "perRecharge": 1000000,
    "perDay": 5000000
  }
}
```

### VIP Recharge Bonus — Get Records
```
GET /programs/vip-recharge-bonus/records?cursor=xxx
```

---

## Pagination

### Convention
All list endpoints support pagination:
```
?page=1&pageSize=20
```

### Response Format
```json
{
  "data": [...],
  "pagination": {
    "page": 1,
    "pageSize": 20,
    "totalItems": 150,
    "totalPages": 8
  }
}
```

---

## Rate Limits

| Endpoint Category | Limit           |
|-------------------|-----------------|
| Authentication    | 5 OTP/day       |
| Referrals         | 10 binds/min    |
| Withdrawals       | 5/min           |
| Records           | 30/min          |
| General           | 100 req/min     |

---

## Error Codes

| Code | Meaning                    |
|------|----------------------------|
| 400  | Bad request               |
| 401  | Unauthorized              |
| 403  | Forbidden                 |
| 404  | Not found                 |
| 429  | Rate limit exceeded       |
| 500  | Internal server error     |

### Error Response Format
```json
{
  "error": {
    "code": "RATE_LIMIT_EXCEEDED",
    "message": "Too many requests. Please try again later.",
    "retryAfter": 60
  }
}
```

---

## Related Documentation

- [Product Specification](README.md)
- [Architecture](architecture.md)
- [Security](security.md)