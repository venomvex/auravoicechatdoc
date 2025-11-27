# Settings Screen — UI/UX Specification

Account management, subscription settings, privacy controls, cache maintenance, legal documents, app info, and session logout.

> **Theme:** Original Purple → White gradient preserved. No cosmic/enhanced UI changes.

---

## 1. Layout

- **App bar:**
  - Back arrow (left)
  - Title "Settings"
- Scrollable list with logical groupings
- Footer: Logout button + Version text

---

## 2. Settings Groups

### Group 1: Account & Subscription
| Item | Description |
|------|-------------|
| Account | Profile edit, change password, manage linked providers |
| SVIP Settings | View active tier, renew/upgrade, billing history |

### Group 2: Privacy
| Item | Description |
|------|-------------|
| Privacy Settings | Online status, profile discoverability, mic permissions |

### Group 3: Maintenance
| Item | Description |
|------|-------------|
| Clean Cache | Shows current cache size (e.g., 3.1MB) |

### Group 4: Legal & Info
| Item | Description |
|------|-------------|
| Terms of Service | Opens in-app WebView |
| Privacy Policy | Opens in-app WebView |
| Refund Policy | Opens in-app WebView |
| About Us | Static page (mission, contact) |
| Contact Us | Support form or email |

### Footer
| Item | Description |
|------|-------------|
| Logout | Confirm dialog, clears session |
| Version | e.g., "Version: 1.4.1" |

---

## 3. Item Functions

### Account
- Profile edit
- Change password (if password auth)
- Manage linked providers (Google/Facebook/Mobile)

### SVIP Settings
- View active tier
- Renew/upgrade options
- Billing history

### Privacy Settings
- Visibility (online status toggle)
- Profile discoverability (searchable toggle)
- Mic permission rationale
- Data sharing preferences (future)

### Clean Cache
- Tap: Show confirm dialog "Clear cached images & temporary files?"
- Actions: "Clear" / "Cancel"
- Update size after clearing
- Success toast: "Cache cleared"

### Legal Documents
- Open in-app WebView with share & close options

### Logout
- Confirm dialog: "Are you sure you want to logout?"
- On success: Clear session tokens, navigate to Login

---

## 4. Visual Specs

- **Item height:** 56dp
- **Padding horizontal:** 16dp
- **Section separation:** 24dp vertical
- **Text:**
  - Title: 16sp Medium
  - Subtext (cache size, version): 12–13sp Regular, dim color (#7A7A88)
- **Logout button:**
  - Height: 52–56dp
  - Brand accent color
  - Radius: 24–28dp
  - Center-aligned label

---

## 5. Data Model

```json
{
  "cacheSizeBytes": 3250583,
  "versionName": "1.4.1",
  "vipTier": null,
  "privacySettings": {
    "showOnlineStatus": true,
    "discoverable": true
  }
}
```

---

## 6. Telemetry

| Event | Description |
|-------|-------------|
| settings_view | Screen opened |
| settings_account_open | Account tapped |
| settings_vip_open | SVIP Settings tapped |
| settings_privacy_open | Privacy Settings tapped |
| settings_cache_clear | Cache cleared |
| settings_doc_open | Legal doc opened |
| settings_logout_confirm | Logout confirmed |
| settings_logout_complete | Session cleared |

---

## 7. Error Handling

| Error | Display |
|-------|---------|
| Cache clear fail | Toast "Unable to clear cache" |
| Document load error | "Failed to load. Retry?" |
| Logout network failure | Retry or offline local clear |

---

## 8. Accessibility

- contentDescription per item
- Focus order top → bottom
- Logout accessible via keyboard navigation
- High contrast mode: Ensure text legibility

---

## Related Documentation

- [Me Screen](./me-screen.md)
- [VIP Systems](../features/vip-systems.md)
- [Privacy & Security](../../security.md)
