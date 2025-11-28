# Ranking & Leaderboards

Comprehensive documentation for all ranking systems in Aura Voice Chat, including room, player, CP, and family rankings.

## Overview

Rankings display top performers across various categories, driving engagement and competition. Rankings are updated in real-time and reset on defined schedules.

---

## Room Ranking

### Metrics

Room rankings are determined by:
- Total gifts received in room
- Active user count
- Room activity duration
- Engagement metrics

### Ranking Periods

| Period | Reset | Display |
|--------|-------|---------|
| Daily | 00:00 UTC | Top 100 |
| Weekly | Sunday 00:00 UTC | Top 100 |
| Monthly | 1st of month 00:00 UTC | Top 100 |
| All-Time | Never | Top 1000 |

### Room Ranking Rewards

| Rank | Daily | Weekly | Monthly |
|------|-------|--------|---------|
| #1 | 100K coins | 1M coins | 10M coins |
| #2-3 | 50K coins | 500K coins | 5M coins |
| #4-10 | 20K coins | 200K coins | 2M coins |
| #11-50 | 5K coins | 50K coins | 500K coins |
| #51-100 | 1K coins | 10K coins | 100K coins |

### Room Card Display

Ranked rooms show:
- Room cover image
- Room name
- Owner avatar and name
- Current rank badge
- Participant count
- Gift total

---

## Player Ranking

### Categories

| Category | Metric | Description |
|----------|--------|-------------|
| Charm | Diamonds received | Most gifts received |
| Wealth | Coins spent on gifts | Most generous givers |
| Level | User level | Highest level players |
| Followers | Follower count | Most followed users |

### Ranking Periods

| Period | Reset | Top Displayed |
|--------|-------|---------------|
| Daily | 00:00 UTC | 50 |
| Weekly | Sunday 00:00 UTC | 100 |
| Monthly | 1st of month | 100 |
| All-Time | Never | 500 |

### Player Ranking Rewards

**Charm Ranking (Gift Receivers):**

| Rank | Weekly | Monthly |
|------|--------|---------|
| #1 | 500K coins + Frame (7d) | 5M coins + Frame (30d) |
| #2-3 | 200K coins | 2M coins + Frame (7d) |
| #4-10 | 50K coins | 500K coins |
| #11-50 | 10K coins | 100K coins |

**Wealth Ranking (Gift Senders):**

| Rank | Weekly | Monthly |
|------|--------|---------|
| #1 | 1M coins + Vehicle (7d) | 10M coins + Vehicle (30d) |
| #2-3 | 500K coins | 5M coins + Frame (7d) |
| #4-10 | 100K coins | 1M coins |
| #11-50 | 20K coins | 200K coins |

### Player Card Display

Ranked players show:
- Avatar with frame
- Username
- Level badge
- SVIP badge (if applicable)
- Ranking metric value
- Current rank position

---

## CP Ranking

### Metrics

CP pairs ranked by:
- Total CP EXP earned
- Mutual gift sending
- CP level achieved

### Ranking Display

| Position | Display |
|----------|---------|
| Top 3 | Featured with special effects |
| #4-20 | Large cards |
| #21-100 | List view |

### CP Ranking Rewards

| Rank | Weekly | Monthly |
|------|--------|---------|
| #1 | 2M coins (split) + CP Frame (7d) | 20M coins + CP Frame (30d) |
| #2-3 | 1M coins (split) | 10M coins + CP Frame (7d) |
| #4-10 | 200K coins (split) | 2M coins |
| #11-50 | 50K coins (split) | 500K coins |

### CP Card Display

Ranked CP pairs show:
- Both partner avatars
- Combined frame effect
- CP Level badge
- Total CP EXP
- Days together
- Current rank

---

## Family Ranking

### Metrics

Family rankings determined by:
- Total member contributions
- Weekly activity points
- Room hosting activity
- Gift economy participation

### Activity Point Calculation

| Activity | Points |
|----------|--------|
| Member daily login | 10/day |
| Room hosted | 50/hour |
| Gifts in family rooms | 1 per 1K coins |
| New member joined | 100 |
| Family event participation | 200/event |

### Ranking Periods

| Period | Reset | Display |
|--------|-------|---------|
| Weekly | Sunday 00:00 UTC | Top 100 |
| Monthly | 1st of month | Top 100 |
| All-Time | Never | Top 500 |

### Family Ranking Rewards

| Rank | Weekly | Monthly |
|------|--------|---------|
| #1 | 5M coins to family | 50M coins + Family Frame (30d) |
| #2-3 | 2M coins | 20M coins + Family Frame (7d) |
| #4-10 | 500K coins | 5M coins |
| #11-20 | 200K coins | 2M coins |
| #21-50 | 50K coins | 500K coins |
| #51-100 | 10K coins | 100K coins |

### Family Reward Distribution

| Role | Share |
|------|-------|
| Owner | 30% |
| Co-Owners | 10% each (20% total) |
| Admins | 5% each |
| Remaining | Split among members by contribution |

### Family Card Display

Ranked families show:
- Family logo
- Family name
- Member count
- Total points
- Current rank
- Owner name

---

## Leaderboard UI

### Navigation

Access rankings via:
- Home → Popular → Rankings tile
- Me → Medals → View Rankings
- Room → Ranking icon

### Tabs

| Tab | Contains |
|-----|----------|
| Room | Room rankings |
| Player | Charm, Wealth, Level, Followers |
| CP | CP pair rankings |
| Family | Family rankings |

### Period Selector

Toggle between:
- Today (Daily)
- This Week
- This Month
- All Time

### Filtering

| Filter | Options |
|--------|---------|
| Region | Global, Country, City |
| Category | Specific ranking type |
| Friends Only | Show only followed users |

---

## Ranking Rules

### Eligibility

| Rule | Requirement |
|------|-------------|
| Minimum Level | Level 5+ for most rankings |
| Account Age | 7+ days for wealth ranking |
| Verification | None required |
| Activity | Must be active in period |

### Disqualification

Users may be removed from rankings for:
- Terms of Service violations
- Fraudulent activity
- Bot/automation use
- Ranking manipulation

### Ranking Freeze

During system maintenance:
- Rankings may freeze temporarily
- Catch-up calculation after maintenance
- No rewards affected

---

## Historical Rankings

### Viewing Past Rankings

- Archive of weekly/monthly winners
- Hall of Fame for #1 positions
- Personal ranking history

### Historical Data

| Period | Retention |
|--------|-----------|
| Daily | 7 days |
| Weekly | 12 weeks |
| Monthly | 12 months |
| All-Time Winners | Permanent |

---

## API Endpoints

```
GET /rankings/rooms?period={period}&page={page}
GET /rankings/players/charm?period={period}&page={page}
GET /rankings/players/wealth?period={period}&page={page}
GET /rankings/players/level?page={page}
GET /rankings/players/followers?page={page}
GET /rankings/cp?period={period}&page={page}
GET /rankings/family?period={period}&page={page}
GET /rankings/history/{type}?period={period}
GET /rankings/me/{type}
```

---

## Data Model

### Ranking Entry

```json
{
  "rankings": {
    "type": "cp",
    "period": "weekly",
    "updatedAt": "2025-11-28T14:00:00Z",
    "entries": [
      {
        "rank": 1,
        "previousRank": 2,
        "change": 1,
        "entity": {
          "type": "cp_pair",
          "user1": {
            "id": "user_001",
            "name": "CoolUser",
            "avatar": "url",
            "level": 60
          },
          "user2": {
            "id": "user_002",
            "name": "Partner",
            "avatar": "url",
            "level": 55
          },
          "cpLevel": 10,
          "totalExp": 9000000000
        },
        "metric": 9000000000,
        "metricLabel": "CP EXP"
      }
    ]
  }
}
```

### User Ranking Position

```json
{
  "myRanking": {
    "type": "charm",
    "period": "weekly",
    "rank": 156,
    "metric": 5000000,
    "topPercentile": 15,
    "toNextRank": 100000
  }
}
```

---

## Telemetry Events

| Event | Properties |
|-------|------------|
| `ranking_view` | type, period, page |
| `ranking_filter` | type, filterApplied |
| `ranking_tap_entry` | type, rank, entityId |
| `ranking_share` | type, period, rank |
| `ranking_reward_claim` | type, period, rank, reward |

---

## Related Documentation

- [Room](./features/rooms.md)
- [CP & Friend System](./cp-friend-system.md)
- [Family System](./family-system.md)
- [Gifts & Records](./gifts-and-records.md)
