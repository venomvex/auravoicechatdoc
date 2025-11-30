/**
 * Family Routes
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Family system with rankings and contributions
 */

import { Router } from 'express';
import { authenticate, optionalAuth } from '../middleware/auth';
import { generalLimiter } from '../middleware/rateLimiter';
import * as familyController from '../controllers/familyController';

const router = Router();

// Apply rate limiting
router.use(generalLimiter);

// ==================== FAMILY RANKINGS (Public) ====================
router.get('/rankings', optionalAuth, familyController.getFamilyRankings);

// ==================== AUTHENTICATED ROUTES ====================
router.use(authenticate);

// Family CRUD
router.post('/create', familyController.createFamily);
router.get('/my', familyController.getMyFamily);
router.get('/search', familyController.searchFamilies);
router.get('/ranking', familyController.getFamilyRanking); // Legacy endpoint
router.get('/:familyId', familyController.getFamily);
router.put('/:familyId', familyController.updateFamily);
router.delete('/:familyId', familyController.disbandFamily);

// Membership
router.post('/:familyId/join', familyController.joinFamily);
router.post('/:familyId/apply', familyController.applyToFamily);
router.post('/:familyId/leave', familyController.leaveFamily);
router.post('/:familyId/invite', familyController.inviteMember);
router.post('/:familyId/kick/:userId', familyController.kickMember);
router.post('/:familyId/role/:userId', familyController.updateMemberRole);
router.post('/:familyId/transfer', familyController.transferOwnership);

// Members
router.get('/:familyId/members', familyController.getMembers);
router.get('/:familyId/activity', familyController.getActivity);

// Contributions
router.get('/:familyId/contributions', familyController.getFamilyMemberContributions);
router.post('/:familyId/contributions', familyController.recordFamilyContribution);

export default router;
