# Medals System

Medals recognize user achievements and activity milestones in Aura Voice Chat.

## Overview

Medals are categorized into three types:
1. **Gift Medals** — Sending/receiving gift milestones
2. **Achievement Medals** — Progress milestones (Levels, VIP, rooms)
3. **Activity Medals** — Participation milestones (login days, sessions)

---

## Medal Categories

### Gift Medals

Awarded for sending and receiving gifts:
- Sending milestones (total coins spent on gifts)
- Receiving milestones (total diamond value received)

### Achievement Medals

Awarded for reaching progression milestones:
- Level milestones (Level 10, 20, 30, etc.)
- VIP tier achievements
- Room creation and participation counts

### Activity Medals

Awarded for consistent engagement:
- Cumulative login days
- Session counts
- Streak achievements

---

## Login Activity Medals

Based on cumulative login days (not consecutive streaks):

| Milestone | Coin Reward  | Cosmetic Reward              |
|-----------|--------------|------------------------------|
| 30 days   | 50,000       | Frame (7 days)               |
| 60 days   | 100,000      | Mic skin (7 days)            |
| 90 days   | 200,000      | Seat heart effect (7 days)   |
| 180 days  | 500,000      | Frame (30 days)              |
| 365 days  | 1,000,000    | Premium cosmetic (permanent) |

**Reward Style:** Balanced (cosmetics + coins), auto-claim when milestone reached.

---

## Display & Ordering

### Profile Display
- Up to **10 medals** displayed under profile name
- Order is customizable by user
- Any medal can be hidden from display

### Default Order Priority
When user hasn't customized: Achievement > Activity > Gift

### Public Gallery
- All earned medals visible in user's medal gallery
- Read-only view for other users

---

## Medal Interaction

### Tap Medal
Opens detail view showing:
- Medal name and icon
- Achieved date
- Criteria for earning
- Description

### View All
"View all medals" button opens full gallery:
- All medal categories
- Earned vs locked status
- Progress toward next medals

---

## Visibility Settings

### Hidden Medals
- Medals can be marked as hidden
- Hidden medals don't appear:
  - Under profile name
  - In public gallery view
- Still visible in user's own medal management

---

## API Endpoints

### Get User's Medals

```
GET /profile/medals
```

Response includes all earned medals with metadata.

### Update Display Order

```
POST /profile/medals/display
```

Body:
```json
{
  "displayedMedals": ["medal_id_1", "medal_id_2", ...],
  "order": [0, 1, 2, ...]
}
```

### View Other User's Medals

```
GET /users/{id}/medals
```

Returns public medals only (excludes hidden).

---

## Telemetry Events

| Event               | Properties                        |
|---------------------|-----------------------------------|
| `medals_view`       | userId                            |
| `medal_detail_view` | medalId, category                 |
| `medal_order_edit`  | newOrder                          |
| `medal_claim`       | medalId, rewardCoins, rewardItem  |

---

## Auto-Claim Behavior

- Login activity medals are **auto-claimed** when milestone is reached
- User receives toast notification
- Coins credited to wallet immediately
- Cosmetic items added to inventory

---

## Related Documentation

- [Daily Login Rewards](./daily-rewards.md)
- [VIP Systems](./vip-systems.md)
- [Store & Items](./store.md)
- [Product Specification](../README.md)
