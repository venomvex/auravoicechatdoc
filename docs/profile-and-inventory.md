# Profile & Inventory

Comprehensive guide to user profiles, inventory management, and cosmetic items in Aura Voice Chat.

## Overview

The profile system displays user identity, achievements, and equipped cosmetics. The inventory stores all owned items including frames, themes, vehicles, entrance styles, and more.

---

## Profile Components

### Profile Header

**Display Elements:**
- Avatar (circular, customizable)
- Username
- User ID (tap to copy)
- Level badge (e.g., "Lv.60")
- SVIP badge (if applicable)
- Gender indicator
- Verification badge (if verified)

### Profile Metrics

| Metric | Description |
|--------|-------------|
| Followers | Users following this profile |
| Following | Users this profile follows |
| Visitors | Profile view count |
| Diamonds | Total diamonds received |
| Level | Current user level |

### Profile Badges

Displayed below username:
- Up to 10 medals
- CP/Friend indicators
- Family badge
- VIP/SVIP tier badge
- Special event badges

---

## Profile Settings

### Personal Information

| Setting | Options |
|---------|---------|
| Avatar | Upload photo, select from gallery |
| Username | Text (3-20 characters) |
| Bio | Text (max 200 characters) |
| Gender | Male, Female, Other, Prefer not to say |
| Birthday | Date picker (private by default) |
| Location | Country/Region selection |

### Privacy Controls

| Setting | Options |
|---------|---------|
| Profile Visibility | Public, Friends Only, Private |
| Show Online Status | On/Off |
| Show Visitors | On/Off |
| Allow DMs | Everyone, Friends Only, Nobody |
| Show Birthday | On/Off |
| Show Location | On/Off |

### Account Settings

| Setting | Description |
|---------|-------------|
| Phone Number | Linked mobile number |
| Email | Optional email linkage |
| Social Accounts | Google, Facebook connections |
| Custom ID | Premium feature (alphanumeric ID) |

---

## Inventory Categories

### 1. Frames

Profile picture borders with decorative effects.

| Tier | Duration Options | Example |
|------|------------------|---------|
| Common | 7d, 30d | Basic frames |
| Rare | 7d, 30d, 90d | Animated frames |
| Epic | 30d, 90d, Permanent | Special effects |
| Legendary | Permanent | Exclusive designs |

**Acquisition:**
- Store purchase
- Medal rewards
- CP/Friend level rewards
- Event rewards
- VIP perks

### 2. Themes

Full UI customization for profile and room presence.

| Type | Description |
|------|-------------|
| Profile Theme | Background and color scheme |
| Room Theme | Room UI appearance |
| CP Theme | Couple-specific theme |
| Friend Theme | Friend-specific theme |

**Features:**
- Color palette customization
- Background images
- Button styles
- Animation effects

### 3. Vehicles

Animated entrance effects when joining rooms.

| Tier | Animation | Duration |
|------|-----------|----------|
| Common | Simple entrance | 2-3 seconds |
| Rare | Enhanced animation | 3-4 seconds |
| Epic | Full-screen effect | 4-5 seconds |
| Legendary | Premium animation + sound | 5-6 seconds |

**Examples:**
- Yacht with balloons (Friend)
- Romantic carriage (CP)
- Sports car
- Flying saucer
- Dragon

### 4. Entrance Styles

Visual effects accompanying vehicle entrance.

| Style | Effect |
|-------|--------|
| Sparkle | Glitter particles |
| Fire | Flame effects |
| Ice | Frost and snowflakes |
| Lightning | Electric effects |
| Petals | Flower petals |
| Hearts | Heart particles (CP) |

### 5. Opening Page Covers

Custom loading/splash screens.

| Type | Display |
|------|---------|
| Personal Cover | Shown to user on app open |
| Room Cover | Shown when others visit room |
| CP Cover | Shared couple cover |

### 6. Mic Skins

Microphone visual customization in rooms.

| Type | Feature |
|------|---------|
| Static | Fixed design |
| Animated | Movement effects |
| Reactive | Responds to voice |
| CP Mic | Shared couple design |
| Friend Mic | Shared friend design |

### 7. Seat Effects

Visual effects when seated in room.

| Type | Effect |
|------|--------|
| Hearts | Heart particles |
| Stars | Star animations |
| Bubbles | Bubble effects |
| Flames | Fire effects |
| CP Hearts | Enhanced couple hearts |

### 8. Chat Bubbles

Message styling in room chat.

| Type | Feature |
|------|---------|
| Standard | Basic bubble |
| Themed | Color/pattern |
| Animated | Moving effects |
| CP Bubbles | Romantic design |
| Friend Bubbles | Friendship design |

### 9. Room Card Backgrounds

Profile card appearance in room lists.

| Type | Feature |
|------|---------|
| Standard | Default design |
| VIP | VIP-themed |
| CP Card | Shows both partners |
| Friend Card | Shows both friends |
| Custom | User-uploaded image |

---

## Inventory Management

### Viewing Inventory

Access via: **Me** → **My Items**

**Filters:**
- Category (Frames, Themes, etc.)
- Status (Active, Expired, All)
- Source (Purchased, Earned, Gift)

### Equipping Items

1. Open inventory
2. Select category
3. Tap item
4. Select "Equip"
5. Item becomes active

**Notes:**
- One item per category active
- Some items are category-specific (CP items require CP)
- Expired items cannot be equipped

### Item Details

Each item shows:
- Name and preview
- Duration remaining (or Permanent)
- Acquisition source
- Equip/Unequip button

### Expired Items

- Remain in inventory as "Expired"
- Can be renewed via purchase
- Cannot be equipped until renewed
- Deletion option available

---

## Custom ID

### Overview
Replace numeric ID with custom alphanumeric identifier.

### Requirements
- VIP 3+ or purchase separately
- 4-12 characters
- Alphanumeric only (letters, numbers)
- Unique across platform

### Pricing
| Duration | Price |
|----------|-------|
| 30 days | 500,000 coins |
| 90 days | 1,200,000 coins |
| Permanent | 5,000,000 coins |

### Restrictions
- Change once per 30 days
- Cannot use reserved words
- Cannot impersonate others
- Subject to content guidelines

---

## Super Mic

### Overview
Enhanced microphone feature with premium effects.

### Requirements
- Level 20+ or VIP 5+
- Unlocked via purchase or achievement

### Features
| Feature | Description |
|---------|-------------|
| Priority Audio | Louder voice presence |
| Visual Indicator | Special mic badge |
| Effects | Enhanced audio effects |
| Animations | Mic-specific animations |

### Pricing
| Duration | Price |
|----------|-------|
| 7 days | 100,000 coins |
| 30 days | 350,000 coins |
| Permanent | 2,000,000 coins |

---

## Gifts Records

### Sent Gifts
Track all gifts sent:
- Date and time
- Recipient
- Gift type and value
- Total coins spent

### Received Gifts
Track all gifts received:
- Date and time
- Sender
- Gift type and value
- Total diamonds earned

### Records Display
- History tab shows last 30 days
- Filter by Coins/Diamonds
- Income/Expend summary
- Transaction details (e.g., "Super77 Pro", "Greedy Baby")

---

## API Endpoints

```
GET /profile/me
PUT /profile/me
GET /profile/{userId}
GET /inventory
GET /inventory/{category}
POST /inventory/equip
POST /inventory/unequip
GET /gifts/records/sent
GET /gifts/records/received
POST /profile/custom-id
GET /profile/super-mic
POST /profile/super-mic/purchase
```

---

## Data Model

```json
{
  "profile": {
    "id": "user_123",
    "username": "CoolUser",
    "customId": "CoolOne",
    "avatar": "https://cdn.aura.app/avatars/user_123.jpg",
    "bio": "Hello world!",
    "level": 60,
    "svipTier": 1,
    "gender": "male",
    "location": "US",
    "followers": 1500,
    "following": 200,
    "visitors": 5000,
    "diamonds": 10000000
  },
  "inventory": {
    "frames": [
      {
        "id": "frame_001",
        "name": "Golden Crown",
        "rarity": "legendary",
        "expiresAt": null,
        "isEquipped": true
      }
    ],
    "vehicles": [],
    "themes": [],
    "micSkins": [],
    "seatEffects": [],
    "chatBubbles": []
  },
  "equipped": {
    "frame": "frame_001",
    "vehicle": "vehicle_002",
    "theme": "theme_cp_01",
    "micSkin": null
  }
}
```

---

## Related Documentation

- [Medals System](./medals-system.md)
- [CP & Friend System](./cp-friend-system.md)
- [Gifts & Records](./gifts-and-records.md)
- [Store](./features/store.md)
