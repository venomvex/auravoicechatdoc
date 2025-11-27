# Troubleshooting

Common issues and their resolutions for Aura Voice Chat.

---

## Authentication Issues

### "OTP not received"
**Cause:** Network issues, carrier blocking, or quota exceeded

**Resolution:**
1. Verify phone number format (E.164)
2. Check OTP quota (max 5/day)
3. Wait for cooldown (30s) before retry
4. Try fallback provider if enabled
5. Check carrier spam filters

### "Login failed - please try again"
**Cause:** OAuth provider error or network timeout

**Resolution:**
1. Check internet connectivity
2. Verify OAuth credentials in dashboard
3. Check provider status page
4. Clear app cache and retry
5. Check device time synchronization

### "Device limit reached"
**Cause:** Max 4 accounts registered on device

**Resolution:**
1. Remove unused accounts from device
2. Contact support for account recovery
3. Do not attempt to bypass (leads to ban)

---

## Daily Rewards Issues

### "Reward not appearing"
**Cause:** Already claimed or sync issue

**Resolution:**
1. Check claim status via API
2. Verify UTC day boundary
3. Force sync user data
4. Check for cached state conflicts

### "VIP multiplier not applied"
**Cause:** VIP expired or sync delay

**Resolution:**
1. Verify VIP expiry date
2. Force refresh VIP status
3. Recalculate reward if needed
4. Contact support for compensation

---

## Wallet Issues

### "Balance not updating"
**Cause:** Cache inconsistency or failed transaction

**Resolution:**
1. Force refresh wallet balance
2. Check transaction logs
3. Verify no pending transactions
4. Clear local cache

### "Exchange failed"
**Cause:** Rate limiting or insufficient balance

**Resolution:**
1. Wait for cooldown (2-10s)
2. Verify diamond balance
3. Check for anti-spam trigger
4. Retry with smaller amount

---

## Room Issues

### "Cannot join room"
**Cause:** Room full, banned, or network issue

**Resolution:**
1. Check room capacity
2. Verify not banned from room
3. Check internet connectivity
4. Try again after 30s

### "Video not syncing"
**Cause:** Latency or client drift

**Resolution:**
1. Refresh playback
2. Check network quality
3. Lower quality setting
4. Rejoin room

### "Mic not working"
**Cause:** Permission denied or hardware issue

**Resolution:**
1. Check microphone permissions
2. Restart app
3. Test with other apps
4. Check device audio settings

---

## Gift Issues

### "Gift send failed"
**Cause:** Insufficient balance or rate limit

**Resolution:**
1. Check coin balance
2. Wait for cooldown (2-5s)
3. Reduce quantity
4. Check recipient status

### "Animation not playing"
**Cause:** Concurrency cap reached or Reduce Motion enabled

**Resolution:**
1. Wait for current animations to complete
2. Check Reduce Motion setting
3. Lower animation quality setting
4. Force refresh room state

---

## Referral Issues

### "Code not binding"
**Cause:** Invalid code or already bound

**Resolution:**
1. Verify code format
2. Check if already bound
3. Contact inviter for correct code
4. Self-referral not allowed

### "Withdrawal pending"
**Cause:** Cooldown or verification required

**Resolution:**
1. Check cooldown timer
2. Verify minimum reached
3. Complete KYC if required
4. Check withdrawal history

---

## Diagnostics

### Collect Debug Info
```
Settings → About → Tap version 7 times → Export logs
```

### Key Logs to Review
- Auth attempts and results
- API errors (status codes)
- Network connectivity events
- Transaction IDs

### Support Escalation Info
When contacting support, include:
- User ID
- Device model and OS version
- App version
- Transaction ID (if applicable)
- Steps to reproduce
- Screenshots

---

## Related Documentation

- [Operations](operations.md)
- [API Reference](api.md)
- [Security](security.md)