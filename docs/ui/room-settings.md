# Room Settings Screen Specification

Centralizes owner (and limited admin) controls for a room's metadata, access, moderation, and advanced audio seat features.

> **Theme:** Original Purple → White gradient preserved. No cosmic/enhanced UI changes.

---

## 1. Purpose

Single management hub for:
- Profile & identity edits (avatar, cover, region)
- Basic metadata (room name, announcement)
- Seat capacity changes & advanced seat features
- Access control (password, blocked list)
- Role management (administrators)
- Visual customization (theme)
- Moderation audit (kick history)

---

## 2. Navigation & Structure

### Entry Points
- From room view: tap settings icon in top bar (owner/admin)
- From "Manage Room" card on Mine tab (owner only)

### Header
- Back arrow + Title "Settings"

### Groupings
1. Identity
2. Participation
3. Access & Security
4. Customization
5. Moderation Logs

---

## 3. Settings Rows

### Identity

| Row | Type | Description |
|-----|------|-------------|
| Profile | Navigation | Avatar upload, display name, cover image |
| Room Name | Navigation | Edit screen with text field (2–30 chars) |
| Announcement | Navigation | Multiline editor (up to 300 chars with emojis) |

### Participation

| Row | Type | Description |
|-----|------|-------------|
| Number of Mic | Selector | Seat count (8, 12, or 16 based on owner level) |
| Super Mic | Toggle | Enable if owner Level ≥40 OR VIP ≥4 |

### Access & Security

| Row | Type | Description |
|-----|------|-------------|
| Room Password | Navigation | Set/clear password (up to 8 chars) |
| Administrators | Navigation | Add/remove admins (max 10) |
| Blocked List | Navigation | View/manage blocked users |
| Kick History | Navigation | View kick/ban logs |

### Customization

| Row | Type | Description |
|-----|------|-------------|
| Room Theme | Navigation | Theme picker (wallpapers grid) |

---

## 4. Super Mic Behavior

- **Visual:** Distinct ring (gradient magenta→cyan)
- **Audio:** No gain boost
- **Priority:** Speaking state highlights mic icon
- **Occupancy:** One user at a time; owner can forcibly revoke
- **Enable Conditions:** Owner Level ≥40 OR VIP Tier ≥4

---

## 5. Seat Capacity Change

1. Owner taps Number of Mic
2. Dialog lists allowed counts
3. **If increasing:** Add new empty seats
4. **If decreasing:** Show confirmation with affected occupants
5. Broadcast: `seat_count_changed(newCount, actorId)`

---

## 6. Room Password Flow

### Setting
- Input field (mask optional toggle)
- Must not match last 3 passwords
- Broadcast: "Room now password protected."

### Clearing
- Confirm dialog: "Remove password?"
- Broadcast: "Room password removed."

---

## 7. Permissions Matrix

| Row | Owner | Admin | Regular |
|-----|-------|-------|---------|
| Profile | Edit | View | View |
| Room Name | Edit | View | View |
| Announcement | Edit | View | View |
| Number of Mic | Edit | View | View |
| Room Password | Edit | View (masked) | None |
| Super Mic | Toggle | Occupy seat | None |
| Room Theme | Edit | View | View |
| Administrators | Manage | View | None |
| Blocked List | Manage | Manage | None |
| Kick History | View/Clear | View | None |

---

## 8. Visual Design

- **Row height:** 56dp
- **Font sizes:** Title 16sp Medium; secondary 14sp Regular
- **Right chevron icon:** 20–24dp
- **Switch:** Standard Material switch with accent color
- **Section separators:** 16dp vertical spacing
- **Background:** Light #FFFFFF, dark #12141A

---

## 9. Telemetry

| Event | Description |
|-------|-------------|
| room_settings_open | Screen opened |
| room_settings_change_name | Name changed |
| room_settings_change_announcement | Announcement changed |
| room_settings_seat_count_change | Seat count changed |
| room_settings_super_mic_toggle | Super Mic toggled |
| room_settings_theme_change | Theme changed |
| room_settings_password_set | Password set |
| room_settings_password_clear | Password cleared |
| room_settings_admin_add | Admin added |
| room_settings_admin_remove | Admin removed |
| room_settings_block_add | User blocked |
| room_settings_block_remove | User unblocked |

---

## Related Documentation

- [Rooms](../features/rooms.md)
- [Home Screen](./home-screen.md)
