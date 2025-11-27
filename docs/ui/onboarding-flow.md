# Onboarding & New User Flow

Capture the "new user" sequence from first login through minimal profile completion, recommendations, and empty states.

> **Theme:** Original Purple → White gradient preserved. No cosmic/enhanced UI changes.

---

## 1. Overall Principles

- Fast entry (≤ 3 screens) before user can explore rooms
- Mandatory minimal data: Display name, gender (non-editable after selection), region
- Skippable tutorial overlays (only Daily Reward highlight initially)
- All actions accessible with one-hand vertical scroll

---

## 2. Screen Sequence

1. Complete Profile (gender + avatar + name + region)
2. Name Settings (if user taps edit again)
3. Recommended Friends (optional follow)
4. Landing: Mine tab empty state + Create Your Room banner + Rewards FAB

---

## 3. Complete Profile Screen

### Elements
- Title: "Complete your profile"
- Avatar placeholder with edit icon (camera/choose image)
- Gender selection (Boy / Girl) — single choice; once selected, locked
- Display name input (pre-filled from auth provider or generic)
- Region selector (dropdown or modal list with search)
- Primary button: "Next"

### Measurements
- Vertical spacing between elements: 24dp
- Avatar size: 96dp circle
- Edit icon: 28dp circle overlay bottom-right
- Gender icons: 72dp circles; spacing 16dp
- Text field height: 52dp; corner radius 12dp
- Button height: 56dp; corner radius 28dp

### Validation
- Name length: 2–30 characters
- Region must be selected
- Gender must be selected before "Next" enabled

---

## 4. Name Settings (Edit Name)

### Trigger
From profile or user taps name field while editing.

### Components
- Avatar preview
- Name input with character counter "12/30"
- Submit button ("Submit")

### Rules
- Character counter updates real-time
- Disallowed characters cause toast or inline note

---

## 5. Recommended Friends Screen

### Purpose
Encourage initial social graph seeding.

### Layout
- Title: "Recommended Friends"
- Grid of user avatars (3 columns, responsive)
- Each avatar cell:
  - Circular image (64dp)
  - Display name under avatar (12–13sp)
  - Selection ring: Yellow stroke (3dp) with checkmark when selected
- Primary button: "Finish" (enabled regardless of selection count)

### Data
- Up to 9–12 recommended accounts
- Sorting: Active senders or region-local trending

---

## 6. Mine Tab — New User Empty State

### Components
- Top tabs: Mine / Popular (Mine active)
- Create Your Room banner:
  - Icon left (room/growth graphic)
  - Text: "Create Your Room"
  - Subtext: "Start your live journey on Aura!"
  - Action badge/button: "+" in yellow circle
  - Banner height: 80dp; corner radius: 16dp
- Sub-tabs row: "Recently" | "Follow"
- Empty state illustration
  - Size: 140×140dp
  - Text: "Oops, currently empty"
- Rewards FAB bottom-right

---

## 7. Avatar Editing

- On tap edit icon → choose image picker (camera/gallery)
- Minimum resolution accepted: 512×512 (auto-crop to circle)

---

## 8. Gender Selection

- Single tap selects; second tap changes until "Next" pressed
- After "Next," gender locked (non-editable permanently)

---

## 9. Region Selector

- Modal with searchable list of countries + flags
- Persist region to profile
- Drives regional gifts catalog

---

## 10. Telemetry

| Event | Description |
|-------|-------------|
| onboarding_profile_complete | Profile completed |
| onboarding_name_edit | Name edited |
| onboarding_recommended_view | Recommendations shown |
| onboarding_recommended_select | Friend selected |
| onboarding_recommended_finish | Recommendations completed |
| onboarding_room_banner_click | Create room tapped |
| onboarding_tutorial_skip | Tutorial skipped |

---

## 11. API Endpoints

| Endpoint | Purpose |
|----------|---------|
| GET /onboarding/recommendations | Get recommended friends |
| POST /onboarding/profile | Save profile data |
| POST /onboarding/recommendations/select | Save selected friends |
| POST /onboarding/complete | Mark onboarding complete |

---

## 12. Accessibility

- All interactive elements ≥ 44dp
- Input fields have contentDescription
- Recommended Friend cells announce selection state
- High contrast ensures yellow accent accessible on white

---

## Related Documentation

- [Home Screen](./home-screen.md)
- [Me Screen](./me-screen.md)
- [Daily Rewards](../features/daily-rewards.md)
