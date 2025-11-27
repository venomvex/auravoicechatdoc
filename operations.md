# Operations

## Monitoring

### Key Metrics

| Metric                    | Threshold        | Alert                    |
|---------------------------|------------------|--------------------------|
| Error rate                | > 1%             | Warning                  |
| Error rate                | > 5%             | Critical                 |
| API latency p99           | > 2s             | Warning                  |
| API latency p99           | > 5s             | Critical                 |
| Active users (drop)       | > 50% in 5 min   | Critical                 |
| Failed logins             | > 100/min        | Warning (possible attack)|

### Dashboards
- Real-time user activity
- Transaction volume
- Error breakdown by endpoint
- Gift economy metrics
- VIP subscription status

---

## Logging

### Log Levels

| Level | Usage                                |
|-------|--------------------------------------|
| DEBUG | Detailed diagnostic (dev only)       |
| INFO  | Normal operations, milestones        |
| WARN  | Recoverable issues                   |
| ERROR | Failures requiring attention         |

### Log Format (JSON)
```json
{
  "timestamp": "2025-11-27T15:30:00Z",
  "level": "INFO",
  "service": "rewards-service",
  "message": "Daily reward claimed",
  "userId": "user_xxx",
  "correlationId": "req_abc123",
  "metadata": { "day": 6, "coins": 30000 }
}
```

### PII Handling
- User IDs: Last 3 chars visible, rest masked
- Phone numbers: Fully masked
- Email: Domain only visible
- Financial data: Aggregates only in logs

### Log Sinks
- Centralized logging service
- Retention: 30 days standard, 90 days for security events

---

## Alerts

### Alert Channels
- Slack/Discord for warnings
- PagerDuty for critical (on-call)
- Email for daily summaries

### On-Call Expectations
- Response within 15 minutes for critical
- Response within 1 hour for warnings
- Escalation path defined

### Alert Fatigue Prevention
- Deduplicate similar alerts
- Group related issues
- Actionable alerts only

---

## Runbooks

### High Error Rate

1. Check error logs for specific endpoint
2. Identify error type (400s vs 500s)
3. Check recent deployments
4. Check external dependency status
5. If 500s: escalate to engineering
6. If 400s: check for client update issues

### Database Connection Issues

1. Check database health metrics
2. Verify connection pool status
3. Check for long-running queries
4. Restart affected service pods if needed
5. Escalate to DBA if persists

### Payment Processing Failures

1. Check payment gateway status
2. Verify API credentials valid
3. Check for rate limiting
4. Review error messages
5. Contact gateway support if needed
6. **Critical:** Do not retry failed transactions manually

### Auth Service Degradation

1. Check OTP provider status (Firebase, Twilio)
2. Verify quota/rate limits not exceeded
3. Check for credential expiry
4. Switch to fallback provider if configured

### Room Service Issues

1. Check WebSocket server health
2. Verify room state consistency
3. Check for memory/connection limits
4. Force-close problematic rooms if needed

---

## Maintenance Windows

### Scheduled Maintenance
- Weekly: Tuesday 3-4 AM UTC
- Announcement: 24 hours in advance
- In-app banner during maintenance

### Emergency Maintenance
- As needed for critical issues
- Immediate notification
- Post-mortem within 24 hours

---

## Related Documentation

- [Troubleshooting](troubleshooting.md)
- [Deployment](deployment.md)
- [Architecture](architecture.md)