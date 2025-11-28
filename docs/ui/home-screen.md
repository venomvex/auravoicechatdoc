# Home Screen — UI/UX Specification

Applies Aura branding (purple→white gradient), includes the Rewards icon (bottom-right FAB), and defines the "Popular" and "Mine" sections with their behaviors and referral program entry points.

> **Theme:** Original Purple → White gradient preserved. No cosmic/enhanced UI changes.

---

## 1. Global Layout

- **App bar (top):**
  - Left: Home icon
    - New user: prompts "Create Your Room" flow
    - Existing room owner: joins their room directly
  - Center: Active tab title (e.g., "Popular")
  - Right: Search action
- **Tabs:** [Mine] [Popular] (default landing: Popular)
- **Hero carousel:** Promotional banners (top posts, latest news)
- **Shortcut tiles:** Three feature boxes
  - Player Ranking (by sending)
  - Room Ranking (by sending)
  - CP (Couple Partnership)
- **Filter chips:** [Popular] [Video/Music]
- **Content grid:** 2-column room cards ordered by popularity
- **Persistent FAB:** Rewards icon, bottom-right above bottom navigation
- **Bottom navigation:** Home (active), Message, Me

---

## 2. Visual Styles

- **Background:** Gradient #c9a8f1 → #ffffff (top→bottom)
- **Accents:** Magenta #d958ff ↔ Cyan #35e8ff
- **Cards:** 12–16dp corner radius, 8–12dp spacing, subtle elevation
- **Typography:** Roboto (or app default) — Titles 18–20sp, Tabs 14–16sp

---

## 3. Home Icon (Top-left)

### Behavior
- **Tap → If user has no room:**
  - Launch "Create Your Room" wizard:
    - Room name
    - Cover image
    - Announcement
    - Welcome message
    - Create (free)
- **Tap → If user owns a room:**
  - Join own room (shortcut)

---

## 4. Tabs

### Mine
- Top: User's own room card (or "Create your room" prompt if new)
- Below: Recently joined rooms list
- Followed on mic: A list of people on mic that the user follows (quick join)

### Popular (Default)
- Banners: Top posts and latest news (carousel)
- Shortcut tiles: Player Ranking, Room Ranking, CP
- Filter chips: [Popular] [Video/Music]
- Rooms grid: Ordered by number of participants

### Tab Interaction
- Swipe horizontally between tabs
- Indicator: underline or pill, 2–4dp, accent color

---

## 5. Shortcut Tiles

- **Player Ranking** (by sending)
- **Room Ranking** (by sending)
- **CP (Couple Partnership)**
  - CP formation: Two users can form CP by paying a one-time fee (e.g., 3M coins)
  - CP rewards (by mutual sending): See [CP documentation](../features/cp.md)
  - CP progression: Each coin sent to each other → +1 EXP

---

## 6. Filter Chips

- **Popular** (active by default)
- **Video/Music**
  - When active: Rooms with the Video/Music mode are listed
  - See [Video/Music Mode](../features/video-music-mode.md) for details

---

## 7. Content Grid (Rooms)

- **Columns:** 2
- **Ordering:** By current participant count (descending)
- **Card elements:**
  - Thumbnail
  - Overlay badges (e.g., live indicator, viewer/participant count)
  - Title or room name
  - Small metadata (e.g., country flag, tags)
- **Pagination:** Infinite scroll; skeleton loaders on initial fetch

---

## 8. Rewards Entry Point (FAB)

- **Placement:** Bottom-right above bottom navigation
- **Size:** 56–64dp; circular; 16dp margins; elevation 6–8dp
- **Icon:** Gift/coin stack; badge "!" when unclaimed today
- **Tap:** Opens Daily Reward popup
- **Visibility:**
  - Shown when authenticated
  - Badge only when unclaimed; cleared after claim

---

## 9. Bottom Navigation

| Tab | Description |
|-----|-------------|
| Home (active) | Current screen |
| Message | Direct messages, notifications |
| Me | Profile, wallet, settings |

### Message Tab Features
- Constraint: A user may send up to 5 messages to another user unless mutual follow
- Notifications include: Rocket events, looted coins, top sender status

### Me Tab Features
- Profile edit, display image
- Stats: Followers, Following, Visitors
- Wallet (Coins, Diamonds, Exchange)
- Invite Friends (referral program)

---

## 10. Telemetry

| Event | Description |
|-------|-------------|
| home_view | Screen opened |
| home_tab_switch | Tab changed |
| home_room_tap | Room card tapped |
| home_ranking_tap | Ranking tile tapped |
| home_cp_tap | CP tile tapped |
| home_search_tap | Search opened |
| home_create_room_start | Create room initiated |

---

## Related Documentation

- [Rooms](../features/rooms.md)
- [Daily Rewards](../features/daily-rewards.md)
- [CP Partnership](../features/cp.md)
- [Video/Music Mode](../features/video-music-mode.md)
- [Rocket System](../features/rocket-system.md)
- [Recharge Event](../features/recharge-event.md)
- [Product Specification](../../README.md)
