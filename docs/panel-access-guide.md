# Panel Access & Usage Guide

Complete guide for accessing and using all administrative panels in Aura Voice Chat.

**App Developer:** Hawkaye Visions LTD — Lahore, Pakistan

---

## Table of Contents

1. [Overview](#overview)
2. [Owner Panel](#owner-panel)
3. [Admin Panel](#admin-panel)
4. [Country Admin Panel](#country-admin-panel)
5. [Reseller Panel](#reseller-panel)
6. [Guide Panel](#guide-panel)
7. [Panel Comparison](#panel-comparison)

---

## Overview

Aura Voice Chat has five distinct administrative panels, each serving different roles in the app hierarchy:

```
┌─────────────────────────────────────────────────────────────┐
│                    PANEL HIERARCHY                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   👑 OWNER PANEL                                            │
│   └── Full app control, economy, all settings               │
│                                                             │
│   🌍 COUNTRY ADMIN PANEL                                    │
│   └── Country-specific management, user bans                │
│                                                             │
│   ⚡ ADMIN PANEL                                            │
│   └── Daily moderation, user management, reports            │
│                                                             │
│   💼 RESELLER PANEL                                         │
│   └── Coin sales, customer management, earnings             │
│                                                             │
│   🎯 GUIDE PANEL                                            │
│   └── Target tracking, earnings, user engagement            │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## Owner Panel

### Who Can Access

- **Only the designated Owner** (single account)
- Identified by: 👑 Owner tag and exclusive Owner Crown Frame

### How to Access

1. **In-App Access:**
   - Open the app and log in with Owner credentials
   - Navigate to Profile → Settings → Owner Panel
   - Complete 2FA verification (OTP + Biometric)
   - Owner Panel dashboard opens

2. **Web Access (if enabled):**
   - Go to `https://admin.auravoice.chat/owner`
   - Enter Owner credentials
   - Complete 2FA verification
   - Access granted

### Main Features & How to Use

#### 1. Dashboard
**Purpose:** Overview of app performance and revenue

**How to use:**
1. View real-time metrics on the main dashboard
2. Click any widget for detailed breakdown
3. Use date picker to view historical data
4. Export reports using the Export button

**Key Metrics:**
- Revenue Today / MTD / YTD
- Active Users (real-time)
- Pending Payouts
- System Health Status

#### 2. Economy Management
**Purpose:** Control coin rates, VIP multipliers, gift taxes

**How to use:**
1. Go to Owner Panel → Economy
2. Select setting to modify (e.g., Coin Purchase Rate)
3. Enter new value or use slider
4. Click "Preview Changes" to see impact
5. Click "Apply" to save changes
6. Changes take effect immediately

**Available Settings:**
| Setting | Description | How to Adjust |
|---------|-------------|---------------|
| Coin Rate | Coins per $1 | Slider: 80K-120K |
| VIP Multiplier | Max VIP bonus | Dropdown: 2x-4x |
| Gift Tax | Platform fee | Percentage input |
| Diamond Rate | Diamond conversion | Slider: 25%-35% |

#### 3. Staff Management
**Purpose:** Appoint/remove Country Admins and Regular Admins

**How to appoint a Country Admin:**
1. Go to Owner Panel → Staff → Country Admins
2. Select country from dropdown
3. Search for user by ID or username
4. Click "Appoint as Country Admin"
5. Confirm appointment
6. User receives notification and Country Admin tag/frame

**How to remove a Country Admin:**
1. Go to Staff → Country Admins
2. Find the Country Admin in the list
3. Click "Remove" button
4. Confirm removal
5. Tag and frame are revoked immediately

#### 4. Payout Management
**Purpose:** Approve/reject user withdrawal requests

**How to process payouts:**
1. Go to Owner Panel → Payouts
2. View pending withdrawal requests
3. Click on a request to see details
4. Verify user information and amount
5. Click "Approve" or "Reject"
6. For rejection, enter reason
7. User is notified of the decision

**Bulk Actions:**
- "Approve All" - Approve all filtered requests
- "Export List" - Download CSV for bank processing
- "Hold" - Put suspicious requests on hold

#### 5. Feature Toggles
**Purpose:** Enable/disable app features globally

**How to toggle features:**
1. Go to Owner Panel → Features
2. Find feature in the list
3. Toggle switch On/Off
4. Confirm change
5. Feature is enabled/disabled immediately

**Available Toggles:**
- Games (all games)
- Individual games (Lucky 777, Greedy Baby, etc.)
- CP System
- Family System
- Earning System
- Guide Program
- Reseller Program

#### 6. Emergency Controls
**Purpose:** Quick actions for emergencies

**Kill Switches:**
| Switch | Effect | Use When |
|--------|--------|----------|
| Disable Purchases | Stops all payments | Payment system issue |
| Disable Withdrawals | Halts all payouts | Suspicious activity |
| Disable Gifts | Stops coin transfers | Economy emergency |
| Maintenance Mode | Full app lockout | Major update/issue |

**How to activate:**
1. Go to Owner Panel → Emergency
2. Click the kill switch
3. Enter reason (required)
4. Confirm with biometric
5. Effect is immediate

---

## Admin Panel

### Who Can Access

- **Regular Admins** (⚡ Admin tag)
- Appointed by Country Admin or Owner

### How to Access

1. **In-App Access:**
   - Open app and log in with Admin account
   - Navigate to Profile → Admin Panel
   - Enter admin PIN (if set)
   - Admin Panel opens

2. **Web Access:**
   - Go to `https://admin.auravoice.chat`
   - Enter admin credentials
   - Complete verification
   - Access granted

### Main Features & How to Use

#### 1. User Management
**Purpose:** Search, view, and moderate users

**How to search for a user:**
1. Go to Admin Panel → Users
2. Enter search term (ID, username, phone, email)
3. Click Search
4. Click on user to view profile

**How to issue a warning:**
1. Find user and view profile
2. Click "Actions" → "Warn User"
3. Select warning type
4. Enter reason
5. Click "Send Warning"
6. User receives notification

**How to mute a user:**
1. Find user and view profile
2. Click "Actions" → "Mute User"
3. Select duration (1 hour to 7 days)
4. Enter reason
5. Click "Apply Mute"
6. User cannot send messages for duration

**How to escalate to Country Admin:**
1. Find user and view profile
2. Click "Escalate"
3. Select escalation type (Ban Request, etc.)
4. Enter detailed reason
5. Click "Submit Escalation"
6. Country Admin receives notification

#### 2. Report Queue
**Purpose:** Handle user reports

**How to process reports:**
1. Go to Admin Panel → Reports
2. View list sorted by priority
3. Click on a report to see details
4. Review evidence (screenshots, chat logs)
5. Choose action:
   - Dismiss (invalid report)
   - Warn (minor violation)
   - Mute (chat violation)
   - Escalate (needs ban)
6. Enter action reason
7. Click "Complete"

**Report Priority:**
| Priority | Type | Response Time |
|----------|------|---------------|
| Critical | Scam/Fraud | 30 minutes |
| High | Harassment | 1 hour |
| Medium | Spam | 4 hours |
| Low | Other | 24 hours |

#### 3. Room Monitoring
**Purpose:** Monitor and moderate rooms

**How to monitor a room:**
1. Go to Admin Panel → Rooms
2. Search for room or browse active rooms
3. Click on room to view details
4. See participants, chat history, activity

**How to close a room:**
1. Find room and view details
2. Click "Actions" → "Close Room"
3. Enter reason
4. Click "Confirm Close"
5. All participants are removed
6. Room owner is notified

#### 4. Guide Management
**Purpose:** Manage guides assigned to you

**How to add guide tag:**
1. Go to Admin Panel → Guides
2. Click "Add New Guide"
3. Search for user
4. Click "Assign Guide Tag"
5. User receives Guide tag and can start earning

**How to monitor guides:**
1. Go to Guides tab
2. View list of your guides
3. See performance metrics:
   - Users engaged
   - Rooms managed
   - Target progress
4. Click guide for detailed report

---

## Country Admin Panel

### Who Can Access

- **Country Admins only** (🌍 Country Admin tag)
- One per country, appointed by Owner

### How to Access

1. **In-App Access:**
   - Log in with Country Admin account
   - Navigate to Profile → Country Admin Panel
   - Complete verification
   - Panel opens showing country-specific data

### Main Features & How to Use

#### 1. Country Dashboard
**Purpose:** Overview of your country's statistics

**View includes:**
- Total users in your country
- Active users today
- Revenue from your country
- Pending reports
- Admin activity

#### 2. Admin Management
**Purpose:** Appoint and manage Regular Admins in your country

**How to appoint a Regular Admin:**
1. Go to Country Admin Panel → Admins
2. Click "Add New Admin"
3. Search for user in your country
4. Click "Appoint as Admin"
5. Select permission level
6. Confirm appointment
7. User receives Admin tag and access

**How to remove an Admin:**
1. Go to Admins tab
2. Find admin in list
3. Click "Remove"
4. Enter reason
5. Confirm
6. Admin access revoked

#### 3. User Banning
**Purpose:** Ban users from your country

**How to ban a user:**
1. Go to Country Admin Panel → Users
2. Search for user
3. Click "Actions" → "Ban User"
4. Select ban type:
   - Temporary (1-30 days)
   - Permanent
5. Enter detailed reason
6. Click "Apply Ban"
7. User is immediately logged out and banned

**How to unban a user:**
1. Go to Users → Banned tab
2. Find user in banned list
3. Click "Unban"
4. Enter reason for unban
5. Confirm
6. User can log in again

#### 4. Escalations
**Purpose:** Handle escalations from Regular Admins

**How to process escalations:**
1. Go to Country Admin Panel → Escalations
2. View pending escalations
3. Click on escalation to review
4. See original report + admin notes
5. Make decision:
   - Approve ban request
   - Reject (send back to admin)
   - Request more info
6. Enter decision notes
7. Click "Complete"

#### 5. Guide Oversight
**Purpose:** Oversee all guides in your country

**View includes:**
- All guides in your country
- Performance rankings
- Earnings reports
- Issue flags

**Actions available:**
- View guide details
- Reassign guides to different admins
- Revoke guide status (if needed)

---

## Reseller Panel

### Who Can Access

- **Approved Resellers** (💼 Reseller tag)
- Must complete offline registration and payment

### How to Access

1. **In-App Access:**
   - Log in with Reseller account
   - Navigate to Profile → Reseller Panel
   - Panel opens showing sales interface

2. **Web Access:**
   - Go to `https://reseller.auravoice.chat`
   - Enter reseller credentials
   - Access granted

### Main Features & How to Use

#### 1. Reseller Dashboard
**Purpose:** Overview of sales and earnings

**Dashboard shows:**
- Today's sales
- This month's sales
- Commission earned
- Tier status and progress
- Pending payouts

#### 2. Recharge Users
**Purpose:** Sell coins to customers

**How to recharge a user:**
1. Go to Reseller Panel → Recharge
2. Enter customer's User ID or search by username
3. Select coin package to sell
4. Confirm amount
5. Click "Process Recharge"
6. Collect payment from customer (cash/bank)
7. User receives coins instantly

**Packages available:**
| Package | Coins | Your Cost | Sell Price | Profit |
|---------|-------|-----------|------------|--------|
| Basic | 100,000 | $0.80 | $1.00 | $0.20 |
| Standard | 500,000 | $3.80 | $5.00 | $1.20 |
| Premium | 1,000,000 | $7.50 | $10.00 | $2.50 |
| (Varies by tier) | | | | |

#### 3. Customer Management
**Purpose:** Track your customers

**How to view customers:**
1. Go to Reseller Panel → Customers
2. See list of users you've recharged
3. View purchase history per customer
4. See total spent by each customer

**Customer Loyalty:**
- Track repeat customers
- See customer lifetime value
- Identify top customers

#### 4. Offline Research (Payment to Platform)
**Purpose:** Add funds to your reseller balance

**How to add funds:**
1. Go to Reseller Panel → Wallet → Add Funds
2. Select payment method (Bank Transfer)
3. See platform bank details
4. Make payment to provided account
5. Upload payment receipt
6. Enter transaction reference
7. Click "Submit for Verification"
8. Wait for approval (1-24 hours)
9. Funds added to your reseller balance

**Payment Methods:**
- Bank Transfer (recommended)
- Mobile Wallet (EasyPaisa, JazzCash)
- International Wire

#### 5. Earnings & Withdrawals
**Purpose:** Track and withdraw your commission

**How to withdraw earnings:**
1. Go to Reseller Panel → Earnings
2. View available balance
3. Click "Withdraw"
4. Enter amount to withdraw
5. Select payment method
6. Confirm withdrawal
7. Funds sent within 1-3 business days

**Commission Structure:**
| Tier | Monthly Sales | Commission |
|------|---------------|------------|
| Bronze | $0-500 | 15% |
| Silver | $500-2,000 | 18% |
| Gold | $2,000-5,000 | 20% |
| Platinum | $5,000-10,000 | 22% |
| Diamond | $10,000+ | 25% |

#### 6. Transaction History
**Purpose:** View all your transactions

**How to view history:**
1. Go to Reseller Panel → History
2. Filter by date range
3. Filter by transaction type
4. Export to CSV for records

---

## Guide Panel

### Who Can Access

- **Approved Guides** (🎯 Guide tag)
- Assigned by Regular Admin or Country Admin

### How to Access

1. **In-App Access:**
   - Log in with Guide account
   - Navigate to Profile → Guide Panel
   - Panel opens showing targets and earnings

### Main Features & How to Use

#### 1. Guide Dashboard
**Purpose:** Overview of targets and earnings

**Dashboard shows:**
- Current target progress
- Earnings this period
- Users you've helped
- Rooms you manage
- Ranking among guides

#### 2. Target Tracking
**Purpose:** Track progress toward earning targets

**How targets work:**
1. Go to Guide Panel → Targets
2. View current active targets
3. See progress bar for each target
4. Check time remaining
5. See potential earnings

**Target Types:**
| Target | Requirement | Earning |
|--------|-------------|---------|
| User Engagement | Help 50 users | $5.00 |
| Room Activity | 20 hours in rooms | $3.00 |
| Gift Encouragement | Users send 1M coins | $10.00 |
| New User Welcome | Onboard 20 new users | $8.00 |

#### 3. User Engagement
**Purpose:** Help and guide users

**How to help users:**
1. Go to Guide Panel → Users
2. See users assigned to you
3. View their activity status
4. Send helpful messages
5. Guide them on app features
6. Your help is tracked for targets

#### 4. Room Management
**Purpose:** Manage rooms assigned to you

**How to manage rooms:**
1. Go to Guide Panel → Rooms
2. See rooms you're responsible for
3. Join room to engage users
4. Encourage gift giving
5. Report issues
6. Your room activity counts toward targets

#### 5. Earnings History
**Purpose:** View your earnings

**How to view earnings:**
1. Go to Guide Panel → Earnings
2. See earnings by period (daily/weekly/monthly)
3. View completed targets
4. See pending earnings (in clearance)
5. Track withdrawal history

**Earning Timeline:**
```
Target Complete → Clearance (5-7 days) → Available → Withdraw
```

#### 6. Target Sheets
**Purpose:** Detailed performance tracking

**How to view target sheets:**
1. Go to Guide Panel → Target Sheets
2. Select period (Week/Month)
3. View detailed breakdown:
   - Each target status
   - Progress percentage
   - Time remaining
   - Estimated earning

**Target Sheet Example:**
| Target | Progress | Status | Deadline | Earning |
|--------|----------|--------|----------|---------|
| User Engagement | 45/50 | 90% | 3 days | $5.00 |
| Room Activity | 18/20 hrs | 90% | 3 days | $3.00 |
| Gift Encourage | 800K/1M | 80% | 3 days | $10.00 |

---

## Panel Comparison

### Access Requirements

| Panel | Role Required | Appointment By |
|-------|---------------|----------------|
| Owner Panel | Owner | System (single account) |
| Country Admin Panel | Country Admin | Owner |
| Admin Panel | Regular Admin | Country Admin or Owner |
| Reseller Panel | Reseller | Owner (with payment) |
| Guide Panel | Guide | Admin or Country Admin |

### Permission Matrix

| Action | Guide | Reseller | Admin | Country Admin | Owner |
|--------|-------|----------|-------|---------------|-------|
| View users | Limited | Customers | ✓ | ✓ | ✓ |
| Warn users | ✗ | ✗ | ✓ | ✓ | ✓ |
| Mute users | ✗ | ✗ | ✓ | ✓ | ✓ |
| Ban users | ✗ | ✗ | ✗ | ✓ | ✓ |
| Sell coins | ✗ | ✓ | ✗ | ✗ | ✓ |
| Manage guides | ✗ | ✗ | ✓ | ✓ | ✓ |
| Manage admins | ✗ | ✗ | ✗ | ✓ (country) | ✓ |
| Economy settings | ✗ | ✗ | ✗ | ✗ | ✓ |
| Feature toggles | ✗ | ✗ | ✗ | ✗ | ✓ |

### Earning Capabilities

| Role | Can Earn | How |
|------|----------|-----|
| Guide | Yes | Complete targets |
| Reseller | Yes | Commission on sales |
| Admin | No | (Volunteer or paid separately) |
| Country Admin | No | (Volunteer or paid separately) |
| Owner | Yes | Platform revenue |

---

## Troubleshooting

### Common Issues

**Can't access panel:**
1. Verify you have the correct role
2. Check if role was revoked
3. Try logging out and back in
4. Contact your supervisor (Admin → Country Admin → Owner)

**Panel not loading:**
1. Check internet connection
2. Update the app to latest version
3. Clear app cache
4. Try web version if available

**Actions not working:**
1. Check if you have permission
2. Verify target user exists
3. Check if feature is enabled
4. Contact Country Admin or Owner

---

## Related Documentation

- [Admin Panel Details](./admin-panel.md)
- [Owner Panel Details](./owner-panel.md)
- [Reseller Panel Details](./reseller-panel.md)
- [Guide System](./guide-system.md)
- [Earning System](./earning-system.md)
