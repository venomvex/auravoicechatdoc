# Recharge Event — Permanent Program

Multi-layer recharge incentive system with daily, weekly, and monthly rewards.

---

## Overview

| Layer | Name | Cycle | Description |
|-------|------|-------|-------------|
| Daily | Daily Surge | Daily reset (00:00 UTC) | Random reward after meeting threshold |
| Monthly | Aurora Milestones | Calendar month | Cumulative recharge ladder |
| Weekly | Recharge Royale | Monday 00:00 UTC → Monday 00:00 UTC | Ranking competition |

---

## 1. Daily Surge (Random Daily Reward)

### Surge Levels

| Level | Daily Recharge Range | Pool Items |
|-------|---------------------|------------|
| Lv1 | 120,000 – 399,999 coins | Nova XP 40K, Star Coins 50K, Mini Gift Pack ×2, Flash Frame 3d |
| Lv2 | 400,000 – 1,999,999 coins | Nova XP 180K, Star Coins 120K, Mini Gift Pack ×2, Guardian Vehicle 3d |
| Lv3 | ≥2,000,000 coins | Nova XP 360K, Star Coins 300K, Premium Gift Pack ×2, Inferno Vehicle 3d |

### Claim Window
- Until next daily reset (or claimWindowHours override)
- Expired rewards logged (rewardExpired event)

---

## 2. Aurora Milestones (Monthly Ladder)

### Thresholds and Bundles

| Threshold | Rewards |
|-----------|---------|
| 5M | Star Coins 125K; Nova XP 14K |
| 10M | Star Coins 330K; Nova XP 26K |
| 30M | Star Coins 620K; Nova XP 75K; Signature Medal 30d; Signature Frame 15d |
| 50M | Star Coins 1,300K; Nova XP 130K; Signature Medal 30d; Signature Frame 15d; Room Theme 15d; Super Mic Access 30d |
| 100M | Star Coins 2,600K; Nova XP 190K; Signature Medal 30d; Signature Frame 15d; Room Theme 15d; Super Mic Access 30d |
| 150M | Star Coins 3,250K; Nova XP 320K; Signature Medal 30d; Signature Frame 15d; Guardian Vehicle 15d; Room Theme 15d; Custom Gift Forge 30d; Gift Mod Tokens ×1 |
| 250M | Star Coins 4,900K; Nova XP 380K; Custom ID 30d; Signature Medal 30d; Signature Frame 15d; Guardian Vehicle 15d; Room Theme 15d; Gift Mod Tokens ×2 |
| 500M | Star Coins 9,800K; Nova XP 750K; Custom ID 30d; Elite Medal 30d; Elite Frame 15d; Lion Vehicle 15d; Lion Theme 15d; Gift Mod Tokens ×2 |
| 1,000M | Star Coins 19,500K; Nova XP 1,200K; Custom ID 30d; Sovereign Medal 30d; Sovereign Frame 15d; Tiger Vehicle 15d; Tiger Theme 15d; Gift Mod Tokens ×2 |
| 1,500M | Star Coins 23,500K; Nova XP 1,650K; Custom ID 30d; Emblem Medal 30d; Emblem Frame 15d; Lion Vehicle (Alt) 15d; Lion Theme (Alt) 15d; Gift Mod Tokens ×2 |
| 2,500M | Star Coins 45,000K; Nova XP 2,200K; Custom ID 30d; Apex Medal 30d; Apex Frame 15d; Dragon Vehicle 15d; Dragon Theme 15d; Gift Mod Tokens ×2; Ascendant Frame 30d |

---

## 3. Recharge Royale (Weekly Ranking)

### Leaderboard
- Real-time or 60s polling updates
- Top ranks with cosmetics
- Pagination (50 per page)
- User's own rank pinned if off screen

### Weekly Rank Rewards

| Rank | Rewards |
|------|---------|
| 1 | Royale Medal 7d; Royale Frame 7d; Star Coins 25,000,000; Broadcast Boost 3d |
| 2 | Royale Medal 7d; Royale Frame 7d; Star Coins 20,000,000 |
| 3 | Royale Medal 7d; Royale Frame 7d; Star Coins 15,000,000 |
| 4–10 | Royale Frame 7d; Star Coins 5,000,000 |
| 11–50 | Star Coins 2,000,000; Nova XP 80K |
| 51–200 | Star Coins 800,000; Nova XP 30K |
| Participation | Nova XP 10K |

### Tie Policy
- FIRST_COME (default) or SHARED_RANK (CMS configurable)

---

## Item Types

| Type | Fields |
|------|--------|
| COINS | amount |
| EXP | amount |
| MEDAL | medalId, durationDays, rarity |
| FRAME | frameId, durationDays, rarity |
| THEME | themeId, durationDays |
| VEHICLE | vehicleId, durationDays, rarity |
| SUPER_MIC | durationDays |
| CUSTOM_ID | durationDays |
| CUSTOM_GIFT_FORGE | durationDays |
| GIFT_MOD_TOKENS | modifications |
| BROADCAST_BOOST | durationDays |

---

## Policies

| Policy | Default |
|--------|---------|
| Auto-Claim Milestones | OFF |
| Refund Adjustment Mode | SUBTRACT_FUTURE |
| Duration Stack Mode | EXTEND |
| Tie Policy Weekly | FIRST_COME |
| Valuation Mode | AUTO |

---

## APIs

| Endpoint | Purpose |
|----------|---------|
| GET /programs/recharge/status | Daily + monthly + weekly composite |
| POST /programs/recharge/daily/claim | Claim Surge random reward |
| POST /programs/recharge/milestone/claim | Claim milestone bundle |
| GET /programs/recharge/milestones | Ladder metadata |
| GET /programs/recharge/daily-pools | Surge level pools |
| GET /programs/recharge/weekly-leaderboard | Ranking page |
| GET /programs/recharge/weekly-rewards | Weekly reward tiers |
| GET /programs/recharge/logs | Claims + recharge entries |
| GET /programs/recharge/rules | Localized rules |

---

## Related Documentation

- [Wallet & Currency](./wallet.md)
- [AuraPass (VIP)](./aurapass.md)
- [VIP Recharge Bonus](./vip-recharge-bonus.md)
- [Product Specification](../../README.md)
