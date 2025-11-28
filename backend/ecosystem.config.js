/**
 * Aura Voice Chat - PM2 Ecosystem Configuration
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Usage:
 *   pm2 start ecosystem.config.js
 *   pm2 start ecosystem.config.js --env production
 */

module.exports = {
  apps: [
    {
      name: 'aura-backend',
      script: 'dist/index.js',
      instances: 'max',
      exec_mode: 'cluster',
      
      // Logging
      log_date_format: 'YYYY-MM-DD HH:mm:ss Z',
      error_file: '/var/log/pm2/aura-backend-error.log',
      out_file: '/var/log/pm2/aura-backend-out.log',
      merge_logs: true,
      
      // Restart behavior
      max_memory_restart: '512M',
      min_uptime: '5s',
      max_restarts: 10,
      restart_delay: 4000,
      
      // Watch (disable in production)
      watch: false,
      ignore_watch: ['node_modules', 'logs', '.git'],
      
      // Graceful shutdown
      kill_timeout: 5000,
      wait_ready: true,
      listen_timeout: 10000,
      
      // Environment variables
      env: {
        NODE_ENV: 'development',
        PORT: 3000,
      },
      env_staging: {
        NODE_ENV: 'staging',
        PORT: 3000,
      },
      env_production: {
        NODE_ENV: 'production',
        PORT: 3000,
      },
    },
  ],
  
  // Deployment configuration
  deploy: {
    production: {
      user: 'ubuntu',
      host: ['your-ec2-ip'],
      ref: 'origin/main',
      repo: 'git@github.com:venomvex/auravoicechatdoc.git',
      path: '/opt/aura-voice-chat',
      'pre-deploy-local': '',
      'post-deploy': 'cd backend && npm install && npm run build && pm2 reload ecosystem.config.js --env production',
      'pre-setup': '',
      ssh_options: 'StrictHostKeyChecking=no',
    },
    staging: {
      user: 'ubuntu',
      host: ['your-staging-ip'],
      ref: 'origin/develop',
      repo: 'git@github.com:venomvex/auravoicechatdoc.git',
      path: '/opt/aura-voice-chat',
      'post-deploy': 'cd backend && npm install && npm run build && pm2 reload ecosystem.config.js --env staging',
    },
  },
};
