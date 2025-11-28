# Family System

The Family system enables users to form communities with shared rankings, perks, and social features.

## Overview

Families are groups of users who join together for shared benefits, rankings, and social interaction. Family members collaborate to climb rankings and unlock exclusive perks.

---

## Family Creation

### Requirements

| Requirement | Value |
|-------------|-------|
| Minimum Level | 20 |
| Creation Fee | 1,000,000 coins (1M) |
| Minimum Members | 1 (founder) |
| Maximum Members | 50 (upgradable) |

### Creation Process

1. Navigate to **Me** → **Family** → **Create Family**
2. Enter family details:
   - Family Name (3-20 characters)
   - Family Description (max 200 characters)
   - Family Logo (upload or select)
   - Privacy Setting (Open/Apply/Closed)
3. Pay creation fee
4. Family created with founder as Owner

### Family Settings

| Setting | Options |
|---------|---------|
| Visibility | Public, Private |
| Join Method | Open, Apply to Join, Invite Only |
| Member Approval | Owner Only, Owner + Admins |
| Chat Access | All Members, Level 2+ |
| Announcement | Text (max 500 characters) |

---

## Family Roles

### Role Hierarchy

| Role | Count | Permissions |
|------|-------|-------------|
| Owner | 1 | All permissions, transfer ownership |
| Co-Owner | 2 | Manage members, edit settings, not transfer |
| Admin | 5 | Approve members, remove non-admins, post announcements |
| Elder | 10 | Invite members, post in family chat |
| Member | Unlimited | Basic access, participate in activities |

### Role Management

**Owner Permissions:**
- Assign/revoke all roles
- Transfer ownership
- Disband family
- All admin permissions

**Admin Permissions:**
- Approve join requests
- Remove members (non-admin)
- Post announcements
- Mute members
- Edit family description

---

## Family Perks

### Member Perks

| Perk | Description |
|------|-------------|
| Family Badge | Displayed on profile |
| Family Chat | Private chat with all members |
| Family Events | Exclusive family activities |
| Bonus EXP | +5% EXP from room activities |
| Priority Matching | Match with family members first |

### Ranking-Based Perks

Perks unlock based on family ranking:

| Rank | Perk |
|------|------|
| Top 100 | Family frame (7d) |
| Top 50 | +10% EXP bonus |
| Top 20 | Family vehicle (7d) |
| Top 10 | +15% EXP bonus, exclusive frame |
| Top 5 | All above + premium vehicle (30d) |
| Top 3 | All above + permanent badge |
| #1 | All above + exclusive title + 1M coins to owner |

---

## Family Ranking

### Ranking Calculation

Family ranking is determined by:
- Total member contributions
- Weekly activity points
- Room hosting metrics
- Gift economy participation

### Activity Points

| Activity | Points |
|----------|--------|
| Member login | 10/day per member |
| Room hosted | 50/hour |
| Gifts sent in family rooms | 1 per 1000 coins |
| New member joined | 100 (one-time) |
| Family event participation | 200/event |

### Ranking Periods

| Period | Reset | Rewards |
|--------|-------|---------|
| Weekly | Sunday 00:00 UTC | Coins to top families |
| Monthly | 1st of month 00:00 UTC | Frames, vehicles |
| All-Time | Never | Prestige ranking |

### Ranking Display

- Top 100 shown on Family Ranking screen
- Family card shows: Logo, Name, Rank, Members, Total Points
- Tap for details: Member list, activity breakdown

---

## Joining a Family

### Methods

1. **Open Join** — Tap "Join" on family card
2. **Apply** — Submit application, wait for approval
3. **Invite** — Accept invitation from family member

### Join Restrictions

| Restriction | Duration |
|-------------|----------|
| Cooldown after leaving | 24 hours |
| Cooldown after kicked | 48 hours |
| Cooldown after disbanding | 72 hours |

### Application Process

1. Browse or search families
2. Tap "Apply"
3. Enter application message (optional)
4. Wait for approval (max 7 days)
5. Receive notification on approval/rejection

---

## Leaving a Family

### Voluntary Leave

- Available anytime
- 24-hour cooldown before joining another
- Contributions remain with family

### Forced Removal (Kick)

- Admins can remove non-admin members
- Owner can remove any member
- 48-hour cooldown before joining another
- Removed member notified

### Ownership Transfer

If owner wants to leave:
1. Transfer ownership to another member
2. Then leave as normal member

If owner inactive:
- After 30 days inactivity, ownership transfers to most active Co-Owner
- If no Co-Owner, transfers to most active Admin
- If no Admin, transfers to most active Elder

---

## Family Disbanding

### Requirements

- Only Owner can disband
- Family must have no other members, OR
- 7-day warning period before disbanding with members

### Disbanding Process

1. Owner selects "Disband Family"
2. If members exist, warning sent to all
3. 7-day countdown begins
4. Owner confirms after countdown
5. Family deleted, members released

### After Disbanding

- 72-hour cooldown for owner to create new family
- Members have standard 24-hour cooldown
- All family-related items deactivated
- Ranking history preserved for 90 days

---

## Family Events

### Daily Events

| Event | Description | Reward |
|-------|-------------|--------|
| Family Check-In | Daily login bonus | 1,000 coins |
| Active Hour | 1+ hour family room activity | 5,000 coins |
| Gift Exchange | Send gift to family member | 500 coins |

### Weekly Events

| Event | Description | Reward |
|-------|-------------|--------|
| Family Day | All members active in 24 hours | 50,000 coins split |
| Ranking Rush | Climb 10+ ranking positions | Bonus points |
| Recruitment Drive | 5+ new members in week | 100,000 coins to family |

---

## Family Chat

### Features

- Text messages
- Image sharing
- Gift stickers
- @mentions
- Announcements (pinned)

### Moderation

| Action | By Role |
|--------|---------|
| Delete message | Admin+ |
| Mute member | Admin+ |
| Clear chat | Owner only |

---

## API Endpoints

```
POST /family/create
GET /family/{familyId}
PUT /family/{familyId}
POST /family/{familyId}/join
POST /family/{familyId}/apply
POST /family/{familyId}/invite
POST /family/{familyId}/leave
POST /family/{familyId}/kick/{userId}
POST /family/{familyId}/role/{userId}
POST /family/{familyId}/transfer
DELETE /family/{familyId}
GET /family/ranking
GET /family/search
GET /family/my
GET /family/{familyId}/members
GET /family/{familyId}/activity
```

---

## Data Model

```json
{
  "family": {
    "id": "family_001",
    "name": "Super Stars",
    "description": "Top family for active users!",
    "logo": "https://cdn.aura.app/family/logo_001.jpg",
    "ownerId": "user_123",
    "ownerName": "CoolUser",
    "memberCount": 45,
    "maxMembers": 50,
    "visibility": "public",
    "joinMethod": "apply",
    "createdAt": "2024-01-01T00:00:00Z",
    "ranking": {
      "weekly": 15,
      "monthly": 22,
      "allTime": 8
    },
    "points": {
      "weekly": 150000,
      "monthly": 580000,
      "allTime": 2500000
    }
  },
  "membership": {
    "familyId": "family_001",
    "role": "admin",
    "joinedAt": "2024-03-15T10:00:00Z",
    "contribution": 50000,
    "lastActive": "2025-11-28T14:00:00Z"
  }
}
```

---

## Related Documentation

- [Ranking & Leaderboards](./ranking-and-leaderboards.md)
- [Profile & Inventory](./profile-and-inventory.md)
- [Medals System](./medals-system.md)
- [Rooms](./features/rooms.md)
