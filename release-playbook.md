# Release Playbook

Step-by-step guide for releasing Aura Voice Chat.

---

## Pre-Release (T-3 days)

### Code Freeze
- [ ] Announce code freeze to team
- [ ] Create release branch from `develop`
- [ ] No new features, only critical fixes

### Verification
- [ ] All tests passing
- [ ] Changelog updated with all changes
- [ ] Version number bumped
- [ ] Migration scripts tested (if any)
- [ ] Feature flags configured correctly

### Documentation
- [ ] Release notes drafted
- [ ] User-facing changes documented
- [ ] API changes documented (if any)

---

## Pre-Release (T-1 day)

### Final Checks
- [ ] QA sign-off received
- [ ] Security scan completed
- [ ] Performance benchmarks acceptable
- [ ] Staging environment tested

### Prepare Artifacts
- [ ] Release APK/AAB built
- [ ] ProGuard mapping files archived
- [ ] Screenshots updated (if UI changes)

### Communication
- [ ] Internal team notified of release schedule
- [ ] Support team briefed on changes
- [ ] Rollback plan reviewed

---

## Release Day

### Execute Pipeline
1. [ ] Trigger production build
2. [ ] Verify build artifacts
3. [ ] Upload to Google Play Console
4. [ ] Configure staged rollout (1% initial)

### Monitor (First Hour)
- [ ] Watch crash reports
- [ ] Monitor error rates
- [ ] Check key metrics baseline
- [ ] Verify critical user flows

### Staged Rollout Schedule
| Phase | Rollout % | Wait Time | Go/No-Go Criteria       |
|-------|-----------|-----------|-------------------------|
| 1     | 1%        | 4 hours   | Error rate < 1%         |
| 2     | 10%       | 12 hours  | Error rate < 1%         |
| 3     | 50%       | 24 hours  | Error rate < 0.5%       |
| 4     | 100%      | —         | Full release            |

### Announce
- [ ] Internal: Slack/Discord announcement
- [ ] External: In-app banner (if significant)
- [ ] External: Social media (if warranted)

---

## Post-Release

### Validate User Flows (First 24 hours)
- [ ] Login (all providers)
- [ ] Daily reward claim
- [ ] Wallet operations
- [ ] Gift sending
- [ ] Room creation and joining
- [ ] Referral binding

### Collect Feedback
- [ ] Monitor Play Store reviews
- [ ] Check support tickets
- [ ] Review user feedback channels
- [ ] Note issues for next release

### Follow-Up Issues
- [ ] Create tickets for discovered issues
- [ ] Prioritize based on impact
- [ ] Schedule hotfix if critical

### Documentation
- [ ] Update production version in docs
- [ ] Archive release notes
- [ ] Update known issues if needed

---

## Rollback Procedure

### Triggers
- Error rate > 5% for 10 minutes
- Critical crash affecting > 1% users
- Payment/transaction failures
- Security vulnerability discovered

### Steps
1. [ ] Halt current rollout in Play Console
2. [ ] Revert to previous version
3. [ ] Notify team immediately
4. [ ] Begin incident investigation
5. [ ] Communicate with affected users

### Post-Rollback
- [ ] Root cause analysis
- [ ] Fix implementation
- [ ] Enhanced testing
- [ ] Re-release when ready

---

## Hotfix Process

For critical issues requiring immediate release:

1. [ ] Create `hotfix/*` branch from `main`
2. [ ] Implement minimal fix
3. [ ] Expedited code review
4. [ ] Run critical test suite
5. [ ] Release with 25% → 100% rollout (2 hours each)
6. [ ] Merge back to `main` and `develop`

---

## Changelog Entry Template

```markdown
## [X.Y.Z] - YYYY-MM-DD

### Added
- New feature descriptions

### Changed
- Modification descriptions

### Fixed
- Bug fix descriptions

### Security
- Security-related changes
```

---

## Related Documentation

- [Deployment](deployment.md)
- [Operations](operations.md)
- [Changelog](changelog.md)