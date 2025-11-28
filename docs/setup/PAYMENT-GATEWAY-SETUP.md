# Payment Gateway Setup Guide

Complete guide for integrating payment gateways and withdrawal methods in Aura Voice Chat.

**Developer:** Hawkaye Visions LTD — Lahore, Pakistan

---

## Overview

Aura Voice Chat requires two types of payment integrations:

1. **In-App Purchases (Recharge)** — Users buy coins with real money
2. **Payouts (Withdrawal)** — Users withdraw earnings to external accounts

---

## Part 1: In-App Purchases (Google Play Billing)

### 1.1 Prerequisites

- Google Play Developer Account ($25 one-time fee)
- Published app on Play Store (at least internal testing)
- Merchant account linked to Play Console

### 1.2 Create In-App Products

1. Go to [Play Console](https://play.google.com/console)
2. Select your app → Monetize → Products → In-app products
3. Click "Create product"

**Coin Packages to Create:**

| Product ID | Name | Price | Coins |
|------------|------|-------|-------|
| `coins_starter` | Starter Pack | $0.99 | 80,000 |
| `coins_basic` | Basic Pack | $4.99 | 450,000 |
| `coins_standard` | Standard Pack | $9.99 | 950,000 |
| `coins_premium` | Premium Pack | $24.99 | 2,600,000 |
| `coins_elite` | Elite Pack | $49.99 | 5,500,000 |
| `coins_ultimate` | Ultimate Pack | $99.99 | 12,000,000 |

### 1.3 Android Integration

**Add dependency in `build.gradle` (app):**

```groovy
dependencies {
    implementation 'com.android.billingclient:billing-ktx:6.1.0'
}
```

**BillingManager.kt:**

```kotlin
import com.android.billingclient.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BillingManager(
    private val context: Context,
    private val onPurchaseComplete: (Purchase) -> Unit
) : PurchasesUpdatedListener {
    
    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()
    
    private val _products = MutableStateFlow<List<ProductDetails>>(emptyList())
    val products: StateFlow<List<ProductDetails>> = _products
    
    fun connect() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProducts()
                }
            }
            
            override fun onBillingServiceDisconnected() {
                // Retry connection
            }
        })
    }
    
    private fun queryProducts() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("coins_starter")
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("coins_basic")
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            // Add all products...
        )
        
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()
        
        billingClient.queryProductDetailsAsync(params) { result, productDetailsList ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                _products.value = productDetailsList
            }
        }
    }
    
    fun launchPurchaseFlow(activity: Activity, productDetails: ProductDetails) {
        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .build()
        
        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()
        
        billingClient.launchBillingFlow(activity, flowParams)
    }
    
    override fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        }
    }
    
    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            // Verify purchase on server
            onPurchaseComplete(purchase)
            
            // Acknowledge purchase
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            
            billingClient.acknowledgePurchase(acknowledgePurchaseParams) { }
        }
    }
}
```

### 1.4 Server-Side Verification

**Backend endpoint to verify purchases:**

```typescript
// routes/purchases.ts
import { google } from 'googleapis';

router.post('/verify', async (req, res) => {
  const { purchaseToken, productId, packageName } = req.body;
  
  try {
    const auth = new google.auth.GoogleAuth({
      keyFile: 'service-account.json',
      scopes: ['https://www.googleapis.com/auth/androidpublisher']
    });
    
    const androidpublisher = google.androidpublisher({ version: 'v3', auth });
    
    const result = await androidpublisher.purchases.products.get({
      packageName,
      productId,
      token: purchaseToken
    });
    
    if (result.data.purchaseState === 0) { // Purchased
      // Credit coins to user
      const coins = getCoinsForProduct(productId);
      await creditCoins(req.user.id, coins);
      
      res.json({ success: true, coinsAdded: coins });
    } else {
      res.status(400).json({ error: 'Invalid purchase' });
    }
  } catch (error) {
    res.status(500).json({ error: 'Verification failed' });
  }
});

function getCoinsForProduct(productId: string): number {
  const products: Record<string, number> = {
    'coins_starter': 80000,
    'coins_basic': 450000,
    'coins_standard': 950000,
    'coins_premium': 2600000,
    'coins_elite': 5500000,
    'coins_ultimate': 12000000
  };
  return products[productId] || 0;
}
```

---

## Part 2: Payout Integration (Withdrawals)

### 2.1 Pakistan — EasyPaisa

**API Documentation:** https://easypay.easypaisa.com.pk/docs

**Integration Steps:**

1. Register as merchant at EasyPaisa
2. Obtain API credentials (username, password, storeId)
3. Implement payout API

```typescript
// services/easypaisa.ts
import axios from 'axios';

interface EasyPaisaPayoutRequest {
  amount: number;
  mobileNumber: string;
  orderId: string;
}

export async function sendEasyPaisaPayout(request: EasyPaisaPayoutRequest) {
  const { EASYPAISA_USERNAME, EASYPAISA_PASSWORD, EASYPAISA_STORE_ID } = process.env;
  
  const payload = {
    orderId: request.orderId,
    storeId: EASYPAISA_STORE_ID,
    transactionAmount: request.amount.toFixed(2),
    transactionType: 'MA', // Mobile Account
    mobileAccountNo: request.mobileNumber,
    emailAddress: ''
  };
  
  const response = await axios.post(
    'https://easypay.easypaisa.com.pk/easypay/Index.jsf',
    payload,
    {
      auth: {
        username: EASYPAISA_USERNAME!,
        password: EASYPAISA_PASSWORD!
      }
    }
  );
  
  return response.data;
}
```

### 2.2 Pakistan — JazzCash

**API Documentation:** https://sandbox.jazzcash.com.pk/apis/

**Integration Steps:**

1. Register at JazzCash Business
2. Obtain Merchant ID, Password, Integrity Salt
3. Implement payout API

```typescript
// services/jazzcash.ts
import crypto from 'crypto';
import axios from 'axios';

interface JazzCashPayoutRequest {
  amount: number;
  mobileNumber: string;
  txnRefNo: string;
}

export async function sendJazzCashPayout(request: JazzCashPayoutRequest) {
  const { JAZZCASH_MERCHANT_ID, JAZZCASH_PASSWORD, JAZZCASH_SALT } = process.env;
  
  const txnDateTime = new Date().toISOString().replace(/[-:T]/g, '').slice(0, 14);
  const txnExpiryDateTime = new Date(Date.now() + 3600000).toISOString().replace(/[-:T]/g, '').slice(0, 14);
  
  const payload: Record<string, string> = {
    pp_MerchantID: JAZZCASH_MERCHANT_ID!,
    pp_Password: JAZZCASH_PASSWORD!,
    pp_TxnRefNo: request.txnRefNo,
    pp_Amount: (request.amount * 100).toString(), // In paisa
    pp_TxnCurrency: 'PKR',
    pp_TxnDateTime: txnDateTime,
    pp_TxnExpiryDateTime: txnExpiryDateTime,
    pp_MobileNumber: request.mobileNumber,
    pp_CNIC: '',
    pp_SecureHash: ''
  };
  
  // Generate secure hash
  const sortedKeys = Object.keys(payload).sort();
  const hashString = JAZZCASH_SALT + '&' + sortedKeys.map(k => payload[k]).join('&');
  payload.pp_SecureHash = crypto.createHmac('sha256', JAZZCASH_SALT!)
    .update(hashString)
    .digest('hex')
    .toUpperCase();
  
  const response = await axios.post(
    'https://sandbox.jazzcash.com.pk/ApplicationAPI/API/2.0/Purchase/DoMWalletTransaction',
    payload
  );
  
  return response.data;
}
```

### 2.3 India — UPI via Razorpay

**API Documentation:** https://razorpay.com/docs/payments/payouts/

**Integration Steps:**

1. Create Razorpay account and enable Payout feature
2. Obtain API Key and Secret
3. Create fund account and process payout

```typescript
// services/razorpay.ts
import Razorpay from 'razorpay';

const razorpay = new Razorpay({
  key_id: process.env.RAZORPAY_KEY_ID!,
  key_secret: process.env.RAZORPAY_KEY_SECRET!
});

interface UPIPayoutRequest {
  amount: number;
  upiId: string;
  name: string;
  referenceId: string;
}

export async function sendUPIPayout(request: UPIPayoutRequest) {
  // Create fund account
  const fundAccount = await razorpay.fundAccount.create({
    contact_id: request.referenceId,
    account_type: 'vpa',
    vpa: {
      address: request.upiId
    }
  });
  
  // Create payout
  const payout = await razorpay.payouts.create({
    account_number: process.env.RAZORPAY_ACCOUNT_NUMBER!,
    fund_account_id: fundAccount.id,
    amount: request.amount * 100, // In paise
    currency: 'INR',
    mode: 'UPI',
    purpose: 'payout',
    queue_if_low_balance: true,
    reference_id: request.referenceId,
    narration: 'Aura Earnings Withdrawal'
  });
  
  return payout;
}
```

### 2.4 International — PayPal Payouts

**API Documentation:** https://developer.paypal.com/docs/payouts/

**Integration Steps:**

1. Create PayPal Business account
2. Enable Payouts in Developer Dashboard
3. Obtain Client ID and Secret

```typescript
// services/paypal.ts
import axios from 'axios';

const PAYPAL_API = process.env.PAYPAL_ENV === 'production'
  ? 'https://api-m.paypal.com'
  : 'https://api-m.sandbox.paypal.com';

async function getAccessToken(): Promise<string> {
  const auth = Buffer.from(
    `${process.env.PAYPAL_CLIENT_ID}:${process.env.PAYPAL_CLIENT_SECRET}`
  ).toString('base64');
  
  const response = await axios.post(
    `${PAYPAL_API}/v1/oauth2/token`,
    'grant_type=client_credentials',
    {
      headers: {
        Authorization: `Basic ${auth}`,
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    }
  );
  
  return response.data.access_token;
}

interface PayPalPayoutRequest {
  amount: number;
  email: string;
  senderBatchId: string;
}

export async function sendPayPalPayout(request: PayPalPayoutRequest) {
  const accessToken = await getAccessToken();
  
  const payload = {
    sender_batch_header: {
      sender_batch_id: request.senderBatchId,
      email_subject: 'You have received a payment from Aura Voice Chat',
      email_message: 'Your earnings withdrawal has been processed.'
    },
    items: [
      {
        recipient_type: 'EMAIL',
        amount: {
          value: request.amount.toFixed(2),
          currency: 'USD'
        },
        receiver: request.email,
        note: 'Aura Earnings Withdrawal',
        sender_item_id: request.senderBatchId
      }
    ]
  };
  
  const response = await axios.post(
    `${PAYPAL_API}/v1/payments/payouts`,
    payload,
    {
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      }
    }
  );
  
  return response.data;
}
```

### 2.5 International — Payoneer

**API Documentation:** https://payoneer.com/developers/

**Note:** Payoneer requires business verification and manual setup.

```typescript
// services/payoneer.ts
import axios from 'axios';

const PAYONEER_API = 'https://api.payoneer.com/v4/programs/{program_id}';

interface PayoneerPayoutRequest {
  amount: number;
  payeeId: string;
  clientReferenceId: string;
}

export async function sendPayoneerPayout(request: PayoneerPayoutRequest) {
  const response = await axios.post(
    `${PAYONEER_API}/payouts`,
    {
      payee_id: request.payeeId,
      client_reference_id: request.clientReferenceId,
      amount: request.amount,
      currency: 'USD',
      description: 'Aura Earnings Withdrawal'
    },
    {
      headers: {
        Authorization: `Bearer ${process.env.PAYONEER_API_KEY}`,
        'Content-Type': 'application/json'
      }
    }
  );
  
  return response.data;
}
```

---

## Part 3: Unified Withdrawal Service

### 3.1 Withdrawal Request Handler

```typescript
// services/withdrawal.ts
import { sendEasyPaisaPayout } from './easypaisa';
import { sendJazzCashPayout } from './jazzcash';
import { sendUPIPayout } from './razorpay';
import { sendPayPalPayout } from './paypal';
import { sendPayoneerPayout } from './payoneer';

interface WithdrawalRequest {
  userId: string;
  amount: number;
  currency: string;
  method: 'easypaisa' | 'jazzcash' | 'upi' | 'paypal' | 'payoneer' | 'bank';
  destination: {
    mobileNumber?: string;
    upiId?: string;
    email?: string;
    payeeId?: string;
    bankAccount?: {
      accountNumber: string;
      ifsc?: string;
      swift?: string;
      bankName: string;
    };
  };
}

interface WithdrawalResult {
  success: boolean;
  transactionId: string;
  status: 'pending' | 'processing' | 'completed' | 'failed';
  estimatedArrival?: string;
  error?: string;
}

export async function processWithdrawal(request: WithdrawalRequest): Promise<WithdrawalResult> {
  const txnId = generateTransactionId();
  
  try {
    // Validate user has sufficient balance
    const user = await getUser(request.userId);
    if (user.earningBalance < request.amount) {
      throw new Error('Insufficient balance');
    }
    
    // Apply withdrawal fee
    const fee = calculateWithdrawalFee(request.method, request.amount);
    const netAmount = request.amount - fee;
    
    // Process based on method
    let result;
    switch (request.method) {
      case 'easypaisa':
        result = await sendEasyPaisaPayout({
          amount: netAmount,
          mobileNumber: request.destination.mobileNumber!,
          orderId: txnId
        });
        break;
        
      case 'jazzcash':
        result = await sendJazzCashPayout({
          amount: netAmount,
          mobileNumber: request.destination.mobileNumber!,
          txnRefNo: txnId
        });
        break;
        
      case 'upi':
        result = await sendUPIPayout({
          amount: netAmount,
          upiId: request.destination.upiId!,
          name: user.name,
          referenceId: txnId
        });
        break;
        
      case 'paypal':
        result = await sendPayPalPayout({
          amount: netAmount,
          email: request.destination.email!,
          senderBatchId: txnId
        });
        break;
        
      case 'payoneer':
        result = await sendPayoneerPayout({
          amount: netAmount,
          payeeId: request.destination.payeeId!,
          clientReferenceId: txnId
        });
        break;
        
      default:
        throw new Error('Unsupported withdrawal method');
    }
    
    // Deduct from user balance
    await deductEarningBalance(request.userId, request.amount);
    
    // Record transaction
    await recordWithdrawal({
      userId: request.userId,
      transactionId: txnId,
      amount: request.amount,
      fee,
      netAmount,
      method: request.method,
      status: 'processing',
      destination: request.destination
    });
    
    return {
      success: true,
      transactionId: txnId,
      status: 'processing',
      estimatedArrival: getEstimatedArrival(request.method)
    };
    
  } catch (error) {
    await recordWithdrawal({
      userId: request.userId,
      transactionId: txnId,
      amount: request.amount,
      fee: 0,
      netAmount: 0,
      method: request.method,
      status: 'failed',
      destination: request.destination,
      error: error.message
    });
    
    return {
      success: false,
      transactionId: txnId,
      status: 'failed',
      error: error.message
    };
  }
}

function calculateWithdrawalFee(method: string, amount: number): number {
  const feeRates: Record<string, { rate: number; min: number }> = {
    'easypaisa': { rate: 0.015, min: 0.50 },
    'jazzcash': { rate: 0.015, min: 0.50 },
    'upi': { rate: 0.01, min: 0.10 },
    'paypal': { rate: 0.025, min: 1.00 },
    'payoneer': { rate: 0.02, min: 1.00 },
    'bank': { rate: 0.02, min: 1.00 }
  };
  
  const config = feeRates[method] || { rate: 0.02, min: 1.00 };
  const fee = amount * config.rate;
  return Math.max(fee, config.min);
}

function getEstimatedArrival(method: string): string {
  const times: Record<string, string> = {
    'easypaisa': 'Within 24 hours',
    'jazzcash': 'Within 24 hours',
    'upi': 'Within 4 hours',
    'paypal': '1-3 business days',
    'payoneer': '2-4 business days',
    'bank': '3-5 business days'
  };
  return times[method] || '3-5 business days';
}
```

---

## Part 4: API Endpoints

### 4.1 Purchase Endpoints

```typescript
// routes/purchases.ts
import express from 'express';
import { verifyPurchase } from '../services/purchase';

const router = express.Router();

// Verify and credit purchase
router.post('/verify', authenticate, async (req, res) => {
  const { purchaseToken, productId, packageName } = req.body;
  
  try {
    const result = await verifyPurchase({
      userId: req.user.id,
      purchaseToken,
      productId,
      packageName
    });
    
    res.json(result);
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

// Get available products
router.get('/products', async (req, res) => {
  const products = [
    { id: 'coins_starter', name: 'Starter Pack', price: 0.99, coins: 80000 },
    { id: 'coins_basic', name: 'Basic Pack', price: 4.99, coins: 450000 },
    { id: 'coins_standard', name: 'Standard Pack', price: 9.99, coins: 950000 },
    { id: 'coins_premium', name: 'Premium Pack', price: 24.99, coins: 2600000 },
    { id: 'coins_elite', name: 'Elite Pack', price: 49.99, coins: 5500000 },
    { id: 'coins_ultimate', name: 'Ultimate Pack', price: 99.99, coins: 12000000 }
  ];
  
  res.json({ products });
});

export default router;
```

### 4.2 Withdrawal Endpoints

```typescript
// routes/withdrawals.ts
import express from 'express';
import { processWithdrawal } from '../services/withdrawal';

const router = express.Router();

// Get available withdrawal methods
router.get('/methods', authenticate, async (req, res) => {
  const user = await getUser(req.user.id);
  
  // Determine methods based on user's country
  const methods = getAvailableMethodsForCountry(user.country);
  
  res.json({ methods });
});

// Get user's payment methods
router.get('/payment-methods', authenticate, async (req, res) => {
  const methods = await getUserPaymentMethods(req.user.id);
  res.json({ methods });
});

// Add payment method
router.post('/payment-methods', authenticate, async (req, res) => {
  const { type, details } = req.body;
  
  const method = await addPaymentMethod(req.user.id, type, details);
  res.json({ method });
});

// Request withdrawal
router.post('/withdraw', authenticate, async (req, res) => {
  const { amount, methodId } = req.body;
  
  // Validate
  if (amount < getMinWithdrawal(req.user.country)) {
    return res.status(400).json({ error: 'Amount below minimum' });
  }
  
  const paymentMethod = await getPaymentMethod(methodId);
  if (!paymentMethod || paymentMethod.userId !== req.user.id) {
    return res.status(400).json({ error: 'Invalid payment method' });
  }
  
  const result = await processWithdrawal({
    userId: req.user.id,
    amount,
    currency: 'USD',
    method: paymentMethod.type,
    destination: paymentMethod.details
  });
  
  res.json(result);
});

// Get withdrawal history
router.get('/history', authenticate, async (req, res) => {
  const { page = 1, limit = 20 } = req.query;
  
  const history = await getWithdrawalHistory(req.user.id, {
    page: Number(page),
    limit: Number(limit)
  });
  
  res.json(history);
});

// Get withdrawal status
router.get('/:withdrawalId/status', authenticate, async (req, res) => {
  const withdrawal = await getWithdrawal(req.params.withdrawalId);
  
  if (!withdrawal || withdrawal.userId !== req.user.id) {
    return res.status(404).json({ error: 'Not found' });
  }
  
  res.json({ status: withdrawal.status });
});

export default router;
```

---

## Part 5: Configuration

### 5.1 Environment Variables

```env
# Google Play Billing
GOOGLE_PLAY_SERVICE_ACCOUNT_KEY=path/to/service-account.json

# EasyPaisa (Pakistan)
EASYPAISA_USERNAME=your_username
EASYPAISA_PASSWORD=your_password
EASYPAISA_STORE_ID=your_store_id

# JazzCash (Pakistan)
JAZZCASH_MERCHANT_ID=your_merchant_id
JAZZCASH_PASSWORD=your_password
JAZZCASH_SALT=your_integrity_salt

# Razorpay (India)
RAZORPAY_KEY_ID=your_key_id
RAZORPAY_KEY_SECRET=your_key_secret
RAZORPAY_ACCOUNT_NUMBER=your_account_number

# PayPal (International)
PAYPAL_ENV=sandbox
PAYPAL_CLIENT_ID=your_client_id
PAYPAL_CLIENT_SECRET=your_client_secret

# Payoneer (International)
PAYONEER_API_KEY=your_api_key
PAYONEER_PROGRAM_ID=your_program_id
```

### 5.2 Database Schema

```sql
-- Payment methods
CREATE TABLE payment_methods (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id),
  type VARCHAR(50) NOT NULL,
  details JSONB NOT NULL,
  is_default BOOLEAN DEFAULT false,
  verified BOOLEAN DEFAULT false,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Withdrawals
CREATE TABLE withdrawals (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id),
  transaction_id VARCHAR(100) UNIQUE NOT NULL,
  amount DECIMAL(10, 2) NOT NULL,
  fee DECIMAL(10, 2) NOT NULL,
  net_amount DECIMAL(10, 2) NOT NULL,
  currency VARCHAR(3) DEFAULT 'USD',
  method VARCHAR(50) NOT NULL,
  destination JSONB NOT NULL,
  status VARCHAR(20) DEFAULT 'pending',
  error_message TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  processed_at TIMESTAMP,
  completed_at TIMESTAMP
);

-- Purchases
CREATE TABLE purchases (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id),
  product_id VARCHAR(100) NOT NULL,
  purchase_token TEXT NOT NULL,
  order_id VARCHAR(100),
  coins_credited INTEGER NOT NULL,
  amount_paid DECIMAL(10, 2) NOT NULL,
  currency VARCHAR(3) DEFAULT 'USD',
  status VARCHAR(20) DEFAULT 'pending',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  verified_at TIMESTAMP
);
```

---

## Part 6: Security Considerations

### 6.1 Purchase Verification

- Always verify purchases server-side
- Store purchase tokens to prevent replay attacks
- Use webhook notifications for order updates

### 6.2 Withdrawal Security

- Implement KYC for large withdrawals
- Rate limit withdrawal requests
- Require 2FA for withdrawals
- Implement clearance period (5-7 days)
- Monitor for suspicious patterns

### 6.3 Anti-Fraud Measures

```typescript
// middleware/antifraud.ts
export async function checkWithdrawalFraud(userId: string, amount: number): Promise<boolean> {
  // Check daily withdrawal limit
  const dailyTotal = await getDailyWithdrawalTotal(userId);
  if (dailyTotal + amount > 500) {
    throw new Error('Daily withdrawal limit exceeded');
  }
  
  // Check velocity
  const recentWithdrawals = await getRecentWithdrawals(userId, 24); // Last 24 hours
  if (recentWithdrawals.length >= 5) {
    throw new Error('Too many withdrawal attempts');
  }
  
  // Check account age
  const user = await getUser(userId);
  const accountAgeDays = daysSince(user.createdAt);
  if (accountAgeDays < 7 && amount > 50) {
    throw new Error('Account too new for large withdrawals');
  }
  
  // Check KYC status
  if (amount > 100 && !user.kycVerified) {
    throw new Error('KYC verification required for this amount');
  }
  
  return true;
}
```

---

## Part 7: Testing

### 7.1 Test Accounts

**Google Play Billing:**
- Use license testers in Play Console
- Use test payment methods

**EasyPaisa Sandbox:**
- Test numbers: 03001234567
- Test OTP: 123456

**JazzCash Sandbox:**
- Sandbox URL: https://sandbox.jazzcash.com.pk

**Razorpay Test Mode:**
- Use test API keys
- Test UPI: success@razorpay

**PayPal Sandbox:**
- Create sandbox accounts at developer.paypal.com
- Use sandbox email addresses

---

## Summary

This guide covers the complete payment integration for Aura Voice Chat:

1. **In-App Purchases** via Google Play Billing
2. **Pakistan Payouts** via EasyPaisa and JazzCash
3. **India Payouts** via UPI (Razorpay)
4. **International Payouts** via PayPal and Payoneer

Remember to:
- Always test in sandbox/test mode first
- Implement proper security measures
- Monitor transactions for fraud
- Handle errors gracefully
- Provide clear user feedback

---

**Developer:** Hawkaye Visions LTD — Lahore, Pakistan
