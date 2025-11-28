# Admin Panel

Complete documentation for the Admin Panel in Aura Voice Chat. The admin system is hierarchical with Owner at the top, Country Admins managing each country, Regular Admins handling daily operations, and Customer Support staff helping users.

**App Developer:** Hawkaye Visions LTD — Lahore, Pakistan

## Overview

The Admin Panel provides tools for day-to-day app management including user moderation, content review, support tickets, and basic analytics. Users escalate issues from Customer Support → Regular Admin → Country Admin → Owner.

---

## Admin Hierarchy

```
┌─────────────────────────────────────────────────────────────┐
│                         OWNER                                │
│           (Single owner with full app control)               │
│    Tag: 👑 Owner | Frame: Exclusive Owner Frame              │
│    Access: Owner Panel - Edit/Add/Remove ANYTHING            │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    COUNTRY ADMINS                            │
│         (ONE per country - unique authority)                 │
│    Tag: 🌍 Country Admin [PK/IN/US/etc.]                     │
│    Frame: Exclusive Country Admin Frame                      │
│    Powers: Ban users, manage all admins in their country     │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    REGULAR ADMINS                            │
│           (Multiple per country - limited powers)            │
│    Tag: ⚡ Admin | Frame: Admin Frame                        │
│    Powers: Manage guides, handle reports, basic moderation   │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   CUSTOMER SUPPORT                           │
│        (Team members in Customer Support Room)               │
│    Tag: 💬 Support | Frame: Support Frame                    │
│    Role: Help users with issues, escalate to admins          │
└─────────────────────────────────────────────────────────────┘
```

---

## Owner

The Owner is the single highest authority in the app with complete control.

### Owner Identification

| Attribute | Value |
|-----------|-------|
| Profile Tag | 👑 Owner |
| Profile Frame | Exclusive Owner Frame (animated, unique) |
| Owner ID | Single designated account |
| Panel Access | Full Owner Panel in-app |

### Owner Powers

- **Full App Control:** Edit, add, remove ANY feature, user, content, setting
- **Economy Control:** Set all prices, rewards, multipliers, balances
- **User Management:** Grant/revoke any role, tag, frame, VIP
- **Admin Management:** Appoint/remove Country Admins and Regular Admins
- **CMS Access:** Complete Owner CMS with versioning and rollback
- **Financial Access:** All transactions, payouts, reseller management
- **Feature Flags:** Enable/disable any feature globally
- **Security:** IP bans, device bans, fraud investigation

### Owner Panel Access

The Owner Panel is accessible in-app only to the designated Owner ID. See [Owner Panel](./owner-panel.md) for complete documentation.

---

## Country Admin

**ONE Country Admin per country** - They are the highest authority within their country's user base.

### Country Admin Identification

| Attribute | Value |
|-----------|-------|
| Profile Tag | 🌍 Country Admin [XX] (where XX = country code) |
| Profile Frame | Exclusive Country Admin Frame (unique per country) |
| Limit | Only 1 per country |
| Assignment | Owner appoints via Owner Panel |

### Country Admin Powers

| Action | Description |
|--------|-------------|
| Ban Users | Permanent or temporary bans in their country |
| Manage Admins | Appoint/remove Regular Admins in their country |
| Manage Guides | Oversee all guides in their country |
| Escalation | Receive escalations from Regular Admins |
| Report to Owner | Direct communication channel to Owner |
| Country Stats | View analytics for their country |
| Room Oversight | Close/ban rooms in their country |
| Coin Limits | Higher coin adjustment limits (up to 50M) |

### Country Admin Panel

Country Admins have a dedicated panel showing:
- All Regular Admins in their country
- All Guides in their country
- Escalated reports
- Country-specific statistics
- User ban management
- Room management

---

## Regular Admin

Multiple Regular Admins can exist per country. They handle day-to-day operations and manage Guides.

### Regular Admin Identification

| Attribute | Value |
|-----------|-------|
| Profile Tag | ⚡ Admin |
| Profile Frame | Admin Frame |
| Limit | Multiple per country |
| Assignment | Country Admin appoints |

### Regular Admin Powers

| Action | Description |
|--------|-------------|
| Guide Management | Add/remove guide tags, monitor guide tasks |
| Handle Reports | Review and act on user reports |
| Mute Users | Temporary mutes (up to 7 days) |
| Warn Users | Issue warnings |
| Room Moderation | Monitor rooms, remove inappropriate content |
| Escalate | Escalate ban requests to Country Admin |
| Support Tickets | Handle tier-2 support issues |
| Coin Limits | Lower coin adjustment limits (up to 5M) |

### Regular Admin Panel

Regular Admins have a panel showing:
- Guides they manage
- Pending reports
- User moderation queue
- Room monitoring
- Escalation interface to Country Admin

### Cannot Do (Must Escalate)

- Permanent bans → Escalate to Country Admin
- Large coin adjustments (>5M) → Escalate to Country Admin
- Admin role management → Country Admin only
- App-wide changes → Owner only

---

## Customer Support

Customer Support staff are team members who help users with issues directly.

### Customer Support Identification

| Attribute | Value |
|-----------|-------|
| Profile Tag | 💬 Support |
| Profile Frame | Support Frame |
| Limit | Multiple team members |
| Assignment | Owner or Country Admin |
| Room | Customer Support Room (official) |

### Customer Support Room

A dedicated room for user support:

| Attribute | Value |
|-----------|-------|
| Room Name | 🎧 Aura Customer Support |
| Type | Official Support Room (cannot be deleted) |
| Visibility | Always visible in room list |
| Staff | Customer Support role holders |
| Access | All users can join |

### Customer Support Powers

| Action | Description |
|--------|-------------|
| Help Users | Answer questions, guide users |
| Create Tickets | Log issues for admins |
| Escalate | Send issues to Regular Admin |
| View User Info | Basic user profile access |
| FAQ Response | Provide standard answers |

### Cannot Do (Must Escalate)

- Mute/ban users → Escalate to Regular Admin
- Coin adjustments → Escalate to Regular Admin
- Account modifications → Escalate to Regular Admin

---

## Escalation Flow

```
User has issue
      │
      ▼
┌─────────────────────┐
│  Customer Support   │ ← User goes to Support Room
│  (Help & Log)       │
└─────────────────────┘
      │ Cannot resolve
      ▼
┌─────────────────────┐
│   Regular Admin     │ ← Mute, warn, investigate
│  (Moderate & Act)   │
└─────────────────────┘
      │ Needs ban/higher action
      ▼
┌─────────────────────┐
│   Country Admin     │ ← Ban, manage admins
│  (Ban & Manage)     │
└─────────────────────┘
      │ App-wide issue
      ▼
┌─────────────────────┐
│       Owner         │ ← Full control
│  (Final Decision)   │
└─────────────────────┘
```

---

## Tags and Frames

### Exclusive Tags

| Role | Tag | Display |
|------|-----|---------|
| Owner | 👑 Owner | Gold badge, always visible |
| Country Admin | 🌍 Country Admin [PK] | Country flag, unique |
| Regular Admin | ⚡ Admin | Blue badge |
| Customer Support | 💬 Support | Green badge |

### Exclusive Frames

| Role | Frame | Description |
|------|-------|-------------|
| Owner | Owner Crown | Animated gold crown frame, only one exists |
| Country Admin | Country Crest | Unique frame per country with flag elements |
| Regular Admin | Admin Shield | Professional blue shield frame |
| Customer Support | Support Aura | Friendly green aura frame |

---

## Access Levels

### Admin Permission Matrix

| Permission | Support | Regular Admin | Country Admin | Owner |
|------------|---------|---------------|---------------|-------|
| View user profiles | ✓ | ✓ | ✓ | ✓ |
| Create tickets | ✓ | ✓ | ✓ | ✓ |
| Mute users | ✗ | ✓ (7d max) | ✓ (30d max) | ✓ |
| Warn users | ✗ | ✓ | ✓ | ✓ |
| Temporary ban | ✗ | ✗ | ✓ | ✓ |
| Permanent ban | ✗ | ✗ | ✓ | ✓ |
| Coin adjustment | ✗ | ✓ (5M max) | ✓ (50M max) | ✓ |
| Manage guides | ✗ | ✓ | ✓ | ✓ |
| Manage admins | ✗ | ✗ | ✓ (own country) | ✓ |
| App settings | ✗ | ✗ | ✗ | ✓ |
| Financial access | ✗ | ✗ | ✗ | ✓ |

---

## Dashboard

### Main Dashboard

- **Active Users:** Real-time online count
- **Daily Stats:** New users, transactions, rooms active
- **Alerts:** Flagged content, pending reviews, urgent tickets
- **Quick Actions:** Common admin tasks

### Key Metrics

| Metric | Description |
|--------|-------------|
| DAU | Daily Active Users |
| New Registrations | Today's signups |
| Active Rooms | Currently open rooms |
| Transactions | Gift/purchase volume |
| Pending Tickets | Support queue |
| Flagged Content | Awaiting review |

---

## User Management

### User Search

Search by:
- User ID
- Username
- Phone number
- Email
- Device ID

### User Profile View

| Section | Information |
|---------|-------------|
| Basic Info | ID, name, level, VIP status, join date |
| Wallet | Coins balance, diamonds, transaction history |
| Activity | Room history, gift history, login logs |
| Social | Friends, followers, CP status, family |
| Flags | Warnings, bans, reports against user |
| Devices | Device history, IP logs |

### User Actions

| Action | Permission Level | Description |
|--------|------------------|-------------|
| View Profile | Level 1 | See user details |
| Send Warning | Level 1 | Issue formal warning |
| Mute User | Level 2 | Restrict chat (1hr-7d) |
| Restrict Gifts | Level 2 | Disable gift sending |
| Temporary Ban | Level 3 | Ban user (1d-30d) |
| Permanent Ban | Level 3 | Permanent account ban |
| Coin Adjustment | Level 3 | Add/remove coins (with reason) |
| Level Adjustment | Level 4 | Modify user level |
| VIP Grant | Level 4 | Grant VIP status |
| Account Restore | Level 4 | Restore banned account |

### Coin Adjustment Limits

| Admin Level | Single Adjustment Limit | Daily Limit |
|-------------|------------------------|-------------|
| Level 3 | 1,000,000 coins | 5,000,000 coins |
| Level 4 | 10,000,000 coins | 50,000,000 coins |

---

## Content Moderation

### Report Queue

| Report Type | Priority | SLA |
|-------------|----------|-----|
| Harassment | High | 1 hour |
| Inappropriate Content | High | 2 hours |
| Scam/Fraud | Critical | 30 minutes |
| Spam | Medium | 4 hours |
| Username Violation | Low | 24 hours |
| Other | Low | 24 hours |

### Moderation Actions

| Action | Effect |
|--------|--------|
| Dismiss | Close report as invalid |
| Warn User | Issue warning, log incident |
| Remove Content | Delete offending content |
| Mute | Temporary chat restriction |
| Ban | Temporary or permanent ban |
| Escalate | Send to senior admin/owner |

### Auto-Moderation Flags

System-flagged content for review:
- Profanity filter triggers
- Spam detection
- Unusual activity patterns
- Mass reporting
- Suspicious transactions

---

## Room Management

### Room Search

Search by:
- Room ID
- Room name
- Owner ID/name

### Room Actions

| Action | Permission Level |
|--------|------------------|
| View Room Details | Level 1 |
| Join Room (Hidden) | Level 2 |
| Close Room | Level 3 |
| Ban Room | Level 3 |
| Edit Room Settings | Level 4 |

### Room Monitoring

- Current participants
- Chat history (last 24 hours)
- Gift activity
- Game activity
- Report history

---

## Support Tickets

### Ticket Categories

| Category | Examples |
|----------|----------|
| Account Issues | Login problems, recovery |
| Payment Issues | Failed purchases, refunds |
| Technical Issues | Bugs, crashes |
| Report User | Harassment, scam reports |
| Feature Request | Suggestions |
| Other | General inquiries |

### Ticket Workflow

```
New → Assigned → In Progress → Resolved → Closed
                     ↓
                 Escalated → Owner Review
```

### Response Templates

Pre-built responses for common issues:
- Account recovery steps
- Payment troubleshooting
- Feature explanations
- Policy reminders

---

## Announcements

### Announcement Types

| Type | Visibility | Duration |
|------|------------|----------|
| System | All users | Until dismissed |
| Event | All users | Event duration |
| Maintenance | All users | Until complete |
| Targeted | Specific users | Custom |

### Create Announcement

- Title (max 50 chars)
- Body (max 500 chars)
- Image (optional)
- Link (optional)
- Target audience
- Start/end time
- Priority level

---

## Event Management

### Event Actions (Level 2+)

| Action | Description |
|--------|-------------|
| View Events | See all active/scheduled events |
| Create Event | Set up new event (requires Owner approval) |
| Edit Event | Modify event details |
| End Event | Terminate event early |
| Award Prizes | Distribute event rewards |

### Event Types Manageable

- Room competitions
- Gift events
- Lucky events
- Seasonal events

---

## Analytics (View Only)

### Available Reports

| Report | Description |
|--------|-------------|
| User Growth | Daily/weekly/monthly signups |
| Retention | User return rates |
| Engagement | DAU, session time, actions |
| Revenue | Purchases, gift volume |
| Top Users | Leaderboards |
| Room Stats | Popular rooms, activity |

---

## Audit Logs

All admin actions are logged:

| Field | Description |
|-------|-------------|
| Timestamp | When action occurred |
| Admin ID | Who performed action |
| Action Type | What was done |
| Target | User/room/content affected |
| Details | Specific parameters |
| Reason | Admin's stated reason |

### Log Retention

- Standard actions: 90 days
- Financial actions: 2 years
- Ban actions: Permanent

---

## Admin Tools

### Bulk Actions

| Action | Permission | Use Case |
|--------|------------|----------|
| Bulk Message | Level 3 | Notify user groups |
| Bulk Mute | Level 3 | Event moderation |
| Bulk Coin Grant | Level 4 | Event rewards |

### Search Tools

- Advanced user search
- Transaction search
- Gift flow analysis
- Device/IP lookup

### Export Tools

- User lists (CSV)
- Transaction reports
- Moderation logs
- Analytics data

---

## Admin Panel Security

### Access Control

- 2FA required
- Session timeout: 30 minutes
- IP whitelist (optional)
- Action confirmation for critical ops

### Prohibited Actions

- Accessing own account data
- Modifying own wallet
- Granting self VIP/coins
- Accessing admin accounts

---

## API Endpoints

```
# Users
GET /admin/users/search
GET /admin/users/{userId}
POST /admin/users/{userId}/warn
POST /admin/users/{userId}/mute
POST /admin/users/{userId}/ban
POST /admin/users/{userId}/coins

# Reports
GET /admin/reports
GET /admin/reports/{reportId}
POST /admin/reports/{reportId}/action

# Tickets
GET /admin/tickets
GET /admin/tickets/{ticketId}
POST /admin/tickets/{ticketId}/respond
POST /admin/tickets/{ticketId}/close

# Rooms
GET /admin/rooms/search
GET /admin/rooms/{roomId}
POST /admin/rooms/{roomId}/close

# Announcements
GET /admin/announcements
POST /admin/announcements
PUT /admin/announcements/{id}
DELETE /admin/announcements/{id}

# Logs
GET /admin/logs
GET /admin/logs/export
```

---

## Related Documentation

- [Owner Panel](./owner-panel.md)
- [Owner CMS](./owner-cms.md)
- [Operations](./operations.md)
