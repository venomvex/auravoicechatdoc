# Medals System

Medals recognize user achievements, activity milestones, and special accomplishments in Aura Voice Chat.

## Overview

Medals are badges earned through various activities and achievements. They are displayed on user profiles and serve as status symbols within the community.

---

## Medal Categories

### 1. Gift Medals

Awarded for sending and receiving gifts:

| Medal | Requirement | Reward |
|-------|-------------|--------|
| Gift Sender I | Send 10,000 coins in gifts | 1,000 coins |
| Gift Sender II | Send 100,000 coins in gifts | 5,000 coins |
| Gift Sender III | Send 1,000,000 coins in gifts | 25,000 coins |
| Gift Sender IV | Send 10,000,000 coins in gifts | 100,000 coins |
| Gift Sender V | Send 100,000,000 coins in gifts | 500,000 coins |
| Gift Receiver I | Receive 10,000 diamonds | 1,000 coins |
| Gift Receiver II | Receive 100,000 diamonds | 5,000 coins |
| Gift Receiver III | Receive 1,000,000 diamonds | 25,000 coins |
| Gift Receiver IV | Receive 10,000,000 diamonds | 100,000 coins |
| Gift Receiver V | Receive 100,000,000 diamonds | 500,000 coins |

### 2. Achievement Medals

Awarded for reaching progression milestones:

**Level Medals:**
| Medal | Requirement | Reward |
|-------|-------------|--------|
| Rising Star | Reach Level 10 | 10,000 coins + Frame (7d) |
| Established | Reach Level 20 | 25,000 coins + Frame (14d) |
| Veteran | Reach Level 30 | 50,000 coins + Mic Skin (7d) |
| Elite | Reach Level 40 | 100,000 coins + Frame (30d) |
| Legend | Reach Level 50 | 250,000 coins + Theme (30d) |
| Master | Reach Level 60 | 500,000 coins + Vehicle (30d) |

**VIP Medals:**
| Medal | Requirement | Reward |
|-------|-------------|--------|
| VIP Starter | Reach VIP 1 | 5,000 coins |
| VIP Rising | Reach VIP 3 | 15,000 coins |
| VIP Elite | Reach VIP 5 | 50,000 coins |
| VIP Master | Reach VIP 7 | 150,000 coins |
| VIP Legend | Reach VIP 10 | 500,000 coins |

**Room Medals:**
| Medal | Requirement | Reward |
|-------|-------------|--------|
| Room Creator | Create first room | 5,000 coins |
| Popular Host | 100 unique visitors | 25,000 coins |
| Super Host | 1,000 unique visitors | 100,000 coins |
| Mega Host | 10,000 unique visitors | 500,000 coins |

### 3. Activity Medals

Awarded for consistent engagement:

**Login Medals (Cumulative Days):**
| Milestone | Coin Reward | Cosmetic Reward |
|-----------|-------------|-----------------|
| 7 days | 10,000 | Badge (7d) |
| 30 days | 50,000 | Frame (7d) |
| 60 days | 100,000 | Mic Skin (7d) |
| 90 days | 200,000 | Seat Heart Effect (7d) |
| 180 days | 500,000 | Frame (30d) |
| 365 days | 1,000,000 | Premium Cosmetic (permanent) |

**Streak Medals (Consecutive Days):**
| Milestone | Reward |
|-----------|--------|
| 7-day streak | 15,000 coins |
| 14-day streak | 35,000 coins |
| 30-day streak | 100,000 coins |
| 60-day streak | 250,000 coins |
| 90-day streak | 500,000 coins |

### 4. Special Medals

**Event Medals:**
- Limited-time event participation
- Seasonal celebrations
- Anniversary medals
- Competition winners

**CP Medals:**
| Medal | Requirement | Reward |
|-------|-------------|--------|
| First Love | Form first CP | 10,000 coins |
| CP Level 5 | Reach CP Level 5 | 100,000 coins |
| CP Level 10 | Reach CP Level 10 | 1,000,000 coins |
| Eternal Love | Maintain CP for 365 days | 500,000 coins + Frame (permanent) |

**Friend Medals:**
| Medal | Requirement | Reward |
|-------|-------------|--------|
| First Friend | Form first friendship | 5,000 coins |
| Social Butterfly | Have 10 friends | 25,000 coins |
| Friend Level 5 | Reach Friend Level 5 | 50,000 coins |
| Friend Level 10 | Reach Friend Level 10 | 500,000 coins |

**Family Medals:**
| Medal | Requirement | Reward |
|-------|-------------|--------|
| Family Member | Join first family | 5,000 coins |
| Family Founder | Create a family | 25,000 coins |
| Family Elite | Family reaches Top 100 | 100,000 coins |
| Family Champion | Family reaches Top 10 | 500,000 coins |

---

## Medal Display

### Profile Display
- Up to **10 medals** displayed under profile name
- Order is customizable by user
- Any medal can be hidden from public view

### Default Order Priority
When user hasn't customized order:
1. Achievement medals (highest tier first)
2. Activity medals
3. Gift medals
4. Special medals

### Medal Gallery
- All earned medals visible in user's medal gallery
- Progress shown for locked medals
- Filter by category
- Sort by earned date or rarity

---

## Medal Visibility Settings

### Public Medals
- Displayed on profile
- Visible in medal gallery
- Shown in room presence

### Hidden Medals
- Not visible to other users
- Still shown in own medal management
- Can be unhidden anytime

---

## Medal Durations

Medals can be:
1. **Permanent** — Never expires
2. **Time-Limited** — Expires after set duration (7d, 14d, 30d)
3. **Renewable** — Can be renewed through continued activity

### Expiration Handling
- 24-hour warning before expiration
- Push notification reminder
- Expired medals become locked
- Re-earn to reactivate

---

## Medal Claiming

### Auto-Claim
Most medals are automatically claimed when criteria are met:
- Login activity medals
- Level milestones
- Automatic coin credit

### Manual Claim
Some medals require manual claim:
- Event participation medals
- Competition rewards
- Displayed in "Claimable" section

---

## Medal Rewards

### Coin Rewards
- Credited to wallet immediately
- VIP multiplier does NOT apply
- Shown in transaction history

### Cosmetic Rewards
- Added to inventory
- Automatic equip option
- Duration starts from claim

---

## API Endpoints

```
GET /profile/medals
GET /profile/medals/claimable
POST /profile/medals/claim/{medalId}
POST /profile/medals/display
POST /profile/medals/hide/{medalId}
GET /users/{id}/medals
GET /medals/categories
GET /medals/progress
```

---

## Data Model

```json
{
  "medals": [
    {
      "id": "login_30d",
      "name": "30 Day Veteran",
      "category": "activity",
      "description": "Logged in for 30 cumulative days",
      "icon": "medal_login_30d.png",
      "earnedAt": "2025-01-15T10:00:00Z",
      "expiresAt": null,
      "isDisplayed": true,
      "displayOrder": 1,
      "rewards": {
        "coins": 50000,
        "cosmetic": {
          "type": "frame",
          "id": "frame_30d_login",
          "duration": "7d"
        }
      }
    }
  ],
  "progress": {
    "login_60d": {
      "current": 45,
      "target": 60,
      "percentage": 75
    }
  },
  "displayedMedals": ["login_30d", "level_20", "gift_sender_3"],
  "totalMedals": 15,
  "hiddenCount": 2
}
```

---

## Telemetry Events

| Event | Properties |
|-------|------------|
| `medals_view` | userId, category |
| `medal_detail_view` | medalId, category |
| `medal_claim` | medalId, rewardCoins, rewardItem |
| `medal_display_edit` | newOrder, displayedCount |
| `medal_hide` | medalId |
| `medal_expire` | medalId |

---

## Related Documentation

- [Profile & Inventory](./profile-and-inventory.md)
- [VIP Systems](./features/vip-systems.md)
- [Daily Rewards](./features/daily-rewards.md)
- [CP & Friend System](./cp-friend-system.md)
