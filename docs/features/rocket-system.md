# Rocket Launch System (Gamified Engagement)

## 1. Concept Overview

The Rocket System gamifies cumulative sending (coins spent as gifts) inside a room within a specific region. Each room tracks its own rocket progression. Six rockets correspond to ascending activity milestones. When a rocket milestone is met, a timed event sequence (countdown → launch → reward distribution → progression) triggers for that room and region.

---

## 2. Rocket Milestones (Per Room)

| Rocket | Launch Trigger (Total Coins Sent) | Description              |
|--------|-----------------------------------|--------------------------|
| 1      | 2,500,000                         | First small milestone    |
| 2      | 5,000,000                         | Growing excitement       |
| 3      | 10,000,000                        | Mid-level event          |
| 4      | 15,000,000                        | High activity milestone  |
| 5      | 20,000,000                        | Major milestone          |
| 6      | 30,000,000                        | Grand rocket event       |

Progress resets only if a room is archived or explicitly reset by admin; otherwise rockets 1→6 proceed linearly.

---

## 3. Event Sequence

1. **Threshold Reached:**
   - Server marks rocket milestone met
   - Generates event entry: `rocket_event_created(roomId, rocketNumber, region)`

2. **Pre-Launch Countdown (10 seconds):**
   - Visible overlay in the room (banner with countdown)
   - FCM notifications sent to region users

3. **Launch Phase:**
   - Rocket animation plays (duration ~3–5s)
   - Contribution snapshot is finalized at launch start

4. **Rewards Distribution:**
   - Top 3 senders computed from contribution window
   - Random room participant rewards rolled
   - Chance-based teddy bear gift (1M coin value) checked for Top 1 sender

5. **Post-Launch:**
   - Broadcast reward summary message in-room
   - Update room rocket state to next rocket (or mark "Complete" after Rocket 6)
   - Persist leaderboard deltas

---

## 4. Reward Structure

### Top Sender Rewards Per Rocket

| Rocket | Top 1 Sender Reward | Chance (Teddy Gift) | Top 2 Reward | Top 3 Reward |
|--------|---------------------|---------------------|--------------|--------------|
| 1      | 200,000 coins + 600,000 EXP      | 10%  | 100,000 coins + 600,000 EXP | 50,000 coins + 600,000 EXP  |
| 2      | 500,000 coins + 1,200,000 EXP    | 25%  | 250,000 coins + 1,200,000 EXP | 100,000 coins + 1,200,000 EXP |
| 3      | 500,000 coins + 2,500,000 EXP    | 40%  | 250,000 coins + 2,500,000 EXP | 100,000 coins + 2,500,000 EXP |
| 4      | 2,000,000 coins + 3,500,000 EXP  | 60%  | 1,000,000 coins + 3,500,000 EXP | 500,000 coins + 3,500,000 EXP |
| 5      | 2,500,000 coins + 4,000,000 EXP  | 75%  | 1,200,000 coins + 4,000,000 EXP | 600,000 coins + 4,000,000 EXP |
| 6      | 3,000,000 coins + 5,000,000 EXP  | 90%  | 1,500,000 coins + 5,000,000 EXP | 800,000 coins + 5,000,000 EXP |

### Teddy Bear Gift
- Value: 1,000,000 coins worth (delivered as a premium gift effect + coin-equivalent diamonds)
- Chance evaluation: Single roll per rocket for Top 1 (not per coin). If success, item enters their Baggage immediately

### Random Participant Rewards
- Distribution: Random subset of current room occupants (top senders included are eligible again)
- Count logic:
  - Rocket 1–2: 3–5 recipients
  - Rocket 3–4: 5–8 recipients
  - Rocket 5–6: 8–12 recipients
- Reward per recipient: Uniform random 1–50,000 coins
- Anti-abuse: Must have sent ≥ minimal engagement threshold (e.g., 1 gift or 1 coin) to be eligible

---

## 5. Calculation Window & Fairness

- **Contribution period:** From previous rocket launch timestamp (or room start if Rocket 1) until current milestone threshold
- **Freeze when threshold met** (no contributions counted after freeze for that rocket)
- **Server authoritative timestamps** to avoid client race conditions

### Tie-breaking (Top senders)
1. Higher total coins sent
2. If tie, earliest last contribution timestamp (first to reach that total)
3. If still tie (rare), random deterministic tiebreak using userId hash

---

## 6. Regional System Integration

### Regional Isolation
- Each region maintains independent rocket states per room
- Example: "Room A" in Pakistan can be at Rocket 4 while "Room A" in India at Rocket 2

### Notification Targeting
- **Primary region:** Users currently inside any room of that region (100% receive rocket pre-launch FCM)
- **Home region:** User's profile region (saved at onboarding). They receive 10% sampled rocket alerts from their home region

### Sampling Strategy for Home Region Alerts
- Use reservoir sampling or consistent hashing: `userId % 10 == 0` → send home rocket alert

### Rate Limiting
- If multiple rockets launch across regions simultaneously, batch FCM messages (merge up to 3 rocket notifications)

---

## 7. Data Model

### Room Rocket State
```json
{
  "roomId": "R112233",
  "region": "Pakistan",
  "currentRocket": 3,
  "currentTotalSent": 10453219,
  "nextMilestone": 15000000,
  "milestones": [
    { "rocket": 1, "trigger": 2500000, "launchedAtUtc": "2025-11-26T05:10:00Z" },
    { "rocket": 2, "trigger": 5000000, "launchedAtUtc": "2025-11-26T06:20:00Z" },
    { "rocket": 3, "trigger": 10000000, "launchedAtUtc": "2025-11-26T07:45:00Z" }
  ]
}
```

### Contribution Snapshot
```json
{
  "rocketNumber": 3,
  "roomId": "R112233",
  "region": "Pakistan",
  "contributors": [
    { "userId": "U1", "coinsSent": 2450000 },
    { "userId": "U2", "coinsSent": 1900000 },
    { "userId": "U3", "coinsSent": 1820000 }
  ],
  "randomRecipients": [
    { "userId": "U17", "rewardCoins": 12000 },
    { "userId": "U55", "rewardCoins": 3400 }
  ],
  "teddyAwarded": true,
  "teddyRecipient": "U1"
}
```

---

## 8. APIs

| Endpoint | Purpose |
|----------|---------|
| GET /room/{roomId}/rocket/state | Current rocket state |
| POST /room/{roomId}/rocket/contribution | Increment & check threshold |
| GET /region/{region}/rockets/active | Cross-region display |
| GET /room/{roomId}/rocket/{rocketNumber}/rewards | Rocket rewards |

---

## 9. Telemetry

| Event | Properties |
|-------|------------|
| rocket_threshold_reached | roomId, region, rocketNumber, totalSent |
| rocket_countdown_start | rocketNumber, roomId |
| rocket_launch | rocketNumber, roomId |
| rocket_rewards_distributed | rocketNumber, top1, top2, top3, teddyAwarded |
| rocket_random_reward | userId, rocketNumber, rewardCoins |
| rocket_notification_sent | region, rocketNumber, audienceSize |

---

## 10. Anti-Abuse & Integrity

- Server-side aggregation only; ignore client claims
- Minimum participant engagement threshold prevents idle accounts farming random rewards
- Probability gift (teddy) generation uses crypto-secure RNG
- Reward duplication prevention: idempotent distribution keyed by (roomId, rocketNumber)
- Alert flooding prevention: Limit rocket notifications per region to N per hour (configurable)

---

## 11. Edge Cases

| Scenario | Handling |
|----------|----------|
| Threshold crossed during previous countdown | Queue next rocket; do not overlap timers |
| Room empties before launch | Suspend event; restart countdown when ≥ required participants |

---

## Related Documentation

- [Rooms](./rooms.md)
- [Gifts & Baggage](./gifts.md)
- [Product Specification](../../README.md)
