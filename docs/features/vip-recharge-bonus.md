# VIP Recharge Bonus — Persistent Program

## Purpose

Always-on program: every recharge grants extra coins according to the user's current AuraPass (VIP) tier.

---

## How It Works

1. At recharge settlement, apply the tier's bonus percentage to the recharge amount
2. Credit bonus coins immediately to the user's balance

---

## Bonus Ladder

| VIP Tier | Bonus % |
|----------|---------|
| VIP1 | 2% |
| VIP2 | 5% |
| VIP3 | 7% |
| VIP4 | 9% |
| VIP5 | 12% |
| VIP6 | 15% |
| VIP7 | 20% |
| VIP8 | 25% |
| VIP9 | 28% |
| VIP10 | 30% |

---

## Economy Caps (Recommended)

| Cap Type | Value |
|----------|-------|
| Per Recharge | 1,000,000 coins |
| Per Day | 5,000,000 coins |
| Per User (30 days) | 30,000,000 coins |
| Global Budget | 0 (unlimited) or configurable |

---

## Eligibility

- The tier applied is the tier at payment authorization time
- CMS editable by App Owner

---

## UI Entry Points

- Room Program Slider → `program://vip-recharge-bonus`
- VIP Center section: current tier rate, ladder table, records

---

## APIs

| Endpoint | Purpose |
|----------|---------|
| GET /programs/vip-recharge-bonus/status | Current bonus rate and caps |
| GET /programs/vip-recharge-bonus/records | Bonus history with cursor pagination |

---

## Anti-Abuse

- Server authoritative calculation
- Idempotent ledger keyed by (txnId + userId)
- Chargebacks revoke proportional bonus
- Velocity checks for suspicious activity

---

## Related Documentation

- [AuraPass (Full VIP)](./aurapass.md)
- [VIP Systems](./vip-systems.md)
- [AuraPass Spin](./aurapass-spin.md)
- [Product Specification](../../README.md)
