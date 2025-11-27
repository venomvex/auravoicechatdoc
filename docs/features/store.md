# Store & Items

The in-app store for purchasing cosmetics and consumables.

## Overview

Users spend Coins to purchase items that enhance their profile and room presence.

---

## Categories

| Category       | Description                              | Equip Limit |
|----------------|------------------------------------------|-------------|
| Frames         | Visual borders around profile/avatar     | 1 active    |
| Entry Effects  | Animations when entering rooms           | 1 active    |
| Mic Skins      | Custom microphone visual                 | 1 active    |
| Seat Hearts    | Heart effects when seated                | 1 active    |
| Consumables    | Single-use items                         | Stack       |

---

## Rarity Tiers

Items are classified by rarity:

| Tier      | Availability        | Price Range (indicative) |
|-----------|---------------------|---------------------------|
| Common    | Always available    | 5,000 – 50,000 coins     |
| Rare      | Rotates weekly      | 50,000 – 200,000 coins   |
| Epic      | Limited events      | 200,000 – 1,000,000 coins|
| Legendary | Ultra-rare/seasonal | 1,000,000+ coins         |

*Final pricing to be determined by product team.*

---

## Purchase Flow

1. Browse store by category
2. Select item to view details
3. Confirm purchase
4. Coins deducted
5. Item added to inventory
6. Toast: "Purchase successful!"

---

## Inventory Management

### Equipping Items
- Access: **Me → My Items**
- One item per category can be active
- Consumables stack in inventory

### Item Duration
- Some items are permanent
- Others are time-limited (7 days, 30 days, etc.)
- Expiring items show countdown

---

## VIP Store

VIP users (VIP4+) have access to exclusive items:
- Separate "VIP" category or filter
- Items not available to non-VIP users
- Special designs and effects

See [VIP Systems](./vip-systems.md) for details.

---

## Flash Sales (Planned)

- Discount: 10–30% off
- Duration: 24 hours
- Feature flag controlled

---

## Expiry Notifications

For time-limited items:
- Notification sent before expiry (24h or 72h lead time)
- User can repurchase to extend

---

## Refund Policy

*To be defined.*

Current placeholder: No refunds (items are final sale).

---

## API Endpoints

### Get Store Catalog

```
GET /store/catalog?category=frames
```

### Purchase Item

```
POST /store/purchase
```

Request:
```json
{
  "itemId": "frame_neon_001",
  "quantity": 1
}
```

### Get Inventory

```
GET /profile/inventory
```

### Equip Item

```
POST /profile/inventory/equip
```

Request:
```json
{
  "itemId": "frame_neon_001"
}
```

---

## Telemetry Events

| Event              | Properties                        |
|--------------------|-----------------------------------|
| `store_view`       | category                          |
| `store_purchase`   | itemId, price, rarity             |
| `item_equip`       | itemId, category                  |
| `item_expire`      | itemId, hoursOwned                |

---

## Related Documentation

- [Wallet & Currency](./wallet.md)
- [VIP Systems](./vip-systems.md)
- [Medals System](./medals.md)
- [Product Specification](../README.md)
