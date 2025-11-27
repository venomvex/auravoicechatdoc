# AuraPass (VIP) — Full Specification

> **Note:** This document contains the complete VIP system (AuraPass). Both VIP systems coexist:
> 1. **Daily Reward Multiplier VIP** — Original system documented in [vip-systems.md](./vip-systems.md)
> 2. **Full AuraPass System** — This document

---

## Overview

- **Program:** AuraPass (VIP1–VIP10)
- **Purchase:** Real-money subscription (7-day, 30-day; CMS-configurable)
- **Tier progress:** VIP Score from recharges/bundles
- **Daily rewards multiplier:** VIP1 1.20x → VIP10 3.00x
- **Super Mic eligibility:** VIP4+ or Level ≥40
- **Visual identity:** Cosmic/Aurora motif (Comet, Nebula, Aurora, Eclipse)

---

## Global Rules

- **Cosmetics precedence:** AuraPass visuals override standard frames/effects while active (user can toggle per slot)
- **Expiry:** All perks/cosmetics end at expiry (optional 24h grace via CMS)
- **Notifications:** Renewal reminders at 7d/3d/24h

---

## VIP Score Thresholds

| Tier | VIP Score Required |
|------|-------------------|
| VIP1 | 2,000 |
| VIP2 | 15,000 |
| VIP3 | 50,000 |
| VIP4 | 125,000 |
| VIP5 | 250,000 |
| VIP6 | 500,000 |
| VIP7 | 850,000 |
| VIP8 | 1,350,000 |
| VIP9 | 1,800,000 |
| VIP10 | 2,500,000 |

---

## Perk Names (Originalized)

| Legacy Name | AuraPass Name |
|-------------|---------------|
| View visitor records | Audience Insights |
| Room online list on top | Room Spotlight |
| Send pictures in room | Rich Media Chat |
| Profile background | Dynamic Covers / Cover Rotation |
| SVIP Gift | Monthly Aura Crate |
| EXP*120% | EXP Boost (tiered) |
| Hide visit records | Ghost Mode |
| Customized theme | Custom Themes |
| Golden name | Aurora Nameplate |
| Hide Online Status | Incognito Status |
| 6-digit Special ID | Short ID |
| 5-digit Special ID | Ultra Short ID |
| Avoid disturbing | Do Not Disturb (DND) |
| Can't be kicked | Kick Shield |
| Dynamic Avatar (GIF) | Animated Avatar |
| Mic Wave | Aura Wave |
| Vehicle | Entry Vehicle |

---

## Tier Details

### VIP1 — "Comet"
- **Daily Multiplier:** 1.20x
- **EXP Boost:** 1.05x
- **Perks:** Audience Insights (50, 7-day), Room Spotlight, Dynamic Cover (1), Monthly Aura Crate I
- **Cosmetics:** Crown I, Medal I, Frame I, Bubble I, Entry Trail I

### VIP2 — "Nebula"
- **Daily Multiplier:** 1.30x
- **EXP Boost:** 1.08x
- **Perks:** Rich Media Chat, Cover Rotation (5), Ghost 10m/day, Insights+ (100), Crate II
- **Cosmetics:** Crown II, Medal II, Frame II, Bubble II, Entry Trail II

### VIP3 — "Aurora"
- **Daily Multiplier:** 1.40x
- **EXP Boost:** 1.10x
- **Perks:** Priority Mic Queue (1 jump/session), Insights Pro (200 + CSV export), Custom Themes, Aurora Nameplate, Short ID request, Crate III
- **Cosmetics:** Crown III, Medal III, Frame III, Bubble III, Entry Trail III, Entry Anthem

### VIP4 — "Eclipse"
- **Daily Multiplier:** 1.60x
- **EXP Boost:** 1.15x
- **New:** Super Mic eligibility
- **Perks:** Spotlight Pro, Ghost 20m/day, Rich Media Persist, Video Covers, Short ID lock window, Crate IV, Seat Aura I
- **Cosmetics:** Crown IV, Medal IV, Frame IV, Bubble IV, Entry Trail IV

### VIP5 — "Solar Flare"
- **Daily Multiplier:** 1.80x
- **EXP Boost:** 1.18x
- **Perks:** DND, Kick Shield, Ghost 25m/day, Nameplate+, Custom Themes+, Cover Rotation 8, Crate V, Seat Aura II
- **Cosmetics:** Crown V, Medal V, Frame V, Bubble V, Entry Trail V

### VIP6 — "Starlight"
- **Daily Multiplier:** 2.00x
- **EXP Boost:** 1.22x
- **Perks:** Priority Join Lane, Ghost 30m/day, Spotlight Elite, Animated Avatar, Chat Mute Guard, DND & Kick Shield persist, Custom Themes++ & Cover Rotation 10, Insights 90-day, Crate VI
- **Cosmetics:** Crown VI, Medal VI, Frame VI, Bubble VI, Entry Trail VI, Seat Aura III

### VIP7 — "Supernova"
- **Daily Multiplier:** 2.20x
- **EXP Boost:** 1.26x
- **Perks:** VIP Room Border, Ultra Short ID (5-digit), Priority Appeal Token ×1/term, Chat Mute Guard+, Ghost 40m/day, Incognito 30m/day, Animated Gradient Nameplate, Insights 180-day, Crate VII
- **Cosmetics:** Crown VII, Medal VII, Frame VII, Bubble VII, Entry Trail VII, VIP Room Border

### VIP8 — "Quasar"
- **Daily Multiplier:** 2.50x
- **EXP Boost:** 1.30x
- **Identity/cosmetics:** Aura Wave, Entry Vehicle, Mic Skin, VIP Room Border (upgraded)
- **Perks:** Ghost 50m/day, Incognito 45m/day, Ultra Short ID guaranteed, Priority Appeal Tokens ×3/term, Dual Entry Trail, Insights 365-day, Crate VIII
- **Cosmetics:** Crown VIII, Medal VIII, Frame VIII, Bubble VIII, Entry Trail VIII, Aura Wave, Entry Vehicle, Mic Skin, VIP Room Border+

### VIP9 — "Aurora Prime"
- **Daily Multiplier:** 2.80x
- **EXP Boost:** 1.30x
- **Perks:** Spotlight Elite+, Ghost 45m/day, Priority Mic Queue×2, Entry Anthem+, Dual Trail Pro, Crate IX, Insights 18-month archive
- **Cosmetics:** Crown IX (animated), Medal IX, Frame IX, Bubble IX, Entry Trail IX

### VIP10 — "Eclipse Prime"
- **Daily Multiplier:** 3.00x
- **EXP Boost:** 1.40x
- **Perks:** Spotlight Champion, Ghost 60m/day, Co-host Express, VIP Lounge Pass, Exclusive Seasonal Cosmetic/month, Crate X, Insights 24-month archive
- **Cosmetics:** Crown X (signature animated), Medal X, Frame X+, Bubble X+, Streaked Comet Entry Trail, Profile Backdrop "Eclipse"

---

## Safety Rails (All Tiers)

- **Kick Shield** never blocks room owner or platform enforcement
- **Chat Mute Guard** restricts low-privilege mod mutes; owner/admin-lead can still apply normal moderation
- **Ghost/Incognito** never hide actions from moderation logs

---

## Store & Pricing

- **Durations:** 7 or 30 days (CMS configurable)
- **Upgrade:** Pay difference; CMS sets proration vs reset
- **Grace:** Default 0h; optional 24h

---

## VIP-Linked Programs (Permanent)

- **VIP Recharge Bonus:** Immediate bonus coins by tier — see [vip-recharge-bonus.md](./vip-recharge-bonus.md)
- **AuraPass Spin:** Tickets → VIP Score wheels — see [aurapass-spin.md](./aurapass-spin.md)

---

## Related Documentation

- [VIP Systems (Daily Multiplier)](./vip-systems.md)
- [VIP Recharge Bonus](./vip-recharge-bonus.md)
- [AuraPass Spin](./aurapass-spin.md)
- [Daily Rewards](./daily-rewards.md)
- [Product Specification](../../README.md)
