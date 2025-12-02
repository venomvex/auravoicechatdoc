/**
 * Database Pool Re-export
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Re-exports the database pool from database.config.ts for convenience.
 * Services can import from either location:
 *   - import pool from '../database/pool'
 *   - import { pool } from '../config/database.config'
 */

export { pool as default, pool, query, transaction, checkDatabaseConnection, getPoolStats, closePool } from '../config/database.config';
