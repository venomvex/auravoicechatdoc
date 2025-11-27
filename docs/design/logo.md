# Logo Specification

Official logo guidelines for Aura Voice Chat.

## Primary Logo

![Aura Voice Chat Logo](../../assets/aura_logo.png)

*Reference: Neon microphone inside concentric techno rings on dark background with pink/purple/cyan accents*

---

## Logo Description

> "Aura Voice Chat logo: neon microphone inside concentric ring on dark, tech-accent background."

---

## Technical Specifications

| Aspect              | Guidance                                               |
|---------------------|--------------------------------------------------------|
| Source Size         | Master artwork at 1024×1024 px (PNG)                   |
| Formats             | PNG (raster), optional SVG, WebP for in-app            |
| Background          | Dark techno grid with neon ring; non-transparent       |
| Safe Area           | Keep ring and mic within 80% of canvas width/height    |
| Minimum Display     | 48×48 dp (in lists). Below this, use "mic only" glyph  |
| Alt Text            | As described above                                      |

---

## Usage Guidelines

### Do

- Maintain glow and ring integrity
- Use on dark or compatible backgrounds
- Provide adequate spacing around logo

### Don't

- Recolor the microphone independently
- Stretch or distort proportions
- Crop the ring

---

## Android Adaptive Icon

### Layers

| Layer      | Content                               |
|------------|---------------------------------------|
| Foreground | Microphone + inner neon ring          |
| Background | Solid or gradient (`#c9a8f1` → `#5e4b85`) |

### Mipmap Assets

| Density  | Size (px) |
|----------|-----------|
| mdpi     | 48        |
| hdpi     | 72        |
| xhdpi    | 96        |
| xxhdpi   | 144       |
| xxxhdpi  | 192       |

### File Structure

```
mipmap-anydpi-v26/ic_launcher.xml
mipmap-anydpi-v26/ic_launcher_round.xml
mipmap-mdpi/ic_launcher.png
mipmap-hdpi/ic_launcher.png
mipmap-xhdpi/ic_launcher.png
mipmap-xxhdpi/ic_launcher.png
mipmap-xxxhdpi/ic_launcher.png
```

### Adaptive XML

```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
```

---

## Extracted Color Palette

Use for accent chips, loading spinners, highlight glows:

| Name             | Hex       |
|------------------|-----------|
| Neon Fuchsia     | `#ff34d9` |
| Vibrant Magenta  | `#d958ff` |
| Cyan Accent      | `#35e8ff` |
| Dark Canvas      | `#12141a` |
| Soft Glow White  | `#e9f2ff` |

---

## Preloader Usage

### Specifications

| Element    | Value                              |
|------------|------------------------------------|
| Duration   | 0.8–1.2s or until init completes   |
| Placement  | Centered horizontally              |
| Top margin | ~64dp from status bar inset        |
| Animation  | Subtle fade-in                     |
| >2s delay  | Show spinner below logo            |

---

## Related Documentation

- [Design Tokens](./tokens.md)
- [Getting Started](../getting-started.md)
