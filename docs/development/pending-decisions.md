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

## Recommended Answers

The following are **recommended answers** based on industry best practices, user experience considerations, and the Aura Voice Chat app design. These can be used as defaults if no explicit decision is made.

| # | Question | Recommended | Rationale |
|---|----------|-------------|-----------|
| 1 | Non-gold icons style | **A) Slight gradient skeuomorphic** | Maintains consistency with existing Aura branding and provides visual richness |
| 2 | Twilio fallback trigger | **A) Auto if AWS Cognito fails** | Provides seamless user experience without manual intervention |
| 3 | Tutorial scope expansion | **B) Add Seat & Gift steps** | Helps users understand core features without overwhelming them |
| 4 | Banner workflow | **C) Owner drafts + auto-publish if passes automated checks** | Balances control with efficiency; reduces moderation load |
| 5 | Banner rotation interval | **5 seconds** | Industry standard for carousel rotation |
| 6 | Banner refresh | **C) Cached for session only** | Balances freshness with performance |
| 7 | Advanced filters | **A) Phase later (basic UID search)** | Launch faster; iterate based on user feedback |
| 8 | 16-seat toggle mid-session | **A) Allowed seamlessly** | Provides flexibility for room owners |
| 9 | Super Mic visual style | **C) Pulsing aura only on speech** | Draws attention when relevant; less visual noise otherwise |
| 10 | Ban durations default | **D) Last chosen remembered** | Respects moderator preferences |
| 11 | Invite seat popup timeout | **30s** | Balances urgency with response time |
| 12 | Announcement edit cooldown | **B) 30s between edits** | Prevents spam while allowing corrections |
| 13 | Initial owner-triggered events | **B) Lucky Bag + Rocket** | Provides engagement variety from start |
| 14 | Event trigger cooldown (Lucky Bag) | **B) 60s** | Prevents spam while allowing frequent events |
| 15 | Multi-send recipient UX | **A) "Select All" + checkboxes** | Most familiar and accessible UI pattern |
| 16 | Anti-spam for multi-send | **C) Rate-limit by cost total per minute** | More flexible; prevents abuse without penalizing small sends |
| 17 | Gift animation concurrency cap | **A) 5 simultaneous** | Balances visual impact with performance |
| 18 | Region precedence | **A) Room owner region drives catalog** | Consistent catalog per room; easier to understand |
| 19 | Adopt Jackpot feature | **Yes** | Adds excitement and engagement |
| 20 | Jackpot model | **C) Hybrid** | Combines progressive excitement with per-send thrill |
| 21 | Show odds | **C) Only rarity indicator** | Maintains excitement without exposing exact mechanics |
| 22 | Seek & skip permissions | **B) Host + admins** | Shared control while preventing disruption |
| 23 | Latency strategy | **A) Server authoritative timestamp** | Most reliable for sync across clients |
| 24 | Baggage gifts zero cost | **B) Yes but daily cap (10 sends/day)** | Encourages use while preventing abuse |
| 25 | Baggage gift log visible | **A) Yes (history)** | Transparency for senders |
| 26 | Baggage UI | **C) Tabs by source (Event/CP/Friend)** | Organized and easy to navigate |
| 27 | Anti-spam exchange debounce | **A) 2s** | Quick enough for legitimate use; prevents abuse |
| 28 | Large gift warning threshold | **B) 10M coins** | High enough to avoid annoying frequent gifters |
| 29 | VIP billing model | **C) Monthly + upgrade path** | Flexible for users at different spending levels |
| 30 | VIP expiry notification lead time | **B) 7 days** | Sufficient notice for renewal decision |
| 31 | VIP seat frame per tier | **B) Only tier milestones (VIP4, VIP7, VIP10)** | Reduces asset workload; creates meaningful milestones |
| 32 | Auto-claim activity medal toast | **A) Yes minimal toast** | Acknowledges achievement without interrupting |
| 33 | Reorder mode activation | **B) "Edit" button** | Clear affordance; discoverable |
| 34 | CP formation payment model | **A) Single payer full fee (3M)** | Simpler transaction; clear gift gesture |
| 35 | CP dissolution cooldown | **7 days** | Prevents rapid abuse while not being too restrictive |
| 36 | CP dissolution refund | **A) None** | Consistent with one-time purchase model |
| 37 | Records page size default | **10** | More content visible; fewer page loads |
| 38 | Records sort order | **A) Bind date desc** | Shows most recent activity first |
| 39 | Internal admin audit log for withdrawals | **Yes** | Essential for fraud prevention and compliance |
| 40 | External payout methods at launch | **B) Bank + PayPal** | Common methods; manageable complexity |
| 41 | KYC threshold | **$100** | Balances compliance with user friction |
| 42 | Permanent frame at which level | **B) Lv.10 only** | Rewards highest achievement |
| 43 | DM attachments after mutual follow | **B) Images + voice (≤60s)** | Rich communication without video complexity |
| 44 | Read receipts default | **B) Off (toggle in Settings)** | Privacy-respecting default |
| 45 | Quiet hours setting | **A) Yes (user chooses range)** | Respects user preferences |
| 46 | Discoverability default | **A) Searchable** | Promotes social discovery |
| 47 | Analytics opt-out | **A) Yes toggle** | GDPR compliance; builds trust |
| 48 | Cache auto-clear | **B) Auto after 30 days** | Balances storage with cache benefits |
| 49 | Next languages | **A) Urdu + Hindi first** | Target market priority |
| 50 | RTL (Arabic) support at launch | **B) No (later)** | Reduces launch complexity |
| 51 | Device fingerprint fallback | **C) Hybrid (SSAID + hardware)** | Most robust anti-abuse |
| 52 | Rooted devices | **B) Block external payouts** | Allows app use; protects financial operations |
| 53 | Offline daily reward claim | **A) Require online (current)** | Prevents clock manipulation |
| 54 | Timeout/backoff defaults | **C) 3 retries + user manual retry** | Balances automation with user control |
| 55 | Flash sales | **A) Yes (discount 10–30%, 24h duration)** | Drives engagement and urgency |
| 56 | Expiry notice lead time | **B) 72h** | Sufficient time to repurchase |
| 57 | Family member cap | **100** | Large enough for community; manageable |
| 58 | Family leaving cooldown | **72h** | Prevents rapid family hopping |
| 59 | Room name max length | **30** | Allows descriptive names |
| 60 | Profanity filter | **A) Yes (blocked terms list)** | Maintains community standards |
| 61 | Cover image min resolution | **512×512** | Good quality; accessible file sizes |
| 62 | Image moderation | **A) AI automated** | Scalable; immediate feedback |
| 63 | Ownership transfer | **A) Allowed (with 7-day cooldown)** | Flexibility with abuse prevention |

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
- ✅ OTP provider: AWS Cognito primary, Twilio fallback
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
   - A) Auto if AWS Cognito fails
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
