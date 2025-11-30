-- ============================================================================
-- Aura Voice Chat - PostgreSQL Database Schema
-- Developer: Hawkaye Visions LTD — Lahore, Pakistan
--
-- This schema replaces the Firestore database structure with PostgreSQL.
-- Run this script to initialize the database:
--   psql -U aura_admin -d auravoicechat -f schema.sql
-- ============================================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================================
-- USERS
-- ============================================================================
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    cognito_sub VARCHAR(255) UNIQUE,
    email VARCHAR(255) UNIQUE,
    phone_number VARCHAR(20) UNIQUE,
    username VARCHAR(50) UNIQUE,
    display_name VARCHAR(100),
    avatar_url TEXT,
    bio TEXT,
    status VARCHAR(20) DEFAULT 'offline' CHECK (status IN ('online', 'offline', 'away', 'busy')),
    level INTEGER DEFAULT 1,
    exp BIGINT DEFAULT 0,
    coins BIGINT DEFAULT 0,
    diamonds BIGINT DEFAULT 0,
    vip_tier INTEGER DEFAULT 0,
    vip_expires_at TIMESTAMP WITH TIME ZONE,
    is_verified BOOLEAN DEFAULT FALSE,
    is_banned BOOLEAN DEFAULT FALSE,
    banned_reason TEXT,
    banned_until TIMESTAMP WITH TIME ZONE,
    last_login_at TIMESTAMP WITH TIME ZONE,
    login_streak INTEGER DEFAULT 0,
    total_login_days INTEGER DEFAULT 0,
    device_id VARCHAR(255),
    fcm_token TEXT,
    locale VARCHAR(10) DEFAULT 'en',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_cognito_sub ON users(cognito_sub);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone_number ON users(phone_number);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_level ON users(level);
CREATE INDEX idx_users_created_at ON users(created_at);

-- ============================================================================
-- USER SETTINGS
-- ============================================================================
CREATE TABLE IF NOT EXISTS user_settings (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    notification_enabled BOOLEAN DEFAULT TRUE,
    push_notifications BOOLEAN DEFAULT TRUE,
    email_notifications BOOLEAN DEFAULT TRUE,
    dark_mode BOOLEAN DEFAULT FALSE,
    language VARCHAR(10) DEFAULT 'en',
    privacy_profile VARCHAR(20) DEFAULT 'public' CHECK (privacy_profile IN ('public', 'friends', 'private')),
    privacy_online_status BOOLEAN DEFAULT TRUE,
    privacy_last_seen BOOLEAN DEFAULT TRUE,
    sound_enabled BOOLEAN DEFAULT TRUE,
    vibration_enabled BOOLEAN DEFAULT TRUE,
    auto_play_videos BOOLEAN DEFAULT TRUE,
    data_saver_mode BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- ROOMS
-- ============================================================================
CREATE TABLE IF NOT EXISTS rooms (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    cover_url TEXT,
    type VARCHAR(20) DEFAULT 'voice' CHECK (type IN ('voice', 'video', 'music', 'party')),
    owner_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    is_private BOOLEAN DEFAULT FALSE,
    password_hash VARCHAR(255),
    max_participants INTEGER DEFAULT 20,
    current_participants INTEGER DEFAULT 0,
    announcement TEXT,
    welcome_message TEXT,
    is_video_mode BOOLEAN DEFAULT FALSE,
    is_music_mode BOOLEAN DEFAULT FALSE,
    is_live BOOLEAN DEFAULT FALSE,
    tags TEXT[],
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_rooms_owner_id ON rooms(owner_id);
CREATE INDEX idx_rooms_type ON rooms(type);
CREATE INDEX idx_rooms_is_live ON rooms(is_live);
CREATE INDEX idx_rooms_current_participants ON rooms(current_participants DESC);
CREATE INDEX idx_rooms_created_at ON rooms(created_at);

-- ============================================================================
-- ROOM MEMBERS
-- ============================================================================
CREATE TABLE IF NOT EXISTS room_members (
    room_id UUID NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(20) DEFAULT 'member' CHECK (role IN ('owner', 'admin', 'moderator', 'member', 'guest')),
    seat_number INTEGER,
    is_muted BOOLEAN DEFAULT FALSE,
    is_speaking BOOLEAN DEFAULT FALSE,
    is_hand_raised BOOLEAN DEFAULT FALSE,
    is_video_on BOOLEAN DEFAULT FALSE,
    joined_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (room_id, user_id)
);

CREATE INDEX idx_room_members_user_id ON room_members(user_id);
CREATE INDEX idx_room_members_role ON room_members(role);

-- ============================================================================
-- MESSAGES
-- ============================================================================
CREATE TABLE IF NOT EXISTS messages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    room_id UUID NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE SET NULL,
    content TEXT NOT NULL,
    type VARCHAR(20) DEFAULT 'text' CHECK (type IN ('text', 'image', 'audio', 'gift', 'system', 'sticker')),
    metadata JSONB,
    reply_to_id UUID REFERENCES messages(id) ON DELETE SET NULL,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_messages_room_id ON messages(room_id);
CREATE INDEX idx_messages_user_id ON messages(user_id);
CREATE INDEX idx_messages_created_at ON messages(created_at);
CREATE INDEX idx_messages_room_created ON messages(room_id, created_at DESC);

-- ============================================================================
-- DIRECT MESSAGES
-- ============================================================================
CREATE TABLE IF NOT EXISTS direct_messages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    sender_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    receiver_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    type VARCHAR(20) DEFAULT 'text' CHECK (type IN ('text', 'image', 'audio', 'gift', 'sticker')),
    metadata JSONB,
    is_read BOOLEAN DEFAULT FALSE,
    is_deleted_by_sender BOOLEAN DEFAULT FALSE,
    is_deleted_by_receiver BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_dm_sender_id ON direct_messages(sender_id);
CREATE INDEX idx_dm_receiver_id ON direct_messages(receiver_id);
CREATE INDEX idx_dm_conversation ON direct_messages(sender_id, receiver_id, created_at DESC);

-- ============================================================================
-- USER FOLLOWS
-- ============================================================================
CREATE TABLE IF NOT EXISTS user_follows (
    follower_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    following_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (follower_id, following_id)
);

CREATE INDEX idx_follows_follower ON user_follows(follower_id);
CREATE INDEX idx_follows_following ON user_follows(following_id);

-- ============================================================================
-- USER BLOCKS
-- ============================================================================
CREATE TABLE IF NOT EXISTS user_blocks (
    blocker_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    blocked_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (blocker_id, blocked_id)
);

-- ============================================================================
-- NOTIFICATIONS
-- ============================================================================
CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    body TEXT,
    data JSONB,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);
CREATE INDEX idx_notifications_created_at ON notifications(created_at DESC);
CREATE INDEX idx_notifications_user_unread ON notifications(user_id, is_read) WHERE is_read = FALSE;

-- ============================================================================
-- DAILY REWARDS
-- ============================================================================
CREATE TABLE IF NOT EXISTS daily_rewards (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    day_number INTEGER NOT NULL CHECK (day_number BETWEEN 1 AND 7),
    coins_awarded BIGINT NOT NULL,
    vip_multiplier DECIMAL(3, 2) DEFAULT 1.0,
    claimed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_daily_rewards_user_id ON daily_rewards(user_id);
CREATE INDEX idx_daily_rewards_claimed_at ON daily_rewards(claimed_at);

-- ============================================================================
-- MEDALS
-- ============================================================================
CREATE TABLE IF NOT EXISTS medals (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL CHECK (category IN ('gift', 'achievement', 'activity', 'special')),
    icon_url TEXT,
    criteria JSONB,
    reward_coins BIGINT DEFAULT 0,
    reward_item_id VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_medals (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    medal_id VARCHAR(50) NOT NULL REFERENCES medals(id) ON DELETE CASCADE,
    achieved_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    display_order INTEGER DEFAULT 0,
    is_displayed BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (user_id, medal_id)
);

CREATE INDEX idx_user_medals_user ON user_medals(user_id);
CREATE INDEX idx_user_medals_displayed ON user_medals(user_id, is_displayed) WHERE is_displayed = TRUE;

-- ============================================================================
-- GIFTS
-- ============================================================================
CREATE TABLE IF NOT EXISTS gifts (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price_coins BIGINT NOT NULL,
    diamond_value BIGINT NOT NULL,
    category VARCHAR(50),
    animation_url TEXT,
    thumbnail_url TEXT,
    is_premium BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS gift_transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    gift_id VARCHAR(50) NOT NULL REFERENCES gifts(id),
    sender_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    receiver_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    room_id UUID REFERENCES rooms(id) ON DELETE SET NULL,
    quantity INTEGER DEFAULT 1,
    coins_spent BIGINT NOT NULL,
    diamonds_earned BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_gift_transactions_sender ON gift_transactions(sender_id);
CREATE INDEX idx_gift_transactions_receiver ON gift_transactions(receiver_id);
CREATE INDEX idx_gift_transactions_room ON gift_transactions(room_id);
CREATE INDEX idx_gift_transactions_created ON gift_transactions(created_at);

-- ============================================================================
-- WALLET TRANSACTIONS
-- ============================================================================
CREATE TABLE IF NOT EXISTS wallet_transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL CHECK (type IN ('recharge', 'purchase', 'gift_sent', 'gift_received', 'reward', 'withdrawal', 'referral', 'transfer', 'exchange')),
    amount BIGINT NOT NULL,
    currency VARCHAR(20) NOT NULL CHECK (currency IN ('coins', 'diamonds', 'usd')),
    balance_after BIGINT,
    description TEXT,
    reference_id VARCHAR(255),
    metadata JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_wallet_transactions_user ON wallet_transactions(user_id);
CREATE INDEX idx_wallet_transactions_type ON wallet_transactions(type);
CREATE INDEX idx_wallet_transactions_created ON wallet_transactions(created_at);

-- ============================================================================
-- CP (COUPLE PARTNERSHIP)
-- ============================================================================
CREATE TABLE IF NOT EXISTS cp_partnerships (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    partner1_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    partner2_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    cp_level INTEGER DEFAULT 1,
    cp_exp BIGINT DEFAULT 0,
    anniversary_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP WITH TIME ZONE,
    UNIQUE (partner1_id, partner2_id)
);

CREATE INDEX idx_cp_partner1 ON cp_partnerships(partner1_id);
CREATE INDEX idx_cp_partner2 ON cp_partnerships(partner2_id);

-- ============================================================================
-- FAMILIES
-- ============================================================================
CREATE TABLE IF NOT EXISTS families (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    badge_url TEXT,
    owner_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    level INTEGER DEFAULT 1,
    exp BIGINT DEFAULT 0,
    max_members INTEGER DEFAULT 50,
    is_public BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS family_members (
    family_id UUID NOT NULL REFERENCES families(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(20) DEFAULT 'member' CHECK (role IN ('owner', 'elder', 'member')),
    contribution BIGINT DEFAULT 0,
    joined_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (family_id, user_id)
);

CREATE INDEX idx_family_members_user ON family_members(user_id);

-- ============================================================================
-- REFERRALS
-- ============================================================================
CREATE TABLE IF NOT EXISTS referrals (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    referrer_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    referred_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    referral_code VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'active', 'rewarded', 'expired')),
    coins_earned BIGINT DEFAULT 0,
    cash_earned DECIMAL(10, 2) DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    activated_at TIMESTAMP WITH TIME ZONE,
    UNIQUE (referred_id)
);

CREATE INDEX idx_referrals_referrer ON referrals(referrer_id);
CREATE INDEX idx_referrals_code ON referrals(referral_code);

-- ============================================================================
-- KYC
-- ============================================================================
CREATE TABLE IF NOT EXISTS kyc_submissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    id_card_front_url TEXT,
    id_card_back_url TEXT,
    selfie_url TEXT,
    liveness_score DECIMAL(5, 4),
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'approved', 'rejected')),
    rejection_reason TEXT,
    reviewed_by UUID REFERENCES users(id),
    submitted_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_kyc_user ON kyc_submissions(user_id);
CREATE INDEX idx_kyc_status ON kyc_submissions(status);

-- ============================================================================
-- REPORTS
-- ============================================================================
CREATE TABLE IF NOT EXISTS reports (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    reporter_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    reported_user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    reported_room_id UUID REFERENCES rooms(id) ON DELETE CASCADE,
    reported_message_id UUID REFERENCES messages(id) ON DELETE SET NULL,
    type VARCHAR(50) NOT NULL,
    reason TEXT NOT NULL,
    evidence_urls TEXT[],
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'reviewed', 'resolved', 'dismissed')),
    resolution_note TEXT,
    reviewed_by UUID REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_reports_reporter ON reports(reporter_id);
CREATE INDEX idx_reports_status ON reports(status);

-- ============================================================================
-- STORE ITEMS
-- ============================================================================
CREATE TABLE IF NOT EXISTS store_items (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL CHECK (category IN ('frame', 'entry_effect', 'mic_skin', 'seat_heart', 'bubble', 'consumable')),
    price_coins BIGINT NOT NULL,
    price_diamonds BIGINT DEFAULT 0,
    rarity VARCHAR(20) DEFAULT 'common' CHECK (rarity IN ('common', 'rare', 'epic', 'legendary')),
    duration_days INTEGER,
    thumbnail_url TEXT,
    asset_url TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_inventory (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    item_id VARCHAR(50) NOT NULL REFERENCES store_items(id),
    quantity INTEGER DEFAULT 1,
    expires_at TIMESTAMP WITH TIME ZONE,
    is_equipped BOOLEAN DEFAULT FALSE,
    purchased_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_inventory_user ON user_inventory(user_id);
CREATE INDEX idx_inventory_equipped ON user_inventory(user_id, is_equipped) WHERE is_equipped = TRUE;

-- ============================================================================
-- EVENTS
-- ============================================================================
CREATE TABLE IF NOT EXISTS events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL,
    banner_url TEXT,
    start_at TIMESTAMP WITH TIME ZONE NOT NULL,
    end_at TIMESTAMP WITH TIME ZONE NOT NULL,
    rules JSONB,
    rewards JSONB,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_events_active ON events(is_active, start_at, end_at);

-- ============================================================================
-- TRIGGERS
-- ============================================================================

-- Update timestamp trigger
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_rooms_updated_at
    BEFORE UPDATE ON rooms
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_user_settings_updated_at
    BEFORE UPDATE ON user_settings
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_families_updated_at
    BEFORE UPDATE ON families
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Update room participant count trigger
CREATE OR REPLACE FUNCTION update_room_participant_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE rooms SET current_participants = current_participants + 1 WHERE id = NEW.room_id;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE rooms SET current_participants = current_participants - 1 WHERE id = OLD.room_id;
    END IF;
    RETURN NULL;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_room_participants
    AFTER INSERT OR DELETE ON room_members
    FOR EACH ROW
    EXECUTE FUNCTION update_room_participant_count();

-- ============================================================================
-- VIEWS
-- ============================================================================

-- User public profile view
CREATE OR REPLACE VIEW user_profiles AS
SELECT 
    u.id,
    u.username,
    u.display_name,
    u.avatar_url,
    u.bio,
    u.status,
    u.level,
    u.vip_tier,
    u.is_verified,
    u.created_at,
    (SELECT COUNT(*) FROM user_follows WHERE following_id = u.id) as followers_count,
    (SELECT COUNT(*) FROM user_follows WHERE follower_id = u.id) as following_count
FROM users u
WHERE u.is_banned = FALSE;

-- Room listing view
CREATE OR REPLACE VIEW room_listings AS
SELECT 
    r.id,
    r.name,
    r.description,
    r.cover_url,
    r.type,
    r.is_private,
    r.max_participants,
    r.current_participants,
    r.is_live,
    r.tags,
    r.created_at,
    u.id as owner_id,
    u.username as owner_username,
    u.display_name as owner_display_name,
    u.avatar_url as owner_avatar_url
FROM rooms r
JOIN users u ON r.owner_id = u.id
WHERE r.is_live = TRUE;

-- ============================================================================
-- INITIAL DATA
-- ============================================================================

-- Insert default medals
INSERT INTO medals (id, name, description, category, reward_coins) VALUES
    ('login_30', 'Early Bird', 'Login for 30 days', 'activity', 50000),
    ('login_60', 'Regular User', 'Login for 60 days', 'activity', 100000),
    ('login_90', 'Loyal Fan', 'Login for 90 days', 'activity', 200000),
    ('login_180', 'Super Fan', 'Login for 180 days', 'activity', 500000),
    ('login_365', 'Legend', 'Login for 365 days', 'activity', 1000000),
    ('level_10', 'Rising Star', 'Reach Level 10', 'achievement', 10000),
    ('level_50', 'Experienced', 'Reach Level 50', 'achievement', 100000),
    ('level_100', 'Master', 'Reach Level 100', 'achievement', 500000),
    ('gifts_100', 'Generous', 'Send 100 gifts', 'gift', 20000),
    ('gifts_1000', 'Philanthropist', 'Send 1000 gifts', 'gift', 200000)
ON CONFLICT (id) DO NOTHING;

-- Insert default store items
INSERT INTO store_items (id, name, description, category, price_coins, rarity, duration_days) VALUES
    ('frame_basic_1', 'Basic Frame', 'Simple profile frame', 'frame', 10000, 'common', 30),
    ('frame_gold_1', 'Gold Frame', 'Elegant gold profile frame', 'frame', 50000, 'rare', 30),
    ('frame_diamond_1', 'Diamond Frame', 'Stunning diamond profile frame', 'frame', 200000, 'epic', 30),
    ('entry_sparkle_1', 'Sparkle Entry', 'Sparkle effect on room entry', 'entry_effect', 30000, 'common', 30),
    ('mic_glow_1', 'Glowing Mic', 'Mic with glow effect', 'mic_skin', 40000, 'rare', 30)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- GREEDY BABY GAME TABLES
-- ============================================================================

-- Game configuration storage
CREATE TABLE IF NOT EXISTS game_configs (
    game_type VARCHAR(50) PRIMARY KEY,
    config_data JSONB NOT NULL DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Greedy Baby pool tracking for house edge calculations
CREATE TABLE IF NOT EXISTS greedy_baby_pool (
    id INTEGER PRIMARY KEY DEFAULT 1 CHECK (id = 1), -- Single row table
    total_bets BIGINT DEFAULT 0,
    total_payouts BIGINT DEFAULT 0,
    profit_loss BIGINT DEFAULT 0,
    round_count BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Initialize pool with default values
INSERT INTO greedy_baby_pool (id, total_bets, total_payouts, profit_loss, round_count)
VALUES (1, 0, 0, 0, 0)
ON CONFLICT (id) DO NOTHING;

-- Greedy Baby rankings (daily/weekly)
CREATE TABLE IF NOT EXISTS greedy_baby_rankings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    ranking_type VARCHAR(10) NOT NULL CHECK (ranking_type IN ('daily', 'weekly')),
    period_date DATE NOT NULL, -- For daily: today's date, For weekly: Monday of the week
    total_winnings BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, ranking_type, period_date)
);

CREATE INDEX idx_greedy_baby_rankings_type_date ON greedy_baby_rankings(ranking_type, period_date);
CREATE INDEX idx_greedy_baby_rankings_winnings ON greedy_baby_rankings(total_winnings DESC);

-- Greedy Baby round history
CREATE TABLE IF NOT EXISTS greedy_baby_rounds (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    room_id UUID REFERENCES rooms(id) ON DELETE SET NULL,
    winning_item VARCHAR(50) NOT NULL,
    special_result VARCHAR(20) CHECK (special_result IN ('fruit_basket', 'full_pizza', NULL)),
    total_bets BIGINT DEFAULT 0,
    total_payouts BIGINT DEFAULT 0,
    participant_count INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_greedy_baby_rounds_room ON greedy_baby_rounds(room_id);
CREATE INDEX idx_greedy_baby_rounds_created ON greedy_baby_rounds(created_at DESC);

-- Greedy Baby individual bets
CREATE TABLE IF NOT EXISTS greedy_baby_bets (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    round_id UUID NOT NULL REFERENCES greedy_baby_rounds(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    item_id VARCHAR(50) NOT NULL,
    chip_value BIGINT NOT NULL,
    chip_count INTEGER NOT NULL DEFAULT 1,
    total_bet BIGINT NOT NULL,
    won BOOLEAN DEFAULT FALSE,
    payout BIGINT DEFAULT 0,
    multiplier INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_greedy_baby_bets_round ON greedy_baby_bets(round_id);
CREATE INDEX idx_greedy_baby_bets_user ON greedy_baby_bets(user_id);

-- Insert default Greedy Baby configuration
INSERT INTO game_configs (game_type, config_data) VALUES
    ('greedy_baby', '{
        "houseEdge": 8,
        "maxWinPerRound": 100000000,
        "winRates": {
            "apple": 17,
            "lemon": 17,
            "strawberry": 17,
            "mango": 17,
            "fish": 12,
            "burger": 8,
            "pizza": 5,
            "chicken": 2
        },
        "fruitBasketTriggerRate": 3,
        "fullPizzaTriggerRate": 2,
        "poolRebalanceThreshold": 1000000
    }'),
    ('lucky_777_pro', '{
        "houseEdge": 10,
        "jackpotContribution": 0.02,
        "minBet": 100,
        "maxBet": 500000
    }'),
    ('lucky_77_pro', '{
        "houseEdge": 8,
        "jackpotContribution": 0.01,
        "minBet": 100,
        "maxBet": 100000
    }'),
    ('lucky_fruit', '{
        "houseEdge": 8,
        "minBet": 100,
        "maxBet": 100000
    }')
ON CONFLICT (game_type) DO NOTHING;

-- ============================================================================
-- EARNINGS SYSTEM TABLES
-- ============================================================================

-- Earnings records
CREATE TABLE IF NOT EXISTS earnings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL CHECK (type IN ('gift', 'hosting', 'game', 'referral', 'bonus', 'family', 'event')),
    amount DECIMAL(15, 2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'diamonds',
    source_type VARCHAR(50), -- e.g., 'room', 'game', 'user'
    source_id UUID,
    description TEXT,
    status VARCHAR(20) DEFAULT 'completed' CHECK (status IN ('pending', 'completed', 'cancelled')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_earnings_user ON earnings(user_id);
CREATE INDEX idx_earnings_type ON earnings(type);
CREATE INDEX idx_earnings_created ON earnings(created_at DESC);

-- Earning targets
CREATE TABLE IF NOT EXISTS earning_targets (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL CHECK (type IN ('gift', 'hosting', 'game', 'referral', 'total')),
    target_amount DECIMAL(15, 2) NOT NULL,
    reward_coins BIGINT NOT NULL,
    reward_diamonds BIGINT DEFAULT 0,
    period VARCHAR(20) DEFAULT 'monthly' CHECK (period IN ('daily', 'weekly', 'monthly', 'lifetime')),
    is_active BOOLEAN DEFAULT TRUE,
    tier INTEGER DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- User activated targets
CREATE TABLE IF NOT EXISTS user_targets (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    target_id UUID NOT NULL REFERENCES earning_targets(id) ON DELETE CASCADE,
    is_active BOOLEAN DEFAULT TRUE,
    activated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, target_id)
);

-- Earning claims
CREATE TABLE IF NOT EXISTS earning_claims (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    target_id UUID NOT NULL REFERENCES earning_targets(id) ON DELETE CASCADE,
    reward_coins BIGINT NOT NULL,
    reward_diamonds BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_earning_claims_user ON earning_claims(user_id);

-- ============================================================================
-- PAYMENT METHODS - COUNTRY SPECIFIC
-- ============================================================================

-- Available withdrawal/payment methods
CREATE TABLE IF NOT EXISTS withdrawal_methods (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    country_codes TEXT[] NOT NULL, -- e.g., {'PK', 'IN', 'ALL'}
    type VARCHAR(50) NOT NULL CHECK (type IN ('mobile_wallet', 'bank', 'card', 'upi', 'international')),
    min_amount DECIMAL(15, 2) NOT NULL DEFAULT 100,
    max_amount DECIMAL(15, 2) NOT NULL DEFAULT 1000000,
    fee_type VARCHAR(20) DEFAULT 'percentage' CHECK (fee_type IN ('fixed', 'percentage')),
    fee_amount DECIMAL(10, 4) DEFAULT 0,
    processing_time VARCHAR(100) DEFAULT '1-3 business days',
    required_fields JSONB DEFAULT '[]',
    is_active BOOLEAN DEFAULT TRUE,
    icon_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- User saved payment methods
CREATE TABLE IF NOT EXISTS user_payment_methods (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    method_id UUID REFERENCES withdrawal_methods(id),
    type VARCHAR(50) NOT NULL,
    details JSONB NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_payment_methods_user ON user_payment_methods(user_id);

-- Withdrawals
CREATE TABLE IF NOT EXISTS withdrawals (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount DECIMAL(15, 2) NOT NULL,
    fee DECIMAL(15, 2) DEFAULT 0,
    net_amount DECIMAL(15, 2) NOT NULL,
    method VARCHAR(50) NOT NULL,
    method_id UUID REFERENCES withdrawal_methods(id),
    account_details JSONB NOT NULL,
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'processing', 'completed', 'rejected', 'cancelled')),
    rejection_reason TEXT,
    transaction_id VARCHAR(255),
    processed_at TIMESTAMP WITH TIME ZONE,
    processed_by UUID REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_withdrawals_user ON withdrawals(user_id);
CREATE INDEX idx_withdrawals_status ON withdrawals(status);

-- Coin purchases/recharges
CREATE TABLE IF NOT EXISTS coin_purchases (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    package_id UUID NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    coins_purchased BIGINT NOT NULL,
    bonus_coins BIGINT DEFAULT 0,
    payment_method VARCHAR(50) NOT NULL,
    payment_provider VARCHAR(50) NOT NULL,
    payment_status VARCHAR(20) DEFAULT 'pending' CHECK (payment_status IN ('pending', 'completed', 'failed', 'refunded')),
    transaction_id VARCHAR(255),
    provider_transaction_id VARCHAR(255),
    provider_response JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_coin_purchases_user ON coin_purchases(user_id);
CREATE INDEX idx_coin_purchases_status ON coin_purchases(payment_status);

-- Coin packages
CREATE TABLE IF NOT EXISTS coin_packages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    coins BIGINT NOT NULL,
    bonus_coins BIGINT DEFAULT 0,
    price_usd DECIMAL(10, 2) NOT NULL,
    price_pkr DECIMAL(10, 2),
    price_inr DECIMAL(10, 2),
    discount_percentage INTEGER DEFAULT 0,
    is_popular BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    sort_order INTEGER DEFAULT 0,
    icon_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Payment providers configuration
CREATE TABLE IF NOT EXISTS payment_providers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    type VARCHAR(50) NOT NULL CHECK (type IN ('mobile_wallet', 'bank', 'card', 'upi', 'international')),
    country_codes TEXT[] NOT NULL,
    api_endpoint TEXT,
    api_key_encrypted TEXT,
    api_secret_encrypted TEXT,
    webhook_secret TEXT,
    config JSONB DEFAULT '{}',
    is_active BOOLEAN DEFAULT TRUE,
    supports_purchase BOOLEAN DEFAULT TRUE,
    supports_withdrawal BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- ROOM RANKINGS
-- ============================================================================

-- Room contribution tracking
CREATE TABLE IF NOT EXISTS room_contributions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    room_id UUID NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    gift_value BIGINT DEFAULT 0,
    chat_count INTEGER DEFAULT 0,
    time_spent_minutes INTEGER DEFAULT 0,
    date DATE NOT NULL DEFAULT CURRENT_DATE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(room_id, user_id, date)
);

CREATE INDEX idx_room_contributions_room ON room_contributions(room_id);
CREATE INDEX idx_room_contributions_date ON room_contributions(date);

-- Room rankings (daily/weekly/monthly)
CREATE TABLE IF NOT EXISTS room_rankings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    room_id UUID NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    ranking_type VARCHAR(20) NOT NULL CHECK (ranking_type IN ('daily', 'weekly', 'monthly')),
    period_date DATE NOT NULL,
    total_gifts_received BIGINT DEFAULT 0,
    total_visitors INTEGER DEFAULT 0,
    total_messages INTEGER DEFAULT 0,
    score BIGINT DEFAULT 0,
    rank INTEGER,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(room_id, ranking_type, period_date)
);

CREATE INDEX idx_room_rankings_type ON room_rankings(ranking_type, period_date);
CREATE INDEX idx_room_rankings_score ON room_rankings(score DESC);

-- ============================================================================
-- FAMILY ENHANCEMENTS
-- ============================================================================

-- Family activity log
CREATE TABLE IF NOT EXISTS family_activity (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    family_id UUID NOT NULL REFERENCES families(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    activity_type VARCHAR(50) NOT NULL CHECK (activity_type IN ('joined', 'left', 'kicked', 'promoted', 'demoted', 'gift', 'contribution', 'event')),
    description TEXT,
    data JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_family_activity_family ON family_activity(family_id);

-- Family applications
CREATE TABLE IF NOT EXISTS family_applications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    family_id UUID NOT NULL REFERENCES families(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    message TEXT,
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'approved', 'rejected')),
    reviewed_by UUID REFERENCES users(id),
    reviewed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_family_applications_family ON family_applications(family_id);
CREATE INDEX idx_family_applications_status ON family_applications(status);

-- Family invitations
CREATE TABLE IF NOT EXISTS family_invitations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    family_id UUID NOT NULL REFERENCES families(id) ON DELETE CASCADE,
    from_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    to_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'accepted', 'declined', 'expired')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP WITH TIME ZONE DEFAULT (CURRENT_TIMESTAMP + INTERVAL '7 days')
);

-- Family rankings
CREATE TABLE IF NOT EXISTS family_rankings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    family_id UUID NOT NULL REFERENCES families(id) ON DELETE CASCADE,
    ranking_type VARCHAR(20) NOT NULL CHECK (ranking_type IN ('daily', 'weekly', 'monthly')),
    period_date DATE NOT NULL,
    total_contribution BIGINT DEFAULT 0,
    member_count INTEGER DEFAULT 0,
    activity_score BIGINT DEFAULT 0,
    rank INTEGER,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(family_id, ranking_type, period_date)
);

CREATE INDEX idx_family_rankings_type ON family_rankings(ranking_type, period_date);

-- ============================================================================
-- GAME SESSION TRACKING
-- ============================================================================

-- Game sessions for all games
CREATE TABLE IF NOT EXISTS game_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    game_type VARCHAR(50) NOT NULL,
    room_id UUID REFERENCES rooms(id) ON DELETE SET NULL,
    bet_amount BIGINT NOT NULL,
    win_amount BIGINT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'active' CHECK (status IN ('active', 'completed', 'cancelled')),
    result JSONB,
    data JSONB DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_game_sessions_user ON game_sessions(user_id);
CREATE INDEX idx_game_sessions_game ON game_sessions(game_type);
CREATE INDEX idx_game_sessions_created ON game_sessions(created_at DESC);

-- Lucky 777 Pro specific rounds
CREATE TABLE IF NOT EXISTS lucky_777_rounds (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    session_id UUID REFERENCES game_sessions(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    bet_amount BIGINT NOT NULL,
    reels JSONB NOT NULL, -- Array of reel results
    paylines JSONB, -- Winning paylines
    multiplier DECIMAL(10, 2) DEFAULT 1,
    is_jackpot BOOLEAN DEFAULT FALSE,
    win_amount BIGINT DEFAULT 0,
    jackpot_contribution BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_lucky_777_rounds_user ON lucky_777_rounds(user_id);

-- Lucky Fruit specific rounds
CREATE TABLE IF NOT EXISTS lucky_fruit_rounds (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    session_id UUID REFERENCES game_sessions(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    bet_amount BIGINT NOT NULL,
    selected_fruit VARCHAR(50) NOT NULL,
    winning_fruit VARCHAR(50) NOT NULL,
    special_result VARCHAR(50), -- 'lucky' or 'super_lucky'
    multiplier DECIMAL(10, 2) DEFAULT 1,
    win_amount BIGINT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_lucky_fruit_rounds_user ON lucky_fruit_rounds(user_id);

-- Jackpot tracking
CREATE TABLE IF NOT EXISTS game_jackpots (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    game_type VARCHAR(50) NOT NULL UNIQUE,
    current_amount BIGINT NOT NULL DEFAULT 0,
    last_won_amount BIGINT,
    last_won_by UUID REFERENCES users(id),
    last_won_at TIMESTAMP WITH TIME ZONE,
    contribution_rate DECIMAL(5, 4) DEFAULT 0.02, -- 2%
    min_jackpot BIGINT DEFAULT 1000000,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- INSERT DEFAULT DATA
-- ============================================================================

-- Insert default withdrawal methods
INSERT INTO withdrawal_methods (name, code, country_codes, type, min_amount, max_amount, fee_amount, required_fields, is_active) VALUES
    ('JazzCash', 'jazzcash', ARRAY['PK'], 'mobile_wallet', 500, 500000, 2.5, '["phone_number"]', true),
    ('EasyPaisa', 'easypaisa', ARRAY['PK'], 'mobile_wallet', 500, 500000, 2.5, '["phone_number"]', true),
    ('Paytm', 'paytm', ARRAY['IN'], 'mobile_wallet', 100, 100000, 2.0, '["phone_number"]', true),
    ('UPI', 'upi', ARRAY['IN'], 'upi', 100, 100000, 0, '["upi_id"]', true),
    ('Bank Transfer (Pakistan)', 'bank_pk', ARRAY['PK'], 'bank', 5000, 5000000, 1.5, '["account_number", "bank_name", "account_title"]', true),
    ('Bank Transfer (India)', 'bank_in', ARRAY['IN'], 'bank', 1000, 1000000, 1.5, '["account_number", "ifsc_code", "account_holder"]', true),
    ('PayPal', 'paypal', ARRAY['ALL'], 'international', 10, 10000, 4.0, '["email"]', true),
    ('Payoneer', 'payoneer', ARRAY['ALL'], 'international', 50, 50000, 3.0, '["email"]', true),
    ('Wise', 'wise', ARRAY['ALL'], 'international', 10, 10000, 1.5, '["email"]', true)
ON CONFLICT (code) DO NOTHING;

-- Insert default coin packages
INSERT INTO coin_packages (name, coins, bonus_coins, price_usd, price_pkr, price_inr, is_popular, sort_order) VALUES
    ('Starter Pack', 1000, 0, 0.99, 280, 80, false, 1),
    ('Basic Pack', 5000, 500, 4.99, 1400, 400, false, 2),
    ('Popular Pack', 12000, 2000, 9.99, 2800, 800, true, 3),
    ('Value Pack', 30000, 6000, 19.99, 5600, 1600, false, 4),
    ('Premium Pack', 70000, 15000, 49.99, 14000, 4000, false, 5),
    ('VIP Pack', 150000, 40000, 99.99, 28000, 8000, false, 6),
    ('Royal Pack', 400000, 120000, 199.99, 56000, 16000, false, 7)
ON CONFLICT DO NOTHING;

-- Insert default earning targets
INSERT INTO earning_targets (name, description, type, target_amount, reward_coins, period, tier) VALUES
    ('Gift Sender I', 'Send gifts worth 10,000 diamonds', 'gift', 10000, 5000, 'monthly', 1),
    ('Gift Sender II', 'Send gifts worth 50,000 diamonds', 'gift', 50000, 30000, 'monthly', 2),
    ('Gift Sender III', 'Send gifts worth 200,000 diamonds', 'gift', 200000, 150000, 'monthly', 3),
    ('Host Star I', 'Earn 5,000 diamonds from hosting', 'hosting', 5000, 2500, 'monthly', 1),
    ('Host Star II', 'Earn 25,000 diamonds from hosting', 'hosting', 25000, 15000, 'monthly', 2),
    ('Host Star III', 'Earn 100,000 diamonds from hosting', 'hosting', 100000, 70000, 'monthly', 3),
    ('Referral Champion I', 'Refer 5 active users', 'referral', 5, 10000, 'lifetime', 1),
    ('Referral Champion II', 'Refer 20 active users', 'referral', 20, 50000, 'lifetime', 2),
    ('Referral Champion III', 'Refer 100 active users', 'referral', 100, 300000, 'lifetime', 3)
ON CONFLICT DO NOTHING;

-- Insert default jackpots
INSERT INTO game_jackpots (game_type, current_amount, contribution_rate, min_jackpot) VALUES
    ('lucky_777_pro', 10000000, 0.02, 5000000),
    ('lucky_77_pro', 5000000, 0.01, 2000000)
ON CONFLICT (game_type) DO NOTHING;

-- Insert payment providers
INSERT INTO payment_providers (name, code, type, country_codes, supports_purchase, supports_withdrawal) VALUES
    ('JazzCash', 'jazzcash', 'mobile_wallet', ARRAY['PK'], true, true),
    ('EasyPaisa', 'easypaisa', 'mobile_wallet', ARRAY['PK'], true, true),
    ('Paytm', 'paytm', 'mobile_wallet', ARRAY['IN'], true, true),
    ('UPI', 'upi', 'upi', ARRAY['IN'], true, true),
    ('PayPal', 'paypal', 'international', ARRAY['ALL'], true, true),
    ('Payoneer', 'payoneer', 'international', ARRAY['ALL'], false, true),
    ('Stripe', 'stripe', 'card', ARRAY['ALL'], true, false),
    ('Bank Transfer', 'bank', 'bank', ARRAY['ALL'], true, true)
ON CONFLICT (code) DO NOTHING;

COMMIT;
