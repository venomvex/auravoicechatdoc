# CP & Friend System

The CP (Couple Partnership) and Friend System enables users to form special bonds with exclusive rewards, cosmetics, and ranking features.

## Overview

Aura Voice Chat offers two relationship systems:
1. **CP (Couple Partnership)** — Romantic partnership with premium rewards
2. **Friend System** — Friendship bonds with shared progression

Both systems are accessed via the **Me** screen under "CP / Friend" and feature dedicated tabs for management.

---

## CP System

### Formation

**Requirements:**
- Both users must accept the partnership
- Formation fee: **3,000,000 coins** (3M)

**Payment Options:**
1. **Single Payer:** One user pays full 3M fee
2. **Split Cost:** Each partner pays 1.5M
3. **Staged Cost:** Initiator pays 25% (750K), partner pays 75% (2.25M)

**Process:**
1. User initiates CP request from partner's profile
2. Select payment model
3. Partner receives notification
4. Partner accepts and completes payment (if applicable)
5. CP partnership created with Level 1

### CP Levels & Progression

CP EXP is earned by sending coins to your partner:
- **1 coin sent = +1 CP EXP**
- Both partners' sending contributes to shared CP EXP

| Level | Cumulative Coins | Gift Reward | Frame Coins | Baggage Coins |
|-------|------------------|-------------|-------------|---------------|
| 1     | 72M              | 3.96M       | 288,000     | 216,000       | 108,000 |
| 2     | 135M             | 7.425M      | 540,000     | 405,000       | 202,500 |
| 3     | 270M             | 17.01M      | 1,296,000   | 810,000       | 405,000 |
| 4     | 450M             | 31.23M      | 2,448,000   | 1,350,000     | 675,000 |
| 5     | 675M             | 46.845M     | 3,672,000   | 2,025,000     | 1,012,500 |
| 6     | 990M             | 68.706M     | 5,385,600   | 2,970,000     | 1,485,000 |
| 7     | 1,350M           | 93.69M      | 7,344,000   | 4,050,000     | 2,025,000 |
| 8     | 2,250M           | 156.15M     | 12,240,000  | 6,750,000     | 3,375,000 |
| 9     | 4,500M           | 312.3M      | 24,480,000  | 13,500,000    | 6,750,000 |
| 10    | 9,000M           | 624.6M      | 48,960,000  | 27,000,000    | 13,500,000 |

### CP Privileges

At each CP level, partners unlock exclusive cosmetics:

**Mic Effect:**
- Shared mic animation showing both partners
- Heart decorations and romantic effects
- Partner avatars displayed in heart frames (No.1, No.2, No.3 positions)

**Room Card Background:**
- Exclusive CP-themed profile card background
- Shows both partners with decorative elements
- Displays CP level badge

**Room Chat Bubbles:**
- Custom chat bubble with romantic theme
- Rose/flower decorations
- Partner indication

**CP Vehicle:**
- Animated entrance vehicle for rooms
- Romantic carriage design with roses
- Shared by both partners

**CP Background:**
- Profile background showing both partners
- Level-based upgrades (Lv.1–Lv.10)
- Progress bar: current/required EXP

**CP Theme:**
- Full UI theme customization
- Matching color scheme for both partners

### CP Ranking

Top CP pairs are displayed on the CP Ranking leaderboard:
- Ranked by total CP EXP
- Weekly and all-time rankings
- Top 3 featured with special badges
- Rewards for top-ranked pairs

### CP Cards

CP Cards display partnership information:
- Both partner avatars
- CP Level badge
- Days since partnership formed
- SVIP status indicators
- ID and follower count

---

## Friend System

### Formation

**Requirements:**
- Mutual friend request acceptance
- No formation fee

**Process:**
1. Send friend request from user's profile
2. Friend accepts request
3. Friend bond established at Level 1

### Friend Levels & Progression

Friend EXP is earned through interaction:
- Sending gifts
- Visiting rooms together
- Completing daily friend tasks

| Level | Required EXP | Unlock |
|-------|--------------|--------|
| 1     | 0            | Basic friend badge |
| 2     | 10,000       | Friend Bubbles |
| 3     | 50,000       | Friend Vehicle |
| 4     | 100,000      | Enhanced bubbles |
| 5     | 200,000      | Friend Background (Lv.5) |
| 6     | 400,000      | Friend Theme |
| 7     | 800,000      | Friend Background (Lv.7) |
| 8     | 1,500,000    | Premium Friend Vehicle |
| 9     | 3,000,000    | Friend Background (Lv.9) |
| 10    | 6,000,000    | Full Friend Set (permanent) |

### Friend Privileges

**Room Card Background:**
- Special friend-themed card design
- Decorative wings and effects
- Friend level indicator

**Friend Bubbles:**
- Custom chat bubbles with "Hi!" text
- Sparkle and wave effects
- Shows friend's avatar and level

**Friend Vehicle:**
- Animated boat/yacht entrance effect
- Balloon decorations
- Level-based upgrades

**Friend Background:**
- Shared profile background
- Shows both friends with level indicators
- Progress bar to next level
- Days since friendship formed (e.g., "999 Days (Built on 22/12/2023)")

**Friend Theme:**
- Blue-themed UI customization
- Jet ski/nautical motifs
- Balloon decorations

### Friend Daily Tasks

Complete tasks to earn Friend EXP:
- Send gifts to friend (any value)
- Spend 10 minutes in same room
- React to friend's messages
- Visit friend's profile

### Friend Cards

Display friendship status:
- Both avatars with friend frame
- Current level and progress
- Friendship duration
- Daily task completion status

---

## CP Permanent Event (Room Slider)

Special CP events appear in the room slider:
- Time-limited CP formation discounts
- Bonus EXP multipliers
- Exclusive cosmetic rewards
- Ranking competition periods

---

## Dissolution

### CP Dissolution

**Cooldown Options:**
- 7-day cooldown before forming new CP
- 14-day cooldown for frequent dissolutions

**Refund Policy:**
- No refund of formation fee
- Earned rewards retained in inventory
- CP level progress reset

### Friend Removal

- No cooldown for re-adding
- Shared cosmetics deactivated
- Friend level progress reset

---

## API Endpoints

### CP Endpoints

```
POST /cp/initiate
POST /cp/accept
GET /cp/status
POST /cp/dissolve
GET /cp/privileges
GET /cp/ranking
```

### Friend Endpoints

```
POST /friend/request
POST /friend/accept
GET /friend/status
POST /friend/remove
GET /friend/tasks
GET /friend/privileges
```

---

## Data Model

```json
{
  "cpStatus": {
    "partnerId": "user_456",
    "partnerName": "Partner",
    "level": 7,
    "totalExp": 93690000,
    "nextLevelExp": 156150000,
    "formationDate": "2023-12-22",
    "daysActive": 999,
    "privileges": ["mic_effect", "room_card", "chat_bubbles", "vehicle", "background", "theme"]
  },
  "friendStatus": {
    "friendId": "user_789",
    "friendName": "Friend",
    "level": 9,
    "totalExp": 3000000,
    "nextLevelExp": 6000000,
    "formationDate": "2023-12-22",
    "daysActive": 999,
    "dailyTasksComplete": 3,
    "dailyTasksTotal": 4
  }
}
```

---

## Related Documentation

- [Gifts & Records](./gifts-and-records.md)
- [Ranking & Leaderboards](./ranking-and-leaderboards.md)
- [Profile & Inventory](./profile-and-inventory.md)
- [Medals System](./medals-system.md)
