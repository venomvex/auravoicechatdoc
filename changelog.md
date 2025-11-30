# Changelog

All notable changes to Aura Voice Chat are documented here.

Format follows [Keep a Changelog](https://keepachangelog.com/) and [Semantic Versioning](https://semver.org/).

---

## [Unreleased]

### Added
- **Games System** — Complete game mechanics for Lucky Spin, Dice Roll, Card Flip, Treasure Box, Lucky Number, Coin Toss, Slot Machine with AWS integration
- **EXP & Level System** — Level 1-100 progression with rewards, feature unlocks, VIP multipliers, and AWS Lambda functions
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
- **AWS Setup** — Cognito Auth, S3 Storage, Pinpoint Analytics/Push, CloudWatch monitoring
- **Build & Gradle** — Gradle 8.7.3, SDK 28-35, CI/CD configuration with AWS
- Complete data JSON files for all systems (games, levels, events, animations, VIP, jar tasks)
- VIP systems documentation (both multiplier-only and full SVIP)
- Comprehensive feature documentation for all major features
- Design tokens and theme specification
- Accessibility guidelines
- Complete API reference
- Troubleshooting guide
- Operations runbooks
- Recommended answers for all 63 pending decisions in pending-decisions.md
- **Android Activities** — AuthActivity, KycActivity, RoomActivity implementations
- **Android Services** — VoiceRoomService, AuraPushNotificationService for background operations
- **Resource Files** — Adaptive launcher icons, vector drawables, Lottie animations
- **Localization Support** — locales_config.xml for per-app language preferences

### Changed
- Consolidated pending decisions into single document
- Reorganized documentation structure with docs/ folder
- Updated all skeleton documentation files with content
- **Migrated from Firebase to AWS** — Replaced all Firebase services with AWS equivalents
- Updated AndroidManifest.xml with Android 14+ requirements
- Changed screen orientation from `portrait` to `fullSensor` for better accessibility
- Split deep link intent-filters for cleaner manifest structure

### Fixed
- Documentation structure and cross-linking
- Fixed 9 broken links in docs/features/ pointing to wrong README.md
- Fixed broken link in pending-decisions.md (../docs/features/ → ../features/)
- Fixed 3 broken getting-started.md links in docs/design/ files
- Fixed logo image path in docs/design/logo.md
- Fixed assets path reference in getting-started.md
- Added missing Settings Screen reference in message-screen.md
- Added READ_MEDIA_VISUAL_USER_SELECTED permission for Android 14+ photo picker

### Deprecated
- N/A

### Removed
- Duplicate question files (consolidated)
- Firebase dependencies and configuration files
- Firebase-related documentation (replaced with AWS)

### Security
- Added security documentation with threat model
- AWS IAM-based security model
- Cognito-based authentication with MFA support

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
- Android 15 (API 35) target
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