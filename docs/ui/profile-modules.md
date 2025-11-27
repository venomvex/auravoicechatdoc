# Profile Modules — Detailed Specification

Consolidates cross-module behavior referenced in Me, Settings, Wallet, and Level systems.

> **Theme:** Original Purple → White gradient preserved. No cosmic/enhanced UI changes.

---

## 1. Wallet

### Balances
- **Coins** (spendable)
- **Diamonds** (received via gifts)

### Exchange
- Input: Diamonds amount
- Output: `floor(Diamonds * 0.30)` Coins
- Example conversions:
  - 1,000 → 300
  - 10,000 → 3,000
  - 1,000,000 → 300,000

### UI
- Two balance cards with icons
- Exchange button → dialog (enter amount or max)
- Validation: Must own sufficient Diamonds; show preview

### Telemetry
- wallet_view
- wallet_exchange_attempt(amount)
- wallet_exchange_success(amount, coinsOut)

---

## 2. Medal

- Tracks cumulative login days
- **Milestones:** 30 / 60 / 90 / 180 / 365 (extendable)
- Each medal card: Icon, milestone number, status (Locked / Achieved / Claimed)
- Claim action for newly achieved milestone

### Telemetry
- medal_claim(milestone)

---

## 3. Level (User Progression)

### EXP Sources
- Sending coins
- Participating in rooms
- CP interactions

### EXP Formula
- +1 EXP per coin sent (base)
- Modifiers: VIP, events

### UI
- Current Level
- Progress bar (currentExp / expToNext)
- Rewards per level: Frames, mic skins, seat hearts, coin packets

### Telemetry
- level_up(newLevel)

---

## 4. CP / Friend

### Formation
- One-time fee (e.g., 3M coins) to create CP
- Confirmation dialog; both users must accept

### Progression
- Mutual coin sending increments shared CP EXP

### Rewards Table

| Threshold (Mutual Coins) | Reward |
|--------------------------|--------|
| 5M | 500k gifts in Baggage |
| 10M | 1M gifts |
| ... | ... |

### Visual
- CP Frame
- Couple seat heart effect upgrades
- Status page: Partner name, CP level, next reward

### Telemetry
- cp_form
- cp_reward_claim(threshold)

---

## 5. Family

- Join/Leave requests
- Family perks: Shared events, banner slot
- "Join Now" opens discovery list

### Data
- familyId, role (Member/Admin), joinDate

---

## 6. Store

### Categories
- Frames
- Entry Effects
- Mic Skins
- Seat Hearts
- Consumables

### Sorting
- Popularity
- New
- Price ascending/descending

### Purchase Flow
- Confirm → deduct Coins → add to My Items inventory

### Telemetry
- store_view(category)
- store_purchase(itemId, price)

---

## 7. My Items

### Tabs
- Frames
- Entry Effects
- Mic Skins
- Seat Hearts
- Consumables

### Each Item
- Icon, name, rarity tag, equipped state (toggle)

### Equip Flow
- Single active per category (except consumables stack)

### Telemetry
- items_equip(itemId)
- items_consume(itemId)

---

## 8. Referral Integration

- Badge "Earn Coins" visible on Invite Friends when unclaimed rewards exist
- Notification integration: New referral earnings adds badge highlight

---

## 9. VIP (SVIP)

### Benefits
- Extra daily reward multiplier
- Increased EXP gain
- Exclusive store skins
- Priority room join

### UI
- VIP status card: Tier, expiry date, renew button

### Telemetry
- vip_purchase(tier)

---

## 10. Language

- Language picker
- Persist selected locale
- Fallback to English if translation missing

---

## 11. Privacy Settings

| Setting | Options |
|---------|---------|
| Show online status | Toggle |
| Allow friend requests | Everyone / Followers only / Nobody |
| Profile visibility | Public / Private |
| Data usage for recommendations | Opt-in |

---

## 12. Error & Edge Cases

| Scenario | Handling |
|----------|----------|
| Exchange rate service down | Disable exchange; message "Exchange unavailable" |
| CP partner offline during formation | Show "Partner must be online" |
| Store purchase insufficient balance | Disable confirm; show "Not enough coins" |
| VIP expired mid-session | Refresh entitlements; adjust multipliers |

---

## Related Documentation

- [Me Screen](./me-screen.md)
- [Wallet](../features/wallet.md)
- [Medals](../features/medals.md)
- [VIP Systems](../features/vip-systems.md)
- [CP Partnership](../features/cp.md)
