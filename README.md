# Aura Voice Chat — Product Specification (Consolidated)

This document bundles core product decisions, UI layouts, economy rules, rewards, referrals, and platform behavior defined so far.

> **Theme:** Original Purple → White gradient UI preserved. No "Cosmic" or altered UI changes.

---

## 📚 Documentation Index

### Feature Documentation
| Feature | Description | Link |
|---------|-------------|------|
| VIP Systems | Both VIP systems (multiplier + full SVIP) | [docs/features/vip-systems.md](docs/features/vip-systems.md) |
| AuraPass (Full VIP) | Complete VIP specification with tiers | [docs/features/aurapass.md](docs/features/aurapass.md) |
| AuraPass Spin | VIP Score earning through spins | [docs/features/aurapass-spin.md](docs/features/aurapass-spin.md) |
| VIP Recharge Bonus | Bonus coins on recharge by VIP tier | [docs/features/vip-recharge-bonus.md](docs/features/vip-recharge-bonus.md) |
| Daily Rewards | 7-day login reward cycle | [docs/features/daily-rewards.md](docs/features/daily-rewards.md) |
| Recharge Event | Daily Surge, Aurora Milestones, Recharge Royale | [docs/features/recharge-event.md](docs/features/recharge-event.md) |
| Medals | Achievement and activity medals | [docs/features/medals.md](docs/features/medals.md) |
| Wallet | Coins, Diamonds, and exchange | [docs/features/wallet.md](docs/features/wallet.md) |
| Store | Cosmetics and item purchases | [docs/features/store.md](docs/features/store.md) |
| Referrals | Get Coins and Get Cash programs | [docs/features/referrals.md](docs/features/referrals.md) |
| CP | Couple Partnership system | [docs/features/cp.md](docs/features/cp.md) |
| Rooms | Voice/video rooms feature | [docs/features/rooms.md](docs/features/rooms.md) |
| Video/Music | Room video/music mode | [docs/features/video-music-mode.md](docs/features/video-music-mode.md) |
| Rocket System | Gamified engagement milestones | [docs/features/rocket-system.md](docs/features/rocket-system.md) |
| Gifts | Gift economy and baggage | [docs/features/gifts.md](docs/features/gifts.md) |

### Design Documentation
| Topic | Link |
|-------|------|
| Design Tokens & Theme | [docs/design/tokens.md](docs/design/tokens.md) |
| Logo Specification | [docs/design/logo.md](docs/design/logo.md) |
| Accessibility | [docs/design/accessibility.md](docs/design/accessibility.md) |
| Naming Glossary | [docs/naming-glossary.md](docs/naming-glossary.md) |

### UI/UX Documentation
| Screen | Link |
|--------|------|
| Home Screen | [docs/ui/home-screen.md](docs/ui/home-screen.md) |
| Me Screen | [docs/ui/me-screen.md](docs/ui/me-screen.md) |
| Message Screen | [docs/ui/message-screen.md](docs/ui/message-screen.md) |
| Settings Screen | [docs/ui/settings-screen.md](docs/ui/settings-screen.md) |
| Onboarding Flow | [docs/ui/onboarding-flow.md](docs/ui/onboarding-flow.md) |
| Room Settings | [docs/ui/room-settings.md](docs/ui/room-settings.md) |
| Profile Modules | [docs/ui/profile-modules.md](docs/ui/profile-modules.md) |

### Development Documentation
| Topic | Link |
|-------|------|
| Pending Decisions | [docs/development/pending-decisions.md](docs/development/pending-decisions.md) |
| API Reference | [api.md](api.md) |
| Architecture | [architecture.md](architecture.md) |
| Configuration | [configuration.md](configuration.md) |
| Security | [security.md](security.md) |

### Operations Documentation
| Topic | Link |
|-------|------|
| Deployment | [deployment.md](deployment.md) |
| Operations | [operations.md](operations.md) |
| Troubleshooting | [troubleshooting.md](troubleshooting.md) |
| Release Playbook | [release-playbook.md](release-playbook.md) |

### Project Documentation
| Topic | Link |
|-------|------|
| Getting Started | [getting-started.md](getting-started.md) |
| Contributing | [contributing.md](contributing.md) |
| Changelog | [changelog.md](changelog.md) |

---

## Contents
- Branding & Platform
- Authentication
- Home & Navigation
- Daily Login Rewards
- VIP (SVIP)
- Medals System
- Wallet, Coins & Diamonds
- Store & Items
- CP (Couple Partnership)
- Rooms: Video/Music Mode
- Messaging & Notifications
- Referral Programs (Get Coins / Get Cash)
- Me, Settings, Message Screens
- Accessibility, Internationalization, Security
- APIs, Data Models, Telemetry

---

## Branding & Platform
- Theme: Purple → White gradient (top-bottom; design may tune direction when needed), magenta↔cyan accent glow.
- Dark mode: Supported.
- Minimum Android: 9+ (API 28). Recommend targeting Android 10+ for behavior consistency.

Design tokens (baseline)
- Primary Purple: #c9a8f1
- Dark Canvas: #12141a
- Accent Magenta: #d958ff
- Accent Cyan: #35e8ff

---

## Authentication
- Methods: Google, Facebook, Mobile (international E.164 phone numbers).
- OTP: 4 digits; resend cooldown 30s; max 5 attempts per day; show warning after first failure.
- First-ever login: Auto-claim Day 1 daily reward.
- First login of each UTC day: Show Daily Reward popup; closing it moves the claim to the Home FAB (bottom-right).

---

## Home & Navigation
- Top-left “Home” icon:
  - No room: launches “Create Your Room” wizard (name, cover, announcement, welcome).
  - Owner with room: joins own room directly.
- Tabs: Mine, Popular.
  - Mine: Own room card (or create prompt); recent joined rooms; followed-on-mic list.
  - Popular: Banner carousel, feature tiles (Player Ranking, Room Ranking, CP), chips (Popular, Video/Music), rooms grid sorted by participants.
- Rewards FAB: Bottom-right above navigation; badge “!” when daily reward unclaimed.
- Bottom nav: Home (default), Message, Me.

---

## Daily Login Rewards
- Rewards (Coins only; Day 7 includes bonus):
  - Day1 5,000; Day2 10,000; Day3 15,000; Day4 20,000; Day5 25,000; Day6 30,000; Day7 35,000 + 15,000 (total 50,000).
- Per-cycle total: 155,000 Coins.
- Popup behavior:
  - Shown once per UTC day on first login if unclaimed.
  - If closed, use Home FAB to claim later the same day.
  - After claim, button shows “Signed in today.”
- Cycle rules:
  - Reset to Day 1 after Day 7; keep long-term streak counter growing.
  - Missing a day resets the 7-day cycle and streak; cumulative login days still count for medals.
- Day boundary: Server UTC.
- Animations:
  - Claimable cell pulse; success overlay “Get Reward” with sparkles and coin count-up; respects Reduce Motion.
- Reminders: 6 hours after first session + 2 hours before reset (if unclaimed).
- VIP application: Multiplier applies to the full day total; round to nearest 10 coins.

---

## VIP (SVIP)

> **Note:** Both VIP systems are retained:
> 1. **Daily Reward Multiplier VIP** — Original VIP system that multiplies daily login rewards
> 2. **Full VIP (SVIP) System** — Complete VIP with levels, EXP boost, exclusive items, and more
>
> See [VIP Systems Documentation](docs/features/vip-systems.md) for comprehensive details.

Tiers and multipliers
- VIP1 1.20x
- VIP2 1.40x
- VIP3 1.60x
- VIP4 1.80x
- VIP5 2.00x
- VIP6 2.20x
- VIP7 2.40x
- VIP8 2.60x
- VIP9 2.80x
- VIP10 3.00x

Notes
- VIP multiplier applies to Daily Rewards at claim.
- Additional VIP benefits (future-ready): EXP boost, exclusive items, priority join.

---

## Medals System
Categories
- Gift Medals: Sending/receiving milestones.
- Achievement Medals: Progress (Levels, VIP tiers, room counts).
- Activity Medals: Participation (login days, sessions).

Login Activity medals (cumulative days)
- Milestones: 30, 60, 90, 180, 365.
- Reward style: Balanced (cosmetics + coins), auto-claim.
  - 30d: 50,000 coins + frame (7d)
  - 60d: 100,000 coins + mic skin (7d)
  - 90d: 200,000 coins + seat heart effect (7d)
  - 180d: 500,000 coins + frame (30d)
  - 365d: 1,000,000 coins + premium cosmetic (permanent)
Display & ordering
- Under profile name, user can display up to 10 medals; order customizable; any medal can be hidden.
Interaction
- Tap medal → details (achieved date, criteria, description). “View all medals” opens read-only gallery.

---

## Wallet, Coins & Diamonds
- Coins: Spendable currency (rewards, purchases, looted).
- Diamonds: Received when other users send gifts; Diamonds convert back to Coins at 30%.
  - Conversion: CoinsOut = floor(Diamonds × 0.30).
- Exchange: Preview shown before confirm; no extra fee beyond rate.
- Transfers: Users can send Coins to others; recipients receive Diamonds 1:1.

---

## Store & Items
- Categories: Frames, Entry Effects, Mic Skins, Seat Hearts, Consumables.
- Rarity tiers: Common, Rare, Epic, Legendary (baseline naming).
- Purchase: Confirm → deduct Coins → inventory updated.
- Equipping: One active per category (consumables stack).
- Refund policy: To be defined later.

---

## CP (Couple Partnership)
- Formation: One-time fee (e.g., 3M coins); both accept.
- Rewards via mutual sending:
  - 1 coin sent to partner = +1 CP EXP.
  - Thresholds grant Baggage gifts, frames, mic skins, seat heart upgrades.
  - Example thresholds include 5M (500k gifts), 10M (1M gifts), scalable to 250M; full table to be finalized.
- Visuals: CP frame; upgraded heart effect when seated side-by-side.

---

## Rooms: Video/Music Mode
- Chip filter: “Video/Music” on Home lists rooms currently in cinema mode.
- In-room:
  - Add YouTube links to playlist.
  - Dark cinema theme replaces standard UI.
  - Exit via Video option → close prompt.
- Control options: Host-controlled; moderator additions configurable.

---

## Messaging & Notifications
Messaging
- DM limit: Up to 5 messages unless mutual follow; limits removed on mutual follow.
- Categories in Message: Notifications, Activity, Family, Say Hi, Feedback, plus user threads.

Notifications
- Channels: Daily reward, messages, system, referral.
- Quiet hours: Configurable (future).
- Daily reward reminders: As above; delivered if unclaimed.

---

## Referral Programs

### Get Coins (Invite Friends)
Layout & functions replicated from reference:
- Reward Rules:
  - Invite 1 friend → 2,500,000 Coins
  - Friend’s recharge ≥ 500,000 Coins → 2,500,000 Coins
  - Friends send gifts → 5% Coins
- Binding: Enter code; on success show inviter name/ID.
- Withdraw panel: “Coins to withdraw” amount + Withdraw button.
- Stats: Number of invitations; total Coins rewarded.
- Records table: Nickname | ID | Recharge ≥ 500,000 | Send Gifts | Total Coins rewarded; paginated.
- Bottom “Invite friends to get coins” button (sticky).

Policy
- Minimum withdrawal: 100 Coins.
- Cooldown: None.
- Per-day cap: Unlimited.
- Verification: Not required.
- Share message (example): “Login and get a reward worth $400, including 50,000 coins and a frame. Come to Aura App for exciting chats. My Aura ID: {ID}”
- Pagination page size: 5 or 10 configurable (defaults 5 in UI; can set to 10).

### Get Cash
Layout & functions replicated from reference:
- Tabs: Get Coins / Get Cash.
- Banner + Marquee: Promotional headline and live earnings ticker.
- My Cash Balance: Value, History, Withdraw.
- Reward Rules:
  - Countdown timer (Days | Hours | Minutes | Seconds).
  - Level tabs Lv.1–Lv.10 with rewards (e.g., $0.20 → $100.00).
  - Level detail: USD reward + BD frame*7days; progress line “Friend Recharge: current / target”.
- Invite Record:
  - Summary cards: Total Recharge Friends; Total Rewards (USD).
  - Date range (weekly); weekly aggregate; records table (Name | ID | Invited Time | Expired Time | This Week Recharge USD); pagination.
- Ranking:
  - Leaderboard showing masked names and earned USD; 20 per page.

Policy
- Withdraw destinations:
  - Wallet: Unlimited per day; minimum $1; cooldown 30 seconds (request pacing).
  - External (bank/card/PayPal/Payoneer): Allowed when balance ≥ $10; 5-day clearance period before release; otherwise same validation.
- Campaign behavior: Auto new cycle when countdown reaches zero.
- Level claims: Auto upon reaching target (no manual claim needed).

---

## Me, Settings, Message Screens
Me
- Header with avatar, name, ID (tap to copy), metrics row (Followers, Following, Visitors).
- List: Wallet, Invite Friends (Earn Coins badge), Medal, SVIP, Level, CP / Friend, Family (Join Now), Store, My Items.
- Utilities: Language, Feedback, Settings.
- Max displayed medals under name: 10.

Message
- Categories at top: Notifications, Activity, Family, Say Hi, Feedback; followed by conversation threads.
- Bulk mark-as-read action.

Settings
- Account, SVIP Settings, Privacy Settings, Clean Cache (size), Terms of Service, Privacy Policy, Refund Policy, About Us, Contact Us.
- Logout button; version label.

---

## Accessibility
- WCAG AA contrast minimum for text/buttons.
- Reduce Motion: Disables particle/glow animations.
- Font scaling: Support system scaling up to at least 1.5x.
- All interactive elements have descriptive content labels.

---

## Internationalization
- Launch language: English (extendable).
- Number formatting: K/M compact style for display (e.g., 11.8M); financial displays use precise decimals.
- Dates: Server UTC reference; user locale formatting where appropriate.
- Currency: USD in Get Cash; extendable to multi-currency later.

---

## Security & Anti-Abuse
- Device policy: Max 4 accounts per device; device ID hashed fingerprint.
- Server UTC authoritative for all time-based rewards.
- Referrals anti‑abuse: bind rate limiting, fraudulent recharge detection, audit logging for withdrawals.
- No verification for Get Coins withdrawals; verification can be added later if needed.

---

## API Overview (Selected)
Authentication
- POST /auth/otp/send, POST /auth/otp/verify

Daily Rewards
- GET /rewards/daily/status
- POST /rewards/daily/claim

VIP
- GET /vip/tier
- POST /vip/purchase

Medals
- GET /profile/medals
- POST /profile/medals/display
- GET /users/{id}/medals

Wallet
- GET /wallet/balances
- POST /wallet/exchange (diamonds→coins)

Referrals — Get Coins
- POST /referrals/bind
- GET /referrals/coins/summary
- POST /referrals/coins/withdraw
- GET /referrals/records?page=N
- POST /referrals/reward/claim (if needed)

Referrals — Get Cash
- GET /referrals/cash/summary
- POST /referrals/cash/withdraw { destination: wallet | bank | card | paypal | payoneer }
- GET /referrals/cash/invite-record?weekStart=YYYY-MM-DD&page=N
- GET /referrals/cash/ranking?page=N

Rooms — Video/Music
- POST /rooms/{id}/video/playlist
- POST /rooms/{id}/video/exit

---

## Data Models (Examples)
Daily reward status
```json
{
  "currentDay": 6,
  "claimable": true,
  "cycle": [
    {"day":1,"coins":5000,"status":"CLAIMED"},
    {"day":2,"coins":10000,"status":"CLAIMED"},
    {"day":3,"coins":15000,"status":"CLAIMED"},
    {"day":4,"coins":20000,"status":"CLAIMED"},
    {"day":5,"coins":25000,"status":"CLAIMED"},
    {"day":6,"coins":30000,"status":"CLAIMABLE"},
    {"day":7,"base":35000,"bonus":15000,"total":50000,"status":"LOCKED"}
  ],
  "streak": 5,
  "nextResetUtc": "2025-11-27T00:00:00Z",
  "vipTier": "VIP5",
  "vipMultiplier": 2.0
}
```

Get Coins summary
```json
{
  "invitationsCount": 9,
  "totalCoinsRewarded": 11797449,
  "withdrawableCoins": 13000,
  "withdrawMin": 100,
  "cooldownSeconds": 0
}
```

Get Cash summary
```json
{
  "balanceUsd": 0.11,
  "minWithdrawalUsd": 1.00,
  "walletCooldownSeconds": 30,
  "externalAllowedMinUsd": 10.00,
  "externalClearanceDays": 5,
  "levels": [
    {"level":1,"targetUsd":1.00,"rewardUsd":0.20,"frameDays":7,"currentUsd":0.00,"status":"ACTIVE"},
    {"level":10,"targetUsd":1000.00,"rewardUsd":100.00,"frameDays":7,"currentUsd":0.00,"status":"LOCKED"}
  ],
  "rankingPageSize": 20,
  "campaignAutoNewCycle": true
}
```

---

## Telemetry (Selected)
- onboarding_view, auth_attempt(provider, result)
- daily_rewards_popup_shown(currentDay), daily_rewards_claim(day, coinsAwarded, vipMultiplier)
- vip_purchase(tier)
- medals_view, medal_detail_view, medal_order_edit, medal_claim
- wallet_exchange_attempt/success
- referrals_get_coins_view, referrals_coins_withdraw_success(amount)
- referrals_get_cash_view, cash_withdraw_success(amount, destination)
- rooms_video_mode_enter/exit
- message_view, message_bulk_mark_read
- settings_view, settings_logout_complete

---

## Open Items To Finalize (Optional Next Steps)
- CP thresholds full table (5M → 250M+) and exact reward packs.
- Store pricing ranges by category and rarity.
- Family feature limits & perks (member cap, roles, boosts).
- Refund policy for store purchases.
- Internationalization: language list for launch.

This spec will be kept as the master reference. As you provide more details, we’ll update this file and link into feature-specific docs.
