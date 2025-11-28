# EXP & Level System

Comprehensive documentation for the Experience Points (EXP) and User Level system in Aura Voice Chat, with Firebase integration and reward structures.

## Overview

The EXP & Level system tracks user progression through activities, unlocking features, rewards, and status symbols as users advance through levels.

---

## Level Structure

### Level Thresholds

| Level | Total EXP Required | EXP for Level | Reward |
|-------|-------------------|---------------|--------|
| 1 | 0 | 0 | Welcome bonus: 5,000 coins |
| 2 | 100 | 100 | 1,000 coins |
| 3 | 300 | 200 | 2,000 coins |
| 4 | 600 | 300 | 3,000 coins |
| 5 | 1,000 | 400 | 5,000 coins + Frame (3d) |
| 6 | 1,500 | 500 | 6,000 coins |
| 7 | 2,100 | 600 | 7,000 coins |
| 8 | 2,800 | 700 | 8,000 coins |
| 9 | 3,600 | 800 | 9,000 coins |
| 10 | 4,500 | 900 | 15,000 coins + Frame (7d) |
| 15 | 11,250 | 1,350 | 25,000 coins + Mic Skin (7d) |
| 20 | 20,000 | 1,750 | 50,000 coins + Vehicle (7d) |
| 25 | 31,250 | 2,250 | 75,000 coins + Theme (7d) |
| 30 | 45,000 | 2,750 | 100,000 coins + Frame (14d) |
| 40 | 80,000 | 3,500 | 200,000 coins + Vehicle (14d) |
| 50 | 125,000 | 4,500 | 500,000 coins + Premium Set (30d) |
| 60 | 180,000 | 5,500 | 1,000,000 coins + Legendary Set |
| 70 | 245,000 | 6,500 | 2,000,000 coins + Exclusive Badge |
| 80 | 320,000 | 7,500 | 3,500,000 coins + Legendary Vehicle |
| 90 | 405,000 | 8,500 | 5,000,000 coins + Master Badge |
| 100 | 500,000 | 9,500 | 10,000,000 coins + Legend Status |

### Level Formula

```
EXP for Level N = 100 × N × (N + 1) / 2
Total EXP at Level N = 100 × N × (N + 1) × (N + 2) / 6
```

---

## EXP Sources

### Daily Activities

| Activity | EXP | Daily Limit |
|----------|-----|-------------|
| Daily login | 50 | 1 |
| First message | 10 | 1 |
| First room join | 20 | 1 |
| Complete daily tasks | 100 | 1 |

### Room Activities

| Activity | EXP | Limit |
|----------|-----|-------|
| Join a room | 5 | 20/day |
| Spend 10 min in room | 10 | 60/day |
| Send chat message | 1 | 100/day |
| Use mic (per minute) | 2 | 120/day |
| Host room (per 10 min) | 15 | 90/day |

### Social Activities

| Activity | EXP | Limit |
|----------|-----|-------|
| Add friend | 25 | 10/day |
| Accept friend request | 15 | 10/day |
| Visit profile | 2 | 50/day |
| Follow user | 10 | 20/day |
| Get followed | 5 | Unlimited |

### Gift Activities

| Activity | EXP | Notes |
|----------|-----|-------|
| Send gift | 1 per 100 coins | Max 500/day |
| Receive gift | 1 per 200 coins | Unlimited |
| Send to new user | 2x bonus | First gift to user |

### Game Activities

| Activity | EXP |
|----------|-----|
| Play any game | 10 |
| Win any game | 25 |
| Win streak (3+) | 50 bonus |
| Hit jackpot | 500 |
| Daily first game | 100 |

### Special Activities

| Activity | EXP |
|----------|-----|
| Complete profile | 200 (one-time) |
| Verify phone | 300 (one-time) |
| Link social account | 100 (per platform) |
| First recharge | 500 (one-time) |
| Join family | 200 (one-time) |
| Form CP | 500 (one-time) |

---

## VIP EXP Multipliers

VIP users earn bonus EXP:

| VIP Tier | EXP Multiplier |
|----------|----------------|
| VIP 1 | 1.1x |
| VIP 2 | 1.15x |
| VIP 3 | 1.2x |
| VIP 4 | 1.25x |
| VIP 5 | 1.3x |
| VIP 6 | 1.35x |
| VIP 7 | 1.4x |
| VIP 8 | 1.45x |
| VIP 9 | 1.5x |
| VIP 10 | 1.6x |

---

## Level Unlocks

### Feature Unlocks

| Level | Feature Unlocked |
|-------|------------------|
| 1 | Basic room access |
| 5 | Create rooms |
| 10 | Custom room settings |
| 15 | 12-seat rooms |
| 20 | 16-seat rooms, Super Mic access |
| 25 | Advanced room themes |
| 30 | Premium game access |
| 40 | VIP room creation |
| 50 | Exclusive events access |
| 60 | Legend chat badge |

### Room Capacity by Level

| Level | Max Room Seats |
|-------|----------------|
| 1-9 | 8 seats |
| 10-14 | 10 seats |
| 15-19 | 12 seats |
| 20-29 | 16 seats |
| 30-49 | 20 seats |
| 50+ | 24 seats |

---

## Firebase Integration

### Database Structure

```
/users/{userId}
  /level
    - currentLevel: number
    - currentExp: number
    - totalExp: number
    - expToNextLevel: number
    - lastLevelUp: timestamp
    
  /expHistory
    /{date}
      - earned: number
      - breakdown: {
          daily: number,
          room: number,
          social: number,
          gifts: number,
          games: number
        }
        
/levelRewards
  /{level}
    - coins: number
    - cosmetics: array
    - features: array
    - claimed: boolean
```

### Security Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/level {
      allow read: if true; // Public level info
      allow write: if false; // Server only
    }
    
    match /users/{userId}/expHistory/{date} {
      allow read: if request.auth.uid == userId;
      allow write: if false; // Server only
    }
    
    match /levelRewards/{level} {
      allow read: if true;
      allow write: if false; // Admin only
    }
  }
}
```

### Cloud Functions

```typescript
// Award EXP function
export const awardExp = functions.https.onCall(async (data, context) => {
  const { userId, amount, source, description } = data;
  
  // Validate
  if (!context.auth) throw new Error('Unauthenticated');
  
  // Get current level data
  const userRef = admin.firestore().doc(`users/${userId}/level`);
  const userData = await userRef.get();
  
  // Calculate new totals
  const currentExp = userData.data()?.currentExp || 0;
  const totalExp = userData.data()?.totalExp || 0;
  const currentLevel = userData.data()?.currentLevel || 1;
  
  // Apply VIP multiplier
  const vipMultiplier = await getVipMultiplier(userId);
  const finalAmount = Math.floor(amount * vipMultiplier);
  
  // Update EXP
  const newTotalExp = totalExp + finalAmount;
  const newLevel = calculateLevel(newTotalExp);
  
  // Update database
  await userRef.update({
    currentExp: calculateCurrentLevelExp(newTotalExp, newLevel),
    totalExp: newTotalExp,
    currentLevel: newLevel,
    expToNextLevel: calculateExpToNextLevel(newTotalExp, newLevel),
    lastUpdated: admin.firestore.FieldValue.serverTimestamp()
  });
  
  // Check for level up
  if (newLevel > currentLevel) {
    await handleLevelUp(userId, currentLevel, newLevel);
  }
  
  return { success: true, expAwarded: finalAmount, newLevel, newTotalExp };
});

// Level up handler
async function handleLevelUp(userId: string, oldLevel: number, newLevel: number) {
  // Award level rewards for each level gained
  for (let level = oldLevel + 1; level <= newLevel; level++) {
    const rewards = await getLevelRewards(level);
    await grantRewards(userId, rewards);
    
    // Send notification
    await sendLevelUpNotification(userId, level, rewards);
  }
  
  // Log analytics event
  await logAnalyticsEvent('level_up', {
    userId,
    oldLevel,
    newLevel,
    timestamp: Date.now()
  });
}
```

---

## Level Display

### Profile Display

- Level badge next to username
- Progress bar to next level
- Current EXP / Required EXP display

### Level Badge Styles

| Level Range | Badge Style |
|-------------|-------------|
| 1-9 | Bronze border |
| 10-19 | Silver border |
| 20-29 | Gold border |
| 30-39 | Platinum border |
| 40-49 | Diamond border |
| 50-59 | Ruby border |
| 60-69 | Emerald border |
| 70-79 | Sapphire border |
| 80-89 | Crown badge |
| 90-99 | Master badge |
| 100 | Legend badge (animated) |

---

## API Endpoints

```
# Level Info
GET /users/{userId}/level
GET /users/me/level

# EXP History
GET /users/me/exp/history
GET /users/me/exp/today

# Level Rewards
GET /level/rewards/{level}
POST /level/rewards/{level}/claim

# Leaderboard
GET /level/leaderboard
GET /level/leaderboard/friends
```

---

## Data Model

### User Level

```json
{
  "level": {
    "userId": "user_123",
    "currentLevel": 45,
    "currentExp": 12500,
    "totalExp": 101250,
    "expToNextLevel": 2500,
    "percentToNext": 83,
    "lastLevelUp": "2025-11-25T10:00:00Z",
    "features": ["super_mic", "16_seats", "premium_games"],
    "badge": {
      "type": "diamond",
      "animated": false
    }
  }
}
```

### Daily EXP Summary

```json
{
  "expSummary": {
    "date": "2025-11-28",
    "total": 450,
    "breakdown": {
      "daily": 50,
      "room": 120,
      "social": 80,
      "gifts": 150,
      "games": 50
    },
    "multiplier": 1.3,
    "bonusApplied": 135
  }
}
```

---

## Telemetry Events

| Event | Properties |
|-------|------------|
| `exp_earned` | amount, source, multiplier |
| `level_up` | oldLevel, newLevel, totalExp |
| `level_reward_claim` | level, rewards |
| `feature_unlock` | feature, level |

---

## Related Documentation

- [Games System](./games-system.md)
- [VIP Systems](./features/vip-systems.md)
- [Medals System](./medals-system.md)
- [Daily Rewards](./features/daily-rewards.md)
