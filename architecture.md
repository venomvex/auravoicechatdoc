# Architecture

## High-Level Design

Aura Voice Chat is a mobile-first voice and video chat application built on a microservices architecture.

### System Components

```
┌─────────────────────────────────────────────────────────────┐
│                       Mobile Client                          │
│                   (Android 9+ / API 28+)                     │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                        API Gateway                           │
│              (Auth, Rate Limiting, Routing)                  │
└─────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        ▼                     ▼                     ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│   Auth       │    │   User       │    │   Rewards    │
│   Service    │    │   Service    │    │   Service    │
└──────────────┘    └──────────────┘    └──────────────┘
        │                     │                     │
        └─────────────────────┼─────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      Database Layer                          │
│              (Primary DB + Redis Cache)                      │
└─────────────────────────────────────────────────────────────┘
```

### Key Components

| Component       | Responsibility                              |
|-----------------|---------------------------------------------|
| API Gateway     | Authentication, rate limiting, routing      |
| Auth Service    | OTP, OAuth, session management              |
| User Service    | Profiles, medals, VIP, relationships        |
| Rewards Service | Daily rewards, referrals, economy           |
| Room Service    | Voice/video rooms, seats, chat              |
| Gift Service    | Gift catalog, transactions, animations      |
| Media Service   | Video/music playback, YouTube integration   |

---

## Technology Choices

### Client
- **Platform:** Android 9+ (API 28), targeting API 34
- **Language:** Kotlin
- **Architecture:** MVVM with Clean Architecture
- **Real-time:** WebSocket for room communication

### Backend
- **API:** RESTful with JSON
- **Authentication:** JWT + OAuth2
- **Real-time:** WebSocket / Server-Sent Events

### Storage
- **Primary Database:** PostgreSQL / MySQL
- **Caching:** Redis
- **File Storage:** Cloud storage for media assets

### Infrastructure
- **Deployment:** Containerized (Docker/Kubernetes)
- **CDN:** For static assets and media delivery

---

## Data Model

### Core Entities

```
User
├── id, name, avatar, level
├── coins, diamonds
├── vipTier, vipExpiry
└── settings

DailyReward
├── userId, currentDay, streak
├── lastClaimDate
└── cycleData

Medal
├── id, category, criteria
└── rewardCoins, rewardItem

Room
├── id, name, ownerId
├── type (voice/video/music)
├── seats[], settings
└── playlist[]

Gift
├── id, name, price
├── region, animation
└── diamondValue
```

### Relationships

- User ↔ User: Following, Blocking, CP Partnership
- User → Room: Ownership, Participation
- User → Medal: Earned, Displayed
- User → Gift: Sent, Received

---

## Error Handling & Resilience

### Retry Strategy
- **Default:** 3 retries with exponential backoff (1s, 2s, 4s)
- **Timeouts:** 30s for standard requests, 60s for media

### Circuit Breaker
- Threshold: 5 failures in 10s window
- Recovery: Half-open after 30s

### Graceful Degradation
- Cache-first for catalog data
- Offline indicator for network issues
- Queue critical operations when offline

---

## Scalability

### Horizontal Scaling
- Stateless services behind load balancer
- Database read replicas
- Redis cluster for distributed caching

### Performance Considerations
- Pagination for all list endpoints
- Lazy loading for media content
- Connection pooling for database

### Known Bottlenecks
- Gift animation rendering (capped at 5-10 concurrent)
- Video sync for large rooms
- Referral leaderboard calculations

---

## Observability

### Logging
- Structured JSON logs
- Levels: DEBUG, INFO, WARN, ERROR
- PII: Hashed/masked in logs

### Metrics
- Request latency (p50, p95, p99)
- Error rates by endpoint
- Active users, rooms, transactions

### Tracing
- Distributed tracing for request flows
- Correlation IDs across services

### Alerting
- Error rate > 1% for 5 minutes
- Latency p99 > 2s for 5 minutes
- Failed authentications spike

---

## Related Documentation

- [API Reference](api.md)
- [Security](security.md)
- [Deployment](deployment.md)
- [Operations](operations.md)