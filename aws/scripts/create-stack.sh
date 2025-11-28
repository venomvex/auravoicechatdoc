#!/bin/bash

# ============================================================================
# Aura Voice Chat - Create AWS CloudFormation Stack
# Developer: Hawkaye Visions LTD — Lahore, Pakistan
#
# Usage: ./create-stack.sh [environment] [region]
# Example: ./create-stack.sh production us-east-1
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
echo "║     🚀 AURA VOICE CHAT - AWS STACK CREATION                      ║"
echo "║                                                                  ║"
echo "║     Developer: Hawkaye Visions LTD — Lahore, Pakistan           ║"
echo "║                                                                  ║"
echo "╚══════════════════════════════════════════════════════════════════╝"
echo -e "${NC}"
echo ""

# Configuration
ENVIRONMENT=${1:-production}
AWS_REGION=${2:-us-east-1}
STACK_NAME="aura-voice-chat-${ENVIRONMENT}"
TEMPLATE_FILE="$(dirname "$0")/../cloudformation/main.yaml"

# Check AWS CLI
if ! command -v aws &> /dev/null; then
    echo -e "${RED}❌ AWS CLI is not installed. Please install it first.${NC}"
    echo "   https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html"
    exit 1
fi

# Check AWS credentials
if ! aws sts get-caller-identity &> /dev/null; then
    echo -e "${RED}❌ AWS credentials not configured. Run 'aws configure' first.${NC}"
    exit 1
fi

echo -e "${CYAN}Configuration:${NC}"
echo "  Environment: $ENVIRONMENT"
echo "  Region: $AWS_REGION"
echo "  Stack Name: $STACK_NAME"
echo ""

# Prompt for required parameters
echo -e "${CYAN}Required Parameters:${NC}"
echo ""

read -p "Database Password (min 8 chars): " -s DB_PASSWORD
echo ""
if [ ${#DB_PASSWORD} -lt 8 ]; then
    echo -e "${RED}❌ Password must be at least 8 characters${NC}"
    exit 1
fi

read -p "Admin Email: " ADMIN_EMAIL
if [[ ! "$ADMIN_EMAIL" =~ ^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$ ]]; then
    echo -e "${RED}❌ Invalid email format${NC}"
    exit 1
fi

# List available key pairs
echo ""
echo -e "${CYAN}Available EC2 Key Pairs:${NC}"
aws ec2 describe-key-pairs --region "$AWS_REGION" --query 'KeyPairs[*].KeyName' --output table

read -p "Enter Key Pair Name: " KEY_PAIR_NAME
if [ -z "$KEY_PAIR_NAME" ]; then
    echo -e "${RED}❌ Key pair name is required${NC}"
    exit 1
fi

# Confirm deployment
echo ""
echo -e "${YELLOW}Ready to deploy stack with:${NC}"
echo "  Stack Name: $STACK_NAME"
echo "  Region: $AWS_REGION"
echo "  Environment: $ENVIRONMENT"
echo "  Admin Email: $ADMIN_EMAIL"
echo "  Key Pair: $KEY_PAIR_NAME"
echo ""
read -p "Proceed with deployment? (y/n): " CONFIRM
if [ "$CONFIRM" != "y" ] && [ "$CONFIRM" != "Y" ]; then
    echo -e "${YELLOW}Deployment cancelled${NC}"
    exit 0
fi

# Check if template exists
if [ ! -f "$TEMPLATE_FILE" ]; then
    echo -e "${RED}❌ Template file not found: $TEMPLATE_FILE${NC}"
    exit 1
fi

# Validate template
echo ""
echo -e "${CYAN}Validating CloudFormation template...${NC}"
aws cloudformation validate-template \
    --template-body "file://$TEMPLATE_FILE" \
    --region "$AWS_REGION" > /dev/null

echo -e "${GREEN}✓ Template validated${NC}"

# Create stack
echo ""
echo -e "${CYAN}Creating CloudFormation stack...${NC}"
aws cloudformation create-stack \
    --stack-name "$STACK_NAME" \
    --template-body "file://$TEMPLATE_FILE" \
    --parameters \
        ParameterKey=Environment,ParameterValue="$ENVIRONMENT" \
        ParameterKey=ProjectName,ParameterValue="aura-voice-chat" \
        ParameterKey=DBPassword,ParameterValue="$DB_PASSWORD" \
        ParameterKey=AdminEmail,ParameterValue="$ADMIN_EMAIL" \
        ParameterKey=KeyPairName,ParameterValue="$KEY_PAIR_NAME" \
    --capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM \
    --region "$AWS_REGION" \
    --tags \
        Key=Environment,Value="$ENVIRONMENT" \
        Key=Project,Value="aura-voice-chat" \
        Key=ManagedBy,Value="CloudFormation"

echo -e "${GREEN}✓ Stack creation initiated${NC}"
echo ""

# Wait for stack creation
echo -e "${CYAN}Waiting for stack creation (this may take 15-20 minutes)...${NC}"
aws cloudformation wait stack-create-complete \
    --stack-name "$STACK_NAME" \
    --region "$AWS_REGION"

echo -e "${GREEN}✓ Stack created successfully!${NC}"
echo ""

# Get outputs
echo -e "${CYAN}Stack Outputs:${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
aws cloudformation describe-stacks \
    --stack-name "$STACK_NAME" \
    --region "$AWS_REGION" \
    --query 'Stacks[0].Outputs[*].[OutputKey,OutputValue]' \
    --output table

echo ""
echo -e "${GREEN}╔══════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║                                                                  ║${NC}"
echo -e "${GREEN}║     🎉 AWS INFRASTRUCTURE DEPLOYED SUCCESSFULLY!                 ║${NC}"
echo -e "${GREEN}║                                                                  ║${NC}"
echo -e "${GREEN}╚══════════════════════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "${CYAN}Next Steps:${NC}"
echo "1. SSH into EC2 instance to deploy the application"
echo "2. Configure Cognito Client Secret in backend .env"
echo "3. Set up SSL with Let's Encrypt"
echo "4. Configure SNS Platform Application for push notifications"
echo ""
echo -e "${PURPLE}Happy deploying! 🚀${NC}"
