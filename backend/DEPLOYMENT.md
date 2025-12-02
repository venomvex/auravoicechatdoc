# Aura Voice Chat Backend - Deployment Guide

Developer: Hawkaye Visions LTD — Pakistan

## Overview

This document describes how to deploy and configure the Aura Voice Chat backend. The backend supports multiple deployment configurations:

1. **Docker Compose (Recommended)** - Best for EC2 instances and production
2. **Local Development** - For development without Docker
3. **AWS RDS** - For production with managed database

---

## Docker Compose Deployment (Recommended)

This is the recommended approach for EC2 instances and production deployments.

### Prerequisites

- Docker and Docker Compose installed
- EC2 instance with at least 2GB RAM
- Ports 80, 443, 3000, 5432, 6379 available

### Quick Start

```bash
# 1. Clone the repository
git clone https://github.com/venomvex/auravoicechatdoc.git
cd auravoicechatdoc/backend

# 2. Copy and configure environment
cp .env.example .env
# Edit .env with your production values (JWT secrets, API keys, etc.)

# 3. Start all services
docker-compose up -d

# 4. Verify services are running
docker-compose ps
```

### Docker Compose Services

| Service | Container Name | Port | Description |
|---------|---------------|------|-------------|
| `app` | aura-backend | 3000 | Node.js backend API |
| `db` | aura-postgres | 5432 | PostgreSQL database |
| `redis` | aura-redis | 6379 | Redis cache/sessions |
| `nginx` | aura-nginx | 80, 443 | Reverse proxy |

### Database Configuration for Docker

The default `.env.example` is configured for Docker Compose:

```env
# Database - uses Docker service name 'db'
DB_HOST=db
DB_PORT=5432
DB_NAME=auravoicechat
DB_USER=postgres
DB_PASSWORD=postgres
DATABASE_URL=postgresql://postgres:postgres@db:5432/auravoicechat?schema=public

# Redis - uses Docker service name 'redis'
REDIS_URL=redis://redis:6379
```

### Database Initialization

The `schema.sql` file is automatically loaded when the PostgreSQL container starts for the first time (via Docker volume mount to `/docker-entrypoint-initdb.d/`).

To verify the database:

```bash
# Connect to the database container
docker-compose exec db psql -U postgres -d auravoicechat

# List tables
\dt

# Expected tables: users, rooms, messages, families, earnings, etc.
```

### Managing the Stack

```bash
# View logs
docker-compose logs -f app
docker-compose logs -f db

# Restart services
docker-compose restart app

# Stop all services
docker-compose down

# Stop and remove data volumes (CAUTION: deletes all data!)
docker-compose down -v
```

---

## Local Development (Without Docker)

For development without Docker, you need PostgreSQL and Redis running locally.

### Prerequisites

- Node.js 18+ and npm
- PostgreSQL 15+
- Redis 7+

### Setup

```bash
# 1. Install dependencies
cd backend
npm install

# 2. Create and configure .env
cp .env.example .env
```

### Configure `.env` for Local Development

Update these values for local development:

```env
# Database - local PostgreSQL
DB_HOST=localhost
DB_PORT=5432
DB_NAME=auravoicechat
DB_USER=postgres
DB_PASSWORD=your_local_postgres_password
DATABASE_URL=postgresql://postgres:your_local_postgres_password@localhost:5432/auravoicechat?schema=public

# Redis - local Redis
REDIS_URL=redis://localhost:6379
```

### Initialize Local Database

```bash
# Create database
createdb -U postgres auravoicechat

# Run schema
psql -U postgres -d auravoicechat -f src/database/schema.sql
```

### Run Development Server

```bash
# Development mode with hot reload
npm run dev

# Or build and run
npm run build
npm start
```

---

## AWS RDS Configuration (Production Alternative)

For production with managed database services.

### RDS PostgreSQL Setup

1. Create an RDS PostgreSQL 15 instance
2. Configure security groups to allow access from EC2
3. Note the endpoint URL

### Configure `.env` for RDS

```env
# Database - AWS RDS
DB_HOST=your-rds-instance.region.rds.amazonaws.com
DB_PORT=5432
DB_NAME=auravoicechat
DB_USER=postgres
DB_PASSWORD=your_rds_password
DATABASE_URL=postgresql://postgres:your_rds_password@your-rds-instance.region.rds.amazonaws.com:5432/auravoicechat?schema=public

# Redis - AWS ElastiCache (optional)
REDIS_URL=redis://your-elasticache-cluster.region.cache.amazonaws.com:6379
```

### Initialize RDS Database

```bash
# Connect to RDS and run schema
psql -h your-rds-instance.region.rds.amazonaws.com -U postgres -d auravoicechat -f src/database/schema.sql
```

---

## Prisma vs. Schema.sql

This project uses **dual schema management**:

### `src/database/schema.sql`
- **Primary schema source** with full PostgreSQL features
- Includes triggers, views, indexes, and initial data
- Loaded automatically by Docker Compose
- Use for raw SQL operations and advanced PostgreSQL features

### `prisma/schema.prisma`
- Used for Prisma ORM operations
- Provides type-safe database access in TypeScript
- May not include all PostgreSQL-specific features

### Sync Strategy

1. **schema.sql is authoritative** - Contains all tables, triggers, and views
2. **Prisma schema reflects schema.sql** - Update Prisma after schema.sql changes
3. **Don't use Prisma migrations** - Apply schema.sql directly to database

### After Schema Changes

```bash
# 1. Update schema.sql with new tables/columns

# 2. Apply to database
docker-compose exec db psql -U postgres -d auravoicechat -f /docker-entrypoint-initdb.d/schema.sql

# 3. Update Prisma schema (prisma/schema.prisma) to match

# 4. Regenerate Prisma client
npx prisma generate
```

---

## Health Checks

### Backend Health

```bash
curl http://localhost:3000/health
# Expected: {"status":"healthy","timestamp":"...","version":"1.0.0"}
```

### Database Health

```bash
docker-compose exec db pg_isready -U postgres
# Expected: /var/run/postgresql:5432 - accepting connections
```

### Redis Health

```bash
docker-compose exec redis redis-cli ping
# Expected: PONG
```

---

## Security Notes

### Production Checklist

- [ ] Change default PostgreSQL password (`POSTGRES_PASSWORD`)
- [ ] Set strong `JWT_SECRET` and `JWT_REFRESH_SECRET`
- [ ] Configure proper `CORS_ORIGIN` (not `*`)
- [ ] Enable SSL for PostgreSQL connections
- [ ] Use HTTPS with valid SSL certificates
- [ ] Set `NODE_ENV=production`
- [ ] Remove debug logging (`LOG_LEVEL=info` or `warn`)

### Environment Variables to Never Commit

- `JWT_SECRET`
- `JWT_REFRESH_SECRET`
- `DB_PASSWORD`
- `DATABASE_URL` (with password)
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `TWILIO_AUTH_TOKEN`

---

## Troubleshooting

### Common Issues

#### 1. Database connection refused
```bash
# Check if PostgreSQL is running
docker-compose ps db

# Check logs
docker-compose logs db
```

#### 2. "relation does not exist" errors
```bash
# Schema may not be loaded. Re-apply:
docker-compose exec db psql -U postgres -d auravoicechat -f /docker-entrypoint-initdb.d/schema.sql
```

#### 3. Redis connection errors
```bash
# Verify Redis is running
docker-compose exec redis redis-cli ping
```

#### 4. Backend can't reach database
- Ensure `DATABASE_URL` uses Docker service name `db` not `localhost`
- Verify containers are on the same Docker network

---

## API Documentation

The backend exposes APIs at `/api/v1/`:

| Route | Description |
|-------|-------------|
| `/api/v1/auth/*` | Authentication (OTP, Google, Facebook) |
| `/api/v1/users/*` | User profiles |
| `/api/v1/rooms/*` | Voice chat rooms |
| `/api/v1/messages/*` | Direct messages |
| `/api/v1/wallet/*` | Wallet/balances |
| `/api/v1/games/*` | Games (Lucky 777, Greedy Baby, etc.) |
| `/api/v1/gifts/*` | Gift catalog and sending |
| `/api/v1/moderation/*` | Content moderation |
| `/api/v1/guide/*` | Guide system |
| `/api/v1/earnings/*` | Earnings and targets |
| `/api/v1/family/*` | Family/clan system |

Full API documentation: See `../api.md`

---

## Contact

For deployment support:
- Developer: Hawkaye Visions LTD — Pakistan
- Repository: https://github.com/venomvex/auravoicechatdoc
