# AuraPass Spin — Persistent Program

## Purpose

Always-on "spin" program to earn AuraPass (VIP) Score to help keep/upgrade VIP tiers.

---

## Tickets

### Earning Tickets
- **+1 Spin Ticket** per 500,000 coins recharged (CMS-adjustable)
- Tickets may also be sold in bundles (CMS)

---

## Wheels

### Spark Wheel
- Lower ticket cost
- Smaller Score yields
- 10x spin = +5% Score bonus

### Flare Wheel
- Higher ticket cost
- Larger Score yields
- 10x spin = +8% Score bonus

### Guarantees
- Each spin guarantees VIP Score (no blanks)

---

## UI Entry Points

- Room Program Slider → `program://aurapass-spin`
- VIP Center "Spin" module with Spark/Flare tabs
- Records and optional Odds display

---

## CMS Controls (App Owner)

- Ticket earn rule
- Wheel segments and probabilities
- Caps and limits
- Targeting and visibility

---

## APIs

| Endpoint | Purpose |
|----------|---------|
| GET /programs/aurapass-spin/status | Current spin status and tickets |
| POST /programs/aurapass-spin/spin | Execute spin { wheel, count: 1\|10 } |
| GET /programs/aurapass-spin/records | Spin history with cursor pagination |

---

## Fairness & Security

- Secure PRNG for random results
- Signed results (resultId) for verification
- Idempotent ticket consumption

---

## Related Documentation

- [AuraPass (Full VIP)](./aurapass.md)
- [VIP Systems](./vip-systems.md)
- [VIP Recharge Bonus](./vip-recharge-bonus.md)
- [Product Specification](../../README.md)
