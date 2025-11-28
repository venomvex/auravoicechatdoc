# Games System

Comprehensive documentation for all games in Aura Voice Chat, including mechanics, logic, Firebase integration, and reward systems.

## Overview

Aura Voice Chat features multiple interactive games that users can play in rooms. Games provide entertainment and opportunities to win coins through skill and chance.

---

## Available Games

### 1. Lucky Spin Wheel

**Mechanics:**
- Wheel with multiple prize segments
- Each spin costs coins
- Prizes range from small rewards to jackpots

| Segment | Probability | Reward |
|---------|------------|--------|
| 2x | 25% | 2x bet amount |
| 3x | 20% | 3x bet amount |
| 5x | 15% | 5x bet amount |
| 10x | 10% | 10x bet amount |
| 25x | 5% | 25x bet amount |
| 50x | 3% | 50x bet amount |
| 100x | 1.5% | 100x bet amount |
| Jackpot | 0.5% | 500x bet amount |
| Lose | 20% | 0 coins |

**Bet Amounts:**
| Tier | Amount | Min Level |
|------|--------|-----------|
| Low | 1,000 coins | 1 |
| Medium | 10,000 coins | 10 |
| High | 100,000 coins | 20 |
| VIP | 1,000,000 coins | 30 + VIP 3 |

**Firebase Structure:**
```json
{
  "games/lucky_spin/{sessionId}": {
    "userId": "user_123",
    "betAmount": 10000,
    "result": "5x",
    "winAmount": 50000,
    "timestamp": 1701234567890,
    "roomId": "room_456"
  }
}
```

---

### 2. Dice Roll

**Mechanics:**
- Roll two dice (1-6 each)
- Bet on outcome: High (8-12), Low (2-6), or Specific (7)
- Payouts vary by bet type

| Bet Type | Outcome | Payout |
|----------|---------|--------|
| High | Sum 8-12 | 1.8x |
| Low | Sum 2-6 | 1.8x |
| Seven | Sum = 7 | 5x |
| Double | Same number | 10x |
| Snake Eyes | Both 1s | 30x |
| Boxcars | Both 6s | 30x |

**Bet Limits:**
- Minimum: 500 coins
- Maximum: 500,000 coins

**Firebase Structure:**
```json
{
  "games/dice/{sessionId}": {
    "userId": "user_123",
    "betType": "high",
    "betAmount": 5000,
    "dice1": 4,
    "dice2": 5,
    "sum": 9,
    "won": true,
    "winAmount": 9000,
    "timestamp": 1701234567890
  }
}
```

---

### 3. Card Flip (Higher or Lower)

**Mechanics:**
- One card shown face-up
- Guess if next card is higher or lower
- Consecutive correct guesses multiply winnings

| Streak | Multiplier | Cumulative |
|--------|------------|------------|
| 1 | 1.5x | 1.5x |
| 2 | 1.5x | 2.25x |
| 3 | 2x | 4.5x |
| 4 | 2x | 9x |
| 5 | 3x | 27x |
| 6+ | 5x | 135x+ |

**Rules:**
- Same value = automatic loss
- Can cash out after any correct guess
- Ace can be high or low (player's choice)

**Firebase Structure:**
```json
{
  "games/card_flip/{sessionId}": {
    "userId": "user_123",
    "initialBet": 10000,
    "currentCard": "7H",
    "streak": 3,
    "currentMultiplier": 4.5,
    "potentialWin": 45000,
    "history": ["3D", "7H"],
    "status": "active"
  }
}
```

---

### 4. Treasure Box

**Mechanics:**
- Grid of 9 treasure boxes
- 3 contain prizes, 3 contain bombs, 3 contain multipliers
- Pick boxes until you hit a bomb or choose to cash out

| Box Type | Effect |
|----------|--------|
| Coin Box | Add fixed coins to pot |
| Multiplier (2x) | Double current pot |
| Multiplier (3x) | Triple current pot |
| Bomb | Lose all, game ends |

**Entry Fee:** 5,000 - 100,000 coins

**Firebase Structure:**
```json
{
  "games/treasure/{sessionId}": {
    "userId": "user_123",
    "entryFee": 10000,
    "boxGrid": ["coin", "bomb", "2x", "coin", "bomb", "3x", "coin", "bomb", "2x"],
    "revealed": [0, 3],
    "currentPot": 25000,
    "status": "active"
  }
}
```

---

### 5. Lucky Number

**Mechanics:**
- Pick a number between 1-100
- System generates random number
- Closer your pick = higher reward

| Distance | Payout |
|----------|--------|
| Exact match | 100x |
| Within 1 | 50x |
| Within 3 | 20x |
| Within 5 | 10x |
| Within 10 | 5x |
| Within 20 | 2x |
| >20 away | 0x |

**Bet Range:** 1,000 - 250,000 coins

---

### 6. Coin Toss

**Mechanics:**
- Simple heads or tails
- 50/50 chance
- Option to double down

| Bet Type | Payout |
|----------|--------|
| Single | 1.9x |
| Double (2 correct) | 3.6x |
| Triple (3 correct) | 6.8x |

---

### 7. Slot Machine

**Mechanics:**
- 3-reel classic slot machine
- Multiple symbol combinations
- Progressive jackpot

| Combination | Payout |
|-------------|--------|
| 🍒🍒🍒 | 5x |
| 🍋🍋🍋 | 10x |
| 🍊🍊🍊 | 15x |
| ⭐⭐⭐ | 25x |
| 💎💎💎 | 50x |
| 7️⃣7️⃣7️⃣ | 100x |
| 👑👑👑 | Progressive Jackpot |

**Progressive Jackpot:**
- 1% of all bets contribute to jackpot pool
- Minimum jackpot: 10,000,000 coins
- Resets to minimum after win

---

## Game Rules & Fair Play

### Random Number Generation (RNG)

All games use cryptographically secure RNG:
- Server-side generation only
- Seeds based on timestamp + user ID + room ID
- Audit logs maintained for 90 days
- Third-party verification available

### Anti-Cheat Measures

| Measure | Implementation |
|---------|----------------|
| Rate Limiting | Max 60 games/minute |
| Pattern Detection | Identify bot behavior |
| Session Validation | Verify authentic sessions |
| Result Verification | Server-authoritative outcomes |

### Responsible Gaming

| Feature | Implementation |
|---------|----------------|
| Daily Loss Limit | Configurable (default: 1,000,000 coins) |
| Session Timeout | Auto-logout after 4 hours continuous play |
| Cool-off Period | 24-hour self-exclusion option |
| Spending Alerts | Notification at 50%, 75%, 100% of limit |

---

## Firebase Database Structure

### Games Collection

```
/games
  /{gameType}
    /{sessionId}
      - userId: string
      - roomId: string
      - betAmount: number
      - result: object
      - winAmount: number
      - timestamp: timestamp
      - verified: boolean
      
/gameStats
  /{userId}
    /summary
      - totalPlayed: number
      - totalWon: number
      - totalLost: number
      - biggestWin: number
      - favoriteGame: string
    /daily
      /{date}
        - played: number
        - won: number
        - lost: number
        
/jackpots
  /{gameType}
    - currentAmount: number
    - lastWinner: string
    - lastWinAmount: number
    - lastWinDate: timestamp
```

### Security Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /games/{gameType}/{sessionId} {
      allow read: if request.auth.uid == resource.data.userId;
      allow create: if request.auth != null 
        && request.resource.data.userId == request.auth.uid
        && validateBet(request.resource.data);
      allow update: if false; // Server only
    }
    
    match /gameStats/{userId}/{document=**} {
      allow read: if request.auth.uid == userId;
      allow write: if false; // Server only
    }
    
    match /jackpots/{gameType} {
      allow read: if true;
      allow write: if false; // Server only
    }
    
    function validateBet(data) {
      return data.betAmount >= 500 
        && data.betAmount <= 1000000
        && data.timestamp == request.time;
    }
  }
}
```

---

## Game Rewards & EXP

### EXP from Games

| Activity | EXP Earned |
|----------|------------|
| Play any game | 10 EXP |
| Win any game | 25 EXP |
| Win streak (3+) | 50 EXP bonus |
| Hit jackpot | 500 EXP |
| Daily first game | 100 EXP |

### Coin Rewards Integration

- Game winnings credited instantly
- VIP multipliers apply to base winnings
- Losses deducted from wallet immediately
- Transaction history shows all game activity

---

## API Endpoints

```
# Game Sessions
POST /games/{gameType}/start
POST /games/{gameType}/action
POST /games/{gameType}/cashout
GET /games/{gameType}/history

# Stats
GET /games/stats/me
GET /games/stats/leaderboard

# Jackpots
GET /games/jackpots
GET /games/jackpots/{gameType}
```

---

## Data Models

### Game Session

```json
{
  "session": {
    "id": "session_abc123",
    "gameType": "lucky_spin",
    "userId": "user_123",
    "roomId": "room_456",
    "betAmount": 10000,
    "status": "completed",
    "result": {
      "segment": "5x",
      "multiplier": 5
    },
    "winAmount": 50000,
    "expEarned": 25,
    "createdAt": "2025-11-28T10:00:00Z",
    "completedAt": "2025-11-28T10:00:05Z"
  }
}
```

---

## Telemetry Events

| Event | Properties |
|-------|------------|
| `game_start` | gameType, betAmount, roomId |
| `game_action` | gameType, action, sessionId |
| `game_win` | gameType, winAmount, multiplier |
| `game_loss` | gameType, lossAmount |
| `game_cashout` | gameType, cashedAmount |
| `jackpot_win` | gameType, jackpotAmount |

---

## Related Documentation

- [EXP & Level System](./exp-level-system.md)
- [Events System](./events-system.md)
- [Wallet](./features/wallet.md)
- [Firebase Setup](./firebase-setup.md)
