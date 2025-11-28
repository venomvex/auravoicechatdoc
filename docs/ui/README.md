# UI Documentation

This folder contains UI/UX specifications for Aura Voice Chat screens.

> **Theme:** Original Purple → White gradient preserved. No cosmic/enhanced UI changes.
> - Background: Gradient #c9a8f1 → #ffffff (top→bottom)
> - Accents: Magenta #d958ff ↔ Cyan #35e8ff
> - No animated tabs, shimmer loading states, or reusable component library (AuraButton, AuraCard, etc.)

---

## Screens

| Screen | Description | Link |
|--------|-------------|------|
| Home Screen | Main landing with tabs, rooms grid, rewards FAB | [home-screen.md](home-screen.md) |
| Me Screen | Profile hub, wallet, progression features | [me-screen.md](me-screen.md) |
| Message Screen | Unified inbox for notifications and DMs | [message-screen.md](message-screen.md) |
| Settings Screen | Account, privacy, cache, legal, logout | [settings-screen.md](settings-screen.md) |
| Onboarding Flow | New user profile setup and recommendations | [onboarding-flow.md](onboarding-flow.md) |
| Room Settings | Owner/admin room management | [room-settings.md](room-settings.md) |
| Profile Modules | Wallet, medal, level, CP, store modules | [profile-modules.md](profile-modules.md) |

---

## Design Principles

### Visual Style
- Purple → White gradient background
- Card-based layouts with 12–16dp corner radius
- Subtle elevation and shadows
- Consistent 8–12dp spacing

### Typography
- Roboto (or app default)
- Titles: 18–20sp
- Body: 14–16sp
- Labels: 12–13sp

### Touch Targets
- Minimum 44dp for all interactive elements
- 56dp for primary buttons and FABs

### Accessibility
- contentDescription for all interactive elements
- 4.5:1 color contrast ratio minimum
- Support for screen readers
- Reduce Motion option for animations

---

## Related Documentation

- [Design Tokens](../design/tokens.md)
- [Accessibility](../design/accessibility.md)
- [Naming Glossary](../naming-glossary.md)
- [Product Specification](../../README.md)
