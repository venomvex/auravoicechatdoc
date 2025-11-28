# Pending Decisions

This document consolidates all pending decisions and open questions for Aura Voice Chat.

> **Note:** This replaces the previous `questions-pending.md` and `open-questions-updated.md` files, consolidating all questions into a single source.

---

## How to Use This Document

Answer by listing the number followed by your choice/value:
```
1: A
2: 5 seconds
3: default
```

---

## Decisions Already Locked

The following decisions have been made and are documented in the relevant feature files:

### Branding & Platform
- ✅ Gradient: Top-to-bottom purple-white (single Aura theme)
- ✅ Dark mode: Palette inversion only, no structural change
- ✅ Iconography: Skeuomorphic/Aura style; coins gold, cash green
- ✅ Minimum Android: API 28 (Android 9+)
- ✅ Target SDK: API 34 (Android 14)

### Authentication
- ✅ OTP provider: Firebase primary, Twilio fallback
- ✅ OTP max attempts: 5 per day
- ✅ OTP length: 4 digits
- ✅ OTP resend cooldown: 30 seconds
- ✅ First-time tutorial: Short overlay, skippable

### Daily Rewards
- ✅ Reward values: Day 1-7 as documented
- ✅ VIP multipliers: VIP1 (1.2x) to VIP10 (3.0x)
- ✅ Day boundary: Server UTC
- ✅ Auto-claim Day 1 on first login: Yes

### VIP Systems
- ✅ Both VIP systems retained (multiplier + full SVIP)
- ✅ 10 tiers with multipliers from 1.2x to 3.0x
- ✅ Purchase: Real money only

### Rooms
- ✅ Default seats: 8
- ✅ Upgradeable to: 12 (any owner), 16 (Level ≥20)
- ✅ Super Mic: Level ≥40 OR VIP4+
- ✅ Announcement max: 300 characters
- ✅ Video/Music: YouTube only
- ✅ Playlist max: 15 items

### Gifts & Economy
- ✅ Price range: 50 coins to 200M coins
- ✅ Diamond→Coin rate: 30%
- ✅ Direct coin transfers: Not allowed (gifts only)

---

## Remaining Open Questions

### Branding & Platform

1. **Non-gold icons style:**
   - A) Slight gradient skeuomorphic
   - B) Flat monochrome tinted
   - C) Outline + subtle emboss

### Authentication

2. **Twilio fallback trigger:**
   - A) Auto if Firebase fails
   - B) Manual fallback after 2 failures
   - C) Region-based

3. **Tutorial scope expansion later:**
   - A) Only Daily Reward (now)
   - B) Add Seat & Gift steps
   - C) Full multi-tip sequence

### Home & Banners

4. **Banner workflow:**
   - A) Owner direct publish
   - B) Admin approval required
   - C) Owner drafts + auto-publish if passes automated checks

5. **Banner rotation interval:** (seconds, e.g., 5 or 6)

6. **Banner refresh:**
   - A) Daily cache purge
   - B) Real-time fetch each view
   - C) Cached for session only

### Search

7. **Advanced filters now or later:**
   - A) Phase later (basic UID search)
   - B) Launch with filters (online, VIP)

### Rooms & Stage

8. **16-seat toggle mid-session:**
   - A) Allowed seamlessly
   - B) Only between sessions

9. **Super Mic visual style:**
   - A) Animated ring gradient
   - B) Static dual-color ring
   - C) Pulsing aura only on speech

10. **Ban durations default:**
    - A) 24h
    - B) 1h
    - C) Forever
    - D) Last chosen remembered

11. **Invite seat popup timeout:** (15s / 30s / 60s)

12. **Announcement edit cooldown:**
    - A) None
    - B) 30s between edits
    - C) 5 min between edits

### Events

13. **Initial owner-triggered events:**
    - A) Lucky Bag only
    - B) Lucky Bag + Rocket
    - C) Lucky Bag + Seasonal banner

14. **Event trigger cooldown (Lucky Bag):**
    - A) None
    - B) 60s
    - C) 5 min

### Gifts & Multi-Send

15. **Multi-send recipient UX:**
    - A) "Select All" + checkboxes
    - B) Drag-to-select row
    - C) Quick toggle icons

16. **Anti-spam for multi-send:**
    - A) 2s cooldown after each send
    - B) 5s cooldown
    - C) Rate-limit by cost total per minute

17. **Gift animation concurrency cap:**
    - A) 5 simultaneous
    - B) 10 simultaneous
    - C) Unlimited

### Regional Gifts

18. **Region precedence:**
    - A) Room owner region drives catalog
    - B) Viewer region overrides
    - C) Intersection (shared + global)

### Jackpot (if adopted)

19. **Adopt Jackpot feature?** (yes / no)

20. **Jackpot model (if yes):**
    - A) Progressive pool
    - B) Per-send probability
    - C) Hybrid

21. **Show odds:**
    - A) Yes visible %
    - B) Hidden
    - C) Only rarity indicator

### Video/Music Mode

22. **Seek & skip permissions:**
    - A) Host only
    - B) Host + admins
    - C) Anyone seated

23. **Latency strategy:**
    - A) Server authoritative timestamp
    - B) Peer host time sync
    - C) CDN adaptive only

### Baggage & Rewards

24. **Baggage gifts zero cost to sender, recipient gets full diamonds?**
    - A) Yes
    - B) Yes but daily cap on free sends (specify value)

25. **Baggage gift log visible to sender?**
    - A) Yes (history)
    - B) No

26. **Baggage UI:**
    - A) Grid with send buttons
    - B) List with expandable details
    - C) Tabs by source (Event/CP/Friend)

### Exchange & Transfers

27. **Anti-spam exchange debounce:**
    - A) 2s
    - B) 5s
    - C) 10s

28. **Large gift warning threshold:**
    - A) 1M coins
    - B) 10M coins
    - C) 50M coins

### VIP

29. **Billing model:**
    - A) Monthly subscription tiers
    - B) One-time purchase per tier (stack)
    - C) Monthly + upgrade path

30. **VIP expiry notification lead time:**
    - A) 3 days
    - B) 7 days
    - C) 24h only

31. **VIP seat frame per tier:**
    - A) Yes each tier unique
    - B) Only tier milestones (VIP4, VIP7, VIP10)
    - C) None until assets designed

### Medals

32. **Auto-claim activity medal toast:**
    - A) Yes minimal toast
    - B) Modal dialog
    - C) Silent (inventory update only)

33. **Reorder mode activation:**
    - A) Long press medal row
    - B) "Edit" button
    - C) Settings toggle

### CP

34. **Formation payment model:**
    - A) Single payer full fee (3M)
    - B) Split 50/50
    - C) Initiator pays full + partner confirmation

35. **CP dissolution cooldown:** (3 / 7 / 14 days)

36. **CP dissolution refund:**
    - A) None
    - B) 25% to initiator
    - C) 50% split

### Referral — Get Coins

37. **Records page size default:** (5 / 10)

38. **Records sort order:**
    - A) Bind date desc
    - B) Total coins desc
    - C) Recently active desc

39. **Internal admin audit log for withdrawals:** (yes / no)

### Referral — Get Cash

40. **External payout methods at launch:**
    - A) None (wallet only)
    - B) Bank + PayPal
    - C) Bank + Card + PayPal + Payoneer

41. **KYC threshold:** ($100 / $500 / $1000)

42. **Permanent frame at which level:**
    - A) None
    - B) Lv.10 only
    - C) Lv.8 & Lv.10

### Messaging & Notifications

43. **DM attachments after mutual follow:**
    - A) Images only
    - B) Images + voice (≤60s)
    - C) Images + voice + short video (≤15s)

44. **Read receipts default:**
    - A) On
    - B) Off (toggle in Settings)

45. **Quiet hours setting:**
    - A) Yes (user chooses range)
    - B) No

### Settings & Privacy

46. **Discoverability default:**
    - A) Searchable
    - B) Non-searchable

47. **Analytics opt-out:**
    - A) Yes toggle
    - B) No (only legal privacy notice)

48. **Cache auto-clear:**
    - A) Manual only
    - B) Auto after 30 days
    - C) Auto after 7 days

### Internationalization

49. **Next languages:**
    - A) Urdu + Hindi first
    - B) Urdu + Hindi + Bengali
    - C) Urdu + Hindi + Arabic (needs RTL)

50. **RTL (Arabic) support at launch:**
    - A) Yes
    - B) No (later)

### Security & Anti-Abuse

51. **Device fingerprint fallback:**
    - A) SSAID + salt
    - B) Hardware build + brand hash
    - C) Hybrid (SSAID + hardware)

52. **Rooted devices:**
    - A) Warn only
    - B) Block external payouts
    - C) Block app entirely

### Performance

53. **Offline daily reward claim:**
    - A) Require online (current)
    - B) Queue & sync later

54. **Timeout/backoff defaults:**
    - A) 3 retries exponential (1s,2s,4s)
    - B) 5 retries (1s,2s,4s,8s,16s)
    - C) 3 retries + user manual retry

### Store

55. **Flash sales:**
    - A) Yes (discount 10–30%, 24h duration)
    - B) No

56. **Expiry notice lead time:**
    - A) 24h
    - B) 72h
    - C) None

### Family Feature (Future)

57. **Member cap:** (50 / 100 / custom)

58. **Leaving cooldown:** (24h / 72h / 7 days)

### Room Creation

59. **Room name max length:** (20 / 30 / custom chars)

60. **Profanity filter:**
    - A) Yes (blocked terms list)
    - B) No

61. **Cover image min resolution:** (512×512 / 720×720 / 1080×720)

62. **Image moderation:**
    - A) AI automated
    - B) Manual review queue
    - C) None at launch

63. **Ownership transfer:**
    - A) Allowed (with 7-day cooldown)
    - B) Not allowed
    - C) Allowed immediate

---

## Pending Asset Deliverables

The following require screenshots or design assets:
- CP thresholds full table (5M → 250M+)
- VIP frames/effects per tier visuals
- Store pricing ranges by category and rarity

---

## Related Documentation

- [Product Specification](../../README.md)
- [Feature Documentation](../features/)
- [VIP Systems](../features/vip-systems.md)
- [AuraPass](../features/aurapass.md)
- [Recharge Event](../features/recharge-event.md)
- [Rocket System](../features/rocket-system.md)
