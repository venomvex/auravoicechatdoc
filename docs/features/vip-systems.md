# VIP Systems

Aura Voice Chat features two VIP systems that work together to provide users with enhanced benefits and rewards.

## Overview

The app supports both VIP systems simultaneously:
1. **Daily Reward Multiplier VIP** — The original VIP system that multiplies daily login rewards
2. **Full VIP (SVIP) System** — The complete VIP system with tiers, levels, and comprehensive benefits

Both systems are intended to coexist, with the Full VIP system extending the capabilities of the original multiplier-based VIP.

---

## 1. Daily Reward Multiplier VIP (Original System)

This is the foundational VIP system that applies multipliers to daily login rewards.

### Tiers and Multipliers

| Tier   | Multiplier | Daily Reward Example (Day 6 = 30,000 base) |
|--------|------------|---------------------------------------------|
| VIP1   | 1.20x      | 36,000 coins                               |
| VIP2   | 1.40x      | 42,000 coins                               |
| VIP3   | 1.60x      | 48,000 coins                               |
| VIP4   | 1.80x      | 54,000 coins                               |
| VIP5   | 2.00x      | 60,000 coins                               |
| VIP6   | 2.20x      | 66,000 coins                               |
| VIP7   | 2.40x      | 72,000 coins                               |
| VIP8   | 2.60x      | 78,000 coins                               |
| VIP9   | 2.80x      | 84,000 coins                               |
| VIP10  | 3.00x      | 90,000 coins                               |

### Behavior

- Multiplier applies at claim time
- Rounded to nearest 10 coins
- VIP tier is included in daily reward status response
- Works with all 7 days of the reward cycle

### API Integration

```json
// GET /rewards/daily/status response
{
  "currentDay": 6,
  "claimable": true,
  "vipTier": "VIP5",
  "vipMultiplier": 2.0
}
```

---

## 2. Full VIP (SVIP) System

The complete VIP system extends beyond daily rewards to provide a comprehensive set of benefits across the app.

### Tier Structure

The full VIP system uses the same 10 tiers (VIP1–VIP10) but adds additional benefits at each level.

> **Note:** The VIP multipliers are documented above in Section 1. This section extends those tiers with additional benefits.

### Benefits by Tier

| Tier   | Daily Multiplier | EXP Boost | Exclusive Items | Priority Join | Seat Frame |
|--------|------------------|-----------|-----------------|---------------|------------|
| VIP1   | 1.20x           | +5%       | ✗               | ✗             | ✗          |
| VIP2   | 1.40x           | +10%      | ✗               | ✗             | ✗          |
| VIP3   | 1.60x           | +15%      | ✗               | ✓             | ✗          |
| VIP4   | 1.80x           | +20%      | ✓               | ✓             | ✓          |
| VIP5   | 2.00x           | +25%      | ✓               | ✓             | ✓          |
| VIP6   | 2.20x           | +30%      | ✓               | ✓             | ✓          |
| VIP7   | 2.40x           | +40%      | ✓               | ✓             | ✓          |
| VIP8   | 2.60x           | +50%      | ✓               | ✓             | ✓          |
| VIP9   | 2.80x           | +60%      | ✓               | ✓             | ✓          |
| VIP10  | 3.00x           | +75%      | ✓               | ✓             | ✓          |

### Feature Details

#### EXP Boost
- Applies to all experience-earning activities
- Bonus calculated after base EXP is determined
- Stacks with event bonuses (if applicable)

#### Exclusive Items
- Access to VIP-only store items
- Special frames, mic skins, and entry effects
- Items may be tier-locked (e.g., VIP7+ only items)

#### Priority Join
- Faster access to popular rooms
- Priority queue when rooms are near capacity
- Available from VIP3+

#### Seat Frames
- Unique visual frame around user's seat
- Design varies by VIP tier (VIP4, VIP7, VIP10 milestones)
- Visible to all room participants

### Super Mic Access

VIP level also affects access to the Super Mic seat:
- **Super Mic** is available to users at Level ≥40 **OR** VIP4+
- Provides enhanced audio presence in rooms

### VIP Settings

Users can manage VIP preferences in:
- **Me → Settings → SVIP Settings**

Options include:
- Auto-renew preferences
- VIP badge visibility
- Notification preferences for expiry

### Purchase & Billing

- VIP is purchased with real money (not coins)
- Pricing and duration details provided via in-app purchase screens
- VIP visuals (frames/effects per tier) will be supplied via design assets

### API Endpoints

```
GET /vip/tier          — Get current VIP tier and benefits
POST /vip/purchase     — Initiate VIP purchase flow
```

---

## Comparison of Both Systems

| Aspect                  | Daily Reward Multiplier VIP | Full VIP (SVIP) System    |
|------------------------|-----------------------------|-----------------------------|
| Purpose                | Boost daily login rewards    | Comprehensive VIP benefits  |
| Multiplier             | ✓                           | ✓ (inherits)               |
| EXP Boost              | ✗                           | ✓                          |
| Exclusive Items        | ✗                           | ✓                          |
| Priority Join          | ✗                           | ✓                          |
| Seat Frames            | ✗                           | ✓                          |
| Super Mic Access       | ✗                           | ✓ (VIP4+)                  |
| Store Access           | Standard                    | VIP-exclusive store        |

---

## Future Enhancements

The following are planned or under consideration for the VIP systems:
- VIP-exclusive events and competitions
- VIP chat badges with tier-specific designs
- VIP-only rooms or early access features
- Loyalty rewards for long-term VIP subscribers

---

## Related Documentation

- [AuraPass (Full VIP)](./aurapass.md)
- [AuraPass Spin](./aurapass-spin.md)
- [VIP Recharge Bonus](./vip-recharge-bonus.md)
- [Daily Login Rewards](./daily-rewards.md)
- [Recharge Event](./recharge-event.md)
- [Medals System](./medals.md)
- [Store & Items](./store.md)
- [Naming Glossary](../naming-glossary.md)
- [Product Specification](../../README.md)
