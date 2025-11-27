# Daily Login Rewards

The daily login reward system encourages consistent user engagement through a 7-day reward cycle.

## Overview

Users receive coin rewards for logging in each day, with bonuses for completing the full 7-day cycle. VIP users receive multiplied rewards.

---

## Reward Schedule

| Day | Base Coins | Notes                                    |
|-----|------------|------------------------------------------|
| 1   | 5,000      |                                          |
| 2   | 10,000     |                                          |
| 3   | 15,000     |                                          |
| 4   | 20,000     |                                          |
| 5   | 25,000     |                                          |
| 6   | 30,000     |                                          |
| 7   | 35,000     | +15,000 bonus = 50,000 total            |

**Per-cycle total:** 155,000 Coins

---

## Claim Behavior

### First Login of the Day
1. Daily Reward popup shown automatically
2. User can claim immediately or close the popup
3. If closed, claim available via Home FAB (bottom-right, badge "!")

### Post-Claim
- Button displays "Signed in today"
- No additional claims until next UTC day

### First-Ever Login
- Day 1 reward is auto-claimed on first app login

---

## Cycle Rules

### 7-Day Cycle
- After Day 7, cycle resets to Day 1
- Long-term streak counter continues growing even after cycle reset

### Missed Days
- Missing a day resets the 7-day cycle
- Streak counter resets to 0
- Cumulative login days (for medals) still count

### Day Boundary
- Server uses UTC for day boundaries
- All users follow the same reset time (00:00 UTC)

---

## VIP Multipliers

VIP users receive boosted rewards. See [VIP Systems](./vip-systems.md) for full details.

| VIP Tier | Multiplier | Day 7 Total (50k base) |
|----------|------------|------------------------|
| None     | 1.0x       | 50,000                |
| VIP5     | 2.0x       | 100,000               |
| VIP10    | 3.0x       | 150,000               |

- Multiplier applies to full day total (including bonus)
- Result rounded to nearest 10 coins

---

## Reminders

Push notifications help users not miss their daily reward:
- **6 hours** after first session of the day (if unclaimed)
- **2 hours** before reset (if unclaimed)

---

## Animations

- **Claimable cell:** Subtle pulse animation
- **Claim success:** Overlay with "Get Reward" text, sparkles, and coin count-up animation
- **Reduce Motion:** All animations disabled when system setting is enabled

---

## API Endpoints

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

## Telemetry Events

| Event                      | Properties                              |
|----------------------------|-----------------------------------------|
| `daily_rewards_popup_shown`| currentDay                              |
| `daily_rewards_claim`      | day, coinsAwarded, vipMultiplier        |

---

## Related Documentation

- [VIP Systems](./vip-systems.md)
- [Medals System](./medals.md)
- [Wallet & Currency](./wallet.md)
- [Product Specification](../README.md)
