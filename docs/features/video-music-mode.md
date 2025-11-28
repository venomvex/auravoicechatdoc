# Rooms: Video/Music Mode

Voice rooms with integrated video and music playback capabilities.

## Overview

Rooms can switch to Video/Music mode for cinema-style content playback with synchronized viewing.

---

## Home Discovery

### Video/Music Filter
- Chip filter on Home: "Video/Music"
- Lists rooms currently in cinema mode
- Located alongside Popular, other filters

---

## In-Room Experience

### Entering Video/Music Mode
1. Room owner/admin initiates from room menu
2. Standard UI transforms to dark cinema theme
3. Video player becomes prominent

### Exiting Video/Music Mode
1. Access Video option in room controls
2. Confirm exit prompt appears
3. Returns to standard room UI

---

## Playlist Management

### Adding Content
- YouTube links only (Spotify deferred)
- Host + admins can add to playlist
- Moderator additions configurable by owner

### Playlist Limits
- Maximum items: **15**

### Queue Behavior
- FIFO (First In, First Out)
- Current video shown with progress
- Up Next preview visible

---

## Controls

### Host/Admin Controls
- Play / Pause
- Skip to next
- Remove from queue
- Seek (scrub timeline)
- Volume adjustment (synced)

### Control Permissions (Configurable)

| Action          | Host | Admins | Seated Users |
|-----------------|------|--------|--------------|
| Add to playlist | ✓    | ✓      | Config       |
| Play/Pause      | ✓    | ✓      | ✗            |
| Skip            | ✓    | ✓      | ✗            |
| Seek            | ✓    | Config | ✗            |
| Volume          | ✓    | ✓      | ✗            |

---

## Synchronization

### Latency Management
- Server authoritative timestamp
- Clients sync to server time
- Buffer for network variance

### Quality Options
- Auto (adaptive)
- Low / Medium / High presets
- Bandwidth-conscious defaults on mobile

---

## Visual Design

### Cinema Theme
- Dark background replaces standard UI
- Minimal controls overlay
- Full-width video player
- Dimmed seat/participant display

### Transitions
- Smooth fade between standard ↔ cinema mode
- Respects Reduce Motion setting

---

## API Endpoints

### Add to Playlist

```
POST /rooms/{id}/video/playlist
```

Request:
```json
{
  "url": "https://youtube.com/watch?v=...",
  "addedBy": "user_123"
}
```

### Exit Video Mode

```
POST /rooms/{id}/video/exit
```

### Get Playlist

```
GET /rooms/{id}/video/playlist
```

### Control Playback

```
POST /rooms/{id}/video/control
```

Request:
```json
{
  "action": "play" | "pause" | "skip" | "seek",
  "seekPosition": 120
}
```

---

## Content Moderation

- Manual report system for inappropriate content
- Room owner/admin can remove content
- Automated NSFW filtering (under consideration)

---

## Telemetry Events

| Event                    | Properties                |
|--------------------------|---------------------------|
| `rooms_video_mode_enter` | roomId, userId            |
| `rooms_video_mode_exit`  | roomId, userId, duration  |
| `video_play`             | roomId, videoId           |
| `video_skip`             | roomId, videoId, reason   |

---

## Related Documentation

- [Rooms Overview](./rooms.md)
- [Rocket System](./rocket-system.md)
- [Gifts & Baggage](./gifts.md)
- [Product Specification](../../README.md)
