# Deployment

## Environments

| Environment | Purpose                | Notes                           |
|-------------|------------------------|---------------------------------|
| Development | Local development      | Debug builds, mock services     |
| Staging     | Pre-release testing    | Production-like, test data      |
| Production  | Live users             | Full monitoring, real data      |

### Environment Differences
- **Dev:** Debug logging, mock payment
- **Staging:** Production APIs, test accounts
- **Production:** Full security, real transactions

### Feature Flags
All major features controlled by flags:
- Gift animations
- Seat upgrades
- Super Mic
- Lucky Bag events
- Video/Music playlist
- Referral cash payouts

---

## CI/CD

### Pipeline Steps

1. **Build**
   - Compile application
   - Run linters
   - Generate APK/AAB

2. **Test**
   - Unit tests
   - Integration tests
   - UI tests (staging)

3. **Security Scan**
   - Dependency vulnerability scan
   - Static code analysis

4. **Deploy**
   - Upload to internal testing
   - Staged rollout (if production)

### Build Artifacts
- APK (debug and release)
- AAB (Google Play)
- Mapping files (ProGuard)

### Promotion Rules
- All tests passing
- Security scan clean
- Code review approved
- QA sign-off (production)

---

## Configuration & Secrets

### Environment Variables

| Variable          | Required | Description              |
|-------------------|----------|--------------------------|
| API_BASE_URL      | Yes      | Backend API endpoint     |
| FIREBASE_CONFIG   | Yes      | Firebase configuration   |
| ANALYTICS_KEY     | No       | Analytics service key    |

### Secret Storage
- **Development:** Local `.env` file (gitignored)
- **CI/CD:** GitHub Actions secrets
- **Production:** Secure vault service

### Sensitive Values
- OAuth client secrets
- Firebase service accounts
- Payment gateway keys
- API signing keys

---

## Rollback Strategy

### Automated Rollback Conditions
- Error rate > 5% for 5 minutes
- Critical crash loop detected
- Payment processing failures

### Manual Rollback Steps

1. **Identify Issue**
   - Check error logs
   - Review recent deploys

2. **Initiate Rollback**
   - Revert to previous stable version
   - Update feature flags if needed

3. **Validate**
   - Confirm services healthy
   - Test critical user flows
   - Monitor error rates

4. **Communicate**
   - Notify team
   - Document incident

---

## Post-Deploy Checks

### Health Probes
- `/health` endpoint returns 200
- Database connectivity verified
- Redis cache accessible
- External APIs reachable

### Smoke Tests
- [ ] User can log in
- [ ] Daily reward displays correctly
- [ ] Wallet balance loads
- [ ] Room creation works
- [ ] Gift sending succeeds

### Monitoring
- Error rate baseline
- Latency metrics
- Active user count
- Transaction volume

---

## Release Schedule

### Regular Releases
- Weekly for minor updates
- Bi-weekly for feature releases

### Hotfixes
- As needed for critical issues
- Expedited review process

### Play Store
- Alpha → Beta → Production
- Staged rollout: 1% → 10% → 50% → 100%

---

## Related Documentation

- [Architecture](architecture.md)
- [Operations](operations.md)
- [Release Playbook](release-playbook.md)