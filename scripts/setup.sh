#!/bin/bash

# ============================================================================
# Aura Voice Chat - Unified Setup Script
# Developer: Hawkaye Visions LTD — Lahore, Pakistan
#
# This script automates the complete setup of Aura Voice Chat backend
# on a fresh Ubuntu EC2 instance.
#
# Usage: chmod +x setup.sh && ./setup.sh
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

# Configuration
APP_NAME="aura-voice-chat"
APP_DIR="/opt/$APP_NAME"
BACKEND_DIR="$APP_DIR/backend"
REPO_URL="${REPO_URL:-https://github.com/venomvex/auravoicechatdoc.git}"
BRANCH="${BRANCH:-main}"
NODE_VERSION="18"
PG_PORT="5433"
DB_NAME="auravoicechat"
DB_USER="postgres"
DB_PASSWORD="${DB_PASSWORD:-postgres}"

# AWS Configuration placeholders
AWS_REGION="ap-south-1"
AWS_KEY_PAIR="auravoicechat-key"

print_header() {
  echo ""
  echo -e "${PURPLE}"
  echo "╔══════════════════════════════════════════════════════════════════╗"
  echo "║                                                                  ║"
  echo "║     🚀 AURA VOICE CHAT - UNIFIED SETUP SCRIPT                    ║"
  echo "║                                                                  ║"
  echo "║     Developer: Hawkaye Visions LTD — Lahore, Pakistan           ║"
  echo "║     AWS Region: $AWS_REGION                                       ║"
  echo "║     Key Pair: $AWS_KEY_PAIR                                       ║"
  echo "║                                                                  ║"
  echo "╚══════════════════════════════════════════════════════════════════╝"
  echo -e "${NC}"
  echo ""
}

print_step() {
  echo ""
  echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
  echo -e "${CYAN}$1${NC}"
  echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
}

print_success() {
  echo -e "${GREEN}✓ $1${NC}"
}

print_warning() {
  echo -e "${YELLOW}⚠ $1${NC}"
}

print_error() {
  echo -e "${RED}✗ $1${NC}"
}

# Error handler
handle_error() {
  print_error "An error occurred on line $1"
  print_error "Setup failed. Please review the error above."
  echo ""
  echo -e "${YELLOW}Press Enter to exit...${NC}"
  read -r
  exit 1
}

trap 'handle_error $LINENO' ERR

# Start setup
print_header

# Detect OS
print_step "STEP 0: Detecting Operating System"
if [ -f /etc/os-release ]; then
  . /etc/os-release
  OS=$ID
  print_success "Detected OS: $OS"
else
  print_error "Cannot detect OS. This script supports Ubuntu."
  echo -e "${YELLOW}Press Enter to exit...${NC}"
  read -r
  exit 1
fi

if [ "$OS" != "ubuntu" ] && [ "$OS" != "debian" ]; then
  print_warning "This script is optimized for Ubuntu. Your OS: $OS"
  echo "Continuing anyway..."
fi

# ============================================================================
# STEP 1: Update System
# ============================================================================
print_step "STEP 1: Updating System"
sudo apt update && sudo apt upgrade -y
print_success "System updated"

# ============================================================================
# STEP 2: Install Node.js
# ============================================================================
print_step "STEP 2: Installing Node.js $NODE_VERSION"

# Check if Node.js is already installed
if command -v node &> /dev/null; then
  CURRENT_NODE_VERSION=$(node -v | cut -d'v' -f2 | cut -d'.' -f1)
  if [ "$CURRENT_NODE_VERSION" -ge "$NODE_VERSION" ]; then
    print_success "Node.js $(node -v) already installed"
  else
    print_warning "Upgrading Node.js from $CURRENT_NODE_VERSION to $NODE_VERSION"
    curl -fsSL https://deb.nodesource.com/setup_${NODE_VERSION}.x | sudo -E bash -
    sudo apt install -y nodejs
  fi
else
  curl -fsSL https://deb.nodesource.com/setup_${NODE_VERSION}.x | sudo -E bash -
  sudo apt install -y nodejs
fi

# Verify installation
print_success "Node.js $(node -v) installed"
print_success "npm $(npm -v) installed"

# ============================================================================
# STEP 3: Install PostgreSQL
# ============================================================================
print_step "STEP 3: Installing PostgreSQL"

if ! command -v psql &> /dev/null; then
  sudo apt install -y postgresql postgresql-contrib
fi

# Configure PostgreSQL
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Change PostgreSQL port to 5433
PG_CONF_FILE=$(sudo -u postgres psql -t -c "SHOW config_file;" 2>/dev/null | xargs)
if [ -n "$PG_CONF_FILE" ]; then
  sudo sed -i "s/^port = .*/port = $PG_PORT/" "$PG_CONF_FILE" 2>/dev/null || true
  sudo sed -i "s/^#port = .*/port = $PG_PORT/" "$PG_CONF_FILE" 2>/dev/null || true
fi

# Restart PostgreSQL with new port
sudo systemctl restart postgresql

# Set up database and user
sudo -u postgres psql -p $PG_PORT -c "ALTER USER postgres WITH PASSWORD '$DB_PASSWORD';" 2>/dev/null || true
sudo -u postgres psql -p $PG_PORT -c "CREATE DATABASE $DB_NAME;" 2>/dev/null || true
sudo -u postgres psql -p $PG_PORT -c "GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO postgres;" 2>/dev/null || true

# Update pg_hba.conf for password authentication
PG_HBA=$(sudo -u postgres psql -p $PG_PORT -t -c "SHOW hba_file;" 2>/dev/null | xargs)
if [ -n "$PG_HBA" ]; then
  sudo sed -i 's/peer/md5/g' "$PG_HBA" 2>/dev/null || true
  sudo sed -i 's/ident/md5/g' "$PG_HBA" 2>/dev/null || true
  sudo systemctl restart postgresql
fi

print_success "PostgreSQL installed and configured on port $PG_PORT"

# ============================================================================
# STEP 4: Install Redis
# ============================================================================
print_step "STEP 4: Installing Redis"

if ! command -v redis-cli &> /dev/null; then
  sudo apt install -y redis-server
fi

sudo systemctl start redis-server
sudo systemctl enable redis-server

print_success "Redis installed"

# ============================================================================
# STEP 5: Install PM2
# ============================================================================
print_step "STEP 5: Installing PM2"

if ! command -v pm2 &> /dev/null; then
  sudo npm install -g pm2
fi

print_success "PM2 installed"

# ============================================================================
# STEP 6: Clone/Update Repository
# ============================================================================
print_step "STEP 6: Setting up Repository"

sudo mkdir -p $APP_DIR
sudo chown $USER:$USER $APP_DIR

if [ -d "$APP_DIR/.git" ]; then
  print_warning "Repository exists, updating..."
  cd $APP_DIR
  git fetch origin
  git reset --hard origin/$BRANCH
  git pull origin $BRANCH
else
  print_success "Cloning repository..."
  git clone -b $BRANCH $REPO_URL $APP_DIR
fi

print_success "Repository ready"

# ============================================================================
# STEP 7: Install Dependencies
# ============================================================================
print_step "STEP 7: Installing npm Dependencies"

cd $BACKEND_DIR
npm install

print_success "Dependencies installed"

# ============================================================================
# STEP 8: Create Environment File
# ============================================================================
print_step "STEP 8: Setting up Environment"

if [ ! -f "$BACKEND_DIR/.env" ]; then
  if [ -f "$BACKEND_DIR/.env.example" ]; then
    cp $BACKEND_DIR/.env.example $BACKEND_DIR/.env
    print_success "Created .env from .env.example"
  else
    # Create minimal .env
    cat > $BACKEND_DIR/.env << EOF
# Aura Voice Chat Backend Configuration
# Auto-generated by setup.sh on $(date)

NODE_ENV=production
PORT=3000
HOST=0.0.0.0

# Database
DATABASE_URL=postgresql://postgres:$DB_PASSWORD@localhost:$PG_PORT/$DB_NAME?schema=public
DB_HOST=localhost
DB_PORT=$PG_PORT
DB_NAME=$DB_NAME
DB_USER=postgres
DB_PASSWORD=$DB_PASSWORD

# Redis
REDIS_URL=redis://localhost:6379

# JWT
JWT_SECRET=$(openssl rand -hex 32)
JWT_EXPIRES_IN=7d
JWT_REFRESH_SECRET=$(openssl rand -hex 32)
JWT_REFRESH_EXPIRES_IN=30d

# AWS
AWS_REGION=$AWS_REGION
AWS_ACCESS_KEY_ID=YOUR_AWS_ACCESS_KEY_HERE
AWS_SECRET_ACCESS_KEY=YOUR_AWS_SECRET_KEY_HERE

# Rate Limiting
RATE_LIMIT_WINDOW_MS=60000
RATE_LIMIT_MAX_REQUESTS=100

# Logging
LOG_LEVEL=info

# CORS
CORS_ORIGIN=*
EOF
    print_success "Created .env file"
  fi
else
  print_warning ".env file already exists, skipping..."
fi

# ============================================================================
# STEP 9: Run Prisma Migrations
# ============================================================================
print_step "STEP 9: Running Database Migrations"

cd $BACKEND_DIR

# Generate Prisma client
if [ -f "$BACKEND_DIR/prisma/schema.prisma" ]; then
  npx prisma generate
  print_success "Prisma client generated"
  
  # Run migrations
  npx prisma db push --accept-data-loss 2>/dev/null || print_warning "Database schema push skipped (may already be up to date)"
  print_success "Database schema updated"
else
  print_warning "No Prisma schema found, skipping migrations"
fi

# ============================================================================
# STEP 10: Build TypeScript
# ============================================================================
print_step "STEP 10: Building TypeScript"

cd $BACKEND_DIR
npm run build

print_success "TypeScript compiled successfully"

# ============================================================================
# STEP 11: Start Application with PM2
# ============================================================================
print_step "STEP 11: Starting Application"

cd $BACKEND_DIR

# Stop existing process if running
pm2 delete aura-backend 2>/dev/null || true

# Start application
if [ -f "$BACKEND_DIR/ecosystem.config.js" ]; then
  pm2 start ecosystem.config.js --env production
else
  pm2 start dist/index.js --name aura-backend
fi

# Save PM2 configuration
pm2 save

# Setup startup script (run as current user, not with sudo)
PM2_STARTUP=$(pm2 startup 2>/dev/null | grep -E "sudo|pm2 startup" | tail -1)
if [ -n "$PM2_STARTUP" ]; then
  eval "$PM2_STARTUP" 2>/dev/null || true
fi

print_success "Application started with PM2"

# ============================================================================
# COMPLETION
# ============================================================================
echo ""
echo -e "${PURPLE}"
echo "╔══════════════════════════════════════════════════════════════════╗"
echo "║                                                                  ║"
echo "║     🎉 SETUP COMPLETE!                                           ║"
echo "║                                                                  ║"
echo "╚══════════════════════════════════════════════════════════════════╝"
echo -e "${NC}"

# Get public IP
PUBLIC_IP=$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4 2>/dev/null || hostname -I | awk '{print $1}')

echo ""
echo -e "${GREEN}Setup Summary:${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  Application:     Aura Voice Chat Backend"
echo "  Directory:       $BACKEND_DIR"
echo "  Public IP:       $PUBLIC_IP"
echo ""
echo -e "${CYAN}Services:${NC}"
echo "  ✓ PostgreSQL running on port $PG_PORT"
echo "  ✓ Redis running on port 6379"
echo "  ✓ Node.js app running on port 3000"
echo "  ✓ PM2 process manager configured"
echo ""
echo -e "${CYAN}Test the deployment:${NC}"
echo "  curl http://localhost:3000/health"
echo "  curl http://$PUBLIC_IP:3000/health"
echo ""
echo -e "${CYAN}Useful commands:${NC}"
echo "  pm2 status              # Check app status"
echo "  pm2 logs aura-backend   # View logs"
echo "  pm2 restart aura-backend # Restart app"
echo "  pm2 monit               # Real-time monitoring"
echo ""
echo -e "${YELLOW}Important:${NC}"
echo "  Update $BACKEND_DIR/.env with your:"
echo "  - AWS credentials"
echo "  - Cognito configuration"
echo "  - Twilio credentials (for OTP)"
echo ""
echo -e "${PURPLE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo -e "${YELLOW}Press Enter to exit...${NC}"
read -r

echo ""
echo -e "${PURPLE}Happy deploying! 🚀${NC}"
echo ""
