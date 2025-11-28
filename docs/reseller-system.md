# Reseller System

Comprehensive documentation for the Reseller (Seller) system in Aura Voice Chat, enabling authorized users to purchase and resell coins.

## Overview

The Reseller System allows verified users to become official coin sellers, purchasing coins at wholesale rates and reselling to other users for profit. Resellers receive special badges, a dedicated seller panel, and volume-based bonuses.

---

## Becoming a Reseller

### Requirements

| Requirement | Details |
|-------------|---------|
| Account Age | 30+ days |
| Level | 20+ |
| KYC | Full verification completed |
| Seller Fee | Offline payment to platform |
| Agreement | Sign reseller terms |

### Seller Tag Pricing

| Tier | Duration | Fee (Offline) | Wholesale Discount |
|------|----------|---------------|-------------------|
| Bronze Seller | 30 days | $50 | 10% off coins |
| Silver Seller | 90 days | $120 | 15% off coins |
| Gold Seller | 180 days | $200 | 20% off coins |
| Platinum Seller | 365 days | $350 | 25% off coins |
| Diamond Seller | Permanent | $500 | 30% off coins |

### Activation Process

1. Contact platform support/owner
2. Complete offline payment
3. Provide account ID
4. Sign reseller agreement
5. Seller tag activated
6. Access to Seller Panel granted

---

## Seller Profile Badge

### Badge Display

- Special "Seller" badge on profile
- Badge tier visible (Bronze/Silver/Gold/Platinum/Diamond)
- Seller verification checkmark
- Listed in app's Reseller Directory

### Profile Visibility

| Element | Public | Details |
|---------|--------|---------|
| Seller Badge | Yes | Tier and status |
| Contact Info | Optional | User-controlled |
| Sales Stats | No | Private to seller |
| Rating | Yes | Buyer reviews |

---

## Seller Panel

### Access

- Only visible to users with active Seller tag
- Access via: **Me** → **Seller Panel**

### Panel Features

#### 1. Coin Inventory

| Display | Description |
|---------|-------------|
| Available Coins | Current coin stock |
| Purchase History | Wholesale purchases |
| Pending Orders | Buyer requests |
| Sold Today | Daily sales volume |

#### 2. Purchase Coins (Wholesale)

Buy coins from platform at discounted rates:

| Package | Regular Price | Bronze | Silver | Gold | Platinum | Diamond |
|---------|---------------|--------|--------|------|----------|---------|
| 1M Coins | $10 | $9 | $8.50 | $8 | $7.50 | $7 |
| 5M Coins | $50 | $45 | $42.50 | $40 | $37.50 | $35 |
| 10M Coins | $100 | $90 | $85 | $80 | $75 | $70 |
| 50M Coins | $500 | $450 | $425 | $400 | $375 | $350 |
| 100M Coins | $1000 | $900 | $850 | $800 | $750 | $700 |

#### 3. Sell to Users

Process for selling coins:

1. Buyer contacts seller (in-app chat)
2. Agree on price and payment method
3. Seller creates sale order in panel
4. Buyer confirms and pays (external)
5. Seller confirms payment received
6. Coins transferred to buyer automatically
7. Transaction recorded

#### 4. Sales Dashboard

| Metric | Description |
|--------|-------------|
| Total Sales | Lifetime coins sold |
| Monthly Sales | Current month volume |
| Revenue | Estimated revenue |
| Active Buyers | Repeat customers |
| Pending Orders | Awaiting completion |

---

## Seller Bonuses

### Volume Bonuses

Earn bonus coins based on monthly sales volume:

| Monthly Sales | Bonus Rate | Example (10M sold) |
|---------------|------------|-------------------|
| 1M - 5M | 2% | 200K bonus |
| 5M - 10M | 3% | 300K bonus |
| 10M - 25M | 4% | 400K bonus |
| 25M - 50M | 5% | 500K bonus |
| 50M - 100M | 6% | 600K bonus |
| 100M+ | 8% | 800K bonus |

### Streak Bonuses

Consistent selling rewards:

| Streak | Bonus |
|--------|-------|
| 7-day active sales | 100K coins |
| 30-day active sales | 500K coins |
| 90-day active sales | 2M coins |

### Referral Bonuses

Refer new sellers:

| Referred Seller Tier | Bonus |
|---------------------|-------|
| Bronze | 500K coins |
| Silver | 1M coins |
| Gold | 2M coins |
| Platinum | 3M coins |
| Diamond | 5M coins |

---

## Buyer Payment Methods

Sellers can receive payments via:

### Pakistan
- EasyPaisa
- JazzCash
- Bank Transfer

### India
- UPI (Google Pay, PhonePe, Paytm)
- Paytm Wallet
- Bank Transfer (NEFT/IMPS)

### International
- PayPal
- Bank Transfer
- Crypto (optional, seller discretion)

### Payment Setup

Sellers configure their accepted payment methods:

```json
{
  "paymentMethods": [
    {
      "type": "easypaisa",
      "accountName": "Seller Name",
      "accountNumber": "03XX-XXXXXXX",
      "enabled": true
    },
    {
      "type": "upi",
      "upiId": "seller@upi",
      "enabled": true
    },
    {
      "type": "bank",
      "bankName": "Bank Name",
      "accountNumber": "XXXXXXXX",
      "ifsc": "BANK0001234",
      "enabled": true
    }
  ]
}
```

---

## Reseller Directory

### In-App Wallet Section

Users find resellers via: **Wallet** → **Resellers**

### Directory Display

| Column | Description |
|--------|-------------|
| Avatar | Seller profile picture |
| Name | Username + Seller badge |
| Tier | Bronze/Silver/Gold/Platinum/Diamond |
| Rating | Average buyer rating (1-5 stars) |
| Online Status | Currently available |
| Payment Methods | Accepted payment icons |

### Filtering & Sorting

| Filter | Options |
|--------|---------|
| Tier | All, Bronze, Silver, Gold, Platinum, Diamond |
| Payment Method | EasyPaisa, JazzCash, UPI, PayPal, etc. |
| Online Only | Show only online sellers |
| Rating | Minimum rating filter |

### Sort Options
- Rating (highest first)
- Sales volume (most active)
- Online status
- Tier (highest first)

---

## Transaction Flow

### Complete Sale Process

```
1. Buyer browses Reseller Directory
2. Buyer contacts seller via chat
3. Negotiate price and payment method
4. Seller creates order in Seller Panel:
   - Buyer ID
   - Coin amount
   - Agreed price
   - Payment method
5. System generates order ID
6. Buyer makes external payment to seller
7. Buyer confirms payment in app (uploads proof optional)
8. Seller confirms payment received
9. System transfers coins from seller inventory to buyer
10. Transaction marked complete
11. Both parties can rate each other
```

### Transaction Limits

| Limit | Value |
|-------|-------|
| Minimum sale | 100K coins |
| Maximum per transaction | 100M coins |
| Daily limit (new buyers) | 5M coins |
| Daily limit (verified buyers) | 50M coins |

---

## Dispute Resolution

### Dispute Triggers

- Buyer claims payment made, seller denies
- Coins not received after confirmation
- Wrong amount transferred
- Scam reports

### Resolution Process

1. Either party opens dispute
2. Transaction frozen (if pending)
3. Support reviews evidence
4. Resolution within 24-48 hours
5. Coins/funds returned to appropriate party
6. Seller rating affected if at fault

### Evidence Requirements

- Payment screenshots
- Chat history
- Transaction IDs
- Bank/wallet statements

---

## Seller Rating System

### Rating Criteria

| Factor | Weight |
|--------|--------|
| Transaction completion | 40% |
| Response time | 20% |
| Buyer feedback | 30% |
| Dispute history | 10% |

### Rating Levels

| Rating | Status |
|--------|--------|
| 4.5 - 5.0 | Excellent Seller |
| 4.0 - 4.4 | Good Seller |
| 3.5 - 3.9 | Average Seller |
| 3.0 - 3.4 | Below Average |
| < 3.0 | At Risk |

### Consequences

| Rating | Action |
|--------|--------|
| < 3.0 for 30 days | Warning issued |
| < 2.5 for 30 days | Seller status suspended |
| Multiple scam reports | Permanent ban |

---

## Coin Winning Strategy

### How Sellers Profit

1. **Purchase wholesale** - Buy coins at discount
2. **Win in games** - Use coins in games to multiply
3. **Sell at market rate** - Sell to users at competitive prices
4. **Earn bonuses** - Volume and streak bonuses

### Example Profit Calculation

| Step | Amount | Value |
|------|--------|-------|
| Buy 10M coins (Gold tier) | 10M | -$80 |
| Win in games (2x) | 20M | $0 |
| Sell 20M to users | 0 | +$180 |
| Volume bonus (4%) | 800K | +$7.20 |
| **Net Profit** | 800K | **$107.20** |

---

## API Endpoints

```
# Seller Status
GET /seller/status
GET /seller/profile
PUT /seller/profile

# Seller Panel
GET /seller/inventory
GET /seller/dashboard
GET /seller/orders
POST /seller/orders/create
PUT /seller/orders/{orderId}/confirm
PUT /seller/orders/{orderId}/cancel

# Wholesale Purchase
GET /seller/packages
POST /seller/purchase

# Reseller Directory (Buyers)
GET /resellers
GET /resellers/{sellerId}
POST /resellers/{sellerId}/contact

# Transactions
GET /seller/transactions
GET /seller/transactions/{txnId}
POST /seller/transactions/{txnId}/dispute

# Bonuses
GET /seller/bonuses
GET /seller/bonuses/history

# Ratings
GET /seller/ratings
POST /seller/transactions/{txnId}/rate
```

---

## Data Model

### Seller Profile

```json
{
  "seller": {
    "userId": "user_123",
    "tier": "gold",
    "activatedAt": "2025-01-01T00:00:00Z",
    "expiresAt": "2025-07-01T00:00:00Z",
    "wholesaleDiscount": 0.20,
    "rating": 4.7,
    "totalReviews": 156,
    "totalSales": 250000000,
    "monthlySales": 45000000,
    "coinInventory": 15000000,
    "paymentMethods": ["easypaisa", "jazzcash", "bank"],
    "status": "active"
  }
}
```

### Sale Order

```json
{
  "order": {
    "id": "order_abc123",
    "sellerId": "user_123",
    "buyerId": "user_456",
    "coinAmount": 5000000,
    "agreedPrice": 45.00,
    "currency": "USD",
    "paymentMethod": "easypaisa",
    "status": "pending_payment",
    "createdAt": "2025-11-28T10:00:00Z",
    "expiresAt": "2025-11-28T22:00:00Z"
  }
}
```

---

## Telemetry Events

| Event | Properties |
|-------|------------|
| `seller_panel_view` | sellerId, tier |
| `seller_package_purchase` | amount, discount, cost |
| `seller_order_create` | orderId, coinAmount, price |
| `seller_order_complete` | orderId, buyerId, coinAmount |
| `seller_bonus_claim` | bonusType, amount |
| `reseller_directory_view` | filters |
| `reseller_contact` | sellerId |

---

## Related Documentation

- [Earning System](./earning-system.md)
- [Wallet](./features/wallet.md)
- [Owner CMS](./owner-cms.md)
- [Gifts & Records](./gifts-and-records.md)
