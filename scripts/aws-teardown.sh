#!/bin/bash

# ============================================================================
# Aura Voice Chat - AWS Infrastructure Teardown
# Developer: Hawkaye Visions LTD — Lahore, Pakistan
#
# This script removes all AWS resources created for Aura Voice Chat.
#
# Usage: ./aws-teardown.sh [--cloudformation | --manual]
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
echo "║     ⚠️  AURA VOICE CHAT - AWS INFRASTRUCTURE TEARDOWN           ║"
echo "║                                                                  ║"
echo "║     Developer: Hawkaye Visions LTD — Lahore, Pakistan           ║"
echo "║                                                                  ║"
echo "╚══════════════════════════════════════════════════════════════════╝"
echo -e "${NC}"
echo ""

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# Check AWS CLI
if ! command -v aws &> /dev/null; then
    echo -e "${RED}❌ AWS CLI is not installed${NC}"
    exit 1
fi

# Check AWS Credentials
if ! aws sts get-caller-identity &> /dev/null; then
    echo -e "${RED}❌ AWS credentials not configured${NC}"
    exit 1
fi

AWS_REGION=$(aws configure get region || echo "us-east-1")
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)

echo -e "${RED}WARNING: This will delete all AWS resources for Aura Voice Chat!${NC}"
echo ""
echo "AWS Account: $AWS_ACCOUNT_ID"
echo "AWS Region: $AWS_REGION"
echo ""

# Check for CloudFormation stack
STACK_NAME="aura-voice-chat-production"
if aws cloudformation describe-stacks --stack-name "$STACK_NAME" --region "$AWS_REGION" &> /dev/null; then
    echo -e "${CYAN}CloudFormation stack detected: $STACK_NAME${NC}"
    echo ""
    read -p "Delete CloudFormation stack? (y/n): " DELETE_CF
    
    if [ "$DELETE_CF" = "y" ] || [ "$DELETE_CF" = "Y" ]; then
        bash "$PROJECT_DIR/aws/scripts/delete-stack.sh" production "$AWS_REGION"
        exit 0
    fi
fi

# Manual resource deletion
echo ""
echo -e "${CYAN}Manual Resource Deletion${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

PREFIX="aura-voice-chat"

echo -e "${RED}This will delete the following resources:${NC}"
echo ""

# List resources
echo "1. EC2 Instances with Name=$PREFIX*"
aws ec2 describe-instances \
    --filters "Name=tag:Name,Values=$PREFIX*" \
    --query 'Reservations[*].Instances[*].[InstanceId,Tags[?Key==`Name`].Value|[0],State.Name]' \
    --output table 2>/dev/null || echo "   No EC2 instances found"

echo ""
echo "2. RDS Instances with identifier=$PREFIX*"
aws rds describe-db-instances \
    --query "DBInstances[?starts_with(DBInstanceIdentifier, '$PREFIX')].[DBInstanceIdentifier,DBInstanceStatus]" \
    --output table 2>/dev/null || echo "   No RDS instances found"

echo ""
echo "3. S3 Buckets with name=$PREFIX*"
aws s3 ls | grep "$PREFIX" || echo "   No S3 buckets found"

echo ""
echo "4. Cognito User Pools with name=$PREFIX*"
aws cognito-idp list-user-pools --max-results 20 \
    --query "UserPools[?starts_with(Name, '$PREFIX')].[Id,Name]" \
    --output table 2>/dev/null || echo "   No Cognito User Pools found"

echo ""
echo "5. SNS Topics with name=$PREFIX*"
aws sns list-topics --query "Topics[?contains(TopicArn, '$PREFIX')]" --output table 2>/dev/null || echo "   No SNS Topics found"

echo ""
echo -e "${RED}This action cannot be undone!${NC}"
read -p "Type 'DELETE ALL' to confirm: " CONFIRM

if [ "$CONFIRM" != "DELETE ALL" ]; then
    echo -e "${YELLOW}Teardown cancelled${NC}"
    exit 0
fi

echo ""
echo -e "${CYAN}Starting teardown...${NC}"

# Delete EC2 Instances
echo ""
echo -e "${BLUE}Deleting EC2 Instances...${NC}"
INSTANCE_IDS=$(aws ec2 describe-instances \
    --filters "Name=tag:Name,Values=$PREFIX*" "Name=instance-state-name,Values=running,stopped" \
    --query 'Reservations[*].Instances[*].InstanceId' \
    --output text)
if [ -n "$INSTANCE_IDS" ]; then
    aws ec2 terminate-instances --instance-ids $INSTANCE_IDS
    echo "Waiting for instances to terminate..."
    aws ec2 wait instance-terminated --instance-ids $INSTANCE_IDS 2>/dev/null || true
    echo -e "${GREEN}✓ EC2 Instances terminated${NC}"
else
    echo "No EC2 instances to delete"
fi

# Delete RDS Instances
echo ""
echo -e "${BLUE}Deleting RDS Instances...${NC}"
RDS_INSTANCES=$(aws rds describe-db-instances \
    --query "DBInstances[?starts_with(DBInstanceIdentifier, '$PREFIX')].DBInstanceIdentifier" \
    --output text)
for RDS_ID in $RDS_INSTANCES; do
    aws rds delete-db-instance \
        --db-instance-identifier "$RDS_ID" \
        --skip-final-snapshot \
        --delete-automated-backups 2>/dev/null || true
    echo "Deleting $RDS_ID (this takes several minutes)..."
done
if [ -n "$RDS_INSTANCES" ]; then
    echo -e "${GREEN}✓ RDS deletion initiated${NC}"
else
    echo "No RDS instances to delete"
fi

# Delete S3 Buckets
echo ""
echo -e "${BLUE}Deleting S3 Buckets...${NC}"
BUCKETS=$(aws s3 ls | grep "$PREFIX" | awk '{print $3}')
for BUCKET in $BUCKETS; do
    echo "Emptying and deleting $BUCKET..."
    aws s3 rm "s3://$BUCKET" --recursive 2>/dev/null || true
    aws s3api delete-bucket --bucket "$BUCKET" 2>/dev/null || true
done
if [ -n "$BUCKETS" ]; then
    echo -e "${GREEN}✓ S3 Buckets deleted${NC}"
else
    echo "No S3 buckets to delete"
fi

# Delete Cognito User Pools
echo ""
echo -e "${BLUE}Deleting Cognito User Pools...${NC}"
USER_POOL_IDS=$(aws cognito-idp list-user-pools --max-results 60 \
    --query "UserPools[?starts_with(Name, '$PREFIX')].Id" \
    --output text)
for POOL_ID in $USER_POOL_IDS; do
    # First delete all clients
    CLIENT_IDS=$(aws cognito-idp list-user-pool-clients --user-pool-id "$POOL_ID" \
        --query 'UserPoolClients[*].ClientId' --output text)
    for CLIENT_ID in $CLIENT_IDS; do
        aws cognito-idp delete-user-pool-client --user-pool-id "$POOL_ID" --client-id "$CLIENT_ID" 2>/dev/null || true
    done
    
    # Delete domain if exists
    aws cognito-idp delete-user-pool-domain --user-pool-id "$POOL_ID" --domain "$PREFIX" 2>/dev/null || true
    
    # Delete the pool
    aws cognito-idp delete-user-pool --user-pool-id "$POOL_ID" 2>/dev/null || true
    echo "Deleted User Pool: $POOL_ID"
done
if [ -n "$USER_POOL_IDS" ]; then
    echo -e "${GREEN}✓ Cognito User Pools deleted${NC}"
else
    echo "No Cognito User Pools to delete"
fi

# Delete SNS Topics
echo ""
echo -e "${BLUE}Deleting SNS Topics...${NC}"
TOPIC_ARNS=$(aws sns list-topics --query "Topics[?contains(TopicArn, '$PREFIX')].TopicArn" --output text)
for TOPIC_ARN in $TOPIC_ARNS; do
    aws sns delete-topic --topic-arn "$TOPIC_ARN" 2>/dev/null || true
    echo "Deleted Topic: $TOPIC_ARN"
done
if [ -n "$TOPIC_ARNS" ]; then
    echo -e "${GREEN}✓ SNS Topics deleted${NC}"
else
    echo "No SNS topics to delete"
fi

# Delete Security Groups
echo ""
echo -e "${BLUE}Deleting Security Groups...${NC}"
SG_IDS=$(aws ec2 describe-security-groups \
    --filters "Name=tag:Name,Values=$PREFIX*" \
    --query 'SecurityGroups[*].GroupId' \
    --output text)
for SG_ID in $SG_IDS; do
    aws ec2 delete-security-group --group-id "$SG_ID" 2>/dev/null || true
    echo "Deleted Security Group: $SG_ID"
done

# Delete RDS Subnet Groups
echo ""
echo -e "${BLUE}Deleting RDS Subnet Groups...${NC}"
aws rds delete-db-subnet-group --db-subnet-group-name "$PREFIX-db-subnet-group" 2>/dev/null || true

# Delete Subnets
echo ""
echo -e "${BLUE}Deleting Subnets...${NC}"
SUBNET_IDS=$(aws ec2 describe-subnets \
    --filters "Name=tag:Name,Values=$PREFIX*" \
    --query 'Subnets[*].SubnetId' \
    --output text)
for SUBNET_ID in $SUBNET_IDS; do
    aws ec2 delete-subnet --subnet-id "$SUBNET_ID" 2>/dev/null || true
    echo "Deleted Subnet: $SUBNET_ID"
done

# Delete Route Tables
echo ""
echo -e "${BLUE}Deleting Route Tables...${NC}"
RTB_IDS=$(aws ec2 describe-route-tables \
    --filters "Name=tag:Name,Values=$PREFIX*" \
    --query 'RouteTables[*].RouteTableId' \
    --output text)
for RTB_ID in $RTB_IDS; do
    # Disassociate first
    ASSOC_IDS=$(aws ec2 describe-route-tables --route-table-id "$RTB_ID" \
        --query 'RouteTables[0].Associations[*].RouteTableAssociationId' --output text)
    for ASSOC_ID in $ASSOC_IDS; do
        aws ec2 disassociate-route-table --association-id "$ASSOC_ID" 2>/dev/null || true
    done
    aws ec2 delete-route-table --route-table-id "$RTB_ID" 2>/dev/null || true
    echo "Deleted Route Table: $RTB_ID"
done

# Delete Internet Gateways
echo ""
echo -e "${BLUE}Deleting Internet Gateways...${NC}"
IGW_IDS=$(aws ec2 describe-internet-gateways \
    --filters "Name=tag:Name,Values=$PREFIX*" \
    --query 'InternetGateways[*].[InternetGatewayId,Attachments[0].VpcId]' \
    --output text)
while read -r IGW_ID VPC_ID; do
    if [ -n "$VPC_ID" ]; then
        aws ec2 detach-internet-gateway --internet-gateway-id "$IGW_ID" --vpc-id "$VPC_ID" 2>/dev/null || true
    fi
    aws ec2 delete-internet-gateway --internet-gateway-id "$IGW_ID" 2>/dev/null || true
    echo "Deleted Internet Gateway: $IGW_ID"
done <<< "$IGW_IDS"

# Delete VPCs
echo ""
echo -e "${BLUE}Deleting VPCs...${NC}"
VPC_IDS=$(aws ec2 describe-vpcs \
    --filters "Name=tag:Name,Values=$PREFIX*" \
    --query 'Vpcs[*].VpcId' \
    --output text)
for VPC_ID in $VPC_IDS; do
    aws ec2 delete-vpc --vpc-id "$VPC_ID" 2>/dev/null || true
    echo "Deleted VPC: $VPC_ID"
done

# Summary
echo ""
echo -e "${GREEN}╔══════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║     AWS INFRASTRUCTURE TEARDOWN COMPLETE                         ║${NC}"
echo -e "${GREEN}╚══════════════════════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "${YELLOW}Note: Some resources like RDS may take several minutes to fully delete.${NC}"
echo -e "${YELLOW}You can check the status in the AWS Console.${NC}"
echo ""
echo -e "${PURPLE}All AWS resources have been removed. 🧹${NC}"
