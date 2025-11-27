# Rooms — Feature Specification

Voice and video chat rooms are the core social experience in Aura Voice Chat.

---

## Entry

- From Home → Popular or Video/Music listings
- Tap a room card → open Room view

---

## Capabilities

### Stage
- Numbered mic seats (8–12 configurable)
- Lock/mute states for each seat
- Owner seat marker (house icon)

### Announcement & Rules
- Room rules area with edit rights for owner
- Announcement section (supports emojis)
- Owner signature row with level, title, medals

### Promotional Content
- Right-side promo banners carousel
- Event banners (rocket launch, seasonal events)

### Bottom Action Bar
- Audio/mic toggles
- Emoji/chat panel access
- Gifts FAB
- Tools grid access

---

## Tools Drawer

### Party Games
- Fishing
- TeenPatti
- GiftWheel
- Lucky Bag

### Tools
- Music (enters Video/Music mode)
- Store
- Effect
- My Items
- Clean Chat

---

## Gift Shop

### Tabs
| Tab | Description |
|-----|-------------|
| Gifts | Standard gift catalog |
| Customized | User-created gifts |
| SVIP | VIP-exclusive gifts |
| CP | Couple partnership gifts |
| Country | Region-specific gifts |
| Baggage | Free gifts from rewards |

### Controls
- Item grid with prices
- Balance display
- Quantity selector
- Send button

---

## Owner Controls

| Control | Description |
|---------|-------------|
| Close/end room | Terminate the room session |
| Lock/unlock seats | Control seat availability |
| Change seat count | 8, 10, or 12 seats |
| Edit announcement/rules | Update room text |
| Manage moderators | Assign/remove mod privileges |

---

## Seat States

| State | Indicator |
|-------|-----------|
| Empty | Default mic icon |
| Occupied (speaking) | Mic with pulse indicator |
| Muted | Mic with mute badge |
| Locked | Lock badge overlay |
| Owner seat | House icon |

---

## Seat Interactions

| Action | Result |
|--------|--------|
| Tap empty seat | Request mic (if allowed) |
| Tap occupied seat | Open user quick actions |
| Long-press (mods) | Seat controls (lock/unlock, move, kick) |

---

## Video/Music Mode

- Toggle via Tools → Music
- Room switches to dark cinema theme
- Rooms in this mode appear in Home's Video/Music filter
- See [Video/Music Mode](./video-music-mode.md) for details

---

## Economy Integration

| Feature | Integration |
|---------|-------------|
| Gifts | Deduct coins; recipient gets diamonds |
| Baggage | Free gifts accumulate from events |
| VIP | Frames/effects visible on stage |
| CP | Seat hearts upgrade as partners level up |

---

## Accessibility

- Button target sizes ≥ 44dp
- Labels for screen readers
- Reduce Motion disables heavy animations

---

## Telemetry

| Event | Description |
|-------|-------------|
| room_open | Room entered |
| seat_request | User requested mic seat |
| gift_send | Gift sent in room |
| tools_open | Tools drawer opened |
| game_launch | Party game started |
| music_mode_enter | Entered video/music mode |
| music_mode_exit | Exited video/music mode |
| announcement_edit | Announcement updated |
| rules_view | Rules expanded |
| room_leave | User left room |

---

## Related Documentation

- [Video/Music Mode](./video-music-mode.md)
- [Gifts & Baggage](./gifts.md)
- [Rocket System](./rocket-system.md)
- [Product Specification](../../README.md)
