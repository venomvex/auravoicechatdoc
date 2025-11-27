# Gifts & Baggage System

The gift economy and baggage storage system in Aura Voice Chat.

## Overview

Gifts are a core monetization and engagement feature:
- Users send gifts using Coins
- Recipients receive Diamonds
- Special gifts come with animations

---

## Gift Sending

### How It Works
1. Select recipient(s) in room
2. Choose gift from catalog
3. Select quantity
4. Confirm and send
5. Animation plays in room
6. Recipient receives Diamonds (1:1 coin value)

### Multi-Send
- Select multiple recipients
- Choose quantity per recipient
- Batch send for efficiency
- Anti-spam cooldown between sends (2-5 seconds)

---

## Gift Catalog

### Source
- Managed via Owner Panel / CMS
- Regional variants available
- Base catalog + region-specific additions

### Price Range
- Minimum: 50 coins
- Maximum: 200,000,000 coins (200M)

### Display
- Grid view with thumbnails
- K/M formatting for prices
- Rarity indicators

---

## Custom Animations

Select gifts include special animations:
- Full-screen effects
- Sound effects (optional)
- Concurrency cap: 5-10 simultaneous animations (configurable)

---

## Baggage System

### What is Baggage?
Storage for special gifts that can be sent for free:
- Event probability gifts
- CP rewards
- Friend/event rewards

### Baggage Rules
- Items are **not convertible** to coins/diamonds
- Can be sent as gifts at **zero coin cost**
- Recipient receives full diamond value of the gift
- Daily cap on free baggage sends (optional)

### Baggage UI Options
1. Grid with send buttons
2. List with expandable details
3. Tabs by source (Event / CP / Friend)

---

## Regional Gifts

### Region Precedence Options
1. **Room owner region** drives catalog
2. **Viewer region** overrides
3. **Intersection** (show shared + global)

*Final decision pending.*

---

## Large Gift Warning

For high-value gifts:
- Confirmation dialog appears
- Threshold configurable (e.g., 1M, 10M, 50M coins)
- Prevents accidental large sends

---

## API Endpoints

### Get Gift Catalog

```
GET /gifts/catalog?region=US
```

### Send Gift

```
POST /gifts/send
```

Request:
```json
{
  "giftId": "gift_rose_001",
  "recipients": ["user_123", "user_456"],
  "quantity": 1
}
```

### Get Baggage

```
GET /profile/baggage
```

### Send Baggage Gift

```
POST /gifts/send/baggage
```

Request:
```json
{
  "baggageItemId": "baggage_001",
  "recipient": "user_123"
}
```

---

## Telemetry Events

| Event            | Properties                           |
|------------------|--------------------------------------|
| `gift_send`      | giftId, quantity, totalCoins, roomId |
| `gift_receive`   | giftId, diamonds, senderId           |
| `baggage_send`   | baggageItemId, recipientId           |

---

## Related Documentation

- [Wallet & Currency](./wallet.md)
- [CP Partnership](./cp.md)
- [Store & Items](./store.md)
- [Product Specification](../README.md)
