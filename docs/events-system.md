# Events System

Comprehensive documentation for all events in Aura Voice Chat, including room slider events, time-limited events, seasonal events, and Firebase integration.

## Overview

Events drive engagement through time-limited activities, special rewards, and competitive elements. Events appear in the room slider, home banners, and dedicated event sections.

---

## Event Types

### 1. Room Slider Events

Events displayed in the horizontal carousel within rooms:

| Event Type | Duration | Description |
|------------|----------|-------------|
| CP Boost | 7 days | Double CP EXP earning |
| Gift Festival | 3 days | Bonus diamonds on gifts |
| Lucky Hour | 1 hour | Enhanced game odds |
| Flash Sale | 6 hours | Discounted store items |
| Room Challenge | 24 hours | Room-specific competitions |

### 2. Recharge Events

Events tied to coin purchases:

| Event | Trigger | Reward |
|-------|---------|--------|
| First Recharge | First purchase ever | 100% bonus coins |
| Daily Surge | First purchase of day | 20% bonus coins |
| Aurora Milestone | Cumulative recharge | Tiered rewards |
| Recharge Royale | Weekly recharge total | Competition rewards |

### 3. Seasonal Events

Major events tied to calendar:

| Season | Event Name | Duration | Special Features |
|--------|------------|----------|------------------|
| New Year | Aura Celebration | 2 weeks | Exclusive gifts, double rewards |
| Valentine | Hearts Festival | 1 week | CP bonuses, romantic gifts |
| Eid | Eid Mubarak | 1 week | Special frames, gift themes |
| Diwali | Festival of Lights | 1 week | Themed cosmetics, bonuses |
| Christmas | Winter Wonderland | 2 weeks | Santa gifts, snow effects |
| Anniversary | Aura Birthday | 1 week | Mega rewards, exclusive items |

### 4. Competition Events

Competitive events with leaderboards:

| Competition | Duration | Metric | Prizes |
|-------------|----------|--------|--------|
| Gift King | Weekly | Coins sent | Coins + Exclusive frame |
| Charm Queen | Weekly | Diamonds received | Coins + Exclusive frame |
| Room Star | Weekly | Room visitors | Coins + Badge |
| CP Champions | Monthly | CP EXP gained | Coins + CP cosmetics |
| Family War | Weekly | Family activity | Family rewards |

---

## Event Structure

### Event Configuration

```json
{
  "event": {
    "id": "evt_cp_boost_nov2025",
    "name": "CP Boost Week",
    "type": "room_slider",
    "category": "relationship",
    "startTime": "2025-11-25T00:00:00Z",
    "endTime": "2025-12-02T00:00:00Z",
    "status": "active",
    "display": {
      "bannerUrl": "https://cdn.aura.app/events/cp_boost_banner.jpg",
      "iconUrl": "https://cdn.aura.app/events/cp_boost_icon.png",
      "color": "#FF69B4",
      "position": "room_slider"
    },
    "rules": {
      "multiplier": 2.0,
      "target": "cp_exp",
      "eligibility": {
        "minLevel": 5,
        "hasCP": true
      }
    },
    "rewards": {
      "participation": {
        "coins": 10000,
        "exp": 500
      },
      "milestones": [
        {"target": 100000, "reward": {"coins": 50000, "frame": "cp_boost_frame_7d"}},
        {"target": 500000, "reward": {"coins": 200000, "vehicle": "love_carriage_14d"}},
        {"target": 1000000, "reward": {"coins": 500000, "theme": "romance_theme_30d"}}
      ]
    }
  }
}
```

---

## Room Slider Events

### Display Logic

1. Events appear in order of:
   - User eligibility
   - Time remaining (ending soon first)
   - Priority setting

2. Maximum 5 events shown simultaneously

3. Tap event → Event details modal

### Event Details Modal

| Section | Content |
|---------|---------|
| Header | Event banner + timer |
| Description | Event rules and mechanics |
| Progress | User's current progress |
| Rewards | Available rewards + claim buttons |
| Leaderboard | Top participants (if competitive) |

---

## Event Rewards

### Reward Types

| Type | Examples |
|------|----------|
| Coins | 10,000 - 10,000,000 |
| Diamonds | 1,000 - 100,000 |
| EXP | 100 - 10,000 |
| Frames | Event-exclusive (7d/14d/30d/permanent) |
| Vehicles | Event-exclusive |
| Themes | Event-themed |
| Badges | Participation/winner badges |
| Titles | Display titles |

### Reward Distribution

| Timing | Method |
|--------|--------|
| Instant | Claimed immediately after achievement |
| End of Event | Distributed within 24 hours |
| Leaderboard | Top N winners announced + rewarded |

---

## Milestone Events

### Daily Surge (Recharge Event)

| Day | First Recharge Bonus |
|-----|---------------------|
| Day 1 | 10% bonus coins |
| Day 2 | 12% bonus coins |
| Day 3 | 15% bonus coins |
| Day 4 | 18% bonus coins |
| Day 5 | 20% bonus coins |
| Day 6 | 25% bonus coins |
| Day 7 | 30% bonus coins + Frame |

### Aurora Milestones

| Cumulative Recharge | Reward |
|--------------------|--------|
| $10 | 100,000 bonus coins |
| $50 | 600,000 bonus coins + Frame (7d) |
| $100 | 1,500,000 bonus coins + Vehicle (7d) |
| $500 | 10,000,000 bonus coins + Theme (30d) |
| $1,000 | 25,000,000 bonus coins + Legendary Set |

---

## Lucky Events

### Lucky Bag

Random gift bags with mystery rewards:

| Bag Tier | Cost | Possible Rewards |
|----------|------|------------------|
| Bronze | 10,000 coins | 5,000 - 50,000 coins |
| Silver | 50,000 coins | 25,000 - 250,000 coins |
| Gold | 200,000 coins | 100,000 - 2,000,000 coins |
| Diamond | 1,000,000 coins | 500,000 - 10,000,000 coins |

**Probabilities:**
| Result | Bronze | Silver | Gold | Diamond |
|--------|--------|--------|------|---------|
| 0.5x | 15% | 12% | 10% | 8% |
| 1x | 40% | 35% | 30% | 25% |
| 2x | 30% | 32% | 35% | 37% |
| 5x | 12% | 15% | 18% | 20% |
| 10x | 2.5% | 5% | 6% | 8% |
| 20x | 0.5% | 1% | 1% | 2% |

### Lucky Draw

Spin to win event items:

| Prize | Probability |
|-------|-------------|
| Small coins | 35% |
| Medium coins | 25% |
| Event frame (3d) | 15% |
| Event frame (7d) | 10% |
| Large coins | 8% |
| Event vehicle (7d) | 5% |
| Grand prize | 2% |

---

## Firebase Integration

### Database Structure

```
/events
  /{eventId}
    - config: object (event settings)
    - status: string (draft/active/ended)
    - stats: object (participation counts)
    
/eventParticipation
  /{eventId}
    /{userId}
      - joined: timestamp
      - progress: number
      - milestones: array
      - rewards: array
      - lastActivity: timestamp
      
/eventLeaderboards
  /{eventId}
    /{rank}
      - userId: string
      - score: number
      - lastUpdated: timestamp
```

### Security Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /events/{eventId} {
      allow read: if true;
      allow write: if false; // Admin only
    }
    
    match /eventParticipation/{eventId}/{userId} {
      allow read: if request.auth.uid == userId;
      allow create: if request.auth.uid == userId 
        && isEventActive(eventId)
        && meetsEligibility(eventId);
      allow update: if false; // Server only
    }
    
    match /eventLeaderboards/{eventId}/{rank} {
      allow read: if true;
      allow write: if false; // Server only
    }
    
    function isEventActive(eventId) {
      let event = get(/databases/$(database)/documents/events/$(eventId)).data;
      return event.status == 'active' 
        && event.startTime <= request.time 
        && event.endTime > request.time;
    }
    
    function meetsEligibility(eventId) {
      // Check user meets event requirements
      return true; // Simplified
    }
  }
}
```

### Cloud Functions

```typescript
// Join event
export const joinEvent = functions.https.onCall(async (data, context) => {
  const { eventId } = data;
  
  if (!context.auth) throw new Error('Unauthenticated');
  
  const userId = context.auth.uid;
  const eventRef = admin.firestore().doc(`events/${eventId}`);
  const event = await eventRef.get();
  
  if (!event.exists) throw new Error('Event not found');
  if (event.data()?.status !== 'active') throw new Error('Event not active');
  
  // Create participation record
  await admin.firestore().doc(`eventParticipation/${eventId}/${userId}`).set({
    joined: admin.firestore.FieldValue.serverTimestamp(),
    progress: 0,
    milestones: [],
    rewards: []
  });
  
  return { success: true, eventId };
});

// Update event progress
export const updateEventProgress = functions.firestore
  .document('transactions/{transactionId}')
  .onCreate(async (snap, context) => {
    const transaction = snap.data();
    
    // Find active events this transaction qualifies for
    const activeEvents = await getActiveEventsForTransaction(transaction);
    
    for (const event of activeEvents) {
      await incrementEventProgress(
        event.id, 
        transaction.userId, 
        calculateProgressValue(event, transaction)
      );
    }
  });
```

---

## Event Notifications

### Push Notifications

| Trigger | Message |
|---------|---------|
| Event start | "🎉 {EventName} has started! Join now for exclusive rewards" |
| 24h remaining | "⏰ Only 24 hours left in {EventName}!" |
| Milestone reached | "🏆 You've reached a milestone in {EventName}!" |
| Event end | "📊 {EventName} has ended. Check your rewards!" |

### In-App Notifications

- Banner on home screen
- Badge on event icon
- Room slider highlight

---

## API Endpoints

```
# Events
GET /events
GET /events/active
GET /events/{eventId}
POST /events/{eventId}/join
GET /events/{eventId}/progress
POST /events/{eventId}/claim/{milestoneId}

# Leaderboards
GET /events/{eventId}/leaderboard
GET /events/{eventId}/leaderboard/around-me

# Lucky Events
POST /events/lucky-bag/open
POST /events/lucky-draw/spin
```

---

## Data Models

### Event Participation

```json
{
  "participation": {
    "eventId": "evt_cp_boost_nov2025",
    "userId": "user_123",
    "joined": "2025-11-25T10:00:00Z",
    "progress": 450000,
    "milestones": [
      {"id": "m1", "target": 100000, "achieved": true, "claimedAt": "2025-11-26T10:00:00Z"}
    ],
    "rewards": [
      {"type": "coins", "amount": 50000, "claimedAt": "2025-11-26T10:00:00Z"},
      {"type": "frame", "id": "cp_boost_frame_7d", "claimedAt": "2025-11-26T10:00:00Z"}
    ],
    "rank": 156,
    "percentile": 15
  }
}
```

---

## Telemetry Events

| Event | Properties |
|-------|------------|
| `event_view` | eventId, eventType |
| `event_join` | eventId, userId |
| `event_progress` | eventId, progress, milestone |
| `event_milestone_claim` | eventId, milestoneId, reward |
| `event_end_reward` | eventId, finalRank, rewards |
| `lucky_bag_open` | tier, result |
| `lucky_draw_spin` | prize, value |

---

## Related Documentation

- [Games System](./games-system.md)
- [EXP & Level System](./exp-level-system.md)
- [Ranking & Leaderboards](./ranking-and-leaderboards.md)
- [Owner CMS](./owner-cms.md)
