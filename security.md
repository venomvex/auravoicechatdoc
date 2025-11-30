# Security

## Authentication & Authorization

### Authentication Methods
| Method   | Provider          | Notes                        |
|----------|-------------------|------------------------------|
| Google   | Google OAuth      | Primary social login         |
| Facebook | Facebook SDK      | Secondary social login       |
| Mobile   | OTP (4 digits)    | AWS Cognito + SNS            |

### OTP Security
- 4-digit code
- 30s resend cooldown
- Max 5 attempts per day
- Temporary lock after limit exceeded
- Delivered via AWS SNS

### Session Management
- JWT tokens with expiry
- Refresh token rotation
- Device binding (max 4 accounts per device)

### Authorization
- Role-based: User, Room Owner, Room Admin
- Resource-based: Room permissions, VIP features

---

## Data Protection

### Encryption in Transit
- TLS 1.2+ for all API communication
- Certificate pinning (optional)

### Encryption at Rest
- Wallet balances encrypted
- Sensitive PII hashed or encrypted
- Database-level encryption

### Key Management
- Secrets stored in secure vault
- Rotation policy for API keys
- Environment-specific credentials

---

## Threat Model

### Attack Surfaces

| Surface              | Mitigation                          |
|----------------------|-------------------------------------|
| Authentication       | Rate limiting, OTP expiry           |
| Referral fraud       | Device fingerprint, pattern detection|
| Gift manipulation    | Server-side validation              |
| Account takeover     | Device binding, session management  |
| API abuse            | Rate limits, request validation     |

### Fraud Detection

- Sudden spike in gifts/hour
- High referral withdrawals
- Unusual recharge velocity
- Large withdrawal patterns

---

## Device Security

### Device Fingerprinting
- Hashed device ID (SSAID + salt or hybrid)
- Max 4 accounts per device

### Rooted Devices
- Detection implemented
- Policy configurable: warn, block payouts, or block app

### Audit Trail
- All financial transactions logged
- Withdrawal audit logging
- Admin action logging

---

## Compliance

### Privacy
- GDPR-ready data handling
- User data export capability
- Account deletion support

### Financial
- KYC threshold for cash withdrawals (see [Pending Decisions](docs/development/pending-decisions.md#referral--get-cash) Question #41)
- Anti-money laundering considerations
- Transaction record retention

---

## Secure Coding Checklist

### Input Validation
- [ ] All user input validated server-side
- [ ] SQL injection prevention (parameterized queries)
- [ ] XSS prevention (output encoding)
- [ ] CSRF tokens for state-changing operations

### Dependency Scanning
- [ ] Regular dependency audits
- [ ] Known vulnerability scanning
- [ ] Version pinning for critical dependencies

### Secrets Hygiene
- [ ] No secrets in source code
- [ ] Environment variable configuration
- [ ] Secrets rotation procedures

### API Security
- [ ] Authentication required for sensitive endpoints
- [ ] Rate limiting per user and IP
- [ ] Request size limits
- [ ] Correlation ID logging

---

## Incident Response

### Classification
| Severity | Description                    | Response Time |
|----------|--------------------------------|---------------|
| Critical | Data breach, system down       | < 15 min      |
| High     | Security vulnerability active  | < 1 hour      |
| Medium   | Potential vulnerability found  | < 24 hours    |
| Low      | Security improvement needed    | < 1 week      |

### Response Steps
1. Detect and classify
2. Contain the threat
3. Investigate root cause
4. Remediate and recover
5. Post-incident review

---

## Related Documentation

- [API Reference](api.md)
- [Architecture](architecture.md)
- [Operations](operations.md)