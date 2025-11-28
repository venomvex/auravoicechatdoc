# Owner CMS

Comprehensive documentation for the Owner Content Management System (CMS) for Aura Voice Chat administrators.

## Overview

The Owner CMS provides administrative controls for managing all app features, content, configuration, and user management. It supports versioning, rollback, and multi-environment deployment.

---

## Access & Authentication

### Access Levels

| Role | Permissions |
|------|-------------|
| Super Admin | All permissions, user management, system config |
| Admin | Feature management, content editing, analytics |
| Content Manager | Content editing, gift catalog, events |
| Moderator | User moderation, report handling |
| Analyst | Read-only analytics access |

### Authentication

- SSO with corporate identity provider
- Two-factor authentication required
- Session timeout: 30 minutes inactive
- Audit logging for all actions

---

## Feature Controls

### Global Feature Toggles

| Feature | Control | Impact |
|---------|---------|--------|
| Gift Animations | Enable/Disable | Room performance |
| Video Mode | Enable/Disable | YouTube integration |
| Super Mic | Enable/Disable | Premium feature |
| Lucky Bag | Enable/Disable | Event feature |
| Cash Payouts | Enable/Disable | Compliance |
| 16-Seat Rooms | Enable/Disable | Room capacity |
| CP System | Enable/Disable | Relationship feature |
| Friend System | Enable/Disable | Social feature |
| Family System | Enable/Disable | Group feature |
| Rankings | Enable/Disable | Leaderboards |

### Feature Configuration

Each feature has detailed configuration:

**Gift System:**
```json
{
  "gifts": {
    "enabled": true,
    "animationCap": 10,
    "largeGiftThreshold": 1000000,
    "cooldownSeconds": 3,
    "maxQuantityPerSend": 999,
    "requireConfirmation": true
  }
}
```

**CP System:**
```json
{
  "cp": {
    "enabled": true,
    "formationFee": 3000000,
    "dissolutionCooldownDays": 7,
    "levels": 10,
    "expPerCoin": 1
  }
}
```

**Family System:**
```json
{
  "family": {
    "enabled": true,
    "creationFee": 1000000,
    "maxMembers": 50,
    "joinCooldownHours": 24,
    "kickCooldownHours": 48
  }
}
```

---

## Content Management

### Gift Catalog

| Action | Description |
|--------|-------------|
| Add Gift | Create new gift with pricing, animation |
| Edit Gift | Modify existing gift properties |
| Disable Gift | Remove from catalog (soft delete) |
| Set Regional | Assign to specific regions |
| Upload Animation | Add/update gift animation |
| Price Change | Modify gift pricing |

**Gift Properties:**
- ID, Name, Description
- Price (coins)
- Category
- Animation file (Lottie/video)
- Sound effect (optional)
- Regional availability
- Active status

### Cosmetic Items

| Category | Properties |
|----------|------------|
| Frames | Name, Image, Duration, Rarity, Price |
| Themes | Name, Colors, Assets, Duration, Price |
| Vehicles | Name, Animation, Sound, Duration, Price |
| Mic Skins | Name, Image/Animation, Duration, Price |
| Seat Effects | Name, Animation, Duration, Price |
| Chat Bubbles | Name, Style, Duration, Price |
| Entrance Styles | Name, Effect, Duration, Price |

### Event Management

| Field | Description |
|-------|-------------|
| Event Name | Display name |
| Event Type | Time-limited, Permanent, Recurring |
| Start Date | Activation time (UTC) |
| End Date | Deactivation time (UTC) |
| Rewards | Coins, items, badges |
| Eligibility | Level, VIP, region requirements |
| Banner | Promotional image |
| Rules | Event-specific rules |

---

## Economy Controls

### Currency Settings

| Setting | Control |
|---------|---------|
| Coin Exchange Rate | Diamond → Coin conversion (default 30%) |
| VIP Multipliers | Daily reward multipliers by tier |
| Minimum Withdrawal | Get Cash/Coins thresholds |
| Withdrawal Limits | Daily/weekly caps |
| Transaction Fees | Platform fees (if any) |

### Reward Configuration

**Daily Rewards:**
```json
{
  "dailyRewards": {
    "day1": 5000,
    "day2": 10000,
    "day3": 15000,
    "day4": 20000,
    "day5": 25000,
    "day6": 30000,
    "day7": {"base": 35000, "bonus": 15000}
  }
}
```

**VIP Multipliers:**
```json
{
  "vipMultipliers": {
    "VIP1": 1.20,
    "VIP2": 1.40,
    "VIP3": 1.60,
    "VIP4": 1.80,
    "VIP5": 2.00,
    "VIP6": 2.20,
    "VIP7": 2.40,
    "VIP8": 2.60,
    "VIP9": 2.80,
    "VIP10": 3.00
  }
}
```

### Pricing Management

| Action | Approval Required |
|--------|-------------------|
| Gift Price Change | Yes (>20% change) |
| VIP Tier Pricing | Yes |
| Formation Fees | Yes |
| Store Item Pricing | Yes (>20% change) |

---

## User Management

### User Actions

| Action | Description | Audit |
|--------|-------------|-------|
| View Profile | Access user details | Logged |
| Edit Balance | Adjust coins/diamonds | Requires approval, logged |
| Grant Item | Add item to inventory | Logged |
| Revoke Item | Remove item | Logged |
| Ban User | Temporary/permanent ban | Logged with reason |
| Unban User | Remove ban | Logged with reason |
| Reset Password | Force password reset | Logged |
| Verify Account | Add verification badge | Logged |

### Bulk Operations

| Operation | Description |
|-----------|-------------|
| Mass Message | Send to user segments |
| Bulk Grant | Award items to groups |
| Bulk Revoke | Remove items from groups |
| User Export | Export user data (GDPR) |

### Moderation Queue

| Queue | Content |
|-------|---------|
| Reports | User-submitted reports |
| Flagged Content | Auto-detected violations |
| Appeals | User ban appeals |
| Verification Requests | Account verification |

---

## Analytics Dashboard

### Real-Time Metrics

| Metric | Update Frequency |
|--------|-----------------|
| Active Users | 1 minute |
| Active Rooms | 1 minute |
| Transaction Volume | 5 minutes |
| Error Rate | 1 minute |

### Historical Reports

| Report | Frequency |
|--------|-----------|
| Daily Summary | Daily |
| Weekly Trends | Weekly |
| Monthly Analysis | Monthly |
| Custom Queries | On-demand |

### Key Metrics

| Category | Metrics |
|----------|---------|
| Users | DAU, MAU, Retention, Churn |
| Revenue | Transactions, ARPU, LTV |
| Engagement | Sessions, Duration, Actions |
| Content | Rooms, Gifts, Messages |
| Performance | Latency, Errors, Uptime |

---

## Versioning & Rollback

### Configuration Versioning

All configuration changes are versioned:

```json
{
  "version": "2.5.1",
  "createdAt": "2025-11-28T14:00:00Z",
  "createdBy": "admin@aura.app",
  "changes": [
    {
      "path": "gifts.largeGiftThreshold",
      "oldValue": 500000,
      "newValue": 1000000
    }
  ],
  "rollbackAvailable": true
}
```

### Version History

- 90-day history retained
- Diff view between versions
- One-click rollback
- Staged rollback option

### Rollback Process

1. Select version to rollback to
2. Review changes (diff view)
3. Select rollback scope (full/partial)
4. Confirm with admin credentials
5. Rollback executes
6. Verification tests run
7. Notification sent

### Emergency Rollback

For critical issues:
- One-click emergency rollback
- Reverts to last known good
- Automatic incident creation
- 24/7 on-call notification

---

## Publishing & Deployment

### Environments

| Environment | Purpose | Access |
|-------------|---------|--------|
| Development | Feature development | Dev team |
| Staging | Testing, QA | QA + Dev |
| Production | Live users | Controlled |

### Publishing Flow

```
Development → Staging → Production
     ↓           ↓           ↓
  Dev Test    QA Test    Canary
                ↓           ↓
             Approve    Full Deploy
```

### Canary Deployment

- 5% of users first
- Monitor metrics for 1 hour
- Auto-rollback on error spike
- Manual approval for full deploy

### Feature Flags

| Flag | Control |
|------|---------|
| Percentage Rollout | 0-100% of users |
| User Segment | Specific user groups |
| Geographic | By region |
| Time-Based | Scheduled activation |

---

## Audit & Compliance

### Audit Logging

All CMS actions logged:
- Timestamp
- User (admin) ID
- Action type
- Target entity
- Old value / New value
- IP address
- Result (success/failure)

### Compliance Features

| Feature | Description |
|---------|-------------|
| GDPR Export | Export user data |
| GDPR Delete | Delete user data |
| Consent Tracking | User consent records |
| Data Retention | Automated data cleanup |

### Access Logs

- Login attempts (success/failure)
- Permission changes
- Configuration changes
- User actions on platform

---

## API Integration

### CMS API Endpoints

```
# Authentication
POST /cms/auth/login
POST /cms/auth/logout
POST /cms/auth/refresh

# Configuration
GET /cms/config/{feature}
PUT /cms/config/{feature}
GET /cms/config/versions
POST /cms/config/rollback/{version}

# Content
GET /cms/gifts
POST /cms/gifts
PUT /cms/gifts/{id}
DELETE /cms/gifts/{id}

# Users
GET /cms/users/{id}
PUT /cms/users/{id}
POST /cms/users/{id}/ban
POST /cms/users/{id}/grant

# Analytics
GET /cms/analytics/realtime
GET /cms/analytics/reports/{type}

# Audit
GET /cms/audit/logs
```

### Webhooks

Configure webhooks for events:
- Configuration changes
- User bans
- High-value transactions
- Error thresholds

---

## Best Practices

### Change Management

1. Document all changes
2. Test in staging first
3. Use canary deployments
4. Monitor after deployment
5. Have rollback plan ready

### Security

1. Use strong authentication
2. Limit access by role
3. Review audit logs regularly
4. Rotate credentials periodically
5. Enable alerts for suspicious activity

### Operations

1. Schedule changes during low-traffic
2. Communicate planned changes
3. Monitor key metrics
4. Document incidents
5. Conduct post-mortems

---

## Related Documentation

- [Configuration](../configuration.md)
- [Firebase Setup](./firebase-setup.md)
- [Operations](../operations.md)
- [Security](../security.md)
