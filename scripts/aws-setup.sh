#!/bin/bash

# ============================================================================
# Aura Voice Chat - Complete AWS Infrastructure Setup
# Developer: Hawkaye Visions LTD — Lahore, Pakistan
#
# This script provides a comprehensive setup for AWS infrastructure.
# It can either use CloudFormation or set up resources individually.
#
# Usage: ./aws-setup.sh [--cloudformation | --manual]
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
echo "║     🚀 AURA VOICE CHAT - AWS INFRASTRUCTURE SETUP               ║"
echo "║                                                                  ║"
echo "║     Developer: Hawkaye Visions LTD — Lahore, Pakistan           ║"
echo "║                                                                  ║"
echo "╚══════════════════════════════════════════════════════════════════╝"
echo -e "${NC}"
echo ""

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# Check prerequisites
check_prerequisites() {
    echo -e "${CYAN}Checking prerequisites...${NC}"
    
    # AWS CLI
    if ! command -v aws &> /dev/null; then
        echo -e "${RED}❌ AWS CLI is not installed${NC}"
        echo "   Install: https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html"
        exit 1
    fi
    echo -e "${GREEN}✓ AWS CLI installed${NC}"
    
    # AWS Credentials
    if ! aws sts get-caller-identity &> /dev/null; then
        echo -e "${RED}❌ AWS credentials not configured${NC}"
        echo "   Run: aws configure"
        exit 1
    fi
    echo -e "${GREEN}✓ AWS credentials configured${NC}"
    
    # Get account info
    AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
    AWS_REGION=$(aws configure get region || echo "us-east-1")
    
    echo ""
    echo -e "${CYAN}AWS Account:${NC} $AWS_ACCOUNT_ID"
    echo -e "${CYAN}AWS Region:${NC} $AWS_REGION"
    echo ""
}

# Setup using CloudFormation
setup_cloudformation() {
    echo -e "${CYAN}Setting up AWS infrastructure using CloudFormation...${NC}"
    echo ""
    
    # Run the create-stack script
    bash "$PROJECT_DIR/aws/scripts/create-stack.sh" production "$AWS_REGION"
}

# Manual setup (individual resources)
setup_manual() {
    echo -e "${CYAN}Setting up AWS infrastructure manually...${NC}"
    echo ""
    
    # Prompt for parameters
    read -p "Enter a prefix for resource names (default: aura-voice-chat): " PREFIX
    PREFIX=${PREFIX:-aura-voice-chat}
    
    read -p "Database password (min 8 chars): " -s DB_PASSWORD
    echo ""
    
    read -p "Admin email: " ADMIN_EMAIL
    
    # Create VPC
    echo ""
    echo -e "${BLUE}Step 1: Creating VPC...${NC}"
    VPC_ID=$(aws ec2 create-vpc \
        --cidr-block 10.0.0.0/16 \
        --query 'Vpc.VpcId' \
        --output text \
        --tag-specifications "ResourceType=vpc,Tags=[{Key=Name,Value=$PREFIX-vpc}]")
    aws ec2 modify-vpc-attribute --vpc-id "$VPC_ID" --enable-dns-hostnames
    aws ec2 modify-vpc-attribute --vpc-id "$VPC_ID" --enable-dns-support
    echo -e "${GREEN}✓ VPC created: $VPC_ID${NC}"
    
    # Create Internet Gateway
    echo ""
    echo -e "${BLUE}Step 2: Creating Internet Gateway...${NC}"
    IGW_ID=$(aws ec2 create-internet-gateway \
        --query 'InternetGateway.InternetGatewayId' \
        --output text \
        --tag-specifications "ResourceType=internet-gateway,Tags=[{Key=Name,Value=$PREFIX-igw}]")
    aws ec2 attach-internet-gateway --internet-gateway-id "$IGW_ID" --vpc-id "$VPC_ID"
    echo -e "${GREEN}✓ Internet Gateway created: $IGW_ID${NC}"
    
    # Create Subnets
    echo ""
    echo -e "${BLUE}Step 3: Creating Subnets...${NC}"
    AZ1=$(aws ec2 describe-availability-zones --query 'AvailabilityZones[0].ZoneName' --output text)
    AZ2=$(aws ec2 describe-availability-zones --query 'AvailabilityZones[1].ZoneName' --output text)
    
    PUBLIC_SUBNET_1=$(aws ec2 create-subnet \
        --vpc-id "$VPC_ID" \
        --cidr-block 10.0.1.0/24 \
        --availability-zone "$AZ1" \
        --query 'Subnet.SubnetId' \
        --output text \
        --tag-specifications "ResourceType=subnet,Tags=[{Key=Name,Value=$PREFIX-public-1}]")
    aws ec2 modify-subnet-attribute --subnet-id "$PUBLIC_SUBNET_1" --map-public-ip-on-launch
    
    PRIVATE_SUBNET_1=$(aws ec2 create-subnet \
        --vpc-id "$VPC_ID" \
        --cidr-block 10.0.10.0/24 \
        --availability-zone "$AZ1" \
        --query 'Subnet.SubnetId' \
        --output text \
        --tag-specifications "ResourceType=subnet,Tags=[{Key=Name,Value=$PREFIX-private-1}]")
    
    PRIVATE_SUBNET_2=$(aws ec2 create-subnet \
        --vpc-id "$VPC_ID" \
        --cidr-block 10.0.11.0/24 \
        --availability-zone "$AZ2" \
        --query 'Subnet.SubnetId' \
        --output text \
        --tag-specifications "ResourceType=subnet,Tags=[{Key=Name,Value=$PREFIX-private-2}]")
    
    echo -e "${GREEN}✓ Subnets created${NC}"
    
    # Create Route Table
    echo ""
    echo -e "${BLUE}Step 4: Creating Route Table...${NC}"
    RTB_ID=$(aws ec2 create-route-table \
        --vpc-id "$VPC_ID" \
        --query 'RouteTable.RouteTableId' \
        --output text \
        --tag-specifications "ResourceType=route-table,Tags=[{Key=Name,Value=$PREFIX-public-rt}]")
    aws ec2 create-route --route-table-id "$RTB_ID" --destination-cidr-block 0.0.0.0/0 --gateway-id "$IGW_ID"
    aws ec2 associate-route-table --route-table-id "$RTB_ID" --subnet-id "$PUBLIC_SUBNET_1"
    echo -e "${GREEN}✓ Route Table created${NC}"
    
    # Create Security Groups
    echo ""
    echo -e "${BLUE}Step 5: Creating Security Groups...${NC}"
    EC2_SG=$(aws ec2 create-security-group \
        --group-name "$PREFIX-ec2-sg" \
        --description "Security group for EC2" \
        --vpc-id "$VPC_ID" \
        --query 'GroupId' \
        --output text \
        --tag-specifications "ResourceType=security-group,Tags=[{Key=Name,Value=$PREFIX-ec2-sg}]")
    aws ec2 authorize-security-group-ingress --group-id "$EC2_SG" --protocol tcp --port 22 --cidr 0.0.0.0/0
    aws ec2 authorize-security-group-ingress --group-id "$EC2_SG" --protocol tcp --port 80 --cidr 0.0.0.0/0
    aws ec2 authorize-security-group-ingress --group-id "$EC2_SG" --protocol tcp --port 443 --cidr 0.0.0.0/0
    aws ec2 authorize-security-group-ingress --group-id "$EC2_SG" --protocol tcp --port 3000 --cidr 0.0.0.0/0
    
    RDS_SG=$(aws ec2 create-security-group \
        --group-name "$PREFIX-rds-sg" \
        --description "Security group for RDS" \
        --vpc-id "$VPC_ID" \
        --query 'GroupId' \
        --output text \
        --tag-specifications "ResourceType=security-group,Tags=[{Key=Name,Value=$PREFIX-rds-sg}]")
    aws ec2 authorize-security-group-ingress --group-id "$RDS_SG" --protocol tcp --port 5432 --source-group "$EC2_SG"
    echo -e "${GREEN}✓ Security Groups created${NC}"
    
    # Create S3 Bucket
    echo ""
    echo -e "${BLUE}Step 6: Creating S3 Bucket...${NC}"
    BUCKET_NAME="$PREFIX-files-$AWS_ACCOUNT_ID"
    if [ "$AWS_REGION" = "us-east-1" ]; then
        aws s3api create-bucket --bucket "$BUCKET_NAME"
    else
        aws s3api create-bucket --bucket "$BUCKET_NAME" --create-bucket-configuration LocationConstraint="$AWS_REGION"
    fi
    aws s3api put-bucket-encryption --bucket "$BUCKET_NAME" --server-side-encryption-configuration '{"Rules":[{"ApplyServerSideEncryptionByDefault":{"SSEAlgorithm":"AES256"}}]}'
    aws s3api put-public-access-block --bucket "$BUCKET_NAME" --public-access-block-configuration "BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true"
    echo -e "${GREEN}✓ S3 Bucket created: $BUCKET_NAME${NC}"
    
    # Create Cognito User Pool
    echo ""
    echo -e "${BLUE}Step 7: Creating Cognito User Pool...${NC}"
    USER_POOL_ID=$(aws cognito-idp create-user-pool \
        --pool-name "$PREFIX-user-pool" \
        --auto-verified-attributes email phone_number \
        --username-attributes email phone_number \
        --policies "PasswordPolicy={MinimumLength=8,RequireUppercase=true,RequireLowercase=true,RequireNumbers=true,RequireSymbols=false}" \
        --query 'UserPool.Id' \
        --output text)
    
    CLIENT_ID=$(aws cognito-idp create-user-pool-client \
        --user-pool-id "$USER_POOL_ID" \
        --client-name "$PREFIX-app-client" \
        --generate-secret \
        --explicit-auth-flows ALLOW_USER_PASSWORD_AUTH ALLOW_REFRESH_TOKEN_AUTH ALLOW_ADMIN_USER_PASSWORD_AUTH \
        --query 'UserPoolClient.ClientId' \
        --output text)
    
    CLIENT_SECRET=$(aws cognito-idp describe-user-pool-client \
        --user-pool-id "$USER_POOL_ID" \
        --client-id "$CLIENT_ID" \
        --query 'UserPoolClient.ClientSecret' \
        --output text)
    echo -e "${GREEN}✓ Cognito User Pool created: $USER_POOL_ID${NC}"
    
    # Create SNS Topic
    echo ""
    echo -e "${BLUE}Step 8: Creating SNS Topic...${NC}"
    SNS_TOPIC_ARN=$(aws sns create-topic \
        --name "$PREFIX-push-notifications" \
        --query 'TopicArn' \
        --output text)
    echo -e "${GREEN}✓ SNS Topic created: $SNS_TOPIC_ARN${NC}"
    
    # Create RDS Subnet Group
    echo ""
    echo -e "${BLUE}Step 9: Creating RDS...${NC}"
    aws rds create-db-subnet-group \
        --db-subnet-group-name "$PREFIX-db-subnet-group" \
        --db-subnet-group-description "Subnet group for RDS" \
        --subnet-ids "$PRIVATE_SUBNET_1" "$PRIVATE_SUBNET_2"
    
    # Create RDS Instance
    aws rds create-db-instance \
        --db-instance-identifier "$PREFIX-postgres" \
        --db-instance-class db.t3.micro \
        --engine postgres \
        --engine-version 15.4 \
        --allocated-storage 20 \
        --storage-type gp3 \
        --db-name auravoicechat \
        --master-username aura_admin \
        --master-user-password "$DB_PASSWORD" \
        --db-subnet-group-name "$PREFIX-db-subnet-group" \
        --vpc-security-group-ids "$RDS_SG" \
        --no-publicly-accessible \
        --backup-retention-period 7 \
        --storage-encrypted \
        --no-multi-az
    
    echo -e "${GREEN}✓ RDS instance creation initiated (takes 5-10 minutes)${NC}"
    
    # Save configuration
    echo ""
    echo -e "${BLUE}Saving configuration...${NC}"
    CONFIG_FILE="$PROJECT_DIR/backend/.env.aws"
    cat > "$CONFIG_FILE" << EOF
# Aura Voice Chat - AWS Configuration
# Generated by aws-setup.sh on $(date)

NODE_ENV=production
PORT=3000

# AWS Configuration
AWS_REGION=$AWS_REGION

# Cognito
COGNITO_USER_POOL_ID=$USER_POOL_ID
COGNITO_CLIENT_ID=$CLIENT_ID
COGNITO_CLIENT_SECRET=$CLIENT_SECRET

# Database (update endpoint when RDS is ready)
DB_HOST=<RDS_ENDPOINT>
DB_PORT=5432
DB_NAME=auravoicechat
DB_USER=aura_admin
DB_PASSWORD=$DB_PASSWORD

# S3
S3_BUCKET_NAME=$BUCKET_NAME
S3_BUCKET_REGION=$AWS_REGION

# SNS
SNS_TOPIC_ARN=$SNS_TOPIC_ARN

# VPC Resources (for reference)
VPC_ID=$VPC_ID
EC2_SECURITY_GROUP=$EC2_SG
PUBLIC_SUBNET=$PUBLIC_SUBNET_1
EOF

    echo -e "${GREEN}✓ Configuration saved to $CONFIG_FILE${NC}"
    
    # Summary
    echo ""
    echo -e "${GREEN}╔══════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║     AWS INFRASTRUCTURE SETUP COMPLETE                            ║${NC}"
    echo -e "${GREEN}╚══════════════════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "${CYAN}Resources Created:${NC}"
    echo "  VPC: $VPC_ID"
    echo "  Internet Gateway: $IGW_ID"
    echo "  Public Subnet: $PUBLIC_SUBNET_1"
    echo "  Private Subnets: $PRIVATE_SUBNET_1, $PRIVATE_SUBNET_2"
    echo "  EC2 Security Group: $EC2_SG"
    echo "  RDS Security Group: $RDS_SG"
    echo "  S3 Bucket: $BUCKET_NAME"
    echo "  Cognito User Pool: $USER_POOL_ID"
    echo "  Cognito Client: $CLIENT_ID"
    echo "  SNS Topic: $SNS_TOPIC_ARN"
    echo "  RDS: $PREFIX-postgres (creating...)"
    echo ""
    echo -e "${CYAN}Next Steps:${NC}"
    echo "1. Wait for RDS to be available:"
    echo "   aws rds wait db-instance-available --db-instance-identifier $PREFIX-postgres"
    echo ""
    echo "2. Get RDS endpoint:"
    echo "   aws rds describe-db-instances --db-instance-identifier $PREFIX-postgres --query 'DBInstances[0].Endpoint.Address' --output text"
    echo ""
    echo "3. Update .env.aws with RDS endpoint"
    echo ""
    echo "4. Launch EC2 instance and deploy the application"
}

# Main menu
check_prerequisites

echo -e "${CYAN}Select setup method:${NC}"
echo ""
echo "1. CloudFormation (Recommended - automated)"
echo "2. Manual (Step-by-step resource creation)"
echo "3. Exit"
echo ""
read -p "Enter choice (1-3): " CHOICE

case $CHOICE in
    1)
        setup_cloudformation
        ;;
    2)
        setup_manual
        ;;
    3)
        echo -e "${YELLOW}Setup cancelled${NC}"
        exit 0
        ;;
    *)
        echo -e "${RED}Invalid choice${NC}"
        exit 1
        ;;
esac

echo ""
echo -e "${PURPLE}Setup complete! 🚀${NC}"
