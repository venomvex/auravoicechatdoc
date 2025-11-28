# Earning System

Comprehensive documentation for the user earning system in Aura Voice Chat, enabling users to earn real money through activity targets with multi-region payout support.

## Overview

The Earning System allows users to earn real money by completing activity targets within the app. Users can withdraw earnings to various payment methods including banks, mobile wallets, and digital payment platforms after a clearance period.

---

## Earning Mechanics

### How It Works

```
User Activity → Target Progress → Target Complete → Earnings Credited
                                                          ↓
                                              Clearance Period (5-7 days)
                                                          ↓
                                              Withdrawal Available
                                                          ↓
                                              Payout to Payment Method
```

### Key Principles

1. **Activity-Based**: Earnings are tied to real app engagement
2. **Target-Driven**: Clear goals with defined rewards
3. **Time-Bound**: Targets must be completed within specified periods
4. **Clearance Period**: 5-7 days before withdrawal to prevent fraud
5. **Multi-Currency**: USD base with regional currency support

---

## Earning Targets

### Gift Sending Targets

Earn by sending gifts in the app:

| Target | Time Period | Coins Required | Earning |
|--------|-------------|----------------|---------|
| Bronze Gift | 7 days | 1,000,000 | $0.50 |
| Silver Gift | 7 days | 5,000,000 | $2.50 |
| Gold Gift | 10 days | 10,000,000 | $5.00 |
| Platinum Gift | 10 days | 25,000,000 | $12.50 |
| Diamond Gift | 14 days | 50,000,000 | $25.00 |
| Elite Gift | 14 days | 100,000,000 | $50.00 |
| Master Gift | 30 days | 500,000,000 | $250.00 |
| Legend Gift | 30 days | 1,000,000,000 | $500.00 |

### Room Activity Targets

Earn by hosting and engaging in rooms:

| Target | Time Period | Requirement | Earning |
|--------|-------------|-------------|---------|
| Active Host | 7 days | Host 20 hours | $1.00 |
| Super Host | 14 days | Host 50 hours | $3.00 |
| Elite Host | 30 days | Host 100 hours | $7.50 |
| Room Star | 7 days | 1,000 unique visitors | $2.00 |
| Room Legend | 30 days | 10,000 unique visitors | $15.00 |

### Social Engagement Targets

Earn through social activities:

| Target | Time Period | Requirement | Earning |
|--------|-------------|-------------|---------|
| Connector | 7 days | Make 10 new friends | $0.25 |
| Social Star | 14 days | Make 50 new friends | $1.50 |
| Influencer | 30 days | Gain 500 followers | $5.00 |
| CP Champion | 30 days | Reach CP Level 5 | $10.00 |

### Daily Streak Targets

Earn for consistent daily activity:

| Target | Requirement | Earning |
|--------|-------------|---------|
| Weekly Streak | 7 consecutive days active | $0.25 |
| Bi-Weekly Streak | 14 consecutive days active | $0.75 |
| Monthly Streak | 30 consecutive days active | $2.00 |
| Quarterly Streak | 90 consecutive days active | $7.50 |

### Combined Targets (Premium)

Complete multiple activities for bonus earnings:

| Target | Time Period | Requirements | Earning |
|--------|-------------|--------------|---------|
| All-Rounder | 14 days | 5M gifts + 20 hours hosting + 10 friends | $5.00 |
| Power User | 30 days | 25M gifts + 50 hours hosting + 50 friends | $20.00 |
| Elite Member | 30 days | 100M gifts + 100 hours hosting + 100 friends | $75.00 |

---

## Earning Wallet

### Balance Types

| Type | Description |
|------|-------------|
| Pending | Earnings in clearance period |
| Available | Ready for withdrawal |
| Withdrawn | Successfully paid out |
| Expired | Unclaimed after 90 days |

### Earning History

Track all earning activities:
- Target name
- Completion date
- Amount earned
- Status (Pending/Cleared/Withdrawn)
- Clearance date

---

## Clearance Period

### Purpose

- Fraud prevention
- Chargeback protection
- Activity verification
- Compliance requirements

### Duration

| Transaction Type | Clearance Period |
|------------------|------------------|
| Gift Targets | 5 days |
| Room Targets | 5 days |
| Social Targets | 5 days |
| Combined Targets | 7 days |
| Large Earnings (>$50) | 7 days |
| First Withdrawal | 7 days |

### Clearance Rules

1. Clock starts when target is completed
2. Weekends and holidays count toward clearance
3. Clearance can be extended if suspicious activity detected
4. Failed verification resets clearance period

---

## Withdrawal System

### Minimum Withdrawal

| Region | Minimum Amount |
|--------|----------------|
| Global (USD) | $5.00 |
| Pakistan (PKR) | Rs. 500 |
| India (INR) | ₹200 |
| Other Regions | $5.00 equivalent |

### Maximum Withdrawal

| Period | Limit |
|--------|-------|
| Daily | $500 |
| Weekly | $2,000 |
| Monthly | $5,000 |

### Withdrawal Fees

| Method | Fee |
|--------|-----|
| Bank Transfer | 2% (min $1) |
| Mobile Wallet | 1.5% (min $0.50) |
| PayPal | 2.5% (min $1) |
| UPI | 1% (min ₹10) |

---

## Payment Methods

### Pakistan

| Method | Provider | Processing Time |
|--------|----------|-----------------|
| Bank Transfer | Local Banks | 2-3 business days |
| EasyPaisa | Telenor | Instant - 24 hours |
| JazzCash | Jazz | Instant - 24 hours |

**Setup Requirements:**
- EasyPaisa/JazzCash: Registered mobile number
- Bank: Account number, bank name, IBAN

### India

| Method | Provider | Processing Time |
|--------|----------|-----------------|
| Bank Transfer (NEFT/IMPS) | All Banks | 1-2 business days |
| UPI | Google Pay, PhonePe, Paytm | Instant - 4 hours |
| Paytm Wallet | Paytm | Instant - 4 hours |

**Setup Requirements:**
- UPI: UPI ID (e.g., name@upi)
- Bank: Account number, IFSC code, account holder name
- Paytm: Registered mobile number

### International

| Method | Provider | Processing Time |
|--------|----------|-----------------|
| PayPal | PayPal | 1-3 business days |
| Stripe | Bank Transfer | 3-5 business days |
| Wire Transfer | International Banks | 5-7 business days |

**Setup Requirements:**
- PayPal: Verified PayPal email
- Stripe: Bank account details
- Wire: SWIFT/BIC, account number, bank address

### Middle East

| Method | Provider | Processing Time |
|--------|----------|-----------------|
| Bank Transfer | Local Banks | 2-3 business days |
| Payoneer | Payoneer | 2-4 business days |

---

## KYC Verification

### Verification Levels

| Level | Requirement | Withdrawal Limit |
|-------|-------------|------------------|
| Basic | Phone verified | $50/month |
| Standard | ID document | $500/month |
| Premium | ID + Address proof | $5,000/month |
| Enterprise | Full KYC | Unlimited |

### Required Documents

**Standard Verification:**
- Government-issued ID (passport, national ID, driver's license)
- Selfie holding ID

**Premium Verification:**
- All Standard documents
- Address proof (utility bill, bank statement - last 3 months)

### Verification Process

1. Submit documents via app
2. Automated review (1-2 hours)
3. Manual review if needed (1-2 business days)
4. Approval notification
5. Increased limits activated

---

## Anti-Fraud Measures

### Detection Systems

| Type | Method |
|------|--------|
| Activity Pattern | Unusual gift sending patterns |
| Device Tracking | Multiple accounts per device |
| IP Monitoring | VPN/proxy usage |
| Velocity Checks | Too-fast target completion |
| Collusion Detection | Circular gift exchanges |

### Fraud Penalties

| Violation | Penalty |
|-----------|---------|
| First Offense | Earnings forfeited, warning |
| Second Offense | 30-day earning suspension |
| Third Offense | Permanent earning ban |
| Severe Fraud | Account termination + legal action |

### Fraud Prevention Rules

1. No self-gifting or alt-account gifting
2. Minimum 5 unique recipients per target
3. No automated or bot activity
4. Real engagement required (not just transactions)

---

## Tax Compliance

### User Responsibility

- Users are responsible for reporting earnings as income
- Platform provides annual earnings statement
- No tax withholding by platform (in most regions)

### Tax Documents

| Region | Document | Availability |
|--------|----------|--------------|
| USA | 1099-K (if >$600) | January |
| India | Form 26AS assistance | April |
| Others | Annual Statement | January |

---

## API Endpoints

```
# Earning Targets
GET /earnings/targets
GET /earnings/targets/active
GET /earnings/targets/{targetId}/progress

# Earning Wallet
GET /earnings/wallet
GET /earnings/history
GET /earnings/pending

# Withdrawals
POST /earnings/withdraw
GET /earnings/withdraw/methods
GET /earnings/withdraw/history
GET /earnings/withdraw/{withdrawalId}/status

# Payment Methods
GET /earnings/payment-methods
POST /earnings/payment-methods
PUT /earnings/payment-methods/{methodId}
DELETE /earnings/payment-methods/{methodId}

# Verification
GET /earnings/kyc/status
POST /earnings/kyc/submit
GET /earnings/kyc/limits
```

---

## Data Model

### Earning Target

```json
{
  "target": {
    "id": "target_gold_gift",
    "name": "Gold Gift Target",
    "description": "Send 10,000,000 coins in gifts within 10 days",
    "category": "gift_sending",
    "requirement": {
      "type": "coins_sent",
      "amount": 10000000,
      "minRecipients": 5
    },
    "timePeriodDays": 10,
    "earning": {
      "amount": 5.00,
      "currency": "USD"
    },
    "clearanceDays": 5
  }
}
```

### User Earning Progress

```json
{
  "progress": {
    "targetId": "target_gold_gift",
    "userId": "user_123",
    "startedAt": "2025-11-20T00:00:00Z",
    "expiresAt": "2025-11-30T00:00:00Z",
    "currentProgress": 7500000,
    "targetAmount": 10000000,
    "percentage": 75,
    "status": "in_progress"
  }
}
```

### Earning Wallet

```json
{
  "wallet": {
    "userId": "user_123",
    "balance": {
      "pending": 15.00,
      "available": 45.50,
      "totalEarned": 125.00,
      "totalWithdrawn": 64.50
    },
    "currency": "USD",
    "kycLevel": "standard",
    "withdrawalLimit": {
      "daily": 500,
      "remaining": 454.50
    }
  }
}
```

### Withdrawal Request

```json
{
  "withdrawal": {
    "id": "wd_abc123",
    "userId": "user_123",
    "amount": 45.50,
    "currency": "USD",
    "method": "easypaisa",
    "destination": {
      "type": "mobile_wallet",
      "provider": "easypaisa",
      "accountNumber": "03XX-XXXXXXX"
    },
    "fee": 0.68,
    "netAmount": 44.82,
    "status": "processing",
    "requestedAt": "2025-11-28T10:00:00Z",
    "estimatedArrival": "2025-11-29T10:00:00Z"
  }
}
```

---

## UI Flow

### Earning Dashboard

1. **Overview Card**
   - Available balance
   - Pending balance
   - Quick withdraw button

2. **Active Targets**
   - Progress bars for each active target
   - Time remaining
   - Estimated earnings

3. **Available Targets**
   - Browse and activate new targets
   - Filter by category

4. **Earning History**
   - Completed targets
   - Status tracking

### Withdrawal Flow

1. Tap "Withdraw"
2. Select/add payment method
3. Enter amount
4. Review fees and net amount
5. Confirm with PIN/biometric
6. Track status

---

## Telemetry Events

| Event | Properties |
|-------|------------|
| `earning_target_view` | targetId, category |
| `earning_target_start` | targetId, requirement |
| `earning_target_progress` | targetId, progress, percentage |
| `earning_target_complete` | targetId, amount |
| `earning_withdrawal_request` | amount, method, currency |
| `earning_withdrawal_complete` | withdrawalId, netAmount |
| `earning_kyc_submit` | level, documents |
| `earning_kyc_approved` | level |

---

## Related Documentation

- [Reseller System](./reseller-system.md)
- [Gifts & Records](./gifts-and-records.md)
- [Wallet](./features/wallet.md)
- [Owner CMS](./owner-cms.md)
