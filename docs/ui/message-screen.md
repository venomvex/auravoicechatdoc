# Message Screen — UI/UX Specification

Unified inbox for system notifications, activities, family messages, feedback channel, and direct conversation threads.

> **Theme:** Original Purple → White gradient preserved. No cosmic/enhanced UI changes.

---

## 1. Layout Structure

- **App bar:**
  - Title: "Message"
  - Right action: Bulk mark as read (check-circle icon)
- List items grouped logically (system categories first, then user threads)
- Bottom navigation (Message active with badge)

---

## 2. Item Types

### System Categories (Top)

| Category | Description |
|----------|-------------|
| Notifications | Latest system message snippet |
| Activity | Event summary (e.g., seasonal competitions) |
| Family | Latest family event or chat summary |
| Say Hi | Follower greeting ("User followed you") |
| Feedback | Status of submitted feedback |

### User Conversations (Below)
- Avatar
- Display name
- Last message snippet
- Timestamp (right side)
- System tag if auto-generated: "[System message]"

---

## 3. Visual Styles

- **Background:** Gradient top → neutral list region
- **Item height:** 64–72dp
- **Icon sizes:** 36–40dp circle or rounded
- **Font:**
  - Title: 16sp Medium
  - Preview snippet: 14sp Regular (single line, ellipsis)
  - Timestamp: 12sp Regular (dimmed color #7A7A88)
- **Unread badge:**
  - Red circle (~16dp) at right or small count bubble
  - contentDescription: "Unread messages: {count}"

---

## 4. Interactions

| Action | Result |
|--------|--------|
| Tap Notifications | Open notifications list |
| Tap Activity | Open activity feed |
| Tap Family | Open family chat |
| Tap Say Hi | Open follower greet screen |
| Tap Feedback | Open feedback submission |
| Long-press conversation | Options (Delete, Pin, Mute) |
| Bulk mark read | Marks all as read |

---

## 5. Messaging Limits

- **Direct messages:** Max 5 messages to a user unless mutual follow
- **Limit reached:** Show dialog "Send limit reached. Follow each other to continue."
- **Mutual follow detection:** Server returns boolean; if true, limits lifted

---

## 6. Data Model

```json
{
  "categories": [
    { "type": "notifications", "unread": 3, "latestSnippet": "Platform update arriving..." },
    { "type": "activity", "unread": 1, "latestSnippet": "Find the best players..." },
    { "type": "family", "unread": 0, "latestSnippet": "Family event starting soon" },
    { "type": "say_hi", "unread": 2, "latestSnippet": "User followed you" },
    { "type": "feedback", "unread": 0, "latestSnippet": "Feedback received" }
  ],
  "conversations": [
    {
      "conversationId": "abc123",
      "displayName": "SampleUser",
      "latestMessage": "Hello!",
      "timestamp": "2025-10-02T01:53:00Z",
      "unread": 0,
      "system": false
    }
  ]
}
```

---

## 7. Telemetry

| Event | Description |
|-------|-------------|
| message_view | Screen opened |
| message_category_open | Category tapped |
| message_bulk_mark_read | All marked read |
| dm_open | Conversation opened |
| dm_send | Message sent |
| dm_limit_block | Limit dialog shown |

---

## 8. Error & Empty States

| State | Display |
|-------|---------|
| No messages | "No messages yet." |
| Network error | "Unable to load messages. Retry." |
| Failed mark-read | Toast "Could not mark all as read." |

---

## 9. Accessibility

- contentDescriptions for each category including unread state
- Focus order: Categories top → conversations sequential
- Badge announced as live region on unread change

---

## Related Documentation

- [Home Screen](./home-screen.md)
- [Me Screen](./me-screen.md)
