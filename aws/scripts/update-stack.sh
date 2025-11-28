#!/bin/bash

# ============================================================================
# Aura Voice Chat - Update AWS CloudFormation Stack
# Developer: Hawkaye Visions LTD — Lahore, Pakistan
#
# Usage: ./update-stack.sh [environment] [region]
# ============================================================================

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
PURPLE='\033[0;35m'
NC='\033[0m'

echo -e "${PURPLE}"
echo "╔══════════════════════════════════════════════════════════════════╗"
echo "║                                                                  ║"
echo "║     🔄 AURA VOICE CHAT - AWS STACK UPDATE                        ║"
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
    echo -e "${RED}❌ AWS CLI is not installed.${NC}"
    exit 1
fi

# Check if template exists
if [ ! -f "$TEMPLATE_FILE" ]; then
    echo -e "${RED}❌ Template file not found: $TEMPLATE_FILE${NC}"
    exit 1
fi

# Check if stack exists
if ! aws cloudformation describe-stacks --stack-name "$STACK_NAME" --region "$AWS_REGION" &> /dev/null; then
    echo -e "${RED}❌ Stack '$STACK_NAME' does not exist in region '$AWS_REGION'${NC}"
    echo "   Run create-stack.sh first"
    exit 1
fi

echo -e "${CYAN}Configuration:${NC}"
echo "  Stack Name: $STACK_NAME"
echo "  Region: $AWS_REGION"
echo ""

# Validate template
echo -e "${CYAN}Validating CloudFormation template...${NC}"
aws cloudformation validate-template \
    --template-body "file://$TEMPLATE_FILE" \
    --region "$AWS_REGION" > /dev/null
echo -e "${GREEN}✓ Template validated${NC}"
echo ""

# Create change set
CHANGE_SET_NAME="update-$(date +%Y%m%d%H%M%S)"

echo -e "${CYAN}Creating change set...${NC}"
aws cloudformation create-change-set \
    --stack-name "$STACK_NAME" \
    --template-body "file://$TEMPLATE_FILE" \
    --change-set-name "$CHANGE_SET_NAME" \
    --use-previous-template false \
    --capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM \
    --region "$AWS_REGION" > /dev/null

# Wait for change set
aws cloudformation wait change-set-create-complete \
    --stack-name "$STACK_NAME" \
    --change-set-name "$CHANGE_SET_NAME" \
    --region "$AWS_REGION" 2>/dev/null || true

# Describe changes
echo ""
echo -e "${CYAN}Proposed Changes:${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
aws cloudformation describe-change-set \
    --stack-name "$STACK_NAME" \
    --change-set-name "$CHANGE_SET_NAME" \
    --region "$AWS_REGION" \
    --query 'Changes[*].ResourceChange.[Action,LogicalResourceId,ResourceType]' \
    --output table

# Check if there are changes
CHANGE_COUNT=$(aws cloudformation describe-change-set \
    --stack-name "$STACK_NAME" \
    --change-set-name "$CHANGE_SET_NAME" \
    --region "$AWS_REGION" \
    --query 'length(Changes)' \
    --output text)

if [ "$CHANGE_COUNT" == "0" ] || [ "$CHANGE_COUNT" == "None" ]; then
    echo ""
    echo -e "${YELLOW}No changes detected. Stack is up to date.${NC}"
    aws cloudformation delete-change-set \
        --stack-name "$STACK_NAME" \
        --change-set-name "$CHANGE_SET_NAME" \
        --region "$AWS_REGION"
    exit 0
fi

echo ""
read -p "Apply these changes? (y/n): " CONFIRM
if [ "$CONFIRM" != "y" ] && [ "$CONFIRM" != "Y" ]; then
    echo -e "${YELLOW}Update cancelled. Deleting change set.${NC}"
    aws cloudformation delete-change-set \
        --stack-name "$STACK_NAME" \
        --change-set-name "$CHANGE_SET_NAME" \
        --region "$AWS_REGION"
    exit 0
fi

# Execute change set
echo ""
echo -e "${CYAN}Executing change set...${NC}"
aws cloudformation execute-change-set \
    --stack-name "$STACK_NAME" \
    --change-set-name "$CHANGE_SET_NAME" \
    --region "$AWS_REGION"

# Wait for update
echo -e "${CYAN}Waiting for stack update...${NC}"
aws cloudformation wait stack-update-complete \
    --stack-name "$STACK_NAME" \
    --region "$AWS_REGION"

echo ""
echo -e "${GREEN}✓ Stack updated successfully!${NC}"
echo ""

# Get outputs
echo -e "${CYAN}Stack Outputs:${NC}"
aws cloudformation describe-stacks \
    --stack-name "$STACK_NAME" \
    --region "$AWS_REGION" \
    --query 'Stacks[0].Outputs[*].[OutputKey,OutputValue]' \
    --output table

echo ""
echo -e "${PURPLE}Update complete! 🚀${NC}"
