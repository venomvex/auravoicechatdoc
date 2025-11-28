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

COMMIT;
