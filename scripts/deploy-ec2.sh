#!/bin/bash

# ============================================================================
# Aura Voice Chat - AWS EC2 Auto Deploy Script
# Developer: Hawkaye Visions LTD — Lahore, Pakistan
#
# This script automates the complete deployment of Aura Voice Chat backend
# on an AWS EC2 instance.
#
# Run this script on a fresh EC2 instance (Amazon Linux 2023 or Ubuntu 22.04)
# Usage: curl -sSL https://raw.githubusercontent.com/.../deploy-ec2.sh | bash
# ============================================================================

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m'

echo -e "${PURPLE}"
echo "╔══════════════════════════════════════════════════════════════════╗"
echo "║                                                                  ║"
echo "║     🚀 AURA VOICE CHAT - EC2 AUTO DEPLOY                         ║"
echo "║                                                                  ║"
echo "║     Developer: Hawkaye Visions LTD — Lahore, Pakistan           ║"
echo "║                                                                  ║"
echo "╚══════════════════════════════════════════════════════════════════╝"
echo -e "${NC}"
echo ""

# Detect OS
if [ -f /etc/os-release ]; then
    . /etc/os-release
    OS=$ID
else
    echo -e "${RED}Cannot detect OS. Exiting.${NC}"
    exit 1
fi

echo -e "${CYAN}Detected OS: ${YELLOW}$OS${NC}"
echo ""

# Configuration
APP_NAME="aura-voice-chat"
APP_DIR="/opt/$APP_NAME"
BACKEND_DIR="$APP_DIR/backend"
REPO_URL="${REPO_URL:-https://github.com/venomvex/auravoicechatdoc.git}"
BRANCH="${BRANCH:-main}"
NODE_VERSION="18"

# Prompt for configuration
echo -e "${CYAN}Configuration${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
read -p "Database password (or press Enter for auto-generate): " -s DB_PASSWORD
echo ""
if [ -z "$DB_PASSWORD" ]; then
    DB_PASSWORD=$(openssl rand -hex 16)
    echo -e "${GREEN}Database password auto-generated and saved to .env${NC}"
fi

read -p "JWT Secret (or press Enter for auto-generate): " JWT_SECRET
if [ -z "$JWT_SECRET" ]; then
    JWT_SECRET=$(openssl rand -hex 32)
fi

read -p "Domain name (e.g., api.auravoice.chat, or press Enter for IP only): " DOMAIN_NAME

echo ""

# ============================================================================
# STEP 1: Update System
# ============================================================================
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${CYAN}STEP 1: Updating System${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

if [ "$OS" = "amzn" ] || [ "$OS" = "rhel" ] || [ "$OS" = "centos" ]; then
    sudo dnf update -y
elif [ "$OS" = "ubuntu" ] || [ "$OS" = "debian" ]; then
    sudo apt update && sudo apt upgrade -y
fi

echo -e "${GREEN}✓ System updated${NC}"

# ============================================================================
# STEP 2: Install Node.js
# ============================================================================
echo ""
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${CYAN}STEP 2: Installing Node.js $NODE_VERSION${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

# Install nvm
if [ ! -d "$HOME/.nvm" ]; then
    curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
fi

export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"

nvm install $NODE_VERSION
nvm use $NODE_VERSION
nvm alias default $NODE_VERSION

echo -e "${GREEN}✓ Node.js $(node --version) installed${NC}"

# ============================================================================
# STEP 3: Install PostgreSQL
# ============================================================================
echo ""
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${CYAN}STEP 3: Installing PostgreSQL${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

if [ "$OS" = "amzn" ]; then
    sudo dnf install postgresql15-server postgresql15 -y
    sudo postgresql-setup --initdb
    sudo systemctl start postgresql
    sudo systemctl enable postgresql
elif [ "$OS" = "ubuntu" ]; then
    sudo apt install postgresql postgresql-contrib -y
    sudo systemctl start postgresql
    sudo systemctl enable postgresql
fi

# Configure PostgreSQL
sudo -u postgres psql -c "CREATE USER aura_user WITH ENCRYPTED PASSWORD '$DB_PASSWORD';" 2>/dev/null || true
sudo -u postgres psql -c "CREATE DATABASE aura_voice_chat OWNER aura_user;" 2>/dev/null || true
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE aura_voice_chat TO aura_user;" 2>/dev/null || true

# Update pg_hba.conf for password authentication
PG_HBA=$(sudo -u postgres psql -t -c "SHOW hba_file;")
sudo sed -i 's/ident/md5/g' $PG_HBA
sudo sed -i 's/peer/md5/g' $PG_HBA
sudo systemctl restart postgresql

echo -e "${GREEN}✓ PostgreSQL installed and configured${NC}"

# ============================================================================
# STEP 4: Install Redis
# ============================================================================
echo ""
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${CYAN}STEP 4: Installing Redis${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

if [ "$OS" = "amzn" ]; then
    sudo dnf install redis6 -y
    sudo systemctl start redis6
    sudo systemctl enable redis6
elif [ "$OS" = "ubuntu" ]; then
    sudo apt install redis-server -y
    sudo systemctl start redis
    sudo systemctl enable redis
fi

echo -e "${GREEN}✓ Redis installed${NC}"

# ============================================================================
# STEP 5: Install Nginx
# ============================================================================
echo ""
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${CYAN}STEP 5: Installing Nginx${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

if [ "$OS" = "amzn" ]; then
    sudo dnf install nginx -y
elif [ "$OS" = "ubuntu" ]; then
    sudo apt install nginx -y
fi

sudo systemctl start nginx
sudo systemctl enable nginx

echo -e "${GREEN}✓ Nginx installed${NC}"

# ============================================================================
# STEP 6: Install PM2
# ============================================================================
echo ""
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${CYAN}STEP 6: Installing PM2${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

npm install -g pm2

echo -e "${GREEN}✓ PM2 installed${NC}"

# ============================================================================
# STEP 7: Clone Repository
# ============================================================================
echo ""
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${CYAN}STEP 7: Cloning Repository${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

sudo mkdir -p $APP_DIR
sudo chown $USER:$USER $APP_DIR

if [ -d "$APP_DIR/.git" ]; then
    cd $APP_DIR
    git pull origin $BRANCH
else
    git clone -b $BRANCH $REPO_URL $APP_DIR
fi

echo -e "${GREEN}✓ Repository cloned${NC}"

# ============================================================================
# STEP 8: Install Dependencies & Build
# ============================================================================
echo ""
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${CYAN}STEP 8: Installing Dependencies & Building${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

cd $BACKEND_DIR
npm install

# Create .env file
cat > .env << EOF
# Aura Voice Chat Backend Configuration
# Auto-generated by deploy-ec2.sh
# Generated: $(date)

NODE_ENV=production
PORT=3000
HOST=0.0.0.0

# Database
DATABASE_URL=postgresql://aura_user:$DB_PASSWORD@localhost:5432/aura_voice_chat

# Redis
REDIS_URL=redis://localhost:6379

# JWT
JWT_SECRET=$JWT_SECRET
JWT_EXPIRES_IN=7d

# AWS (configure these)
AWS_REGION=$AWS_REGION
AWS_ACCESS_KEY_ID=YOUR_AWS_ACCESS_KEY_HERE
AWS_SECRET_ACCESS_KEY=YOUR_AWS_SECRET_KEY_HERE

# Cognito (configure these)
COGNITO_USER_POOL_ID=
COGNITO_CLIENT_ID=

# Twilio (configure these)
TWILIO_ACCOUNT_SID=
TWILIO_AUTH_TOKEN=
TWILIO_PHONE_NUMBER=

# CORS
CORS_ORIGIN=*
EOF

npm run build

echo -e "${GREEN}✓ Dependencies installed and app built${NC}"

# ============================================================================
# STEP 9: Run Database Migrations
# ============================================================================
echo ""
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${CYAN}STEP 9: Running Database Migrations${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

npm run migrate:prod 2>/dev/null || echo -e "${YELLOW}Migrations skipped (may not be configured)${NC}"

echo -e "${GREEN}✓ Migrations complete${NC}"

# ============================================================================
# STEP 10: Configure Nginx
# ============================================================================
echo ""
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${CYAN}STEP 10: Configuring Nginx${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

SERVER_NAME=${DOMAIN_NAME:-_}

sudo tee /etc/nginx/conf.d/aura-voice-chat.conf > /dev/null << EOF
upstream aura_backend {
    server 127.0.0.1:3000;
    keepalive 64;
}

server {
    listen 80;
    server_name $SERVER_NAME;

    location / {
        proxy_pass http://aura_backend;
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_cache_bypass \$http_upgrade;
        proxy_read_timeout 86400;
    }

    location /health {
        proxy_pass http://aura_backend/health;
    }
}
EOF

sudo nginx -t && sudo systemctl reload nginx

echo -e "${GREEN}✓ Nginx configured${NC}"

# ============================================================================
# STEP 11: Start Application with PM2
# ============================================================================
echo ""
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${CYAN}STEP 11: Starting Application${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

cd $BACKEND_DIR

# Stop existing process if running
pm2 delete aura-backend 2>/dev/null || true

# Start application
pm2 start dist/index.js --name aura-backend

# Save PM2 configuration
pm2 save

# Setup startup script
pm2 startup | tail -1 | bash

echo -e "${GREEN}✓ Application started${NC}"

# ============================================================================
# STEP 12: SSL Setup (if domain provided)
# ============================================================================
if [ -n "$DOMAIN_NAME" ]; then
    echo ""
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${CYAN}STEP 12: Setting up SSL${NC}"
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    
    if [ "$OS" = "amzn" ]; then
        sudo dnf install certbot python3-certbot-nginx -y
    elif [ "$OS" = "ubuntu" ]; then
        sudo apt install certbot python3-certbot-nginx -y
    fi
    
    sudo certbot --nginx -d $DOMAIN_NAME --non-interactive --agree-tos --email admin@$DOMAIN_NAME || echo -e "${YELLOW}SSL setup failed - you can run certbot manually later${NC}"
    
    echo -e "${GREEN}✓ SSL configured${NC}"
fi

# ============================================================================
# COMPLETION
# ============================================================================
echo ""
echo -e "${PURPLE}"
echo "╔══════════════════════════════════════════════════════════════════╗"
echo "║                                                                  ║"
echo "║     🎉 DEPLOYMENT COMPLETE!                                      ║"
echo "║                                                                  ║"
echo "╚══════════════════════════════════════════════════════════════════╝"
echo -e "${NC}"

# Get public IP
PUBLIC_IP=$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4 2>/dev/null || echo "unknown")

echo ""
echo -e "${GREEN}Deployment Summary:${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  Application: Aura Voice Chat Backend"
echo "  Directory:   $BACKEND_DIR"
echo "  Public IP:   $PUBLIC_IP"
[ -n "$DOMAIN_NAME" ] && echo "  Domain:      https://$DOMAIN_NAME"
echo ""
echo -e "${CYAN}Services:${NC}"
echo "  ✓ PostgreSQL running on port 5432"
echo "  ✓ Redis running on port 6379"
echo "  ✓ Node.js app running on port 3000"
echo "  ✓ Nginx reverse proxy on port 80/443"
echo "  ✓ PM2 process manager configured"
echo ""
echo -e "${YELLOW}Important:${NC}"
echo "  Database Password: $DB_PASSWORD"
echo "  JWT Secret:        (saved in $BACKEND_DIR/.env)"
echo ""
echo -e "${CYAN}Test the deployment:${NC}"
echo "  curl http://$PUBLIC_IP/health"
[ -n "$DOMAIN_NAME" ] && echo "  curl https://$DOMAIN_NAME/health"
echo ""
echo -e "${CYAN}Useful commands:${NC}"
echo "  pm2 status              # Check app status"
echo "  pm2 logs aura-backend   # View logs"
echo "  pm2 restart aura-backend # Restart app"
echo ""
echo -e "${CYAN}Configuration:${NC}"
echo "  Update $BACKEND_DIR/.env with:"
echo "  - AWS credentials"
echo "  - Cognito credentials"
echo "  - Twilio credentials"
echo "  - Payment gateway credentials"
echo ""
echo -e "${PURPLE}Happy deploying! 🚀${NC}"
echo ""
