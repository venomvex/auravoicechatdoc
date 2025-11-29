# Games System

Comprehensive documentation for all games in Aura Voice Chat, including mechanics, logic, Firebase integration, and reward systems.

## Overview

Aura Voice Chat features five interactive games that users can play in rooms. Games provide entertainment and opportunities to win coins through skill and chance.

---

## Available Games

### 1. Lucky 777 Pro (5-Line Slot Machine)

**Mechanics:**
- 5-reel slot machine with 5 paylines
- Classic fruit symbols with high payouts
- Progressive jackpot available

| Symbol | Payout (3 Match) |
|--------|------------------|
| 777 | 1,000,000,000 |
| Bell | 300,000,000 |
| Diamond | 100,000,000 |
| Watermelon | 50,000,000 |
| Orange | 30,000,000 |
| Grape | 15,000,000 |
| Mango | 5,000,000 |
| Cherry | 3,000,000 |

**Bet Amounts:**
- Minimum: 100 coins
- Maximum: 500,000 coins

**Firebase Structure:**
```json
{
  "games/lucky_777_pro/{sessionId}": {
    "userId": "user_123",
    "betAmount": 10000,
    "reels": [["cherry", "bell", "777"], ["orange", "777", "diamond"], ...],
    "middleRow": ["bell", "777", "777", "grape", "mango"],
    "winningSymbol": null,
    "matchCount": 0,
    "isJackpot": false,
    "winAmount": 0,
    "timestamp": 1701234567890,
    "roomId": "room_456"
  }
}
```

---

### 2. Lucky 77 Pro (Single-Line Slot Machine)

**Mechanics:**
- 3-reel classic slot machine
- Simple and fast gameplay
- 77x multiplier for triple 7s

| Combination | Multiplier |
|-------------|------------|
| 7-7-7 | 77x |
| Crown-Crown-Crown | 50x |
| Star-Star-Star | 25x |
| Gem-Gem-Gem | 15x |
| Bell-Bell-Bell | 10x |
| Cherry-Cherry-Cherry | 5x |
| Bar-Bar-Bar | 3x |

**Bet Limits:**
- Minimum: 100 coins
- Maximum: 100,000 coins

**Firebase Structure:**
```json
{
  "games/lucky_77_pro/{sessionId}": {
    "userId": "user_123",
    "betAmount": 5000,
    "reels": ["7", "7", "crown"],
    "multiplier": 0,
    "isJackpot": false,
    "winAmount": 0,
    "timestamp": 1701234567890
  }
}
```

---

### 3. Greedy Baby (Food Wheel Selection)

**Mechanics:**
- Colorful food-themed wheel with 9 items
- Select an item and bet on the outcome
- Timer counts down during bet period
- Elephant mascot spins the wheel

| Food Item | Multiplier | Probability |
|-----------|------------|-------------|
| Chicken | 45x | 2% |
| Pizza | 25x | 5% |
| Burger | 25x | 5% |
| Orange | 15x | 8% |
| Fish | 10x | 12% |
| Apple | 5x | 20% |
| Lemon | 5x | 20% |
| Strawberry | 5x | 20% |
| Fruit Bowl | 5x | 8% |

**Bet Options:** 100, 1K, 5K, 10K, 50K, 100K coins

**Firebase Structure:**
```json
{
  "games/greedy_baby/{sessionId}": {
    "userId": "user_123",
    "betAmount": 10000,
    "selectedItem": "chicken",
    "winningItem": "pizza",
    "won": false,
    "winAmount": 0,
    "todaysWin": 0,
    "timestamp": 1701234567890
  }
}
```

---

### 4. Lucky Fruit (3x3 Grid Fruit Selection)

**Mechanics:**
- 3x3 grid of fruit slots
- Select one fruit and bet on the outcome
- Special Lucky and Super Lucky bonus items
- Result history shown at bottom

| Fruit | Multiplier | Probability |
|-------|------------|-------------|
| Strawberry | 45x | 3% |
| Mango | 25x | 6% |
| Watermelon | 15x | 8% |
| Apple | 10x | 5% |
| Orange | 5x | 20% |
| Lemon | 5x | 20% |
| Grape | 5x | 18% |
| Cherry | 5x | 18% |
| Lucky | 10-40x bonus | 1.5% |
| Super Lucky | 50-150x bonus | 0.5% |

**Bet Options:** 5K, 10K, 50K, 100K, 500K coins

**Firebase Structure:**
```json
{
  "games/lucky_fruit/{sessionId}": {
    "userId": "user_123",
    "betAmount": 50000,
    "selectedFruit": "lemon",
    "winningFruit": "super_lucky",
    "won": true,
    "specialBonus": "super_lucky",
    "winAmount": 5000000,
    "resultHistory": [{"fruit": "orange", "timestamp": "..."}],
    "timestamp": 1701234567890
  }
}
```

---

### 5. Gift Wheel System

**Mechanics:**
- Spin the wheel to win gift items
- Standard and Advanced (premium) draw options
- Draw records track all wins
- Items can be collected or sent as gifts

| Gift Item | Value | Standard Prob | Advanced Prob |
|-----------|-------|---------------|---------------|
| Love Wings | 920,000 | 1% | 2% |
| Rose Bouquet | 100,000 | 5% | 10% |
| Golden Butterfly | 50,000 | 10% | 15% |
| Heart Gift | 35,000 | 15% | 18% |
| Crown | 25,000 | 18% | 15% |
| Star | 15,000 | 20% | 16% |
| Gem | 10,000 | 22% | 18% |
| Lucky Coin | 5,000 | 9% | 6% |

**Spin Costs:**
- Standard Draw: 10,000 coins per spin
- Advanced Draw: 50,000 coins per spin
- Multi-draw options: 1x, 10x

**Draw Records:**
- Full history of all draws
- Shows draw type, items won, total value
- Date and time of each draw

**Firebase Structure:**
```json
{
  "games/gift_wheel/{sessionId}": {
    "userId": "user_123",
    "drawType": "advanced",
    "drawCount": 10,
    "wonItems": [
      {"id": "rose_bouquet", "name": "Rose Bouquet", "value": 100000},
      {"id": "heart_gift", "name": "Heart Gift", "value": 35000}
    ],
    "totalValue": 920000,
    "timestamp": 1701234567890
  }
}
```

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
  /lucky_777_pro
    - currentAmount: number
    - lastWinner: string
    - lastWinAmount: number
    - lastWinDate: timestamp
  /lucky_77_pro
    - currentAmount: number
    - lastWinner: string
    - lastWinAmount: number
    - lastWinDate: timestamp

/giftWheelRecords
  /{userId}
    /{recordId}
      - drawType: string
      - drawCount: number
      - items: array
      - totalValue: number
      - timestamp: timestamp
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
    
    match /giftWheelRecords/{userId}/{recordId} {
      allow read: if request.auth.uid == userId;
      allow write: if false; // Server only
    }
    
    function validateBet(data) {
      return data.betAmount >= 100 
        && data.betAmount <= 500000
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
| Lucky/Super Lucky bonus | 100 EXP |

### Coin Rewards Integration

- Game winnings credited instantly
- VIP multipliers apply to base winnings
- Losses deducted from wallet immediately
- Transaction history shows all game activity

---

## API Endpoints

```
# Game Info
GET /games
GET /games/stats
GET /games/jackpots
GET /games/jackpots/{gameType}

# Game Sessions
POST /games/{gameType}/start
POST /games/{gameType}/action
POST /games/{gameType}/cashout
GET /games/{gameType}/history

# Gift Wheel Specific
GET /games/gift-wheel/draw-records
```

---

## Data Models

### Game Session

```json
{
  "session": {
    "id": "session_abc123",
    "gameType": "lucky_fruit",
    "userId": "user_123",
    "roomId": "room_456",
    "betAmount": 50000,
    "status": "completed",
    "result": {
      "selectedFruit": "lemon",
      "winningFruit": "super_lucky",
      "won": true,
      "specialBonus": "super_lucky",
      "multiplier": 100
    },
    "winAmount": 5000000,
    "expEarned": 100,
    "createdAt": "2025-11-28T10:00:00Z",
    "completedAt": "2025-11-28T10:00:05Z"
  }
}
```

### Gift Wheel Draw Record

```json
{
  "record": {
    "id": "record_xyz789",
    "drawType": "advanced",
    "drawCount": 10,
    "items": [
      {"id": "rose_bouquet", "name": "Rose Bouquet", "value": 100000},
      {"id": "golden_butterfly", "name": "Golden Butterfly", "value": 50000},
      {"id": "heart_gift", "name": "Heart Gift", "value": 35000}
    ],
    "totalValue": 920000,
    "timestamp": "2025-11-28T10:00:00Z"
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
| `gift_wheel_draw` | drawType, drawCount, totalValue |
| `special_bonus` | gameType, bonusType, multiplier |

---

## Related Documentation

- [EXP & Level System](./exp-level-system.md)
- [Events System](./events-system.md)
- [Wallet](./features/wallet.md)
- [Firebase Setup](./firebase-setup.md)
