# Accessibility Guidelines

Accessibility standards and implementation for Aura Voice Chat.

## Standards

The app targets **WCAG 2.1 Level AA** compliance.

---

## Color & Contrast

### Minimum Contrast Ratios

| Element          | Ratio Required |
|------------------|----------------|
| Normal text      | 4.5:1          |
| Large text (18sp+)| 3:1           |
| UI components    | 3:1            |

### Implementation Notes

- Use dark text (`#2E2E2E` or `#1A1A1A`) on lighter gradient portions
- Test all color combinations with contrast checkers
- Provide sufficient contrast in both light and dark modes

---

## Motion & Animation

### Reduce Motion Setting

When system "Reduce Motion" is enabled, disable:

- Particle effects
- Glow animations
- Banner auto-rotation
- Gift animations
- Pulse effects on claimable items
- Auto-playing visual effects

### Keep Functional Transitions

- Navigation transitions
- State change indicators
- Loading spinners

### Animation Guidelines

- No flashing > 3 times per second
- Provide pause/stop for auto-playing content
- Keep essential animations subtle

---

## Touch Targets

### Minimum Sizes

| Element        | Minimum Size |
|----------------|--------------|
| Buttons        | 44×44 dp     |
| Icons          | 44×44 dp     |
| List items     | 48 dp height |
| Interactive    | 44×44 dp     |

### Spacing

- Minimum 8dp between touch targets
- Increase spacing on smaller screens

---

## Screen Reader Support

### Content Descriptions

All interactive elements must have:

- Descriptive `contentDescription` (Android)
- Meaningful labels
- State announcements (selected, disabled, etc.)

### Focus Order

- Logical reading order
- Skip repetitive navigation
- Announce dynamic content changes

### Examples

```xml
<!-- Button example -->
<Button
    android:contentDescription="Login with Google"
    ... />

<!-- Image example -->
<ImageView
    android:contentDescription="Aura Voice Chat logo: neon microphone inside concentric ring"
    ... />
```

---

## Typography

### Font Scaling

| Maximum Scale | Behavior                    |
|---------------|------------------------------|
| 1.5x          | Full support required        |
| >1.5x         | Best effort, may truncate    |

### Overflow Handling

- Text truncation with ellipsis
- Multi-line where appropriate
- Icon-only mode at extreme scales

---

## Alternative Text

### Images

- Decorative: `contentDescription=""` (empty)
- Informative: Descriptive text
- Functional: Action description

### Icons

- Paired with label: description optional
- Standalone: description required

---

## Navigation

### Keyboard/D-pad

- All functionality accessible without touch
- Visible focus indicators
- Logical focus order

### Back Navigation

- System back button works consistently
- Clear navigation hierarchy

---

## Forms & Inputs

### Labels

- All fields have associated labels
- Error messages clearly associated
- Required fields indicated

### Validation

- Errors announced to screen readers
- Clear error descriptions
- Not color-only error indication

---

## Testing Checklist

- [ ] Test with TalkBack (Android screen reader)
- [ ] Test with font scaling at 1.5x
- [ ] Test with Reduce Motion enabled
- [ ] Verify all touch targets ≥ 44×44 dp
- [ ] Check color contrast with analyzer
- [ ] Test keyboard/d-pad navigation
- [ ] Verify focus order is logical

---

## Related Documentation

- [Design Tokens](./tokens.md)
- [Getting Started](../getting-started.md)
