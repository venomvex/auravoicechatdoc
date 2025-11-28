# Changelog

All notable changes to Aura Voice Chat are documented here.

Format follows [Keep a Changelog](https://keepachangelog.com/) and [Semantic Versioning](https://semver.org/).

---

## [Unreleased]

### Added
- **Games System** — Complete game mechanics for Lucky Spin, Dice Roll, Card Flip, Treasure Box, Lucky Number, Coin Toss, Slot Machine with Firebase integration
- **EXP & Level System** — Level 1-100 progression with rewards, feature unlocks, VIP multipliers, and Firebase cloud functions
- **Events System** — Room slider events, recharge events, seasonal events, competitions, lucky events with full configuration
- **Custom Animations** — Original Aura-branded vehicles, gift animations, frames, seat effects, mic effects, and themes
- **Earning System** — User earning through activity targets with multi-region payouts (EasyPaisa, JazzCash, UPI, Paytm, PayPal, bank)
- **Reseller System** — Seller tiers, wholesale pricing, seller panel, volume bonuses
- **CP & Friend System** — Complete CP levels (Lv.1-10), Friend levels, privileges, daily tasks
- **Family System** — Creation, roles, perks, ranking, cooldowns
- **Medals System** — Gift/Achievement/Activity/Special medals with milestones
- **Profile & Inventory** — Frames, themes, vehicles, Custom ID, Super Mic
- **Rankings** — Room, Player, CP, Family leaderboards with rewards
- **Privacy & Terms** — Consolidated Privacy Policy and Terms of Service
- **Owner CMS** — Full admin controls, versioning, rollback capabilities
- **Firebase Setup** — Auth, Crashlytics, Analytics, Firestore rules, cloud functions
- **Build & Gradle** — Gradle 8.4, SDK 28-34, CI/CD configuration
- Complete data JSON files for all systems (games, levels, events, animations, VIP, jar tasks)
- VIP systems documentation (both multiplier-only and full SVIP)
- Comprehensive feature documentation for all major features
- Design tokens and theme specification
- Accessibility guidelines
- Complete API reference
- Troubleshooting guide
- Operations runbooks
- Recommended answers for all 63 pending decisions in pending-decisions.md

### Changed
- Consolidated pending decisions into single document
- Reorganized documentation structure with docs/ folder
- Updated all skeleton documentation files with content

### Fixed
- Documentation structure and cross-linking
- Fixed 9 broken links in docs/features/ pointing to wrong README.md
- Fixed broken link in pending-decisions.md (../docs/features/ → ../features/)
- Fixed 3 broken getting-started.md links in docs/design/ files
- Fixed logo image path in docs/design/logo.md
- Fixed assets path reference in getting-started.md
- Added missing Settings Screen reference in message-screen.md

### Deprecated
- N/A

### Removed
- Duplicate question files (consolidated)

### Security
- Added security documentation with threat model

---

## [1.0.0] - TBD

Initial release planned features:

### Core Features
- User authentication (Google, Facebook, Mobile OTP)
- Daily login rewards with VIP multipliers
- VIP system with tiers and benefits
- Medal system for achievements
- Coin and Diamond economy
- Gift sending with animations
- Voice/video rooms
- Video/Music mode (YouTube)
- Referral programs (Get Coins, Get Cash)
- CP (Couple Partnership)
- Store with cosmetic items
- Messaging and notifications

### Platform
- Android 9+ (API 28) minimum
- Android 14 (API 34) target
- Purple/White gradient theme
- Dark mode support

---

## Version History Template

For future releases, use this format:

```markdown
## [X.Y.Z] - YYYY-MM-DD

### Added
- New features

### Changed
- Changes in existing functionality

### Fixed
- Bug fixes

### Deprecated
- Features to be removed in future

### Removed
- Removed features

### Security
- Vulnerability fixes
```

---

## Related Documentation

- [Release Playbook](release-playbook.md)
- [Contributing](contributing.md)