#!/bin/bash

#######################################################################
# Aura Voice Chat - Complete Setup Script
# Developer: Hawkaye Visions LTD — Lahore, Pakistan
#
# This script sets up the complete Aura Voice Chat infrastructure:
# 1. AWS Infrastructure (VPC, EC2, RDS, S3, Cognito)
# 2. Database setup and data upload
# 3. Backend deployment
# 4. Android APK build
#
# The script will NOT exit on errors - it will pause and wait for
# user input (Enter key) before continuing or exiting.
#######################################################################

set -o pipefail

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Configuration variables (can be overridden by environment)
AWS_REGION="${AWS_REGION:-us-east-1}"
STACK_NAME="${STACK_NAME:-aura-voice-chat-production}"
DB_NAME="${DB_NAME:-auravoicechat}"
DB_USER="${DB_USER:-aura_admin}"
NODE_VERSION="${NODE_VERSION:-18}"
JAVA_VERSION="${JAVA_VERSION:-17}"

# Log file
LOG_FILE="setup-$(date +%Y%m%d-%H%M%S).log"

# Function to print colored messages
print_header() {
    echo -e "\n${PURPLE}======================================${NC}"
    echo -e "${PURPLE}$1${NC}"
    echo -e "${PURPLE}======================================${NC}\n"
}

print_step() {
    echo -e "${CYAN}[STEP] $1${NC}"
}

print_success() {
    echo -e "${GREEN}[SUCCESS] $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}[WARNING] $1${NC}"
}

print_error() {
    echo -e "${RED}[ERROR] $1${NC}"
}

print_info() {
    echo -e "${BLUE}[INFO] $1${NC}"
}

# Function to handle errors without exiting
handle_error() {
    local error_msg="$1"
    local step_name="$2"
    
    print_error "$error_msg"
    print_error "Step failed: $step_name"
    echo ""
    echo -e "${YELLOW}The script encountered an error but will NOT exit automatically.${NC}"
    echo -e "${YELLOW}Review the error above and decide how to proceed.${NC}"
    echo ""
    echo -e "${CYAN}Options:${NC}"
    echo "  - Press ENTER to continue to the next step"
    echo "  - Press Ctrl+C to exit the script"
    echo ""
    read -p "Press ENTER to continue..." 
}

# Function to check if a command exists
check_command() {
    if ! command -v "$1" &> /dev/null; then
        return 1
    fi
    return 0
}

# Function to wait for user confirmation
wait_for_confirmation() {
    echo ""
    echo -e "${YELLOW}$1${NC}"
    read -p "Press ENTER to continue or Ctrl+C to exit..."
}

# Function to prompt for value with default
prompt_with_default() {
    local prompt="$1"
    local default="$2"
    local var_name="$3"
    
    read -p "$prompt [$default]: " input
    if [ -z "$input" ]; then
        eval "$var_name='$default'"
    else
        eval "$var_name='$input'"
    fi
}

#######################################################################
# SECTION 1: PREREQUISITES CHECK
#######################################################################
check_prerequisites() {
    print_header "SECTION 1: Checking Prerequisites"
    
    local all_ok=true
    
    # Check AWS CLI
    print_step "Checking AWS CLI..."
    if check_command aws; then
        print_success "AWS CLI is installed: $(aws --version 2>&1 | head -n 1)"
    else
        print_warning "AWS CLI is not installed"
        print_info "Install: https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html"
        all_ok=false
    fi
    
    # Check AWS credentials
    print_step "Checking AWS credentials..."
    if aws sts get-caller-identity &> /dev/null; then
        local account_id=$(aws sts get-caller-identity --query Account --output text)
        print_success "AWS credentials configured (Account: $account_id)"
    else
        print_warning "AWS credentials not configured"
        print_info "Run: aws configure"
        all_ok=false
    fi
    
    # Check Node.js
    print_step "Checking Node.js..."
    if check_command node; then
        local node_ver=$(node --version)
        print_success "Node.js is installed: $node_ver"
    else
        print_warning "Node.js is not installed"
        print_info "Install: https://nodejs.org/"
        all_ok=false
    fi
    
    # Check npm
    print_step "Checking npm..."
    if check_command npm; then
        print_success "npm is installed: $(npm --version)"
    else
        print_warning "npm is not installed"
        all_ok=false
    fi
    
    # Check Git
    print_step "Checking Git..."
    if check_command git; then
        print_success "Git is installed: $(git --version)"
    else
        print_warning "Git is not installed"
        all_ok=false
    fi
    
    # Check Java (for Android builds)
    print_step "Checking Java..."
    if check_command java; then
        print_success "Java is installed: $(java -version 2>&1 | head -n 1)"
    else
        print_warning "Java is not installed (required for Android builds)"
        print_info "Install: https://adoptium.net/"
        all_ok=false
    fi
    
    # Check psql (for database operations)
    print_step "Checking PostgreSQL client..."
    if check_command psql; then
        print_success "PostgreSQL client is installed: $(psql --version)"
    else
        print_warning "PostgreSQL client not installed (optional, for direct DB access)"
        print_info "Install: apt-get install postgresql-client"
    fi
    
    if [ "$all_ok" = false ]; then
        handle_error "Some prerequisites are missing" "Prerequisites Check"
    else
        print_success "All prerequisites are installed!"
    fi
}

#######################################################################
# SECTION 2: AWS CONFIGURATION
#######################################################################
configure_aws() {
    print_header "SECTION 2: AWS Configuration"
    
    # Check current region
    print_step "Checking AWS region..."
    local current_region=$(aws configure get region 2>/dev/null)
    
    if [ -z "$current_region" ]; then
        print_warning "No default region configured"
        prompt_with_default "Enter AWS region" "$AWS_REGION" "AWS_REGION"
        aws configure set region "$AWS_REGION"
        if [ $? -ne 0 ]; then
            handle_error "Failed to set AWS region" "AWS Configuration"
            return
        fi
    else
        print_info "Current region: $current_region"
        AWS_REGION="$current_region"
    fi
    
    print_success "AWS region set to: $AWS_REGION"
}

#######################################################################
# SECTION 3: CREATE EC2 KEY PAIR
#######################################################################
create_key_pair() {
    print_header "SECTION 3: EC2 Key Pair"
    
    local KEY_NAME="aura-voice-chat-key"
    local KEY_FILE="${KEY_NAME}.pem"
    
    print_step "Checking for existing key pair..."
    
    if [ -f "$KEY_FILE" ]; then
        print_info "Key file $KEY_FILE already exists locally"
        wait_for_confirmation "Do you want to skip key pair creation?"
        return
    fi
    
    # Check if key exists in AWS
    if aws ec2 describe-key-pairs --key-names "$KEY_NAME" &> /dev/null; then
        print_warning "Key pair '$KEY_NAME' already exists in AWS but no local file found"
        print_info "You may need to delete it from AWS if you've lost the private key"
        wait_for_confirmation "Press ENTER to continue (or delete the key in AWS Console first)"
        return
    fi
    
    print_step "Creating new key pair..."
    if aws ec2 create-key-pair --key-name "$KEY_NAME" --query 'KeyMaterial' --output text > "$KEY_FILE" 2>&1; then
        chmod 400 "$KEY_FILE"
        print_success "Key pair created: $KEY_FILE"
        print_warning "IMPORTANT: Keep this file secure! You cannot download it again."
    else
        handle_error "Failed to create key pair" "EC2 Key Pair Creation"
    fi
}

#######################################################################
# SECTION 4: DEPLOY AWS INFRASTRUCTURE (CloudFormation)
#######################################################################
deploy_infrastructure() {
    print_header "SECTION 4: AWS Infrastructure Deployment"
    
    # Get database password
    print_step "Setting up database password..."
    if [ -z "$DB_PASSWORD" ]; then
        read -sp "Enter database password (min 8 chars): " DB_PASSWORD
        echo ""
        if [ ${#DB_PASSWORD} -lt 8 ]; then
            handle_error "Password must be at least 8 characters" "Database Password"
            return
        fi
    fi
    
    # Get admin email
    if [ -z "$ADMIN_EMAIL" ]; then
        prompt_with_default "Enter admin email" "admin@example.com" "ADMIN_EMAIL"
    fi
    
    # Check if stack already exists
    print_step "Checking if CloudFormation stack exists..."
    if aws cloudformation describe-stacks --stack-name "$STACK_NAME" &> /dev/null; then
        print_info "Stack '$STACK_NAME' already exists"
        print_info "Stack status: $(aws cloudformation describe-stacks --stack-name "$STACK_NAME" --query 'Stacks[0].StackStatus' --output text)"
        wait_for_confirmation "Do you want to skip infrastructure deployment?"
        return
    fi
    
    # Check for CloudFormation template
    local TEMPLATE_FILE="aws/cloudformation/main.yaml"
    if [ ! -f "$TEMPLATE_FILE" ]; then
        print_warning "CloudFormation template not found at $TEMPLATE_FILE"
        print_info "Skipping infrastructure deployment"
        print_info "You can manually create resources using AWS Console"
        wait_for_confirmation "Press ENTER to continue..."
        return
    fi
    
    print_step "Deploying CloudFormation stack..."
    print_info "This may take 10-20 minutes..."
    
    aws cloudformation create-stack \
        --stack-name "$STACK_NAME" \
        --template-body "file://$TEMPLATE_FILE" \
        --parameters \
            ParameterKey=Environment,ParameterValue=production \
            ParameterKey=DBPassword,ParameterValue="$DB_PASSWORD" \
            ParameterKey=AdminEmail,ParameterValue="$ADMIN_EMAIL" \
            ParameterKey=KeyPairName,ParameterValue=aura-voice-chat-key \
        --capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM \
        2>&1 | tee -a "$LOG_FILE"
    
    if [ ${PIPESTATUS[0]:-1} -ne 0 ]; then
        handle_error "Failed to create CloudFormation stack" "Infrastructure Deployment"
        return
    fi
    
    print_step "Waiting for stack creation to complete..."
    print_info "This can take 15-20 minutes. You can also monitor in AWS Console."
    
    aws cloudformation wait stack-create-complete --stack-name "$STACK_NAME" 2>&1 | tee -a "$LOG_FILE"
    
    if [ ${PIPESTATUS[0]:-1} -ne 0 ]; then
        handle_error "Stack creation failed or timed out. Check AWS Console for details." "Infrastructure Deployment"
        return
    fi
    
    print_success "CloudFormation stack created successfully!"
    
    # Get stack outputs
    print_step "Retrieving stack outputs..."
    aws cloudformation describe-stacks \
        --stack-name "$STACK_NAME" \
        --query 'Stacks[0].Outputs' \
        --output table
}

#######################################################################
# SECTION 5: DATABASE SETUP
#######################################################################
setup_database() {
    print_header "SECTION 5: Database Setup"
    
    # Try to get RDS endpoint from CloudFormation
    print_step "Getting RDS endpoint..."
    
    local RDS_ENDPOINT=$(aws cloudformation describe-stacks \
        --stack-name "$STACK_NAME" \
        --query "Stacks[0].Outputs[?OutputKey=='RDSEndpoint'].OutputValue" \
        --output text 2>/dev/null)
    
    if [ -z "$RDS_ENDPOINT" ] || [ "$RDS_ENDPOINT" = "None" ]; then
        print_warning "Could not get RDS endpoint from CloudFormation"
        prompt_with_default "Enter RDS endpoint manually (or press ENTER to skip)" "" "RDS_ENDPOINT"
        
        if [ -z "$RDS_ENDPOINT" ]; then
            print_info "Skipping database setup"
            return
        fi
    fi
    
    print_info "RDS Endpoint: $RDS_ENDPOINT"
    
    # Check for schema file
    local SCHEMA_FILE="backend/src/database/schema.sql"
    if [ ! -f "$SCHEMA_FILE" ]; then
        print_warning "Schema file not found at $SCHEMA_FILE"
        print_info "Checking for Prisma migrations..."
        
        if [ -d "backend/prisma/migrations" ]; then
            print_step "Running Prisma migrations..."
            cd backend
            
            # Set DATABASE_URL
            export DATABASE_URL="postgresql://${DB_USER}:${DB_PASSWORD}@${RDS_ENDPOINT}:5432/${DB_NAME}"
            
            npm run prisma:migrate deploy 2>&1 | tee -a "../$LOG_FILE"
            
            if [ ${PIPESTATUS[0]:-1} -ne 0 ]; then
                handle_error "Prisma migration failed" "Database Setup"
            else
                print_success "Prisma migrations applied!"
            fi
            
            cd ..
        else
            print_warning "No schema file or Prisma migrations found"
            print_info "You may need to set up the database manually"
        fi
        return
    fi
    
    # Run schema using psql
    print_step "Applying database schema..."
    
    if check_command psql; then
        PGPASSWORD="$DB_PASSWORD" psql \
            -h "$RDS_ENDPOINT" \
            -U "$DB_USER" \
            -d "$DB_NAME" \
            -f "$SCHEMA_FILE" 2>&1 | tee -a "$LOG_FILE"
        
        if [ ${PIPESTATUS[0]:-1} -ne 0 ]; then
            handle_error "Failed to apply database schema" "Database Setup"
        else
            print_success "Database schema applied successfully!"
        fi
    else
        print_warning "psql not installed. Cannot apply schema directly."
        print_info "You can run the schema file manually from an EC2 instance"
    fi
}

#######################################################################
# SECTION 6: BACKEND SETUP
#######################################################################
setup_backend() {
    print_header "SECTION 6: Backend Setup"
    
    if [ ! -d "backend" ]; then
        print_error "Backend directory not found"
        handle_error "Cannot find backend directory" "Backend Setup"
        return
    fi
    
    cd backend
    
    # Install dependencies
    print_step "Installing backend dependencies..."
    npm install 2>&1 | tee -a "../$LOG_FILE"
    
    if [ ${PIPESTATUS[0]:-1} -ne 0 ]; then
        handle_error "Failed to install dependencies" "Backend Setup"
        cd ..
        return
    fi
    print_success "Dependencies installed!"
    
    # Build TypeScript
    print_step "Building TypeScript..."
    npm run build 2>&1 | tee -a "../$LOG_FILE"
    
    if [ ${PIPESTATUS[0]:-1} -ne 0 ]; then
        handle_error "TypeScript build failed" "Backend Setup"
        cd ..
        return
    fi
    print_success "Backend built successfully!"
    
    # Create .env file if it doesn't exist
    if [ ! -f ".env" ]; then
        print_step "Creating .env file..."
        
        # Try to get values from CloudFormation
        local RDS_ENDPOINT=$(aws cloudformation describe-stacks \
            --stack-name "$STACK_NAME" \
            --query "Stacks[0].Outputs[?OutputKey=='RDSEndpoint'].OutputValue" \
            --output text 2>/dev/null)
        
        local S3_BUCKET=$(aws cloudformation describe-stacks \
            --stack-name "$STACK_NAME" \
            --query "Stacks[0].Outputs[?OutputKey=='S3BucketName'].OutputValue" \
            --output text 2>/dev/null)
        
        local COGNITO_USER_POOL=$(aws cloudformation describe-stacks \
            --stack-name "$STACK_NAME" \
            --query "Stacks[0].Outputs[?OutputKey=='CognitoUserPoolId'].OutputValue" \
            --output text 2>/dev/null)
        
        local COGNITO_CLIENT=$(aws cloudformation describe-stacks \
            --stack-name "$STACK_NAME" \
            --query "Stacks[0].Outputs[?OutputKey=='CognitoClientId'].OutputValue" \
            --output text 2>/dev/null)
        
        cat > .env << EOF
# Aura Voice Chat - Backend Configuration
# Generated by setup script on $(date)

NODE_ENV=production
PORT=3000

# Database
DB_HOST=${RDS_ENDPOINT:-your-rds-endpoint}
DB_PORT=5432
DB_NAME=${DB_NAME}
DB_USER=${DB_USER}
DB_PASSWORD=${DB_PASSWORD:-your-db-password}
DATABASE_URL=postgresql://${DB_USER}:${DB_PASSWORD:-password}@${RDS_ENDPOINT:-localhost}:5432/${DB_NAME}

# AWS
AWS_REGION=${AWS_REGION}
S3_BUCKET=${S3_BUCKET:-your-s3-bucket}
COGNITO_USER_POOL_ID=${COGNITO_USER_POOL:-your-user-pool-id}
COGNITO_CLIENT_ID=${COGNITO_CLIENT:-your-client-id}

# JWT
JWT_SECRET=$(openssl rand -base64 32 2>/dev/null || echo "change-this-secret-key")
JWT_EXPIRES_IN=7d

# Redis (optional)
REDIS_URL=redis://localhost:6379
EOF
        
        print_success ".env file created!"
        print_warning "Please review and update .env with correct values"
    else
        print_info ".env file already exists"
    fi
    
    # Run linting (optional)
    print_step "Running linter..."
    npm run lint 2>&1 | tee -a "../$LOG_FILE" || true
    
    # Run tests if they exist
    print_step "Running tests..."
    npm test -- --passWithNoTests 2>&1 | tee -a "../$LOG_FILE" || true
    
    cd ..
    print_success "Backend setup complete!"
}

#######################################################################
# SECTION 7: ANDROID BUILD
#######################################################################
build_android() {
    print_header "SECTION 7: Android APK Build"
    
    if [ ! -d "android" ]; then
        print_error "Android directory not found"
        handle_error "Cannot find android directory" "Android Build"
        return
    fi
    
    cd android
    
    # AWS-based build - No Firebase/google-services.json required
    print_info "Building AWS-based Android app (No Firebase)"
    
    # Check for AWS configuration
    if [ ! -f "app/src/main/res/raw/awsconfiguration.json" ]; then
        print_warning "AWS configuration file not found"
        print_info "Creating default AWS configuration..."
        
        mkdir -p app/src/main/res/raw
        cat > app/src/main/res/raw/awsconfiguration.json << 'AWSEOF'
{
    "Version": "1.0",
    "CredentialsProvider": {
        "CognitoIdentity": {
            "Default": {
                "PoolId": "YOUR_IDENTITY_POOL_ID",
                "Region": "us-east-1"
            }
        }
    },
    "CognitoUserPool": {
        "Default": {
            "PoolId": "YOUR_USER_POOL_ID",
            "AppClientId": "YOUR_APP_CLIENT_ID",
            "Region": "us-east-1"
        }
    },
    "S3TransferUtility": {
        "Default": {
            "Bucket": "YOUR_S3_BUCKET",
            "Region": "us-east-1"
        }
    },
    "PinpointAnalytics": {
        "Default": {
            "AppId": "YOUR_PINPOINT_APP_ID",
            "Region": "us-east-1"
        }
    }
}
AWSEOF
        print_warning "Please update app/src/main/res/raw/awsconfiguration.json with your AWS values"
    fi
    
    # Check for gradlew
    if [ ! -f "gradlew" ]; then
        print_warning "gradlew not found. Creating Gradle wrapper..."
        
        if check_command gradle; then
            gradle wrapper 2>&1 | tee -a "../$LOG_FILE"
        else
            print_warning "Gradle not installed. Cannot create wrapper."
            print_info "Install Gradle or create wrapper using Android Studio"
            cd ..
            return
        fi
    fi
    
    chmod +x gradlew
    
    # Check for keystore
    local KEYSTORE_FILE="release.keystore"
    if [ ! -f "$KEYSTORE_FILE" ] && [ ! -f "../$KEYSTORE_FILE" ]; then
        print_warning "Release keystore not found"
        print_info "Building debug APK only..."
        
        print_step "Building debug APK..."
        ./gradlew assembleDebug 2>&1 | tee -a "../$LOG_FILE"
        
        if [ ${PIPESTATUS[0]:-1} -ne 0 ]; then
            handle_error "Debug APK build failed" "Android Build"
        else
            print_success "Debug APK built!"
            
            # Find and display APK location
            local APK_PATH=$(find app/build/outputs/apk -name "*.apk" 2>/dev/null | head -n 1)
            if [ -n "$APK_PATH" ]; then
                print_info "APK location: $APK_PATH"
                print_info "APK size: $(ls -lh "$APK_PATH" | awk '{print $5}')"
            fi
        fi
    else
        print_step "Building release APK..."
        
        # Check for keystore.properties
        if [ ! -f "keystore.properties" ]; then
            print_warning "keystore.properties not found"
            print_info "Create keystore.properties with your signing credentials"
            wait_for_confirmation "Press ENTER after creating keystore.properties (or to build debug)"
        fi
        
        ./gradlew assembleRelease bundleRelease 2>&1 | tee -a "../$LOG_FILE"
        
        if [ ${PIPESTATUS[0]:-1} -ne 0 ]; then
            handle_error "Release build failed. Trying debug build..." "Android Build"
            
            ./gradlew assembleDebug 2>&1 | tee -a "../$LOG_FILE"
            
            if [ ${PIPESTATUS[0]:-1} -ne 0 ]; then
                handle_error "Debug build also failed" "Android Build"
            else
                print_success "Debug APK built (release failed)"
            fi
        else
            print_success "Release APK and AAB built!"
            
            # Find and display output locations
            local APK_PATH=$(find app/build/outputs/apk -name "*release*.apk" 2>/dev/null | head -n 1)
            local AAB_PATH=$(find app/build/outputs/bundle -name "*.aab" 2>/dev/null | head -n 1)
            
            if [ -n "$APK_PATH" ]; then
                print_info "APK: $APK_PATH"
            fi
            if [ -n "$AAB_PATH" ]; then
                print_info "AAB: $AAB_PATH"
            fi
        fi
    fi
    
    cd ..
}

#######################################################################
# SECTION 8: SUMMARY AND NEXT STEPS
#######################################################################
print_summary() {
    print_header "SETUP COMPLETE - Summary"
    
    echo -e "${GREEN}============================================${NC}"
    echo -e "${GREEN}  Aura Voice Chat Setup Complete!${NC}"
    echo -e "${GREEN}============================================${NC}"
    echo ""
    echo "Setup log saved to: $LOG_FILE"
    echo ""
    
    # Print CloudFormation outputs if available
    if aws cloudformation describe-stacks --stack-name "$STACK_NAME" &> /dev/null; then
        echo -e "${CYAN}AWS Resources:${NC}"
        aws cloudformation describe-stacks \
            --stack-name "$STACK_NAME" \
            --query 'Stacks[0].Outputs[*].[OutputKey,OutputValue]' \
            --output text 2>/dev/null | while read key value; do
            echo "  - $key: $value"
        done
        echo ""
    fi
    
    # Check for built artifacts
    if [ -f "backend/dist/index.js" ]; then
        echo -e "${GREEN}✓${NC} Backend built successfully"
    else
        echo -e "${YELLOW}!${NC} Backend build status unknown"
    fi
    
    local APK_FILES=$(find android -name "*.apk" 2>/dev/null | wc -l)
    if [ "$APK_FILES" -gt 0 ]; then
        echo -e "${GREEN}✓${NC} Android APK(s) built: $APK_FILES file(s)"
    else
        echo -e "${YELLOW}!${NC} No APK files found"
    fi
    
    echo ""
    echo -e "${CYAN}Next Steps:${NC}"
    echo "1. Review and update backend/.env with correct values"
    echo "2. Deploy backend to EC2 or your hosting platform"
    echo "3. Test the Android APK on a device"
    echo "4. Configure AWS SNS for push notifications"
    echo "5. Set up domain and SSL certificates"
    echo "6. Update awsconfiguration.json with your AWS resource IDs"
    echo ""
    echo -e "${CYAN}AWS Services Used (No Firebase):${NC}"
    echo "- Authentication: AWS Cognito"
    echo "- Storage: AWS S3"
    echo "- Database: AWS RDS PostgreSQL"
    echo "- Push Notifications: AWS SNS"
    echo "- Analytics: AWS Pinpoint"
    echo ""
    echo -e "${CYAN}Documentation:${NC}"
    echo "- AWS Setup: docs/aws-setup.md"
    echo "- RDS Setup: docs/rds-setup.md"
    echo "- Cognito Setup: docs/cognito-setup.md"
    echo "- S3 Setup: docs/s3-setup.md"
    echo "- APK Build: docs/apk-build-guide.md"
    echo ""
    
    echo -e "${YELLOW}Press ENTER to exit...${NC}"
    read
}

#######################################################################
# MAIN EXECUTION
#######################################################################
main() {
    echo ""
    echo -e "${PURPLE}╔══════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${PURPLE}║                                                              ║${NC}"
    echo -e "${PURPLE}║     Aura Voice Chat - Complete Setup Script                  ║${NC}"
    echo -e "${PURPLE}║     Developer: Hawkaye Visions LTD — Pakistan                ║${NC}"
    echo -e "${PURPLE}║                                                              ║${NC}"
    echo -e "${PURPLE}╚══════════════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo "This script will set up:"
    echo "  1. AWS Infrastructure (VPC, EC2, RDS, S3, Cognito)"
    echo "  2. Database schema and initial data"
    echo "  3. Backend application"
    echo "  4. Android APK"
    echo ""
    echo -e "${YELLOW}NOTE: This script will NOT exit on errors.${NC}"
    echo -e "${YELLOW}      It will pause and wait for you to press ENTER.${NC}"
    echo ""
    
    wait_for_confirmation "Ready to begin setup?"
    
    # Run all setup steps
    check_prerequisites
    configure_aws
    create_key_pair
    deploy_infrastructure
    setup_database
    setup_backend
    build_android
    print_summary
}

# Run main function
main "$@"
