# AWS EC2 Deployment Guide

## Aura Voice Chat Backend Deployment

**Developer: Hawkaye Visions LTD — Pakistan**

This guide covers deploying the Aura Voice Chat backend to AWS EC2.

---

## Prerequisites

### 1. AWS Account Setup

1. Create an AWS account at [aws.amazon.com](https://aws.amazon.com)
2. Enable billing alerts to monitor costs
3. Create an IAM user with programmatic access:
   - Go to IAM → Users → Add User
   - Enable "Programmatic access"
   - Attach policies: `AmazonEC2FullAccess`, `AmazonRDSFullAccess`, `ElasticCacheFullAccess`

### 2. Required Permissions

- EC2: Launch, terminate, and manage instances
- RDS: Create and manage PostgreSQL databases
- ElastiCache: Create Redis clusters
- VPC: Manage networking
- Route 53: DNS management (optional)
- ACM: SSL certificate management

---

## Step 1: Launch EC2 Instance

### 1.1 Choose AMI

1. Go to EC2 Dashboard → Launch Instance
2. Select **Amazon Linux 2023** or **Ubuntu 22.04 LTS**
3. Recommended: **t3.medium** or larger for production

### 1.2 Instance Configuration

```
Instance Type: t3.medium (2 vCPU, 4GB RAM)
Network: Default VPC or custom VPC
Subnet: Public subnet
Auto-assign Public IP: Enable
```

### 1.3 Storage

- Root volume: 30GB gp3
- Additional EBS: 50GB for logs and data (optional)

### 1.4 Security Group

Create a new security group with these rules:

| Type | Port | Source | Description |
|------|------|--------|-------------|
| SSH | 22 | Your IP | SSH access |
| HTTP | 80 | 0.0.0.0/0 | Web traffic |
| HTTPS | 443 | 0.0.0.0/0 | Secure web traffic |
| Custom TCP | 3000 | 0.0.0.0/0 | Node.js app |
| Custom TCP | 5432 | Security Group ID | PostgreSQL |
| Custom TCP | 6379 | Security Group ID | Redis |

### 1.5 Key Pair

1. Create a new key pair or use existing
2. Download the `.pem` file
3. Set permissions: `chmod 400 your-key.pem`

---

## Step 2: Connect to Instance

```bash
# Connect via SSH
ssh -i "your-key.pem" ec2-user@your-ec2-public-ip

# Or for Ubuntu
ssh -i "your-key.pem" ubuntu@your-ec2-public-ip
```

---

## Step 3: Install Dependencies

### 3.1 Update System

```bash
# Amazon Linux 2023
sudo dnf update -y

# Ubuntu
sudo apt update && sudo apt upgrade -y
```

### 3.2 Install Node.js 18+

```bash
# Install Node.js using nvm
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
source ~/.bashrc
nvm install 18
nvm use 18
nvm alias default 18

# Verify installation
node --version  # Should be v18.x.x
npm --version
```

### 3.3 Install PostgreSQL

```bash
# Amazon Linux 2023
sudo dnf install postgresql15-server postgresql15 -y
sudo postgresql-setup --initdb
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Ubuntu
sudo apt install postgresql postgresql-contrib -y
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

### 3.4 Configure PostgreSQL

```bash
# Switch to postgres user
sudo -u postgres psql

# Create database and user
CREATE DATABASE aura_voice_chat;
CREATE USER aura_user WITH ENCRYPTED PASSWORD 'your-secure-password';
GRANT ALL PRIVILEGES ON DATABASE aura_voice_chat TO aura_user;
\q
```

Update PostgreSQL authentication:

```bash
# Edit pg_hba.conf
sudo nano /var/lib/pgsql/data/pg_hba.conf

# Change "ident" to "md5" for local connections
# local   all   all   md5

sudo systemctl restart postgresql
```

### 3.5 Install Redis

```bash
# Amazon Linux 2023
sudo dnf install redis6 -y
sudo systemctl start redis6
sudo systemctl enable redis6

# Ubuntu
sudo apt install redis-server -y
sudo systemctl start redis
sudo systemctl enable redis

# Verify
redis-cli ping  # Should return PONG
```

### 3.6 Install Nginx

```bash
# Amazon Linux 2023
sudo dnf install nginx -y

# Ubuntu
sudo apt install nginx -y

sudo systemctl start nginx
sudo systemctl enable nginx
```

### 3.7 Install PM2

```bash
npm install -g pm2
```

---

## Step 4: Deploy Application

### 4.1 Clone Repository

```bash
cd /home/ec2-user  # or /home/ubuntu
git clone https://github.com/your-repo/aura-voice-chat.git
cd aura-voice-chat/backend
```

### 4.2 Install Dependencies

```bash
npm install
```

### 4.3 Configure Environment

```bash
cp .env.example .env
nano .env
```

Update the following:

```bash
NODE_ENV=production
PORT=3000
HOST=0.0.0.0

# Database
DATABASE_URL=postgresql://aura_user:your-secure-password@localhost:5432/aura_voice_chat

# Redis
REDIS_URL=redis://localhost:6379

# JWT - Generate secure secrets
JWT_SECRET=your-256-bit-secret
JWT_REFRESH_SECRET=your-256-bit-refresh-secret

# AWS Cognito (configure in AWS Console)
AWS_COGNITO_USER_POOL_ID=your-user-pool-id
AWS_COGNITO_CLIENT_ID=your-client-id
AWS_COGNITO_REGION=ap-south-1

# Twilio (OTP fallback)
TWILIO_ACCOUNT_SID=your-account-sid
TWILIO_AUTH_TOKEN=your-auth-token
TWILIO_PHONE_NUMBER=+1234567890
```

### 4.4 Run Database Migrations

```bash
npm run migrate:prod
```

### 4.5 Build Application

```bash
npm run build
```

### 4.6 Start with PM2

```bash
# Start application
pm2 start dist/index.js --name aura-backend

# Save PM2 configuration
pm2 save

# Setup PM2 startup script
pm2 startup
# Run the command it outputs

# Monitor
pm2 status
pm2 logs aura-backend
```

---

## Step 5: Configure Nginx

### 5.1 Create Nginx Configuration

```bash
sudo nano /etc/nginx/conf.d/aura-voice-chat.conf
```

```nginx
upstream aura_backend {
    server 127.0.0.1:3000;
    keepalive 64;
}

server {
    listen 80;
    server_name api.auravoice.chat;

    location / {
        proxy_pass http://aura_backend;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
        proxy_read_timeout 86400;
    }
}
```

### 5.2 Test and Reload Nginx

```bash
sudo nginx -t
sudo systemctl reload nginx
```

---

## Step 6: SSL/TLS with Let's Encrypt

### 6.1 Install Certbot

```bash
# Amazon Linux 2023
sudo dnf install certbot python3-certbot-nginx -y

# Ubuntu
sudo apt install certbot python3-certbot-nginx -y
```

### 6.2 Obtain Certificate

```bash
sudo certbot --nginx -d api.auravoice.chat
```

### 6.3 Auto-Renewal

```bash
# Test renewal
sudo certbot renew --dry-run

# Cron job for auto-renewal (usually added automatically)
echo "0 0,12 * * * root certbot renew --quiet" | sudo tee /etc/cron.d/certbot
```

---

## Step 7: Domain Setup

### 7.1 Route 53 Configuration

1. Go to Route 53 → Hosted Zones
2. Create or select your domain
3. Add A Record:
   - Name: `api`
   - Type: A
   - Value: Your EC2 Elastic IP
   - TTL: 300

### 7.2 Elastic IP (Recommended)

```bash
# Allocate Elastic IP in EC2 Console
# Associate with your instance
```

---

## Monitoring & Maintenance

### CloudWatch Setup

1. Go to CloudWatch → Alarms
2. Create alarms for:
   - CPU > 80%
   - Memory > 80%
   - Disk > 85%
   - Network errors

### Log Management

```bash
# PM2 logs
pm2 logs

# Rotate logs
pm2 install pm2-logrotate

# View application logs
tail -f /home/ec2-user/aura-voice-chat/backend/logs/combined.log
```

### Backup Strategy

```bash
# PostgreSQL backup
pg_dump -U aura_user aura_voice_chat > backup_$(date +%Y%m%d).sql

# Automated backups with cron
echo "0 3 * * * pg_dump -U aura_user aura_voice_chat > /backups/backup_\$(date +\%Y\%m\%d).sql" | sudo tee /etc/cron.d/db-backup
```

### Auto-Scaling (Optional)

1. Create AMI from configured instance
2. Create Launch Template
3. Create Auto Scaling Group
4. Configure scaling policies

---

## Troubleshooting

### Common Issues

**Application won't start:**
```bash
pm2 logs aura-backend --lines 100
```

**Database connection failed:**
```bash
sudo systemctl status postgresql
pg_isready
```

**Redis connection failed:**
```bash
redis-cli ping
sudo systemctl status redis6
```

**Nginx 502 Bad Gateway:**
```bash
curl http://localhost:3000/health
pm2 status
```

---

## Security Checklist

- [ ] SSH key authentication only (disable password auth)
- [ ] Security groups configured properly
- [ ] SSL/TLS enabled
- [ ] Database passwords secured
- [ ] JWT secrets rotated
- [ ] Firewall configured (iptables/ufw)
- [ ] Regular security updates
- [ ] Log monitoring enabled
- [ ] Backup strategy implemented

---

## Estimated Costs (us-east-1)

| Resource | Size | Monthly Cost |
|----------|------|--------------|
| EC2 t3.medium | On-demand | ~$30 |
| EBS 80GB | gp3 | ~$7 |
| RDS PostgreSQL (optional) | db.t3.micro | ~$15 |
| ElastiCache Redis (optional) | cache.t3.micro | ~$12 |
| Data Transfer | 100GB | ~$9 |
| **Total** | | **~$75/month** |

---

*For production deployments, consider using RDS for PostgreSQL and ElastiCache for Redis for better reliability and management.*
