# Design Tokens & Theming

Visual design specifications for Aura Voice Chat.

## Theme Identity

The app uses a **Purple → White gradient** theme with neon accents.

> **Note:** The original UI is preserved. No "Cosmic" or altered UI changes are applied.

---

## Color Palette

### Primary Colors

| Token            | Hex       | Usage                           |
|------------------|-----------|----------------------------------|
| Primary Purple   | `#c9a8f1` | Gradient start, buttons, accents|
| Primary White    | `#ffffff` | Gradient end, backgrounds        |
| Dark Canvas      | `#12141a` | Dark mode background             |

### Accent Colors

| Token           | Hex       | Usage                            |
|-----------------|-----------|-----------------------------------|
| Accent Magenta  | `#d958ff` | Highlights, glow effects          |
| Accent Cyan     | `#35e8ff` | Secondary highlights              |
| Neon Fuchsia    | `#ff34d9` | Glow effects, special elements    |
| Soft Glow White | `#e9f2ff` | Subtle highlights                 |

### Functional Colors

| Token        | Hex       | Usage            |
|--------------|-----------|------------------|
| Coin Gold    | `#ffd700` | Coin indicators  |
| Cash Green   | `#22c55e` | Cash/USD values  |
| Error Red    | `#ef4444` | Error states     |
| Success Green| `#22c55e` | Success states   |

---

## Gradients

### Background Gradient (Light Mode)

```css
background: linear-gradient(180deg, #c9a8f1 0%, #ffffff 100%);
```

**Android XML:**
```xml
<gradient
    android:startColor="#c9a8f1"
    android:endColor="#ffffff"
    android:angle="270" />
```

### Dark Mode Variant

```css
background: linear-gradient(180deg, #1a1524 0%, #33264d 100%);
```

---

## Typography

### Font Family
- Primary: **Roboto** (system default on Android)
- Fallback: System sans-serif

### Font Sizes

| Style         | Size (sp) | Weight   | Usage                |
|---------------|-----------|----------|----------------------|
| Display Large | 28        | Medium   | Headers              |
| Title         | 24        | Medium   | Section titles       |
| Body          | 16        | Regular  | Main content         |
| Caption       | 14        | Regular  | Secondary text       |
| Label         | 12        | Medium   | Buttons, labels      |

### Font Scaling
- Support system scaling up to **1.5x**
- UI adapts to prevent overflow

---

## Spacing & Layout

### Spacing Scale

| Token | Value (dp) |
|-------|------------|
| xs    | 4          |
| sm    | 8          |
| md    | 16         |
| lg    | 24         |
| xl    | 32         |
| xxl   | 48         |

### Common Layouts

| Element         | Specification            |
|-----------------|--------------------------|
| Side padding    | 16dp minimum             |
| Button height   | 48-56dp                  |
| Corner radius   | 8dp (buttons, cards)     |
| Icon size       | 24dp (standard)          |
| Touch target    | ≥44×44dp                 |

---

## Components

### Buttons

```
Height: 48-56dp
Corner radius: 8dp
Horizontal padding: 24dp
```

### Cards

```
Corner radius: 12dp
Elevation: 2dp (light), 4dp (dark)
Padding: 16dp
```

### Bottom Navigation

```
Height: 56dp
Items: Home, Message, Me
Default selection: Home
```

---

## Iconography

### Style
- Skeuomorphic / Aura style throughout app
- Consistent with platform icons

### Specific Icons

| Element    | Style                |
|------------|----------------------|
| Coins      | Gold colored         |
| Cash/USD   | Green colored        |
| Navigation | Tinted monochrome    |
| Actions    | Platform consistent  |

---

## Accessibility

### Contrast
- WCAG AA minimum for text/buttons
- Dark text on light gradient portions

### Motion
- Reduce Motion disables:
  - Particle animations
  - Glow effects
  - Auto-play animations
- Keep functional transitions

### Screen Readers
- All interactive elements have content descriptions
- Logical focus order

---

## Dark Mode

### Support Status
- **Supported:** Same layout/components with palette inversion

### Differences
- Background: Dark Canvas (`#12141a`)
- Text: Light colors for contrast
- Gradients: Inverted dark variant
- No structural changes

---

## Related Documentation

- [Logo Specification](./logo.md)
- [Getting Started](../getting-started.md)
- [Accessibility Guidelines](./accessibility.md)
