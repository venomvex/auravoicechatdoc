# Greedy Baby Game

Complete documentation for the Greedy Baby circular betting wheel game in Aura Voice Chat.

**Developer:** Hawkaye Visions LTD — Pakistan

---

## Overview

Greedy Baby is a **live multiplayer betting game** where users place bets on food items arranged in a circular wheel. The game features timer-based rounds, real-time multiplayer betting, and configurable win/loss algorithms.

**This is NOT a single-player feeding game.** It's a live in-room betting game where multiple users compete simultaneously.

---

## Game Structure

### Header Section

| Element | Position | Description |
|---------|----------|-------------|
| Coins Display | Top Left | Shows user's coin balance with live updates from wallet |
| Add Coins (+) | Next to coins | Quick access to purchase more coins |
| Game Title | Center | "Greedy Baby" |
| Sound Toggle | Top Right | Mute/Unmute game sounds |
| Help Icon (?) | Top Right | Opens game rules popup |
| Close Button (X) | Top Right | Exits the game |

### Action Icons

| Icon | Position | Function |
|------|----------|----------|
| Trophy 🏆 | Below header, left | Daily and Weekly winnings rankings |
| Clock ⏱️ | Below header, right | Personal winning records history |

---

## Circular Wheel Layout

The wheel contains **8 food items** arranged clockwise starting from the top (12 o'clock position):

| Position | Item | Emoji | Multiplier | Category |
|----------|------|-------|------------|----------|
| Top (12 o'clock) | Apple | 🍎 | 5x | Fruit |
| Top-Right | Lemon | 🍋 | 5x | Fruit |
| Right | Strawberry | 🍓 | 5x | Fruit |
| Bottom-Right | Mango | 🥭 | 5x | Fruit |
| Bottom (6 o'clock) | Fish | 🐟 | 10x | Non-Fruit |
| Bottom-Left | Burger | 🍔 | 15x | Non-Fruit |
| Left | Pizza | 🍕 | 25x | Non-Fruit |
| Top-Left | Chicken | 🍗 | 45x | Non-Fruit |

### Center Element

- **Mascot:** Elephant (🐘) with fork - the "Greedy Baby"
- **Timer Display:** Shows current phase and countdown
  - "Bet Time" with seconds during betting phase
  - "Show Time" during spin/reveal phase
  - "Result" during result phase

---

## Special Combo Features

### Fruit Basket 🧺

- **Location:** Bottom left of wheel
- **Trigger Rate:** 3% (configurable)
- **Mechanic:** When result shows "Fruit Basket", users who bet on **ALL 4 fruits** (Apple, Lemon, Strawberry, Mango) win on ALL of them

### Full Pizza 🍕

- **Location:** Bottom right of wheel
- **Trigger Rate:** 2% (configurable)
- **Mechanic:** When result shows "Full Pizza", users who bet on **ALL 4 non-fruits** (Fish, Burger, Pizza, Chicken) win on ALL of them

### Today's Winning

- **Location:** Between Fruit Basket and Full Pizza
- **Display:** Shows total winnings for the current day

---

## Betting System

### Chip Values

| Chip Display | Value (Coins) |
|--------------|---------------|
| 100 | 100 |
| 1K | 1,000 |
| 5K | 5,000 |
| 10K | 10,000 |
| 50K | 50,000 |
| 100K | 100,000 |
| 1M | 1,000,000 |
| 2M | 2,000,000 |
| 5M | 5,000,000 |
| 10M | 10,000,000 |
| 50M | 50,000,000 |

### Betting Flow

1. **Select Chip:** Choose a chip value from the chip selection box
2. **Place Bet:** Tap on a food item in the wheel
3. **Animation:** Chip flies from selection to the item
4. **Stack Bets:** Each tap places one chip; multiple taps stack chips
5. **Live Updates:** Other players' bets visible in real-time

### Betting Phase

- Duration: 15 seconds (configurable)
- Users can place multiple bets on multiple items
- Bets are deducted from wallet immediately
- Visual indicators show bet amounts on each item

---

## Game Phases

### 1. Betting Phase (15 seconds)

- Timer shows "Bet Time"
- Users select chips and place bets
- Live display of other players' bets

### 2. Show Time Phase (7 seconds)

- Timer shows "Show Time"
- Wheel spins/animates
- Betting disabled
- Anticipation builds

### 3. Result Phase (5 seconds)

- Winning item highlighted
- Result popup shows:
  - User's total winnings for this round
  - Top 3 winners with their amounts
  - Wallet update animation

---

## Result Display

### Last 10 Results

- **Location:** Bottom of screen
- **Display:** Icons of last 10 winning items
- **Latest:** Marked with "New" badge

### Result Popup

When timer reaches zero:

1. **Winner Announcement:** Which item won
2. **Special Result:** If Fruit Basket or Full Pizza triggered
3. **User Winnings:** How much you won/lost
4. **Top 3 Winners:** Leaderboard for this round
5. **Wallet Update:** Animated coin addition to balance

---

## Win/Loss Algorithm

### NOT Random

The algorithm is **complex and configurable**, not simple random generation.

### Default Probabilities

| Item | Base Win Rate |
|------|---------------|
| Apple | 17% |
| Lemon | 17% |
| Strawberry | 17% |
| Mango | 17% |
| Fish | 12% |
| Burger | 8% |
| Pizza | 5% |
| Chicken | 2% |
| **Total Fruits** | **68%** |
| **Total Non-Fruits** | **27%** |
| **Fruit Basket** | 3% |
| **Full Pizza** | 2% |

### House Edge System

The algorithm maintains a configurable house edge (default 8%) through:

1. **Pool Tracking:** Monitor total bets vs payouts
2. **Dynamic Adjustment:** Rates adjust based on pool status
3. **Pattern Prevention:** Avoid predictable winning patterns
4. **Variance Control:** Manage lucky/unlucky streaks

### Pool Rebalancing

When pool losses exceed threshold:
- Reduce high multiplier win rates (Chicken, Pizza, Burger)
- Increase fruit win rates to compensate

When pool profits exceed threshold:
- Slightly increase high multiplier win rates
- Reduce fruit win rates to compensate

---

## Owner Panel Configuration

### Available Settings

| Setting | Description | Default | Range |
|---------|-------------|---------|-------|
| House Edge % | Platform profit margin | 8% | 0-20% |
| Max Win per Round | Cap on single round winnings | 100,000,000 | 1M-1B |
| Apple Win Rate | Base probability | 17% | 10-25% |
| Lemon Win Rate | Base probability | 17% | 10-25% |
| Strawberry Win Rate | Base probability | 17% | 10-25% |
| Mango Win Rate | Base probability | 17% | 10-25% |
| Fish Win Rate | Base probability | 12% | 5-20% |
| Burger Win Rate | Base probability | 8% | 3-15% |
| Pizza Win Rate | Base probability | 5% | 1-10% |
| Chicken Win Rate | Base probability | 2% | 0.5-5% |
| Fruit Basket Trigger | How often full basket wins | 3% | 0-10% |
| Full Pizza Trigger | How often full pizza wins | 2% | 0-10% |
| Pool Rebalance Threshold | When to adjust rates | 1,000,000 | 100K-10M |

### Tuning Guidelines

1. **Conservative Settings:**
   - Higher house edge (10-15%)
   - Lower high multiplier rates
   - Higher rebalance threshold

2. **Player-Friendly Settings:**
   - Lower house edge (5-8%)
   - Standard multiplier rates
   - More frequent special results

3. **Special Events:**
   - Temporarily increase special result rates
   - Lower house edge during promotions

---

## API Endpoints

### Game Actions

```
# Start game session
POST /games/greedy_baby/start
Body: { betAmount: number, roomId?: string }

# Place bets and resolve round
POST /games/greedy_baby/action
Body: {
  sessionId: string,
  action: "play",
  data: {
    bets: [
      { itemId: string, chipValue: number, chipCount: number }
    ],
    roomId?: string
  }
}

# Get game history
GET /games/greedy_baby/history?page=1&limit=20
```

### Rankings

```
# Get daily rankings
GET /games/greedy-baby/rankings/daily?limit=50

# Get weekly rankings
GET /games/greedy-baby/rankings/weekly?limit=50
```

### Owner Panel (Admin Only)

```
# Get current configuration
GET /games/greedy-baby/config

# Update configuration
PUT /games/greedy-baby/config
Body: { houseEdge?: number, winRates?: {...}, ... }

# Get pool statistics
GET /games/greedy-baby/pool-stats

# Reset rankings
POST /games/greedy-baby/rankings/reset
Body: { type: "daily" | "weekly" | "both" }
```

---

## Database Structure

### Round Data

```json
{
  "greedy_baby_rounds": {
    "roundId": "uuid",
    "roomId": "room_123",
    "startTime": "2025-11-30T10:00:00Z",
    "endTime": "2025-11-30T10:00:27Z",
    "result": "apple",
    "specialResult": null,
    "totalBets": 5000000,
    "totalPayouts": 4200000,
    "bets": [
      {
        "userId": "user_123",
        "itemId": "apple",
        "chipValue": 100000,
        "chipCount": 5,
        "totalBet": 500000,
        "won": true,
        "payout": 2500000
      }
    ]
  }
}
```

### Rankings

```json
{
  "greedy_baby_rankings": {
    "daily": {
      "user_123": 5000000,
      "user_456": 3200000
    },
    "weekly": {
      "user_123": 25000000,
      "user_456": 18000000
    }
  }
}
```

### Configuration

```json
{
  "greedy_baby_config": {
    "houseEdge": 8,
    "maxWinPerRound": 100000000,
    "winRates": {
      "apple": 17,
      "lemon": 17,
      "strawberry": 17,
      "mango": 17,
      "fish": 12,
      "burger": 8,
      "pizza": 5,
      "chicken": 2
    },
    "fruitBasketTriggerRate": 3,
    "fullPizzaTriggerRate": 2,
    "poolRebalanceThreshold": 1000000
  }
}
```

---

## WebSocket Events

### Client → Server

```typescript
// Join game room
{ event: "greedy_baby:join", data: { roomId: string } }

// Place bet
{ event: "greedy_baby:bet", data: { itemId: string, chipValue: number } }

// Leave game
{ event: "greedy_baby:leave" }
```

### Server → Client

```typescript
// Game state update
{ event: "greedy_baby:state", data: {
  phase: "betting" | "show" | "result",
  timer: number,
  bets: { [itemId]: { total: number, users: number } }
}}

// Bet placed (by any user)
{ event: "greedy_baby:bet_placed", data: {
  userId: string,
  itemId: string,
  amount: number
}}

// Round result
{ event: "greedy_baby:result", data: {
  winningItem: string,
  specialResult: string | null,
  topWinners: [{ userId, username, amount }],
  userResult: { won: boolean, amount: number }
}}
```

---

## Medal Integration

Greedy Baby integrates with the **Greedy Medal** in the medal system:

| Medal | Requirement |
|-------|-------------|
| Greedy Baby Bronze | Win 100,000 total |
| Greedy Baby Silver | Win 1,000,000 total |
| Greedy Baby Gold | Win 10,000,000 total |
| Greedy Baby Platinum | Win 100,000,000 total |
| Greedy Baby Diamond | Win 1,000,000,000 total |

---

## Visual Reference

Screenshots of the Greedy Baby game UI are available in:
- `/docs/assets/Greedy (1).jpeg` - Betting phase
- `/docs/assets/Greedy (2).jpeg` - Show time with top winners
- `/docs/assets/Greedy (3).jpeg` - Betting with chips placed

---

## Related Documentation

- [Games System](./games-system.md)
- [Owner Panel](./owner-panel.md)
- [Medals System](./medals-system.md)
- [EXP & Level System](./exp-level-system.md)
