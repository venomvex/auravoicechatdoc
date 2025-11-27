# Contributing

Guidelines for contributing to Aura Voice Chat.

---

## Workflow

### Branching Model
- `main` — Production-ready code
- `develop` — Integration branch
- `feature/*` — New features
- `bugfix/*` — Bug fixes
- `hotfix/*` — Production hotfixes

### Branch Naming
```
feature/add-lucky-bag-event
bugfix/fix-daily-reward-multiplier
hotfix/fix-payment-crash
```

### Commit Message Conventions

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <description>

[optional body]

[optional footer]
```

**Types:**
- `feat` — New feature
- `fix` — Bug fix
- `docs` — Documentation
- `style` — Formatting
- `refactor` — Code restructuring
- `test` — Adding tests
- `chore` — Maintenance

**Examples:**
```
feat(rewards): add VIP multiplier to daily claim
fix(wallet): correct diamond exchange calculation
docs(api): update referral endpoints documentation
```

---

## Pull Request Checklist

Before submitting a PR:

- [ ] Code compiles without errors
- [ ] All tests pass
- [ ] New features have tests
- [ ] Documentation updated
- [ ] No sensitive data in code
- [ ] Follows coding standards
- [ ] PR description explains changes

### Review Expectations
- At least 1 approval required
- Address all comments
- Squash commits before merge

---

## Coding Standards

### Kotlin Style
- Follow [Kotlin style guide](https://kotlinlang.org/docs/coding-conventions.html)
- Use ktlint for formatting
- Prefer immutability
- Use meaningful names

### Android Guidelines
- MVVM architecture
- Repository pattern for data
- Use ViewBinding
- Avoid memory leaks

### Linters and Formatters
```bash
# Run linter
./gradlew ktlintCheck

# Auto-format
./gradlew ktlintFormat

# Run all checks
./gradlew check
```

### Directory Structure
```
app/
├── src/main/
│   ├── java/com/aura/voicechat/
│   │   ├── data/           # Repositories, data sources
│   │   ├── domain/         # Use cases, models
│   │   ├── presentation/   # ViewModels, UI
│   │   └── di/             # Dependency injection
│   └── res/                # Resources
└── src/test/               # Unit tests
```

---

## Local Development

### Setup Steps

1. Clone repository
2. Install Android Studio
3. Copy `.env.example` to `.env`
4. Add required API keys
5. Sync Gradle
6. Run app

### Useful Commands
```bash
# Build debug APK
./gradlew assembleDebug

# Run tests
./gradlew test

# Run lint
./gradlew lint

# Generate coverage report
./gradlew jacocoTestReport
```

### Testing
- Write unit tests for business logic
- Use MockK for mocking
- UI tests for critical flows

---

## Issue Management

### Labels

| Label       | Description                     |
|-------------|---------------------------------|
| bug         | Something isn't working         |
| feature     | New feature request             |
| enhancement | Improvement to existing         |
| docs        | Documentation                   |
| good first issue | Good for newcomers        |

### Priorities

| Priority | Description              | Response Time |
|----------|--------------------------|---------------|
| P0       | Critical/blocking        | Same day      |
| P1       | High priority            | Within week   |
| P2       | Medium priority          | Backlog       |
| P3       | Low priority/nice to have| As available  |

### Proposing Features
1. Check existing issues first
2. Create issue with template
3. Provide use case and requirements
4. Discuss in comments
5. Wait for approval before implementing

---

## Related Documentation

- [Architecture](architecture.md)
- [API Reference](api.md)
- [Getting Started](getting-started.md)