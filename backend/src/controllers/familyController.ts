/**
 * Family Controller
 * Developer: Hawkaye Visions LTD — Pakistan
 */

import { Request, Response, NextFunction } from 'express';
import { query } from '../config/database.config';
import { AppError } from '../middleware/errorHandler';
import { logger } from '../utils/logger';

// Create family
export const createFamily = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { name, description, avatar_url, is_public } = req.body;

    // Check if user is already in a family
    const existingMember = await query(
      'SELECT * FROM family_members WHERE user_id = $1',
      [userId]
    );

    if (existingMember.rows.length > 0) {
      throw new AppError('You are already in a family', 400, 'ALREADY_IN_FAMILY');
    }

    // Create family
    const familyResult = await query(
      `INSERT INTO families (name, description, avatar_url, is_public, owner_id, created_at)
       VALUES ($1, $2, $3, $4, $5, NOW()) RETURNING *`,
      [name, description, avatar_url, is_public !== false, userId]
    );

    const family = familyResult.rows[0];

    // Add owner as member
    await query(
      `INSERT INTO family_members (family_id, user_id, role, joined_at)
       VALUES ($1, $2, 'owner', NOW())`,
      [family.id, userId]
    );

    res.json({ family });
  } catch (error) {
    next(error);
  }
};

// Get my family
export const getMyFamily = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;

    const result = await query(
      `SELECT f.*, fm.role as my_role
       FROM families f
       JOIN family_members fm ON f.id = fm.family_id
       WHERE fm.user_id = $1`,
      [userId]
    );

    if (result.rows.length === 0) {
      res.json({ hasFamily: false });
      return;
    }

    res.json({ hasFamily: true, family: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Search families
export const searchFamilies = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { q, page = 1, limit = 20 } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    const result = await query(
      `SELECT f.*, COUNT(fm.id) as member_count
       FROM families f
       LEFT JOIN family_members fm ON f.id = fm.family_id
       WHERE f.is_public = true AND f.name ILIKE $1
       GROUP BY f.id
       ORDER BY member_count DESC
       LIMIT $2 OFFSET $3`,
      [`%${q}%`, limit, offset]
    );

    res.json({ families: result.rows });
  } catch (error) {
    next(error);
  }
};

// Get family ranking
export const getFamilyRanking = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { page = 1, limit = 20 } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    const result = await query(
      `SELECT f.*, COUNT(fm.id) as member_count
       FROM families f
       LEFT JOIN family_members fm ON f.id = fm.family_id
       GROUP BY f.id
       ORDER BY f.total_contribution DESC
       LIMIT $1 OFFSET $2`,
      [limit, offset]
    );

    res.json({ ranking: result.rows });
  } catch (error) {
    next(error);
  }
};

// Get family details
export const getFamily = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { familyId } = req.params;

    const result = await query(
      `SELECT f.*, COUNT(fm.id) as member_count
       FROM families f
       LEFT JOIN family_members fm ON f.id = fm.family_id
       WHERE f.id = $1
       GROUP BY f.id`,
      [familyId]
    );

    if (result.rows.length === 0) {
      throw new AppError('Family not found', 404, 'FAMILY_NOT_FOUND');
    }

    res.json({ family: result.rows[0] });
  } catch (error) {
    next(error);
  }
};

// Update family
export const updateFamily = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { familyId } = req.params;
    const { name, description, avatar_url, is_public, announcement } = req.body;

    // Check if user is owner or admin
    const memberResult = await query(
      'SELECT role FROM family_members WHERE family_id = $1 AND user_id = $2',
      [familyId, userId]
    );

    if (memberResult.rows.length === 0 || !['owner', 'admin'].includes(memberResult.rows[0].role)) {
      throw new AppError('Permission denied', 403, 'PERMISSION_DENIED');
    }

    await query(
      `UPDATE families SET name = COALESCE($1, name), description = COALESCE($2, description), 
       avatar_url = COALESCE($3, avatar_url), is_public = COALESCE($4, is_public), 
       announcement = COALESCE($5, announcement), updated_at = NOW()
       WHERE id = $6`,
      [name, description, avatar_url, is_public, announcement, familyId]
    );

    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Disband family
export const disbandFamily = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { familyId } = req.params;

    // Check if user is owner
    const familyResult = await query(
      'SELECT * FROM families WHERE id = $1 AND owner_id = $2',
      [familyId, userId]
    );

    if (familyResult.rows.length === 0) {
      throw new AppError('Only owner can disband family', 403, 'PERMISSION_DENIED');
    }

    // Delete all members and family
    await query('DELETE FROM family_members WHERE family_id = $1', [familyId]);
    await query('DELETE FROM families WHERE id = $1', [familyId]);

    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Join family
export const joinFamily = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { familyId } = req.params;

    // Check if already in a family
    const existingMember = await query(
      'SELECT * FROM family_members WHERE user_id = $1',
      [userId]
    );

    if (existingMember.rows.length > 0) {
      throw new AppError('You are already in a family', 400, 'ALREADY_IN_FAMILY');
    }

    // Check if family is public
    const familyResult = await query(
      'SELECT * FROM families WHERE id = $1 AND is_public = true',
      [familyId]
    );

    if (familyResult.rows.length === 0) {
      throw new AppError('Family not found or not public', 404, 'FAMILY_NOT_FOUND');
    }

    // Join family
    await query(
      `INSERT INTO family_members (family_id, user_id, role, joined_at)
       VALUES ($1, $2, 'member', NOW())`,
      [familyId, userId]
    );

    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Apply to family
export const applyToFamily = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { familyId } = req.params;
    const { message } = req.body;

    // Check if already in a family
    const existingMember = await query(
      'SELECT * FROM family_members WHERE user_id = $1',
      [userId]
    );

    if (existingMember.rows.length > 0) {
      throw new AppError('You are already in a family', 400, 'ALREADY_IN_FAMILY');
    }

    // Create application
    await query(
      `INSERT INTO family_applications (family_id, user_id, message, status, created_at)
       VALUES ($1, $2, $3, 'pending', NOW())`,
      [familyId, userId, message]
    );

    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Leave family
export const leaveFamily = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { familyId } = req.params;

    // Check if owner (owner can't leave without transferring)
    const memberResult = await query(
      'SELECT role FROM family_members WHERE family_id = $1 AND user_id = $2',
      [familyId, userId]
    );

    if (memberResult.rows.length === 0) {
      throw new AppError('Not a member of this family', 400, 'NOT_MEMBER');
    }

    if (memberResult.rows[0].role === 'owner') {
      throw new AppError('Owner must transfer ownership before leaving', 400, 'OWNER_CANT_LEAVE');
    }

    await query(
      'DELETE FROM family_members WHERE family_id = $1 AND user_id = $2',
      [familyId, userId]
    );

    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Invite member
export const inviteMember = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { familyId } = req.params;
    const { targetUserId } = req.body;

    // Check if user has permission
    const memberResult = await query(
      'SELECT role FROM family_members WHERE family_id = $1 AND user_id = $2',
      [familyId, userId]
    );

    if (memberResult.rows.length === 0 || !['owner', 'admin'].includes(memberResult.rows[0].role)) {
      throw new AppError('Permission denied', 403, 'PERMISSION_DENIED');
    }

    // Create invitation
    await query(
      `INSERT INTO family_invitations (family_id, from_user_id, to_user_id, status, created_at)
       VALUES ($1, $2, $3, 'pending', NOW())`,
      [familyId, userId, targetUserId]
    );

    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Kick member
export const kickMember = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { familyId, userId: targetUserId } = req.params;

    // Check if user has permission
    const memberResult = await query(
      'SELECT role FROM family_members WHERE family_id = $1 AND user_id = $2',
      [familyId, userId]
    );

    if (memberResult.rows.length === 0 || !['owner', 'admin'].includes(memberResult.rows[0].role)) {
      throw new AppError('Permission denied', 403, 'PERMISSION_DENIED');
    }

    // Can't kick owner
    const targetResult = await query(
      'SELECT role FROM family_members WHERE family_id = $1 AND user_id = $2',
      [familyId, targetUserId]
    );

    if (targetResult.rows[0]?.role === 'owner') {
      throw new AppError('Cannot kick owner', 400, 'CANNOT_KICK_OWNER');
    }

    await query(
      'DELETE FROM family_members WHERE family_id = $1 AND user_id = $2',
      [familyId, targetUserId]
    );

    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Update member role
export const updateMemberRole = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { familyId, userId: targetUserId } = req.params;
    const { role } = req.body;

    // Only owner can change roles
    const memberResult = await query(
      'SELECT role FROM family_members WHERE family_id = $1 AND user_id = $2',
      [familyId, userId]
    );

    if (memberResult.rows.length === 0 || memberResult.rows[0].role !== 'owner') {
      throw new AppError('Only owner can change roles', 403, 'PERMISSION_DENIED');
    }

    await query(
      'UPDATE family_members SET role = $1, updated_at = NOW() WHERE family_id = $2 AND user_id = $3',
      [role, familyId, targetUserId]
    );

    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Transfer ownership
export const transferOwnership = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const userId = req.user?.id;
    const { familyId } = req.params;
    const { newOwnerId } = req.body;

    // Check if current user is owner
    const familyResult = await query(
      'SELECT * FROM families WHERE id = $1 AND owner_id = $2',
      [familyId, userId]
    );

    if (familyResult.rows.length === 0) {
      throw new AppError('Only owner can transfer ownership', 403, 'PERMISSION_DENIED');
    }

    // Update family owner
    await query(
      'UPDATE families SET owner_id = $1, updated_at = NOW() WHERE id = $2',
      [newOwnerId, familyId]
    );

    // Update member roles
    await query(
      'UPDATE family_members SET role = $1, updated_at = NOW() WHERE family_id = $2 AND user_id = $3',
      ['owner', familyId, newOwnerId]
    );
    await query(
      'UPDATE family_members SET role = $1, updated_at = NOW() WHERE family_id = $2 AND user_id = $3',
      ['admin', familyId, userId]
    );

    res.json({ success: true });
  } catch (error) {
    next(error);
  }
};

// Get members
export const getMembers = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { familyId } = req.params;
    const { page = 1, limit = 50 } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    const result = await query(
      `SELECT fm.*, u.username, u.display_name, u.avatar_url, u.level, u.vip_tier
       FROM family_members fm
       JOIN users u ON fm.user_id = u.id
       WHERE fm.family_id = $1
       ORDER BY 
         CASE fm.role 
           WHEN 'owner' THEN 1 
           WHEN 'admin' THEN 2 
           WHEN 'moderator' THEN 3
           ELSE 4 
         END,
         fm.contribution DESC
       LIMIT $2 OFFSET $3`,
      [familyId, limit, offset]
    );

    res.json({ members: result.rows });
  } catch (error) {
    next(error);
  }
};

// Get activity
export const getActivity = async (req: Request, res: Response, next: NextFunction): Promise<void> => {
  try {
    const { familyId } = req.params;
    const { page = 1, limit = 20 } = req.query;
    const offset = (Number(page) - 1) * Number(limit);

    const result = await query(
      `SELECT fa.*, u.username, u.display_name
       FROM family_activity fa
       JOIN users u ON fa.user_id = u.id
       WHERE fa.family_id = $1
       ORDER BY fa.created_at DESC
       LIMIT $2 OFFSET $3`,
      [familyId, limit, offset]
    );

    res.json({ activity: result.rows });
  } catch (error) {
    next(error);
  }
};
