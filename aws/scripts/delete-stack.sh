#!/bin/bash

# ============================================================================
# Aura Voice Chat - Delete AWS CloudFormation Stack
# Developer: Hawkaye Visions LTD — Lahore, Pakistan
#
# Usage: ./delete-stack.sh [environment] [region]
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
echo "║     ⚠️  AURA VOICE CHAT - AWS STACK DELETION                     ║"
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

# Check AWS CLI
if ! command -v aws &> /dev/null; then
    echo -e "${RED}❌ AWS CLI is not installed.${NC}"
    exit 1
fi

# Check if stack exists
if ! aws cloudformation describe-stacks --stack-name "$STACK_NAME" --region "$AWS_REGION" &> /dev/null; then
    echo -e "${YELLOW}Stack '$STACK_NAME' does not exist in region '$AWS_REGION'${NC}"
    exit 0
fi

echo -e "${RED}WARNING: This will delete all resources in the stack!${NC}"
echo ""
echo "Stack: $STACK_NAME"
echo "Region: $AWS_REGION"
echo ""

# List resources to be deleted
echo -e "${CYAN}Resources to be deleted:${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
aws cloudformation list-stack-resources \
    --stack-name "$STACK_NAME" \
    --region "$AWS_REGION" \
    --query 'StackResourceSummaries[*].[ResourceType,LogicalResourceId,ResourceStatus]' \
    --output table

echo ""
echo -e "${RED}This action cannot be undone!${NC}"
read -p "Type 'DELETE' to confirm: " CONFIRM

if [ "$CONFIRM" != "DELETE" ]; then
    echo -e "${YELLOW}Deletion cancelled${NC}"
    exit 0
fi

# Get S3 bucket name
S3_BUCKET=$(aws cloudformation describe-stacks \
    --stack-name "$STACK_NAME" \
    --region "$AWS_REGION" \
    --query "Stacks[0].Outputs[?OutputKey=='S3BucketName'].OutputValue" \
    --output text 2>/dev/null || echo "")

# Empty S3 bucket if exists
if [ -n "$S3_BUCKET" ] && [ "$S3_BUCKET" != "None" ]; then
    echo ""
    echo -e "${CYAN}Emptying S3 bucket: $S3_BUCKET${NC}"
    aws s3 rm "s3://$S3_BUCKET" --recursive --region "$AWS_REGION" 2>/dev/null || true
    echo -e "${GREEN}✓ S3 bucket emptied${NC}"
fi

# Delete stack
echo ""
echo -e "${CYAN}Deleting CloudFormation stack...${NC}"
aws cloudformation delete-stack \
    --stack-name "$STACK_NAME" \
    --region "$AWS_REGION"

# Wait for deletion
echo -e "${CYAN}Waiting for stack deletion (this may take several minutes)...${NC}"
aws cloudformation wait stack-delete-complete \
    --stack-name "$STACK_NAME" \
    --region "$AWS_REGION"

echo ""
echo -e "${GREEN}✓ Stack deleted successfully!${NC}"
echo ""
echo -e "${PURPLE}All AWS resources have been removed.${NC}"
