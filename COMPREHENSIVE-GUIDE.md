# Aura Voice Chat — Complete Implementation Guide

**Developed by:** Hawkaye Visions LTD — Lahore, Pakistan

This is the **Master Implementation Guide** that consolidates all documentation, interlinks all functions, and provides complete setup instructions for building and deploying the Aura Voice Chat application.

---

## 📖 Table of Contents

1. [Application Overview](#application-overview)
2. [Complete Feature Map](#complete-feature-map)
3. [Administrative Hierarchy & Panels](#administrative-hierarchy--panels)
4. [Core Systems & Functions](#core-systems--functions)
5. [Economy & Monetization](#economy--monetization)
6. [Firebase Integration](#firebase-integration)
7. [Payment Gateway Setup](#payment-gateway-setup)
8. [Google Play Store Submission](#google-play-store-submission)
9. [Regional Configuration](#regional-configuration)
10. [FAQ & Common Questions](#faq--common-questions)

---

## Application Overview

Aura Voice Chat is a mobile-first social voice and video chat application that enables users to:

- **Connect globally** through live voice and video chat rooms
- **Build relationships** via CP (Couple Partnership), Family, and Friend systems
- **Earn rewards** through daily logins, games, events, and referrals
- **Express themselves** with gifts, frames, vehicles, and cosmetic items
- **Monetize activity** through the earning and reseller systems

### Tech Stack

| Component | Technology |
|-----------|------------|
| **Android App** | Kotlin, Jetpack Compose, MVVM, Agora SDK |
| **Backend** | Node.js, Express, TypeScript, Prisma |
| **Database** | PostgreSQL (AWS RDS) + Redis (ElastiCache) |
| **Real-time** | WebSocket (Socket.io) |
| **Authentication** | AWS Cognito (Phone, Google, Facebook) |
| **Cloud Functions** | AWS Lambda |
| **Analytics** | AWS Pinpoint + CloudWatch |
| **Storage** | AWS S3 |
| **Push Notifications** | AWS Pinpoint |

### Theme & Branding

- **Primary Color:** Purple (#c9a8f1) → White gradient
- **Accent Colors:** Magenta (#d958ff), Cyan (#35e8ff)
- **Dark Canvas:** #12141a
- **Minimum Android:** 9+ (API 28)
- **Target Android:** 14 (API 34)

---

## Complete Feature Map

### 🎯 All Systems & Their Interconnections

```
┌─────────────────────────────────────────────────────────────────────┐
│                         USER PROGRESSION                             │
├─────────────────────────────────────────────────────────────────────┤
│  ┌────────────┐    ┌────────────┐    ┌────────────┐                │
│  │ EXP/Level  │───▶│  Unlocks   │───▶│  Features  │                │
│  │  System    │    │  Rewards   │    │  Access    │                │
│  └────────────┘    └────────────┘    └────────────┘                │
│        │                 │                 │                        │
│        ▼                 ▼                 ▼                        │
│  ┌────────────┐    ┌────────────┐    ┌────────────┐                │
│  │    VIP     │───▶│ Multipliers│───▶│  Benefits  │                │
│  │   System   │    │ & Boosts   │    │  & Items   │                │
│  └────────────┘    └────────────┘    └────────────┘                │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                         ECONOMY SYSTEM                               │
├─────────────────────────────────────────────────────────────────────┤
│  ┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐   │
│  │ Recharge │────▶│  Coins   │────▶│  Gifts   │────▶│ Diamonds │   │
│  │   ($)    │     │          │     │          │     │          │   │
│  └──────────┘     └──────────┘     └──────────┘     └──────────┘   │
│       │                │                │                │          │
│       │                ▼                │                ▼          │
│       │         ┌──────────┐            │         ┌──────────┐     │
│       │         │  Store   │            │         │ Exchange │     │
│       │         │ Purchase │            │         │  (30%)   │     │
│       │         └──────────┘            │         └──────────┘     │
│       │                                 │                │          │
│       ▼                                 ▼                ▼          │
│  ┌──────────┐                    ┌──────────┐     ┌──────────┐     │
│  │ Reseller │                    │ Receiver │────▶│  Wallet  │     │
│  │  System  │                    │ Earnings │     │  Balance │     │
│  └──────────┘                    └──────────┘     └──────────┘     │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                        SOCIAL SYSTEMS                                │
├─────────────────────────────────────────────────────────────────────┤
│  ┌────────────┐    ┌────────────┐    ┌────────────┐                │
│  │  Friends   │    │     CP     │    │   Family   │                │
│  │   System   │    │Partnership │    │   System   │                │
│  └────────────┘    └────────────┘    └────────────┘                │
│        │                 │                 │                        │
│        ▼                 ▼                 ▼                        │
│  ┌────────────────────────────────────────────────────────┐        │
│  │              Shared Benefits & Rankings                │        │
│  │   • Exclusive frames  • CP cosmetics  • Family perks   │        │
│  │   • Friend rewards    • Heart effects • EXP bonuses    │        │
│  └────────────────────────────────────────────────────────┘        │
└─────────────────────────────────────────────────────────────────────┘
```

### Core Features List

| # | Feature | Documentation | Data File |
|---|---------|---------------|-----------|
| 1 | EXP & Level System | [docs/exp-level-system.md](docs/exp-level-system.md) | [data/level-rewards.json](data/level-rewards.json) |
| 2 | VIP/SVIP Systems | [docs/features/vip-systems.md](docs/features/vip-systems.md) | [data/vip-multipliers.json](data/vip-multipliers.json) |
| 3 | Daily Rewards | [docs/features/daily-rewards.md](docs/features/daily-rewards.md) | — |
| 4 | Wallet (Coins/Diamonds) | [docs/features/wallet.md](docs/features/wallet.md) | — |
| 5 | Gift System | [docs/features/gifts.md](docs/features/gifts.md) | — |
| 6 | Rooms (Voice/Video) | [docs/features/rooms.md](docs/features/rooms.md) | — |
| 7 | Video/Music Mode | [docs/features/video-music-mode.md](docs/features/video-music-mode.md) | — |
| 8 | Games System | [docs/games-system.md](docs/games-system.md) | [data/games.json](data/games.json) |
| 9 | Events System | [docs/events-system.md](docs/events-system.md) | [data/events.json](data/events.json) |
| 10 | CP Partnership | [docs/features/cp.md](docs/features/cp.md) | [data/cp-levels.json](data/cp-levels.json) |
| 11 | Friend System | [docs/cp-friend-system.md](docs/cp-friend-system.md) | [data/friend-rewards.json](data/friend-rewards.json) |
| 12 | Family System | [docs/family-system.md](docs/family-system.md) | — |
| 13 | Medals System | [docs/features/medals.md](docs/features/medals.md) | [data/medals.json](data/medals.json) |
| 14 | Rankings/Leaderboards | [docs/ranking-and-leaderboards.md](docs/ranking-and-leaderboards.md) | [data/rankings.json](data/rankings.json) |
| 15 | Store | [docs/features/store.md](docs/features/store.md) | — |
| 16 | Referral Programs | [docs/features/referrals.md](docs/features/referrals.md) | — |
| 17 | Earning System | [docs/earning-system.md](docs/earning-system.md) | [data/earning-targets.json](data/earning-targets.json) |
| 18 | Reseller System | [docs/reseller-system.md](docs/reseller-system.md) | [data/reseller-config.json](data/reseller-config.json) |
| 19 | Recharge Events | [docs/features/recharge-event.md](docs/features/recharge-event.md) | — |
| 20 | Rocket System | [docs/features/rocket-system.md](docs/features/rocket-system.md) | — |
| 21 | AuraPass | [docs/features/aurapass.md](docs/features/aurapass.md) | — |
| 22 | AuraPass Spin | [docs/features/aurapass-spin.md](docs/features/aurapass-spin.md) | — |
| 23 | Profile & Inventory | [docs/profile-and-inventory.md](docs/profile-and-inventory.md) | — |
| 24 | Animations & Effects | [docs/animations-effects.md](docs/animations-effects.md) | [data/animations.json](data/animations.json) |

---

## Administrative Hierarchy & Panels

### Complete Role Hierarchy

```
┌─────────────────────────────────────────────────────────────────────┐
│                              OWNER                                   │
│                    👑 Single Supreme Authority                       │
│  ════════════════════════════════════════════════════════════════   │
│  • FULL app control (edit/add/remove ANYTHING)                      │
│  • Economy management (prices, rewards, multipliers)                 │
│  • Financial access (all transactions, payouts)                      │
│  • Appoint ALL staff (Country Admins, Admins, Support)              │
│  • Kill switches (disable features, maintenance mode)                │
│  • Version control & rollback                                        │
│  ────────────────────────────────────────────────────────────────   │
│  Panel: docs/owner-panel.md                                         │
│  CMS: docs/owner-cms.md                                             │
└─────────────────────────────────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│                         COUNTRY ADMINS                               │
│              🌍 ONE per Country (Unique Authority)                   │
│  ════════════════════════════════════════════════════════════════   │
│  • Ban users permanently in their country                            │
│  • Manage ALL Regular Admins in their country                        │
│  • Oversee ALL Guides in their country                               │
│  • View country-specific statistics                                  │
│  • Close/ban rooms in their country                                  │
│  • Higher coin adjustment limits (up to 50M)                         │
│  ────────────────────────────────────────────────────────────────   │
│  Tag: 🌍 Country Admin [PK/IN/US/etc.]                              │
│  Frame: Exclusive Country Admin Frame                                │
│  Panel: Country Admin Panel (within Admin Panel)                     │
└─────────────────────────────────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│                          REGULAR ADMINS                              │
│                ⚡ Multiple per Country (Daily Ops)                   │
│  ════════════════════════════════════════════════════════════════   │
│  • Manage Guides (add/remove guide tags, monitor tasks)              │
│  • Handle user reports & moderation queue                            │
│  • Mute users (up to 7 days)                                         │
│  • Issue warnings                                                    │
│  • Room moderation (remove inappropriate content)                    │
│  • Coin adjustment limits (up to 5M)                                 │
│  • Escalate ban requests to Country Admin                            │
│  ────────────────────────────────────────────────────────────────   │
│  Tag: ⚡ Admin                                                       │
│  Frame: Admin Frame                                                  │
│  Panel: docs/admin-panel.md                                         │
└─────────────────────────────────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        CUSTOMER SUPPORT                              │
│                     💬 Team in Support Room                          │
│  ════════════════════════════════════════════════════════════════   │
│  • Help users with questions and issues                              │
│  • Create support tickets for admins                                 │
│  • Escalate issues to Regular Admins                                 │
│  • Access basic user profile info                                    │
│  • Provide FAQ responses                                             │
│  ────────────────────────────────────────────────────────────────   │
│  Tag: 💬 Support                                                     │
│  Frame: Support Frame                                                │
│  Room: 🎧 Aura Customer Support (Official)                          │
└─────────────────────────────────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│                            GUIDES                                    │
│                 📖 Community helpers (Earn rewards)                  │
│  ════════════════════════════════════════════════════════════════   │
│  • Complete monthly targets for earnings                             │
│  • Help new users navigate the app                                   │
│  • Host rooms and engage community                                   │
│  • Earn through guide program                                        │
│  ────────────────────────────────────────────────────────────────   │
│  Tag: 📖 Guide                                                       │
│  Docs: docs/guide-system.md                                         │
└─────────────────────────────────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────┐
│                           RESELLERS                                  │
│               💎 Verified Coin Sellers (Earn profits)                │
│  ════════════════════════════════════════════════════════════════   │
│  • Purchase coins at wholesale rates (10-30% off)                    │
│  • Sell to users and keep profit margin                              │
│  • Access Seller Panel for inventory & sales                         │
│  • Listed in public Reseller Directory                               │
│  • Volume & streak bonuses                                           │
│  ────────────────────────────────────────────────────────────────   │
│  Tiers: Bronze → Silver → Gold → Platinum → Diamond                 │
│  Panel: docs/reseller-panel.md                                      │
│  System: docs/reseller-system.md                                    │
└─────────────────────────────────────────────────────────────────────┘
```

### Permission Matrix

| Permission | Support | Admin | Country Admin | Owner |
|------------|---------|-------|---------------|-------|
| View user profiles | ✓ | ✓ | ✓ | ✓ |
| Create tickets | ✓ | ✓ | ✓ | ✓ |
| Issue warnings | ✗ | ✓ | ✓ | ✓ |
| Mute users | ✗ | ✓ (7d max) | ✓ (30d max) | ✓ (unlimited) |
| Temporary ban | ✗ | ✗ | ✓ | ✓ |
| Permanent ban | ✗ | ✗ | ✓ | ✓ |
| Coin adjustment | ✗ | ✓ (5M max) | ✓ (50M max) | ✓ (unlimited) |
| Manage guides | ✗ | ✓ | ✓ | ✓ |
| Manage admins | ✗ | ✗ | ✓ (own country) | ✓ |
| Manage country admins | ✗ | ✗ | ✗ | ✓ |
| App settings | ✗ | ✗ | ✗ | ✓ |
| Financial access | ✗ | ✗ | ✗ | ✓ |
| Feature toggles | ✗ | ✗ | ✗ | ✓ |
| Economy control | ✗ | ✗ | ✗ | ✓ |

---

## Core Systems & Functions

### 1. EXP & Level System

**Purpose:** Track user progression and unlock features/rewards

**How it works:**
1. User earns EXP from activities (room, social, gifts, games)
2. EXP accumulates and user levels up at thresholds
3. Level ups grant coin rewards, cosmetics, and feature unlocks
4. VIP users get EXP multipliers (1.1x to 1.6x)

**Level Formula:**
```
EXP for Level N = 100 × N × (N + 1) / 2
Total EXP at Level N = 100 × N × (N + 1) × (N + 2) / 6
```

**Key Levels & Rewards:**

| Level | Total EXP | Reward | Unlock |
|-------|-----------|--------|--------|
| 1 | 0 | 5,000 coins | Basic room access |
| 5 | 1,000 | 5,000 coins + Frame | Create rooms |
| 10 | 4,500 | 15,000 coins + Frame | Custom room settings |
| 20 | 20,000 | 50,000 coins + Vehicle | Super Mic, 16-seat rooms |
| 30 | 45,000 | 100,000 coins + Frame | Premium games |
| 50 | 125,000 | 500,000 coins + Bundle | Exclusive events |
| 100 | 500,000 | 10,000,000 coins | Legend status |

**EXP Sources:**

| Source | EXP/Action | Daily Limit |
|--------|------------|-------------|
| Daily login | 50 | 1 |
| Join room | 5 | 20 |
| Use mic (per min) | 2 | 120 |
| Send gift | 1 per 100 coins | 500 |
| Play game | 10 | Unlimited |
| Win game | 25 | Unlimited |

**API Endpoints:**
```
GET /users/{userId}/level
GET /users/me/level
GET /level/rewards/{level}
POST /level/rewards/{level}/claim
```

**Firebase Path:** `/users/{userId}/level`

---

### 2. VIP System

**Purpose:** Provide premium benefits and multipliers

**VIP Tiers & Benefits:**

| Tier | Daily Multiplier | EXP Boost | Exclusive Items | Priority Join |
|------|------------------|-----------|-----------------|---------------|
| VIP1 | 1.20x | +5% | ✗ | ✗ |
| VIP2 | 1.40x | +10% | ✗ | ✗ |
| VIP3 | 1.60x | +15% | ✗ | ✓ |
| VIP4 | 1.80x | +20% | ✓ | ✓ |
| VIP5 | 2.00x | +25% | ✓ | ✓ |
| VIP6 | 2.20x | +30% | ✓ | ✓ |
| VIP7 | 2.40x | +40% | ✓ | ✓ |
| VIP8 | 2.60x | +50% | ✓ | ✓ |
| VIP9 | 2.80x | +60% | ✓ | ✓ |
| VIP10 | 3.00x | +75% | ✓ | ✓ |

**VIP Benefits:**
- Daily reward multipliers
- EXP boost on all activities
- Access to VIP-exclusive store items
- Priority room join
- Seat frames (VIP4+)
- Super Mic access (VIP4+ or Level 40+)

**API Endpoints:**
```
GET /vip/tier
POST /vip/purchase
```

---

### 3. Wallet & Currency

**Purpose:** Manage in-app currencies

**Currency Types:**

| Currency | How to Get | How to Use |
|----------|------------|------------|
| **Coins** | Recharge, daily rewards, referrals, diamond exchange | Store purchases, gifts, fees |
| **Diamonds** | Receive gifts from other users | Exchange to coins (30% rate) |

**Exchange Formula:**
```
Coins Received = floor(Diamonds × 0.30)
```

**API Endpoints:**
```
GET /wallet/balances
POST /wallet/exchange
```

---

### 4. Gift System

**Purpose:** Enable coin economy and social gifting

**Gift Flow:**
```
Sender (Coins) ─────▶ Gift ─────▶ Receiver (Diamonds)
       │                               │
       └──── Coins Deducted            └──── Diamonds 1:1
```

**Gift Categories:**
- Standard gifts
- VIP-exclusive gifts
- CP partnership gifts
- Country-specific gifts
- Customized gifts
- Baggage (free gifts from rewards)

**API Endpoints:**
```
GET /gifts
POST /rooms/{roomId}/gifts/send
```

---

### 5. Rooms System

**Purpose:** Core social experience through voice/video rooms

**Room Features:**
- 8-24 seats (based on level)
- Voice and video modes
- Music/video playlist mode
- Games integration
- Gift animations
- Room rankings

**Seat States:**
- Empty, Occupied, Muted, Locked, Owner

**Owner Controls:**
- Close room, lock seats, change capacity
- Edit announcements, manage moderators

---

### 6. Games System

**Purpose:** Entertainment and coin economy

**Available Games:**

| Game | Mechanics | Max Payout |
|------|-----------|------------|
| Lucky Spin | Wheel of fortune | 500x |
| Dice Roll | Bet on outcomes | 30x |
| Card Flip | Higher/lower | 135x+ |
| Treasure Box | Find prizes | 3x pot |
| Lucky Number | Guess number | 100x |
| Coin Toss | Heads/tails | 6.8x |
| Slot Machine | 3-reel slots | Jackpot |

**API Endpoints:**
```
POST /games/{gameType}/start
POST /games/{gameType}/action
POST /games/{gameType}/cashout
```

---

### 7. CP (Couple Partnership)

**Purpose:** Social relationship system

**How it works:**
1. Pay formation fee (3M coins)
2. Both partners accept
3. Send gifts to earn CP EXP
4. Level up for rewards (frames, effects)

**CP Benefits:**
- Exclusive CP frame
- Heart effect upgrades
- Baggage gifts at thresholds

---

### 8. Family System

**Purpose:** Community groups with shared benefits

**Family Features:**
- Create: Level 20+, 1M coins
- Roles: Owner, Co-Owner, Admin, Elder, Member
- Max members: 50 (upgradable)
- Weekly/monthly rankings
- Family chat & events

---

### 9. Earning System

**Purpose:** Enable users to earn real money

**Earning Targets:**

| Target Type | Example | Earning |
|-------------|---------|---------|
| Gift Sending | Send 10M coins in 10 days | $5.00 |
| Room Hosting | Host 50 hours in 14 days | $3.00 |
| Social | Gain 500 followers in 30 days | $5.00 |
| Streak | 30 consecutive days active | $2.00 |

**Withdrawal Methods:**
- Bank transfer
- EasyPaisa, JazzCash (Pakistan)
- UPI, Paytm (India)
- PayPal, Payoneer (International)

---

### 10. Reseller System

**Purpose:** Enable verified sellers to profit from coin sales

**Reseller Tiers:**

| Tier | Fee | Discount | Daily Limit |
|------|-----|----------|-------------|
| Bronze | $50 | 10% | 50M |
| Silver | $150 | 15% | 100M |
| Gold | $300 | 20% | 250M |
| Platinum | $500 | 25% | 500M |
| Diamond | $1,000 | 30% | Unlimited |

---

## Economy & Monetization

### Complete Economy Flow

```
                            MONEY IN
                               │
    ┌──────────────────────────┼──────────────────────────┐
    │                          │                          │
    ▼                          ▼                          ▼
┌─────────┐            ┌─────────────┐            ┌───────────┐
│ Direct  │            │  Reseller   │            │   VIP     │
│Recharge │            │  Purchase   │            │ Purchase  │
│  ($)    │            │   ($)       │            │   ($)     │
└────┬────┘            └──────┬──────┘            └─────┬─────┘
     │                        │                         │
     │    ┌───────────────────┘                         │
     │    │                                             │
     ▼    ▼                                             │
┌──────────────┐                                        │
│    COINS     │◀───────────────────────────────────────┘
│  (Primary    │
│  Currency)   │
└──────┬───────┘
       │
       ├────────────────┬────────────────┬────────────────┐
       │                │                │                │
       ▼                ▼                ▼                ▼
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│  Gifts   │    │  Store   │    │   Fees   │    │  Games   │
│  (Send)  │    │ Purchase │    │(CP/Family│    │   Bets   │
│          │    │          │    │ /Room)   │    │          │
└────┬─────┘    └──────────┘    └──────────┘    └────┬─────┘
     │                                               │
     ▼                                               ▼
┌──────────────┐                              ┌──────────────┐
│  DIAMONDS    │                              │    WIN/      │
│ (Receiver    │                              │    LOSE      │
│  gets 1:1)   │                              │              │
└──────┬───────┘                              └──────────────┘
       │
       ▼
┌──────────────┐
│   EXCHANGE   │
│   (30% to    │
│    Coins)    │
└──────┬───────┘
       │
       ▼
                            MONEY OUT
                               │
    ┌──────────────────────────┼──────────────────────────┐
    │                          │                          │
    ▼                          ▼                          ▼
┌─────────┐            ┌─────────────┐            ┌───────────┐
│ Earning │            │  Referral   │            │ Reseller  │
│ System  │            │  Get Cash   │            │  Profit   │
│ ($)     │            │   ($)       │            │   ($)     │
└─────────┘            └─────────────┘            └───────────┘
```

### Coin Packages (Revenue)

| Package | Price | Coins | Bonus (First Recharge) |
|---------|-------|-------|------------------------|
| Starter | $0.99 | 80,000 | +80,000 |
| Basic | $4.99 | 450,000 | +450,000 |
| Standard | $9.99 | 950,000 | +950,000 |
| Premium | $24.99 | 2,600,000 | +2,600,000 |
| Elite | $49.99 | 5,500,000 | +5,500,000 |
| Ultimate | $99.99 | 12,000,000 | +12,000,000 |

---

## AWS Integration

### Complete AWS Setup

See [docs/aws-setup.md](docs/aws-setup.md) for detailed setup.

**Quick Start:**

```bash
# Run automated setup
./scripts/aws-setup.sh
```

**Manual Steps:**
1. Create AWS account and configure IAM
2. Set up Cognito User Pool (Phone, Google, Facebook)
3. Configure S3 bucket for storage
4. Set up RDS PostgreSQL database
5. Configure Pinpoint for analytics and push notifications
6. Set up CloudWatch for monitoring

### Database Structure (PostgreSQL)

```sql
-- users table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    phone VARCHAR(20),
    email VARCHAR(255),
    name VARCHAR(100),
    avatar_url TEXT,
    level INTEGER DEFAULT 1,
    vip_tier INTEGER DEFAULT 0,
    coins BIGINT DEFAULT 0,
    diamonds BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW()
);

-- rooms table
CREATE TABLE rooms (
    id UUID PRIMARY KEY,
    name VARCHAR(100),
    owner_id UUID REFERENCES users(id),
    type VARCHAR(20),
    capacity INTEGER DEFAULT 8,
    is_active BOOLEAN DEFAULT true
);

-- gifts table
CREATE TABLE gifts (
    id UUID PRIMARY KEY,
    name VARCHAR(100),
    price INTEGER,
    animation_url TEXT,
    category VARCHAR(50)
);

-- families table
CREATE TABLE families (
    id UUID PRIMARY KEY,
    name VARCHAR(100),
    owner_id UUID REFERENCES users(id),
    created_at TIMESTAMP DEFAULT NOW()
);

-- cp_partnerships table
CREATE TABLE cp_partnerships (
    id UUID PRIMARY KEY,
    partner1_id UUID REFERENCES users(id),
    partner2_id UUID REFERENCES users(id),
    level INTEGER DEFAULT 1,
    exp BIGINT DEFAULT 0
);
```

### Security (IAM Policies)

AWS services are secured via IAM roles and policies:
- Cognito handles authentication
- S3 bucket policies restrict access
- RDS is in private subnet
- Lambda functions use least-privilege IAM roles

### AppConfig Parameters (Feature Flags)

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| feature_daily_rewards | Boolean | true | Enable daily rewards |
| feature_kyc_enabled | Boolean | true | Enable KYC |
| feature_cp_enabled | Boolean | true | Enable CP system |
| feature_video_mode | Boolean | true | Enable video mode |
| min_app_version | String | 1.0.0 | Minimum version |
| maintenance_mode | Boolean | false | Maintenance toggle |
| daily_reward_base | Number | 5000 | Day 1 reward |
| exchange_rate | Number | 0.3 | Diamond to coin rate |

---

## Payment Gateway Setup

### Supported Payment Methods

| Region | Methods |
|--------|---------|
| Pakistan | EasyPaisa, JazzCash, Bank Transfer |
| India | UPI (GPay, PhonePe, Paytm), NEFT/IMPS |
| International | PayPal, Stripe, Payoneer, Wire |
| Middle East | Bank Transfer, Payoneer |

### In-App Purchases (Google Play Billing)

For coin recharge purchases, use Google Play Billing:

```kotlin
// build.gradle (app)
implementation 'com.android.billingclient:billing-ktx:6.1.0'
```

**Setup Steps:**
1. Create In-App Products in Play Console
2. Integrate Billing Library
3. Handle purchase flow
4. Verify purchases server-side

### Withdrawal Integration

For earnings/cash withdrawals, integrate with:

**Pakistan:**
```
EasyPaisa API: https://easypay.easypaisa.com.pk
JazzCash API: https://sandbox.jazzcash.com.pk/apis/
```

**India:**
```
UPI: Integrate via payment aggregator (Razorpay, Cashfree)
```

**International:**
```
PayPal Payouts API: https://developer.paypal.com/docs/payouts/
Payoneer: https://payoneer.com/developers/
```

### KYC Verification

| Level | Requirement | Withdrawal Limit |
|-------|-------------|------------------|
| Basic | Phone verified | $50/month |
| Standard | ID document | $500/month |
| Premium | ID + Address proof | $5,000/month |
| Enterprise | Full KYC | Unlimited |

---

## Google Play Store Submission

See [docs/play-store-submission.md](docs/play-store-submission.md)

### Pre-Submission Checklist

- [ ] Google Play Developer account ($25)
- [ ] Privacy Policy URL (public)
- [ ] Terms of Service URL
- [ ] Support email address
- [ ] Signed release AAB
- [ ] App icon (512x512 PNG)
- [ ] Feature graphic (1024x500 PNG)
- [ ] Screenshots (phone + tablet)
- [ ] Short description (80 chars max)
- [ ] Full description (4000 chars max)
- [ ] Content rating questionnaire
- [ ] Data safety form completed
- [ ] Target API 34

### Build Commands

```bash
# Navigate to android directory
cd android

# Build debug APK
./gradlew assembleDebug

# Build release AAB (for Play Store)
./gradlew bundleRelease

# Build release APK
./gradlew assembleRelease
```

### App Signing

```bash
# Generate upload key
keytool -genkey -v -keystore upload-keystore.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias upload-key
```

### Timeline

| Phase | Duration |
|-------|----------|
| Account setup | 1-2 days |
| Prepare assets | 2-3 days |
| Store listing | 1 day |
| Internal testing | 1-2 weeks |
| Closed beta | 1-2 weeks |
| Production review | 1-7 days |
| **Total** | **4-8 weeks** |

---

## Regional Configuration

### Country-Specific Features

| Feature | Configuration |
|---------|---------------|
| Payment Methods | Per-country payment provider setup |
| Currency | Local currency display with USD base |
| Gifts | Region-specific gift catalog |
| Events | Regional events and competitions |
| Language | Localization support |
| Country Admin | One admin per country |

### Adding New Country

1. **Owner Panel:** Add country admin slot
2. **Payment:** Configure local payment methods
3. **Gifts:** Add regional gift catalog (optional)
4. **Currency:** Set local currency display
5. **Events:** Configure regional events
6. **Localization:** Add language support

### Currency Display

| Region | Currency | Display Format |
|--------|----------|----------------|
| Pakistan | PKR | Rs. 1,000 |
| India | INR | ₹1,000 |
| UAE | AED | د.إ 1,000 |
| USA | USD | $10.00 |
| Europe | EUR | €10.00 |

---

## FAQ & Common Questions

### General Questions

**Q: What is Aura Voice Chat?**
A: A social voice and video chat app where users connect through rooms, send gifts, and build relationships.

**Q: What platforms is it available on?**
A: Currently Android (9+), with iOS potential for future.

**Q: How do users earn coins?**
A: Daily rewards, referrals, medal achievements, and diamond exchange.

**Q: What's the difference between coins and diamonds?**
A: Coins are spent (gifts, store), Diamonds are earned (receiving gifts) and convert to coins at 30%.

### Economy Questions

**Q: What's the exchange rate for diamonds to coins?**
A: 30% (100,000 diamonds = 30,000 coins)

**Q: How do resellers profit?**
A: Buy coins at 10-30% discount, sell at market rate.

**Q: What are the earning system requirements?**
A: Complete activity targets (gifts, hosting, social) within time periods.

**Q: How long is the clearance period for withdrawals?**
A: 5-7 days depending on transaction type.

### Technical Questions

**Q: What's the minimum Android version?**
A: Android 9 (API 28)

**Q: How is real-time handled?**
A: WebSocket (Socket.io) for real-time communication, AWS RDS for data.

**Q: What SDK is used for voice/video?**
A: Agora SDK

**Q: What backend technologies are used?**
A: Node.js, Express, TypeScript, Prisma, PostgreSQL (AWS RDS), Redis (ElastiCache)

### Admin Questions

**Q: How many Country Admins per country?**
A: ONE per country (unique authority)

**Q: Can Regular Admins ban users?**
A: No, they escalate to Country Admin.

**Q: Who can adjust coin balances?**
A: Admins (up to 5M), Country Admins (up to 50M), Owner (unlimited)

**Q: How is the admin hierarchy structured?**
A: Owner → Country Admins → Regular Admins → Customer Support

### VIP Questions

**Q: How many VIP tiers are there?**
A: 10 tiers (VIP1 to VIP10)

**Q: What's the maximum daily reward multiplier?**
A: 3.00x (VIP10)

**Q: When do VIP benefits apply?**
A: At claim time for daily rewards, continuously for EXP boost

### Room Questions

**Q: How many seats can a room have?**
A: 8-24 seats depending on owner's level

**Q: Can anyone create rooms?**
A: Users need Level 5+ to create rooms

**Q: What is Video/Music mode?**
A: Cinema-like mode for watching YouTube videos together

---

## Quick Start Guide

### 1. Clone & Setup

```bash
# Clone repository
git clone https://github.com/venomvex/auravoicechatdoc.git
cd auravoicechatdoc

# Setup AWS Infrastructure
./scripts/aws-setup.sh

# Setup Backend
cd backend
npm install
cp .env.example .env
# Edit .env with your AWS configuration
npm run dev
```

### 2. Android Build

```bash
cd android

# Configure AWS Amplify
# Place amplifyconfiguration.json and awsconfiguration.json in android/app/src/main/res/raw/

# Build debug
./gradlew assembleDebug

# Build release
./gradlew bundleRelease
```

### 3. Deploy

See [deployment.md](deployment.md) and [release-playbook.md](release-playbook.md)

---

## Related Documentation

| Document | Description |
|----------|-------------|
| [README.md](README.md) | Product specification |
| [api.md](api.md) | API reference |
| [architecture.md](architecture.md) | System architecture |
| [configuration.md](configuration.md) | Configuration guide |
| [security.md](security.md) | Security documentation |
| [operations.md](operations.md) | Operations guide |
| [troubleshooting.md](troubleshooting.md) | Troubleshooting guide |
| [changelog.md](changelog.md) | Version history |
| [contributing.md](contributing.md) | Contribution guidelines |

---

**Developer:** Hawkaye Visions LTD — Lahore, Pakistan

*This guide consolidates all documentation into a single comprehensive reference. For detailed information on specific features, refer to the linked documentation files.*
