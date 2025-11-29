#!/bin/bash

# Aura Voice Chat - Backend Deployment Script
# Developer: Hawkaye Visions LTD — Pakistan
#
# This script automates deploying the Node.js backend to a server

set -e

echo "======================================"
echo "Aura Voice Chat - Backend Deployment"
echo "Developer: Hawkaye Visions LTD"
echo "======================================"
echo ""

# Configuration
DEPLOY_ENV=${1:-production}
BACKEND_DIR="$(dirname "$0")/../backend"
PM2_APP_NAME="aura-backend"

# Navigate to backend directory
cd "$BACKEND_DIR"

# Check for required files
if [ ! -f ".env" ] && [ ! -f ".env.${DEPLOY_ENV}" ]; then
    echo "❌ Error: Environment file not found"
    echo "   Expected: .env or .env.${DEPLOY_ENV}"
    echo ""
    echo "   Create .env file with required variables:"
    echo "   - NODE_ENV"
    echo "   - PORT"
    echo "   - DATABASE_URL"
    echo "   - REDIS_URL"
    echo "   - JWT_SECRET"
    echo "   - AWS_REGION"
    exit 1
fi

# Load environment-specific config
if [ -f ".env.${DEPLOY_ENV}" ]; then
    echo "📝 Using .env.${DEPLOY_ENV}"
    cp ".env.${DEPLOY_ENV}" .env
fi

# Check Node.js version
NODE_VERSION=$(node -v | cut -d'v' -f2 | cut -d'.' -f1)
if [ "$NODE_VERSION" -lt 18 ]; then
    echo "❌ Node.js 18+ is required. Current version: $(node -v)"
    exit 1
fi
echo "✅ Node.js $(node -v) detected"

# Install dependencies
echo ""
echo "📦 Installing dependencies..."
npm ci --production=false

# Run database migrations
echo ""
echo "🗃️  Running database migrations..."
npm run migrate:prod || {
    echo "⚠️  Migration failed or no migrations to run"
}

# Build TypeScript
echo ""
echo "🔨 Building TypeScript..."
npm run build

# Check if PM2 is installed
if ! command -v pm2 &> /dev/null; then
    echo ""
    echo "📦 Installing PM2..."
    npm install -g pm2
fi

# Stop existing instance if running
echo ""
echo "🔄 Restarting application..."
pm2 stop $PM2_APP_NAME 2>/dev/null || true
pm2 delete $PM2_APP_NAME 2>/dev/null || true

# Start application with PM2
pm2 start dist/index.js \
    --name $PM2_APP_NAME \
    --time \
    --log-date-format 'YYYY-MM-DD HH:mm:ss Z' \
    --max-memory-restart 1G \
    --exp-backoff-restart-delay 100

# Save PM2 configuration
pm2 save

# Health check
echo ""
echo "🏥 Performing health check..."
sleep 3

HEALTH_URL="http://localhost:${PORT:-3000}/health"
HEALTH_RESPONSE=$(curl -s "$HEALTH_URL" || echo "failed")

if echo "$HEALTH_RESPONSE" | grep -q "healthy"; then
    echo "✅ Health check passed!"
else
    echo "❌ Health check failed"
    echo "   Response: $HEALTH_RESPONSE"
    echo ""
    echo "📋 Checking logs..."
    pm2 logs $PM2_APP_NAME --lines 20
    exit 1
fi

# Display status
echo ""
echo "======================================"
echo "✅ Deployment Complete!"
echo "======================================"
echo ""
echo "Environment: $DEPLOY_ENV"
echo "Application: $PM2_APP_NAME"
echo "Port: ${PORT:-3000}"
echo ""
echo "PM2 Status:"
pm2 status $PM2_APP_NAME

echo ""
echo "Useful commands:"
echo "  pm2 logs $PM2_APP_NAME     # View logs"
echo "  pm2 status                 # View status"
echo "  pm2 restart $PM2_APP_NAME  # Restart app"
echo "  pm2 stop $PM2_APP_NAME     # Stop app"
echo ""
echo "API Endpoints:"
echo "  Health: http://localhost:${PORT:-3000}/health"
echo "  Auth:   http://localhost:${PORT:-3000}/api/v1/auth"
echo "  Users:  http://localhost:${PORT:-3000}/api/v1/users"
echo ""
