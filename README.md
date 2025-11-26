# Aura Voice Chat — Product Specification (Consolidated)

This document bundles confirmed features, UI layouts, economy rules, referrals, VIP tiers, medals, and daily rewards into one reference.

Contents
- Branding & Onboarding
- Daily Login Rewards
- Home Screen
- Me, Message, and Settings
- Wallet, Store, Items
- Medals
- VIP (SVIP)
- CP (Couple Partnership)
- Video/Music Mode
- Referrals (Get Coins & Get Cash)
- Notifications, Accessibility, Internationalization
- Security & Anti-Abuse
- APIs & Data Models (overview)
- Open Next Steps

---

## Branding & Onboarding

- Theme: Purple → White gradient (top-to-bottom); accent glow Magenta ↔ Cyan.
- Dark Mode: Supported (purple-dark variants).
- Minimum Android: 9+ (API 28). Target 10+ where possible.
- First app launch:
  - Preloader with logo.
  - Login methods: Google, Facebook, Mobile (OTP 4 digits; resend cooldown 30s; max 5 attempts; warning after first failure).
  - Phone format: International (E.164).
- First login of the day:
  - Show Daily Reward popup (if unclaimed).
  - If closed without claim: popup won’t reappear; claim via Home Rewards icon (bottom-right above navigation).

---

## Daily Login Rewards

- Rewards (Coins only):
  - Day1: 5,000
  - Day2: 10,000
  - Day3: 15,000
  - Day4: 20,000
  - Day5: 25,000
  - Day6: 30,000
  - Day7: 35,000 + 15,000 bonus = 50,000
- VIP multipliers apply to the daily total at claim time; rounding to nearest 10 coins.
- Cycle & streak:
  - Reset day counter to Day 1 after Day 7.
  - Keep separate long-term streak counter across cycles.
  - Missed day resets 7-day cycle to Day 1 (streak resets).
- Popup behavior:
  - Shown once per UTC day if unclaimed.
  - Claim animation: pulse + sparkle; respect Reduce Motion.
  - Post-claim: CTA disabled “Signed in today”.
- Home Rewards icon: persistent FAB bottom-right above navigation; badge “!” when unclaimed.
- Reminders: 6h after first session; 2h before UTC reset.
- Number style: K/M display (e.g., 50k, 1.2M).

---

## Home Screen

- App bar:
  - Left Home icon: 
    - New user: “Create Your Room” wizard (name, cover image, announcement, welcome message).
    - Room owner: join own room directly.
  - Center: Active tab title.
  - Right: Search.
- Tabs: Mine, Popular (default landing).
- Popular:
  - Banner carousel (top posts/news).
  - Shortcuts: Player Ranking (by sending), Room Ranking (by sending), CP.
  - Filter chips: Popular, Video/Music.
  - Grid: Rooms ordered by participant count.
- Mine:
  - Top: Own room card (or create prompt).
  - Recent joined rooms.
  - People on mic the user follows.
- Video/Music filter:
  - Rooms playing videos/music appear here.
- Rewards FAB: bottom-right above bottom navigation.

Bottom Navigation
- Home, Message, Me.

---

## Me Screen

- Header: Avatar, Display Name, ID (tap to copy), Gradient header.
- Metrics: Followers, Following, Visitors.
- Features:
  - Wallet (Coins, Diamonds; Exchange at 30%: 1M Diamonds → 300k Coins).
  - Invite Friends (badge “Earn Coins”).
  - Medal (library & display management).
  - SVIP.
  - Level (user progression).
  - CP / Friend.
  - Family.
  - Store.
  - My Items.
- Utilities:
  - Language, Feedback, Settings.

---

## Message Screen

- Categories: Notifications, Activity, Family, Say Hi, Feedback.
- Conversations below categories.
- Bulk mark-as-read action.
- DM limit: up to 5 messages unless mutual follow; lifted after mutual follow.

---

## Settings Screen

- Items: Account, SVIP Settings, Privacy Settings, Clean Cache (size), Terms, Privacy Policy, Refund Policy, About Us, Contact Us.
- Logout button + version text.
- Cache clear confirm; WebViews for docs.

---

## Wallet, Store, Items

Wallet
- Coins: spendable; Diamonds: earned from receiving gifts (1:1 conversion from sender coins).
- Exchange Diamonds → Coins at 30% (floor). Optional daily limits TBD (currently none).
- Preview totals before exchange.

Store
- Categories: Frames, Entry Effects, Mic Skins, Seat Hearts, Consumables.
- Sorting: Popularity, New, Price.
- Purchase: Deduct coins; item added to My Items.

My Items
- Tabs: Frames, Entry Effects, Mic Skins, Seat Hearts, Consumables.
- Equip one per category; consumables stack.

---

## Medals

- Categories: Gift Medals, Achievement Medals, Activity Medals.
- Activity – Login Milestones: 30, 60, 90, 180, 365 total login days.
- Display: Up to 10 medals below profile name; user controls order and visibility.
- Interaction: Tap medal → detail (achieved date, criteria, progress).
- Rewards (Balanced; Auto claim):
  - 30 days: 50k coins + frame (7 days)
  - 60 days: 100k coins + mic skin (7 days)
  - 90 days: 200k coins + seat heart (7 days)
  - 180 days: 500k coins + frame (30 days)
  - 365 days: 1,000k coins + premium cosmetic (permanent)
- Me → Medal library: manage, reorder, show/hide.

---

## VIP (SVIP)

- Tiers & multipliers:
  - VIP1: 1.20x
  - VIP2: 1.40x
  - VIP3: 1.60x
  - VIP4: 1.80x
  - VIP5: 2.00x
  - VIP6: 2.20x
  - VIP7: 2.40x
  - VIP8: 2.60x
  - VIP9: 2.80x
  - VIP10: 3.00x
- Benefits:
  - Daily reward multiplier (above).
  - Potential EXP boosts, exclusive store items, priority join (expand later).
- Duration: Monthly or configurable (TBD).

---

## CP (Couple Partnership)

- Formation: One-time fee (example: 3M coins).
- Rewards by mutual sending:
  - EXP: +1 per coin sent to each other.
  - Thresholds unlock couple frames, mic skins, seat heart effects, coin packs (table to be finalized).
- Seat hearts: Upgrade per level when seated side by side.
- Display & management in CP module.

---

## Video/Music Mode

- Entry: Room “Video/Music” option.
- Playlist: Add YouTube links; queue management.
- Theme: Dark cinema.
- Exit: Tap Video/Music option → prompt closes and reverts.
- Listing: Rooms in this mode appear under Video/Music filter.

---

## Referrals

### Get Coins Tab (Exact Layout & Functions)
- Top: Segmented tabs (Get Coins active).
- Banner: “Invite Friends To Get Coins”.
- Reward Rules:
  - Invite 1 friend → 2.5M coins.
  - Friend’s recharge ≥ 500k coins → 2.5M coins.
  - Friends send gifts → 5% coins.
- Binding: Enter invite code; shows inviter status line when bound.
- Withdraw panel: “Coins to withdraw”, amount, tooltip, Withdraw button.
- Stats cards: Number of invitations; Total coins rewarded.
- Records table: NickName | ID | Recharge ≥ 500k | Send Gifts | Total coins rewarded; pagination.
- Bottom button: “Invite friends to get coins” opens share chooser (pre-copied message).
- Withdraw rules (confirmed):
  - Minimum: 100 coins.
  - Cooldown: None.
  - Daily cap: Unlimited.
  - Verification: None.

### Get Cash Tab (Exact Layout & Functions)
- Top: Segmented tabs (Get Cash active).
- Banner: “Invite Friend Earn 1000 USD Easily”.
- Marquee announcement bar.
- My Cash Balance panel: value, History, Withdraw.
- Reward Rules:
  - Countdown timer (auto new cycle on zero).
  - Level tabs Lv.1–Lv.10 with rewards:
    - Lv.1 $0.20; Lv.2 $0.40; Lv.3 $0.60; Lv.4 $1.00; Lv.5 $2.00; Lv.6 $5.00; Lv.7 $10.00; Lv.8 $25.00; Lv.9 $50.00; Lv.10 $100.00.
    - Level detail shows cash reward + BD frame (e.g., Frame*7days) and progress “Friend Recharge: current / target”.
- Invite Record view:
  - Stats: Total Recharge Friends; Total Rewards.
  - Date range weekly navigator.
  - “Friend Weekly Recharge: $X.00”.
  - Table: Name | ID | Invited Time | Expired Time | This Week Recharge USD; pagination 1/2.
- Ranking view:
  - Page size: 20; pagination.
- Withdraw rules (confirmed):
  - Minimum: $1 USD.
  - Wallet withdrawals: Unlimited per day; 30-second cooldown for pacing.
  - External payouts (bank/card/PayPal/Payoneer): Allowed at ≥ $10; with 5-day clearance period before release.

Invitation Message
- “Login and get a reward worth $400, including 50,000 coins and a frame. Come to Aura App for exciting chats. My Aura ID: {USER_ID}”
- Localize text per locale; replace {USER_ID} dynamically.

---

## Notifications

- Channels: Daily rewards, Messages, System, Referral.
- Reminders: Daily rewards schedule (6h after first session; 2h before reset).
- Quiet hours: Respect user settings (if enabled later).
- In-room notifications: Banner or subtle icon (TBD).

---

## Accessibility

- WCAG AA contrast minimum; use off-white text on dark panels.
- Reduce Motion disables particle/glow animations.
- Font scaling: Support system scaling; ensure layouts adapt.
- Content descriptions for icons, badges, medals, tabs, and table rows.

---

## Internationalization

- Launch: English; add locales as needed.
- Number formatting:
  - Coins: K/M (e.g., 50k, 1.2M).
  - Large values in tables: Use separators (e.g., 11,797,449) where readability is needed
