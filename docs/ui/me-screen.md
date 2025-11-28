# Me Screen — UI/UX Specification

Central personal hub where the user views identity, social metrics, progression modules, virtual economy balances, referral programs, and navigates to account/settings.

> **Theme:** Original Purple → White gradient preserved. No cosmic/enhanced UI changes.

---

## 1. Screen Purpose

Profile overview, personal status modules, wallet, referral entry, progression features (Medal, Level, CP), inventory, and system utilities (Language, Feedback, Settings).

---

## 2. Layout Structure

1. Header block (profile identity)
2. Metrics row (Followers, Following, Visitors)
3. Primary feature list group (Wallet → My Items)
4. Secondary utilities group (Language, Feedback, Settings)
5. Bottom navigation bar (Home, Message with badge, Me active)

### Spacing
- Edge horizontal padding: 16dp
- Vertical spacing between list groups: 24dp
- List item height: 56dp (touch target >= 48dp)

---

## 3. Header Block

### Elements
- Profile avatar (circular)
- Display name (single line, ellipsis if overflow)
- User ID with copy action (id:XXXXXXXX tap → copy toast "ID copied")
- Background: Gradient top (Aura theme: #c9a8f1 → #ffffff)

### Typography
- Name: 18–20sp Medium
- ID: 14sp Regular
- Metrics values: 16sp Medium; labels 12–13sp Regular

### Interactions
- Tap avatar: Open profile edit screen
- Tap ID or copy icon: Copy clipboard
- Long-press name (optional): Edit name if feature allowed

---

## 4. Metrics Row

Items (3 equally spaced columns):
- **Followers**
- **Following**
- **Visitors**

### Style
- Value top (bold)
- Label beneath
- Tap each metric to open respective list

---

## 5. Feature List Group

| Order | Item | Description |
|-------|------|-------------|
| 1 | Wallet | Coins, Diamonds, Exchange |
| 2 | Invite Friends | Badge "Earn Coins" pill |
| 3 | Medal | Cumulative login milestones |
| 4 | SVIP | Status/prompt "Join now" or tier |
| 5 | Level | Badge showing current level |
| 6 | CP / Friend | CP creation and status |
| 7 | Family | Prompt "Join Now" if not in a family |
| 8 | Store | Frames, entry effects, mic skins |
| 9 | My Items | Inventory of owned items |

### Item Anatomy
- Leading icon (vector)
- Title text
- Optional trailing status pill
- Trailing chevron (navigation)

---

## 6. Utilities Group

| Item | Action |
|------|--------|
| Language | Opens language picker |
| Feedback | Opens feedback form |
| Settings | Navigates to Settings screen |

---

## 7. Visual Styles

### List Items
- Background: White (#FFFFFF) with radius 16dp for grouped block
- Separator lines: 1dp #E6E6F2 between items
- Icons: 24–28dp; consistent baseline alignment

### Status Pills
- Height: 24dp
- Padding: 8–12dp horizontal
- Corner radius: 12dp
- Colors:
  - Earn Coins: Soft yellow background (#FFF2C7) / text #C58A00
  - Level badge: Bronze/brown gradient
  - Join now: Accent gradient or outlined style

---

## 8. State Variations

| State | Display |
|-------|---------|
| SVIP subscribed | Pill shows "VIP" or tier name |
| Level up event | Temporary glow around Level item |
| New items | Badge dot on My Items |
| Language changed | Reflect new locale code |
| Family joined | Pill changes to family name |

---

## 9. Data Model

```json
{
  "userId": "12096412",
  "displayName": "Example User",
  "avatarUrl": "https://...",
  "followers": 1,
  "following": 2,
  "visitors": 2,
  "vipTier": null,
  "level": 2,
  "cpStatus": null,
  "family": null,
  "hasNewItems": true,
  "language": "en",
  "medalProgress": { "loginDays": 42, "nextMedalAt": 60 }
}
```

---

## 10. Telemetry

| Event | Description |
|-------|-------------|
| me_view | Screen opened |
| me_wallet_open | Wallet tapped |
| me_invite_open | Invite Friends tapped |
| me_vip_open | SVIP tapped |
| me_level_open | Level tapped |
| me_cp_open | CP tapped |
| me_family_open | Family tapped |
| me_store_open | Store tapped |
| me_items_open | My Items tapped |
| me_id_copied | ID copied to clipboard |

---

## 11. Accessibility

- All list items: contentDescription with item name and status
- Copy ID: Announce "User ID copied"
- Color contrasts: Pill text ≥ 4.5:1 on background
- Focus order: Header → metrics → list items sequential

---

## Related Documentation

- [Settings Screen](./settings-screen.md)
- [Wallet](../features/wallet.md)
- [Medals](../features/medals.md)
- [VIP Systems](../features/vip-systems.md)
- [AuraPass](../features/aurapass.md)
- [Referrals](../features/referrals.md)
- [Store](../features/store.md)
- [Product Specification](../../README.md)
