# Gifts & Records

Comprehensive documentation for the gift economy, gift catalog, pricing, records, and baggage system in Aura Voice Chat.

## Overview

Gifts are a core engagement and monetization feature. Users send gifts using Coins; recipients receive Diamonds equal to the gift value.

---

## Gift Economy Flow

```
Sender (Coins) → Gift → Recipient (Diamonds)
                  ↓
         Animation in Room
```

**Key Points:**
- 1 Coin spent = 1 Diamond received by recipient
- Gifts trigger room animations
- Transactions recorded permanently
- VIP bonuses may apply

---

## Gift Catalog

### Categories

| Category | Description | Price Range |
|----------|-------------|-------------|
| Basic | Simple expressions | 50 - 1,000 coins |
| Standard | Common gifts | 1,000 - 10,000 coins |
| Premium | Enhanced animations | 10,000 - 100,000 coins |
| Luxury | Full-screen effects | 100,000 - 1,000,000 coins |
| Supreme | Ultimate animations | 1,000,000 - 10,000,000 coins |
| Legendary | Rarest gifts | 10,000,000 - 200,000,000 coins |

### Popular Gifts

| Gift Name | Price (Coins) | Category | Animation |
|-----------|---------------|----------|-----------|
| Rose | 100 | Basic | Single rose |
| Heart | 500 | Basic | Heart pulse |
| Crown | 5,000 | Standard | Crown effect |
| Fireworks | 50,000 | Premium | Fireworks display |
| Super77 Pro | 100 | Basic | Number effect |
| Greedy Baby | 100 | Basic | Character animation |
| Yacht | 500,000 | Luxury | Boat animation |
| Castle | 2,000,000 | Supreme | Castle reveal |
| Dragon | 10,000,000 | Legendary | Dragon flight |

### Special Gifts

| Type | Description |
|------|-------------|
| Event Gifts | Limited-time availability |
| CP Gifts | Exclusive to CP partners |
| VIP Gifts | Exclusive to VIP members |
| Regional Gifts | Location-specific |

---

## Sending Gifts

### Process

1. Enter room or open profile
2. Tap gift icon
3. Browse/select gift
4. Choose quantity (1-999)
5. Select recipient(s)
6. Confirm send

### Multi-Send

- Select multiple recipients
- Same gift to all selected
- Quantity applies per recipient
- Bulk confirmation required

### Sending Limits

| Limit | Value | Purpose |
|-------|-------|---------|
| Cooldown | 2-5 seconds | Anti-spam |
| Max per transaction | 999 | UI limit |
| Daily limit | None | No cap |
| Large gift warning | 1M+ coins | Confirmation |

---

## Gift Animations

### Animation Types

| Type | Trigger | Duration |
|------|---------|----------|
| Inline | Any gift | 1-2 seconds |
| Banner | 10K+ coins | 3-4 seconds |
| Full-screen | 100K+ coins | 4-6 seconds |
| Premium | 1M+ coins | 5-8 seconds |

### Animation Queue

- Maximum 5-10 concurrent animations
- Queue order by send time
- Skip option for users
- Reduce Motion disables animations

### Animation Controls

| Setting | Default | Options |
|---------|---------|---------|
| Enable Animations | On | On/Off |
| Animation Quality | High | Low/Medium/High |
| Sound Effects | On | On/Off |
| Full-screen Effects | On | On/Off |

---

## Gift Records

### Transaction History

Access via: **Me** → **Wallet** → **History**

**Record Details:**
- Transaction amount (positive/negative)
- Description (gift name, event)
- Date and time
- Income/Expend totals

### Record Types

| Type | Display | Example |
|------|---------|---------|
| Gift Sent | Red (-) | -5,000 Super77 Pro |
| Gift Received | Black (+) | +15,254 invite new user reward |
| Purchase | Red (-) | -100 Greedy Baby |
| Reward | Black (+) | +50,000 Daily Reward |

### Filtering

| Filter | Options |
|--------|---------|
| Currency | Coins, Diamonds |
| Type | All, Sent, Received, Rewards, Purchases |
| Date Range | Last 30 days (default) |

### Summary View

| Metric | Display |
|--------|---------|
| Income | Total coins/diamonds received |
| Expend | Total coins/diamonds spent |
| Net | Income - Expend |

---

## Baggage System

### What is Baggage?

Storage for special gifts that can be sent at zero coin cost.

### Baggage Sources

| Source | How Obtained |
|--------|--------------|
| CP Rewards | Level-up rewards |
| Friend Rewards | Friendship milestones |
| Event Rewards | Event participation |
| Probability Gifts | Lucky draws, spins |
| VIP Perks | VIP level rewards |

### Baggage Rules

| Rule | Description |
|------|-------------|
| Not Convertible | Cannot exchange for coins/diamonds |
| Zero Cost Send | Free to send |
| Full Value | Recipient gets full diamond value |
| No Daily Cap | Unlimited sends |
| No Expiration | Items don't expire in baggage |

### Baggage UI

Access via: **Me** → **My Items** → **Baggage**

**Views:**
- Grid view with send buttons
- Filter by source (CP, Friend, Event, VIP)
- Sort by value, date acquired

### Sending Baggage

1. Open baggage
2. Select item
3. Choose recipient
4. Confirm send
5. Recipient receives diamonds

---

## Regional Gifts

### Regional Catalog

Certain gifts are region-specific:
- Local celebrations
- Cultural items
- Regional pricing

### Region Determination

| Option | Description |
|--------|-------------|
| Room Owner Region | Uses room owner's region |
| Viewer Region | Uses sender's region |
| Intersection | Shows shared + global gifts |

### Current Regions

| Region | Special Gifts |
|--------|---------------|
| Global | All basic gifts |
| Asia | Festival-specific |
| Middle East | Cultural items |
| Europe | Regional celebrations |
| Americas | Holiday-themed |

---

## Gift Pricing & Economy

### Pricing Tiers

| Tier | Price Range | Coin Conversion |
|------|-------------|-----------------|
| Micro | 50-500 | Direct |
| Small | 500-5,000 | Direct |
| Medium | 5,000-50,000 | Direct |
| Large | 50,000-500,000 | Direct |
| Mega | 500,000-5,000,000 | Direct |
| Ultimate | 5,000,000+ | Direct |

### Large Gift Warning

For gifts over configurable threshold (default 1M coins):
- Confirmation dialog appears
- Shows total cost
- Requires explicit confirm
- Prevents accidental sends

---

## API Endpoints

```
GET /gifts/catalog
GET /gifts/catalog?region={region}
POST /gifts/send
GET /gifts/records
GET /gifts/records/sent
GET /gifts/records/received
GET /baggage
POST /baggage/send
GET /gifts/animations/{giftId}
```

---

## Data Model

### Gift Transaction

```json
{
  "transactions": [
    {
      "id": "txn_001",
      "type": "gift_sent",
      "amount": -5000,
      "currency": "coins",
      "description": "Super77 Pro",
      "recipientId": "user_456",
      "giftId": "gift_super77",
      "quantity": 50,
      "timestamp": "2025-11-28T18:25:08Z"
    },
    {
      "id": "txn_002",
      "type": "reward",
      "amount": 15254,
      "currency": "coins",
      "description": "invite new user reward",
      "timestamp": "2025-11-28T18:24:51Z"
    }
  ],
  "summary": {
    "income": 83593562,
    "expend": 83593700,
    "period": "last_30_days"
  }
}
```

### Baggage Item

```json
{
  "baggage": [
    {
      "id": "bag_001",
      "giftId": "gift_rose",
      "giftName": "Rose",
      "diamondValue": 100,
      "source": "cp_reward",
      "quantity": 50,
      "acquiredAt": "2025-11-01T00:00:00Z"
    }
  ]
}
```

---

## Telemetry Events

| Event | Properties |
|-------|------------|
| `gift_catalog_view` | category, region |
| `gift_send` | giftId, quantity, totalCoins, recipientCount |
| `gift_receive` | giftId, diamonds, senderId |
| `gift_animation_play` | giftId, animationType |
| `baggage_view` | source, itemCount |
| `baggage_send` | baggageItemId, recipientId, diamondValue |
| `records_view` | filter, currency |

---

## Related Documentation

- [CP & Friend System](./cp-friend-system.md)
- [Wallet](./features/wallet.md)
- [Profile & Inventory](./profile-and-inventory.md)
- [VIP Systems](./features/vip-systems.md)
