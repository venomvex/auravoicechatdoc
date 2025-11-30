#!/bin/bash

# ============================================================================
# Aura Voice Chat - Master Setup Script
# ============================================================================
#
# Developer: Hawkaye Visions LTD — Lahore, Pakistan
# Updated: November 2025
#
# This is the UNIFIED setup script that handles everything:
# - Prerequisites checking
# - Backend setup (Node.js, PostgreSQL, Redis)
# - AWS infrastructure setup
# - Android APK build
# - Database migrations
# - Server deployment
#
# Usage:
#   ./setup.sh                    # Interactive full setup
#   ./setup.sh --backend          # Backend setup only
#   ./setup.sh --android          # Android build only
#   ./setup.sh --aws              # AWS infrastructure only
#   ./setup.sh --deploy           # Deploy to EC2
#   ./setup.sh --help             # Show help
#
# ============================================================================

set -o pipefail

# =============================================================================
# CONFIGURATION
# =============================================================================

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Project paths
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$SCRIPT_DIR"
BACKEND_DIR="$ROOT_DIR/backend"
ANDROID_DIR="$ROOT_DIR/android"
AWS_DIR="$ROOT_DIR/aws"
DOCS_DIR="$ROOT_DIR/docs"
DATA_DIR="$ROOT_DIR/data"

# AWS Configuration (can be overridden by environment)
AWS_REGION="${AWS_REGION:-us-east-1}"
STACK_NAME="${STACK_NAME:-aura-voice-chat-production}"

# Backend Configuration
DB_NAME="${DB_NAME:-auravoicechat}"
DB_USER="${DB_USER:-aura_admin}"
DB_PASSWORD="${DB_PASSWORD:-}"
DB_PORT="${DB_PORT:-5432}"
NODE_VERSION="${NODE_VERSION:-18}"

# Log file
LOG_FILE="$ROOT_DIR/setup-$(date +%Y%m%d-%H%M%S).log"

# =============================================================================
# UTILITY FUNCTIONS
# =============================================================================

print_header() {
    echo ""
    echo -e "${PURPLE}"
    echo "╔══════════════════════════════════════════════════════════════════╗"
    echo "║                                                                  ║"
    echo "║     🎤 AURA VOICE CHAT - MASTER SETUP                            ║"
    echo "║                                                                  ║"
    echo "║     Developer: Hawkaye Visions LTD — Lahore, Pakistan           ║"
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

print_info() {
    echo -e "${BLUE}ℹ $1${NC}"
}

check_command() {
    if command -v "$1" &> /dev/null; then
        return 0
    fi
    return 1
}

prompt_continue() {
    echo ""
    echo -e "${YELLOW}$1${NC}"
    read -p "Press ENTER to continue or Ctrl+C to exit..." 
}

prompt_yes_no() {
    local prompt="$1"
    local default="${2:-y}"
    local response
    
    if [ "$default" = "y" ]; then
        read -p "$prompt [Y/n]: " response
        response="${response:-y}"
    else
        read -p "$prompt [y/N]: " response
        response="${response:-n}"
    fi
    
    [[ "$response" =~ ^[Yy] ]]
}

prompt_value() {
    local prompt="$1"
    local default="$2"
    local var_name="$3"
    local value
    
    if [ -n "$default" ]; then
        read -p "$prompt [$default]: " value
        value="${value:-$default}"
    else
        read -p "$prompt: " value
    fi
    
    eval "$var_name='$value'"
}

show_help() {
    echo "Aura Voice Chat - Master Setup Script"
    echo ""
    echo "Usage: ./setup.sh [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  --backend       Backend setup only (Node.js, dependencies, DB)"
    echo "  --android       Android APK build only"
    echo "  --aws           AWS infrastructure setup only"
    echo "  --deploy        Deploy backend to EC2"
    echo "  --teardown      Remove AWS infrastructure"
    echo "  --help          Show this help message"
    echo ""
    echo "Environment Variables:"
    echo "  AWS_REGION      AWS region (default: us-east-1)"
    echo "  DB_PASSWORD     Database password"
    echo "  DB_PORT         Database port (default: 5432)"
    echo ""
    echo "Examples:"
    echo "  ./setup.sh                    # Full interactive setup"
    echo "  ./setup.sh --backend          # Setup backend only"
    echo "  ./setup.sh --android          # Build Android APK"
    echo "  DB_PASSWORD=mypass ./setup.sh # Use preset password"
    echo ""
}

# =============================================================================
# PREREQUISITES CHECK
# =============================================================================

check_prerequisites() {
    print_step "Checking Prerequisites"
    
    local all_ok=true
    
    # Check Node.js
    print_info "Checking Node.js..."
    if check_command node; then
        local node_ver=$(node -v | cut -d'v' -f2 | cut -d'.' -f1)
        if [ "$node_ver" -ge "$NODE_VERSION" ]; then
            print_success "Node.js $(node -v) installed"
        else
            print_warning "Node.js 18+ recommended (found v$node_ver)"
            all_ok=false
        fi
    else
        print_error "Node.js not found"
        print_info "Install: https://nodejs.org/"
        all_ok=false
    fi
    
    # Check npm
    print_info "Checking npm..."
    if check_command npm; then
        print_success "npm $(npm -v) installed"
    else
        print_error "npm not found"
        all_ok=false
    fi
    
    # Check Git
    print_info "Checking Git..."
    if check_command git; then
        print_success "Git $(git --version | cut -d' ' -f3) installed"
    else
        print_error "Git not found"
        all_ok=false
    fi
    
    # Check Java (for Android builds)
    print_info "Checking Java..."
    if check_command java; then
        local java_ver=$(java -version 2>&1 | head -1)
        print_success "Java installed: $java_ver"
    else
        print_warning "Java not found (required for Android builds)"
        print_info "Install JDK 17+: https://adoptium.net/"
    fi
    
    # Check AWS CLI (optional)
    print_info "Checking AWS CLI..."
    if check_command aws; then
        print_success "AWS CLI $(aws --version 2>&1 | cut -d' ' -f1 | cut -d'/' -f2) installed"
        
        # Check AWS credentials
        if aws sts get-caller-identity &> /dev/null; then
            local account_id=$(aws sts get-caller-identity --query Account --output text 2>/dev/null)
            print_success "AWS credentials configured (Account: $account_id)"
        else
            print_warning "AWS credentials not configured (run: aws configure)"
        fi
    else
        print_warning "AWS CLI not installed (optional for AWS deployments)"
    fi
    
    # Check PostgreSQL client (optional)
    print_info "Checking PostgreSQL client..."
    if check_command psql; then
        print_success "PostgreSQL client installed"
    else
        print_warning "psql not installed (optional for direct DB access)"
    fi
    
    echo ""
    if [ "$all_ok" = true ]; then
        print_success "All core prerequisites met!"
        return 0
    else
        print_warning "Some prerequisites are missing"
        return 1
    fi
}

# =============================================================================
# BACKEND SETUP
# =============================================================================

setup_backend() {
    print_step "Setting up Backend"
    
    if [ ! -d "$BACKEND_DIR" ]; then
        print_error "Backend directory not found at: $BACKEND_DIR"
        return 1
    fi
    
    cd "$BACKEND_DIR"
    
    # Install dependencies
    print_info "Installing npm dependencies..."
    npm install 2>&1 | tee -a "$LOG_FILE"
    
    if [ ${PIPESTATUS[0]} -ne 0 ]; then
        print_error "Failed to install dependencies"
        return 1
    fi
    print_success "Dependencies installed"
    
    # Create .env if not exists
    if [ ! -f ".env" ]; then
        if [ -f ".env.example" ]; then
            print_info "Creating .env from .env.example..."
            cp .env.example .env
            
            # Generate JWT secrets
            local jwt_secret=$(openssl rand -hex 32 2>/dev/null || echo "change-this-jwt-secret-key")
            local jwt_refresh_secret=$(openssl rand -hex 32 2>/dev/null || echo "change-this-refresh-secret-key")
            
            # Update .env with generated values
            sed -i "s|JWT_SECRET=.*|JWT_SECRET=$jwt_secret|g" .env 2>/dev/null || true
            sed -i "s|JWT_REFRESH_SECRET=.*|JWT_REFRESH_SECRET=$jwt_refresh_secret|g" .env 2>/dev/null || true
            
            print_success "Created .env file with generated secrets"
            print_warning "Please update .env with your AWS and database credentials"
        else
            print_warning ".env.example not found, creating minimal .env"
            cat > .env << EOF
# Aura Voice Chat Backend Configuration
# Generated by setup.sh on $(date)

NODE_ENV=development
PORT=3000
HOST=0.0.0.0

# Database (update with your values)
DATABASE_URL=postgresql://postgres:postgres@localhost:5432/auravoicechat?schema=public
DB_HOST=localhost
DB_PORT=5432
DB_NAME=auravoicechat
DB_USER=postgres
DB_PASSWORD=postgres

# Redis
REDIS_URL=redis://localhost:6379

# JWT
JWT_SECRET=$(openssl rand -hex 32 2>/dev/null || echo "change-this-jwt-secret-key")
JWT_EXPIRES_IN=7d
JWT_REFRESH_SECRET=$(openssl rand -hex 32 2>/dev/null || echo "change-this-refresh-secret-key")
JWT_REFRESH_EXPIRES_IN=30d

# AWS (update with your values)
AWS_REGION=us-east-1
AWS_ACCESS_KEY_ID=YOUR_AWS_ACCESS_KEY
AWS_SECRET_ACCESS_KEY=YOUR_AWS_SECRET_KEY

# Cognito (update with your values)
COGNITO_USER_POOL_ID=
COGNITO_CLIENT_ID=
COGNITO_CLIENT_SECRET=

# S3 (update with your values)
S3_BUCKET_NAME=

# Twilio (for OTP - update with your values)
TWILIO_ACCOUNT_SID=
TWILIO_AUTH_TOKEN=
TWILIO_PHONE_NUMBER=

# CORS
CORS_ORIGIN=*
EOF
            print_success "Created .env file"
        fi
    else
        print_info ".env file already exists"
    fi
    
    # Generate Prisma client if schema exists
    if [ -f "prisma/schema.prisma" ]; then
        print_info "Generating Prisma client..."
        npx prisma generate 2>&1 | tee -a "$LOG_FILE" || true
        print_success "Prisma client generated"
    fi
    
    # Build TypeScript
    print_info "Building TypeScript..."
    npm run build 2>&1 | tee -a "$LOG_FILE"
    
    if [ ${PIPESTATUS[0]} -ne 0 ]; then
        print_warning "TypeScript build completed with warnings"
    else
        print_success "TypeScript build completed"
    fi
    
    cd "$ROOT_DIR"
    print_success "Backend setup complete!"
    
    echo ""
    echo -e "${CYAN}To start the backend:${NC}"
    echo "  cd backend"
    echo "  npm run dev       # Development mode"
    echo "  npm start         # Production mode"
    echo ""
}

# =============================================================================
# ANDROID BUILD
# =============================================================================

build_android() {
    print_step "Building Android APK"
    
    if [ ! -d "$ANDROID_DIR" ]; then
        print_error "Android directory not found at: $ANDROID_DIR"
        return 1
    fi
    
    cd "$ANDROID_DIR"
    
    # Check for Java
    if ! check_command java; then
        print_error "Java not found. Install JDK 17+ to build Android."
        cd "$ROOT_DIR"
        return 1
    fi
    
    # Check for gradlew
    if [ ! -f "gradlew" ]; then
        print_error "gradlew not found. Please ensure the Android project is complete."
        cd "$ROOT_DIR"
        return 1
    fi
    
    chmod +x gradlew
    
    # Check for local.properties
    if [ ! -f "local.properties" ]; then
        print_info "Creating local.properties..."
        
        if [ -n "$ANDROID_HOME" ]; then
            echo "sdk.dir=$ANDROID_HOME" > local.properties
            print_success "Created local.properties with ANDROID_HOME"
        elif [ -d "$HOME/Android/Sdk" ]; then
            echo "sdk.dir=$HOME/Android/Sdk" > local.properties
            print_success "Created local.properties"
        elif [ -d "$HOME/Library/Android/sdk" ]; then
            echo "sdk.dir=$HOME/Library/Android/sdk" > local.properties
            print_success "Created local.properties"
        else
            print_warning "Could not detect Android SDK. Please create local.properties manually:"
            print_info "  echo 'sdk.dir=/path/to/android/sdk' > android/local.properties"
        fi
    fi
    
    # Check AWS configuration
    local aws_config="app/src/main/res/raw/awsconfiguration.json"
    if [ ! -f "$aws_config" ]; then
        print_warning "AWS configuration not found at $aws_config"
        print_info "The build may fail without proper AWS configuration"
    fi
    
    # Build type selection
    local build_type="debug"
    if prompt_yes_no "Build release APK (requires signing key)?"; then
        build_type="release"
        
        # Check for keystore
        if [ ! -f "keystore.properties" ] && [ -z "$KEYSTORE_PATH" ]; then
            print_warning "No keystore configuration found"
            print_info "For release builds, create keystore.properties:"
            print_info "  storeFile=../release.keystore"
            print_info "  storePassword=your-password"
            print_info "  keyAlias=your-alias"
            print_info "  keyPassword=your-password"
            print_info ""
            print_info "Or set environment variables:"
            print_info "  KEYSTORE_PATH, KEYSTORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD"
            
            if ! prompt_yes_no "Continue with debug build instead?"; then
                cd "$ROOT_DIR"
                return 1
            fi
            build_type="debug"
        fi
    fi
    
    # Clean previous builds
    print_info "Cleaning previous builds..."
    ./gradlew clean 2>&1 | tee -a "$LOG_FILE"
    
    # Build
    print_info "Building ${build_type} APK..."
    
    if [ "$build_type" = "release" ]; then
        ./gradlew assembleProdRelease bundleProdRelease 2>&1 | tee -a "$LOG_FILE"
    else
        ./gradlew assembleDevDebug 2>&1 | tee -a "$LOG_FILE"
    fi
    
    local build_result=${PIPESTATUS[0]}
    
    if [ $build_result -ne 0 ]; then
        print_error "Build failed. Check the log for details: $LOG_FILE"
        cd "$ROOT_DIR"
        return 1
    fi
    
    print_success "Android build complete!"
    
    # Find and display APK locations
    echo ""
    echo -e "${CYAN}Build outputs:${NC}"
    find app/build/outputs -name "*.apk" 2>/dev/null | while read apk; do
        local size=$(ls -lh "$apk" | awk '{print $5}')
        echo "  APK: $apk ($size)"
    done
    
    find app/build/outputs -name "*.aab" 2>/dev/null | while read aab; do
        local size=$(ls -lh "$aab" | awk '{print $5}')
        echo "  AAB: $aab ($size)"
    done
    
    cd "$ROOT_DIR"
    echo ""
}

# =============================================================================
# AWS INFRASTRUCTURE SETUP
# =============================================================================

setup_aws() {
    print_step "Setting up AWS Infrastructure"
    
    # Check AWS CLI
    if ! check_command aws; then
        print_error "AWS CLI not installed. Install from: https://aws.amazon.com/cli/"
        return 1
    fi
    
    # Check AWS credentials
    if ! aws sts get-caller-identity &> /dev/null; then
        print_error "AWS credentials not configured. Run: aws configure"
        return 1
    fi
    
    local account_id=$(aws sts get-caller-identity --query Account --output text)
    local region=$(aws configure get region 2>/dev/null || echo "$AWS_REGION")
    
    echo ""
    echo -e "${CYAN}AWS Configuration:${NC}"
    echo "  Account: $account_id"
    echo "  Region: $region"
    echo ""
    
    # Check for CloudFormation template
    local cf_template="$AWS_DIR/cloudformation/main.yaml"
    if [ ! -f "$cf_template" ]; then
        print_warning "CloudFormation template not found at: $cf_template"
        print_info "You can manually create AWS resources using the AWS Console"
        return 1
    fi
    
    # Get database password
    if [ -z "$DB_PASSWORD" ]; then
        read -sp "Enter database password (min 8 characters): " DB_PASSWORD
        echo ""
        
        if [ ${#DB_PASSWORD} -lt 8 ]; then
            print_error "Password must be at least 8 characters"
            return 1
        fi
    fi
    
    # Get admin email
    prompt_value "Admin email for notifications" "admin@example.com" "ADMIN_EMAIL"
    
    # Check if stack exists
    if aws cloudformation describe-stacks --stack-name "$STACK_NAME" --region "$region" &> /dev/null; then
        print_info "Stack '$STACK_NAME' already exists"
        
        if prompt_yes_no "Update existing stack?"; then
            print_info "Updating CloudFormation stack..."
            aws cloudformation update-stack \
                --stack-name "$STACK_NAME" \
                --template-body "file://$cf_template" \
                --parameters \
                    ParameterKey=Environment,ParameterValue=production \
                    ParameterKey=DBPassword,ParameterValue="$DB_PASSWORD" \
                    ParameterKey=AdminEmail,ParameterValue="$ADMIN_EMAIL" \
                --capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM \
                --region "$region" \
                2>&1 | tee -a "$LOG_FILE"
        fi
    else
        print_info "Creating CloudFormation stack..."
        print_warning "This may take 15-20 minutes..."
        
        aws cloudformation create-stack \
            --stack-name "$STACK_NAME" \
            --template-body "file://$cf_template" \
            --parameters \
                ParameterKey=Environment,ParameterValue=production \
                ParameterKey=DBPassword,ParameterValue="$DB_PASSWORD" \
                ParameterKey=AdminEmail,ParameterValue="$ADMIN_EMAIL" \
            --capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM \
            --region "$region" \
            2>&1 | tee -a "$LOG_FILE"
        
        if [ ${PIPESTATUS[0]} -ne 0 ]; then
            print_error "Failed to create stack"
            return 1
        fi
        
        print_info "Waiting for stack creation..."
        aws cloudformation wait stack-create-complete \
            --stack-name "$STACK_NAME" \
            --region "$region" \
            2>&1 | tee -a "$LOG_FILE"
    fi
    
    # Get stack outputs
    print_step "AWS Resources Created"
    aws cloudformation describe-stacks \
        --stack-name "$STACK_NAME" \
        --region "$region" \
        --query 'Stacks[0].Outputs' \
        --output table
    
    print_success "AWS infrastructure setup complete!"
    echo ""
}

# =============================================================================
# AWS TEARDOWN
# =============================================================================

teardown_aws() {
    print_step "AWS Infrastructure Teardown"
    
    echo -e "${RED}WARNING: This will DELETE all AWS resources!${NC}"
    echo ""
    
    if ! prompt_yes_no "Are you sure you want to delete all AWS resources?" "n"; then
        print_info "Teardown cancelled"
        return 0
    fi
    
    read -p "Type 'DELETE' to confirm: " confirm
    if [ "$confirm" != "DELETE" ]; then
        print_info "Teardown cancelled"
        return 0
    fi
    
    local region=$(aws configure get region 2>/dev/null || echo "$AWS_REGION")
    
    # Check if stack exists
    if aws cloudformation describe-stacks --stack-name "$STACK_NAME" --region "$region" &> /dev/null; then
        print_info "Deleting CloudFormation stack: $STACK_NAME"
        
        aws cloudformation delete-stack \
            --stack-name "$STACK_NAME" \
            --region "$region"
        
        print_info "Waiting for stack deletion (this may take several minutes)..."
        aws cloudformation wait stack-delete-complete \
            --stack-name "$STACK_NAME" \
            --region "$region"
        
        print_success "CloudFormation stack deleted"
    else
        print_info "Stack '$STACK_NAME' not found"
    fi
    
    print_success "AWS teardown complete!"
}

# =============================================================================
# DEPLOY TO EC2
# =============================================================================

deploy_to_ec2() {
    print_step "Deploying to EC2"
    
    # Check if stack outputs exist
    if ! aws cloudformation describe-stacks --stack-name "$STACK_NAME" &> /dev/null; then
        print_error "AWS infrastructure not set up. Run: ./setup.sh --aws"
        return 1
    fi
    
    # Get EC2 instance IP
    local ec2_ip=$(aws cloudformation describe-stacks \
        --stack-name "$STACK_NAME" \
        --query "Stacks[0].Outputs[?OutputKey=='EC2PublicIP'].OutputValue" \
        --output text 2>/dev/null)
    
    if [ -z "$ec2_ip" ] || [ "$ec2_ip" = "None" ]; then
        print_error "Could not get EC2 instance IP from CloudFormation outputs"
        prompt_value "Enter EC2 instance IP manually" "" "ec2_ip"
    fi
    
    # Check for SSH key
    local key_file="$ROOT_DIR/aura-voice-chat-key.pem"
    if [ ! -f "$key_file" ]; then
        prompt_value "Path to SSH key file" "$HOME/.ssh/aura-voice-chat-key.pem" "key_file"
        
        if [ ! -f "$key_file" ]; then
            print_error "SSH key file not found: $key_file"
            return 1
        fi
    fi
    
    chmod 400 "$key_file"
    
    local ssh_user="ubuntu"  # or ec2-user for Amazon Linux
    
    print_info "Connecting to EC2: $ec2_ip"
    
    # Copy backend files
    print_info "Copying backend files..."
    rsync -avz --progress \
        -e "ssh -i $key_file -o StrictHostKeyChecking=no" \
        --exclude 'node_modules' \
        --exclude '.git' \
        --exclude 'dist' \
        "$BACKEND_DIR/" \
        "${ssh_user}@${ec2_ip}:/opt/aura-voice-chat/backend/" \
        2>&1 | tee -a "$LOG_FILE"
    
    # Run setup on EC2
    print_info "Running setup on EC2..."
    ssh -i "$key_file" -o StrictHostKeyChecking=no "${ssh_user}@${ec2_ip}" << 'REMOTE_SCRIPT'
        cd /opt/aura-voice-chat/backend
        
        # Install Node.js if not present
        if ! command -v node &> /dev/null; then
            curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
            sudo apt install -y nodejs
        fi
        
        # Install PM2 if not present
        if ! command -v pm2 &> /dev/null; then
            sudo npm install -g pm2
        fi
        
        # Install dependencies
        npm install --production
        
        # Build
        npm run build
        
        # Restart with PM2
        pm2 delete aura-backend 2>/dev/null || true
        pm2 start dist/index.js --name aura-backend
        pm2 save
        
        echo "Deployment complete!"
        pm2 status
REMOTE_SCRIPT
    
    print_success "Deployment complete!"
    echo ""
    echo -e "${CYAN}Backend deployed to: http://${ec2_ip}:3000${NC}"
    echo ""
}

# =============================================================================
# FULL INTERACTIVE SETUP
# =============================================================================

full_setup() {
    print_header
    
    echo "This script will help you set up Aura Voice Chat."
    echo ""
    echo "Available setup options:"
    echo "  1. Backend Setup (Node.js, dependencies)"
    echo "  2. Android APK Build"
    echo "  3. AWS Infrastructure"
    echo "  4. Full Setup (all of the above)"
    echo "  5. Deploy to EC2"
    echo ""
    
    prompt_value "Select option (1-5)" "4" "setup_option"
    
    case $setup_option in
        1)
            check_prerequisites
            setup_backend
            ;;
        2)
            check_prerequisites
            build_android
            ;;
        3)
            check_prerequisites
            setup_aws
            ;;
        4)
            check_prerequisites || prompt_continue "Some prerequisites missing. Continue anyway?"
            setup_backend
            
            if prompt_yes_no "Set up AWS infrastructure?"; then
                setup_aws
            fi
            
            if prompt_yes_no "Build Android APK?"; then
                build_android
            fi
            ;;
        5)
            deploy_to_ec2
            ;;
        *)
            print_error "Invalid option"
            return 1
            ;;
    esac
    
    # Summary
    print_step "Setup Complete!"
    
    echo ""
    echo -e "${GREEN}What's been set up:${NC}"
    [ -f "$BACKEND_DIR/node_modules/.package-lock.json" ] && echo "  ✓ Backend dependencies installed"
    [ -f "$BACKEND_DIR/.env" ] && echo "  ✓ Backend environment configured"
    [ -f "$BACKEND_DIR/dist/index.js" ] && echo "  ✓ Backend built"
    
    echo ""
    echo -e "${CYAN}Next steps:${NC}"
    echo ""
    echo "1. Update configuration files:"
    echo "   - backend/.env (AWS credentials, database)"
    echo "   - android/app/src/main/res/raw/awsconfiguration.json"
    echo ""
    echo "2. Start the backend:"
    echo "   cd backend && npm run dev"
    echo ""
    echo "3. Build Android APK:"
    echo "   ./setup.sh --android"
    echo ""
    echo "4. Deploy to production:"
    echo "   ./setup.sh --deploy"
    echo ""
    echo -e "${CYAN}Documentation:${NC}"
    echo "   - Developer Guide: DEVELOPER-GUIDE.md"
    echo "   - API Reference: api.md"
    echo "   - Deployment: deployment.md"
    echo ""
    echo -e "${PURPLE}Setup log saved to: $LOG_FILE${NC}"
    echo ""
}

# =============================================================================
# MAIN ENTRY POINT
# =============================================================================

main() {
    # Parse arguments
    case "${1:-}" in
        --backend)
            print_header
            check_prerequisites
            setup_backend
            ;;
        --android)
            print_header
            check_prerequisites
            build_android
            ;;
        --aws)
            print_header
            check_prerequisites
            setup_aws
            ;;
        --deploy)
            print_header
            deploy_to_ec2
            ;;
        --teardown)
            print_header
            teardown_aws
            ;;
        --help|-h)
            show_help
            ;;
        "")
            full_setup
            ;;
        *)
            print_error "Unknown option: $1"
            show_help
            exit 1
            ;;
    esac
}

# Run main
main "$@"
