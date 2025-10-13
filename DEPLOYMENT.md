# Lucknow Healthcare Services - Deployment Guide

This guide provides comprehensive instructions for deploying the Lucknow Healthcare Services platform to production.

## 🏗️ Architecture Overview

The platform consists of:
- **Backend**: Spring Boot API with PostgreSQL database
- **Frontend**: Flutter web application for customers
- **Provider Dashboard**: React application for healthcare providers
- **Admin Dashboard**: React application for administrators
- **Infrastructure**: Docker containers with Nginx reverse proxy

## 📋 Prerequisites

### System Requirements
- **OS**: Linux (Ubuntu 20.04+ recommended) or macOS
- **RAM**: Minimum 4GB, Recommended 8GB+
- **Storage**: Minimum 20GB free space
- **CPU**: 2+ cores recommended

### Software Requirements
- **Docker**: 20.10+
- **Docker Compose**: 2.0+
- **Git**: Latest version
- **curl**: For health checks

### Environment Setup
1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd lucknow-healthcare
   ```

2. Create production environment file:
   ```bash
   cp env.example .env.production
   ```

3. Update `.env.production` with your production values:
   ```bash
   # Database Configuration
   DB_PASSWORD=your_secure_database_password
   
   # Redis Configuration
   REDIS_PASSWORD=your_secure_redis_password
   
   # JWT Configuration
   JWT_SECRET=your_super_secure_jwt_secret_key
   
   # Email Configuration
   MAIL_USERNAME=your_email@gmail.com
   MAIL_PASSWORD=your_app_password
   
   # Application URLs
   APP_URL=https://your-domain.com
   FRONTEND_URL=https://your-domain.com
   PROVIDER_DASHBOARD_URL=https://providers.your-domain.com
   ADMIN_DASHBOARD_URL=https://admin.your-domain.com
   API_URL=https://api.your-domain.com
   
   # Monitoring
   GRAFANA_PASSWORD=your_grafana_password
   ```

## 🚀 Quick Deployment

### Option 1: Automated Deployment (Recommended)
```bash
# Make deployment script executable
chmod +x scripts/deploy.sh

# Deploy all services
./scripts/deploy.sh deploy
```

### Option 2: Manual Deployment
```bash
# Build and start all services
docker-compose -f docker-compose.prod.yml --env-file .env.production up -d --build

# Check service status
docker-compose -f docker-compose.prod.yml ps

# View logs
docker-compose -f docker-compose.prod.yml logs -f
```

## 🔧 Service Configuration

### Backend Configuration
The Spring Boot backend includes:
- **Database**: PostgreSQL with Flyway migrations
- **Cache**: Redis for session management
- **Security**: JWT authentication with Spring Security
- **Email**: SMTP configuration for notifications
- **Monitoring**: Actuator endpoints for health checks

### Frontend Configuration
The Flutter web application includes:
- **Routing**: GoRouter for navigation
- **State Management**: BLoC pattern
- **API Integration**: Dio HTTP client
- **Offline Support**: Local caching with SharedPreferences

### Dashboard Configuration
Both React dashboards include:
- **API Integration**: Axios HTTP client
- **State Management**: React Query for server state
- **Authentication**: JWT token management
- **UI Components**: Tailwind CSS styling

## 📊 Monitoring and Health Checks

### Service URLs
- **Frontend**: http://localhost:3000
- **Provider Dashboard**: http://localhost:3001
- **Admin Dashboard**: http://localhost:3002
- **Backend API**: http://localhost:8080/api
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **Grafana Monitoring**: http://localhost:3003

### Health Check Endpoints
```bash
# Backend health
curl http://localhost:8080/api/actuator/health

# Frontend health
curl http://localhost:3000

# Provider dashboard health
curl http://localhost:3001

# Admin dashboard health
curl http://localhost:3002
```

### Monitoring Setup
1. **Grafana**: Access at http://localhost:3003
   - Default credentials: admin/admin
   - Pre-configured dashboards for application metrics

2. **Prometheus**: Access at http://localhost:9090
   - Metrics collection and storage
   - Service discovery and scraping

## 🧪 Testing

### Run All Tests
```bash
# Make test script executable
chmod +x scripts/test.sh

# Run comprehensive test suite
./scripts/test.sh all
```

### Individual Test Suites
```bash
# Backend tests only
./scripts/test.sh backend

# Frontend tests only
./scripts/test.sh frontend

# Dashboard tests only
./scripts/test.sh dashboards

# API integration tests
./scripts/test.sh api

# End-to-end tests
./scripts/test.sh e2e
```

### Test Coverage Reports
- **Backend**: `backend/target/site/jacoco/index.html`
- **Frontend**: `frontend/coverage/index.html`
- **Admin Dashboard**: `admin-dashboard/coverage/index.html`
- **Provider Dashboard**: `provider-dashboard/coverage/index.html`

## 🔒 Security Configuration

### SSL/TLS Setup
1. Obtain SSL certificates (Let's Encrypt recommended)
2. Update Nginx configuration with SSL settings
3. Configure HTTPS redirects
4. Update environment variables with HTTPS URLs

### Security Headers
The Nginx configuration includes:
- X-Frame-Options
- X-XSS-Protection
- X-Content-Type-Options
- Content-Security-Policy

### Database Security
- Use strong passwords
- Enable SSL connections
- Regular security updates
- Backup encryption

## 📈 Performance Optimization

### Backend Optimization
- JVM tuning with container-aware settings
- Database connection pooling
- Redis caching for sessions
- Gzip compression

### Frontend Optimization
- Flutter web build optimization
- Static asset caching
- CDN integration (optional)
- Service worker for offline support

### Database Optimization
- Proper indexing on frequently queried columns
- Connection pooling configuration
- Query optimization
- Regular maintenance

## 🔄 Backup and Recovery

### Database Backup
```bash
# Create database backup
docker-compose -f docker-compose.prod.yml exec postgres pg_dump -U healthcare_user lucknow_healthcare > backup.sql

# Restore from backup
docker-compose -f docker-compose.prod.yml exec -T postgres psql -U healthcare_user lucknow_healthcare < backup.sql
```

### Application Backup
```bash
# Backup application data
tar -czf app-backup-$(date +%Y%m%d).tar.gz \
  --exclude=node_modules \
  --exclude=target \
  --exclude=.git \
  .
```

## 🚨 Troubleshooting

### Common Issues

#### Services Not Starting
```bash
# Check service status
docker-compose -f docker-compose.prod.yml ps

# View service logs
docker-compose -f docker-compose.prod.yml logs [service-name]

# Restart specific service
docker-compose -f docker-compose.prod.yml restart [service-name]
```

#### Database Connection Issues
```bash
# Check database connectivity
docker-compose -f docker-compose.prod.yml exec backend curl -f http://localhost:8080/api/actuator/health

# Check database logs
docker-compose -f docker-compose.prod.yml logs postgres
```

#### Frontend Build Issues
```bash
# Rebuild frontend
docker-compose -f docker-compose.prod.yml build --no-cache frontend

# Check frontend logs
docker-compose -f docker-compose.prod.yml logs frontend
```

### Log Management
```bash
# View all logs
docker-compose -f docker-compose.prod.yml logs -f

# View specific service logs
docker-compose -f docker-compose.prod.yml logs -f backend

# View logs with timestamps
docker-compose -f docker-compose.prod.yml logs -f -t
```

## 🔄 Updates and Maintenance

### Application Updates
```bash
# Pull latest changes
git pull origin main

# Rebuild and restart services
docker-compose -f docker-compose.prod.yml up -d --build

# Run database migrations
docker-compose -f docker-compose.prod.yml exec backend ./mvnw flyway:migrate
```

### Security Updates
```bash
# Update base images
docker-compose -f docker-compose.prod.yml pull

# Rebuild with updated images
docker-compose -f docker-compose.prod.yml up -d --build
```

### Monitoring Updates
- Regular log rotation
- Database maintenance
- Security patches
- Performance monitoring

## 📞 Support

### Documentation
- [API Documentation](http://localhost:8080/swagger-ui.html)
- [System Architecture](docs/architecture/)
- [User Guides](docs/user-guides/)

### Monitoring
- [Grafana Dashboards](http://localhost:3003)
- [Application Logs](logs/)
- [Health Checks](http://localhost:8080/api/actuator/health)

### Contact
- **Technical Support**: support@lucknowhealthcare.com
- **Emergency**: +91-9876543210
- **Documentation**: [Project Wiki](wiki.md)

---

**Note**: This deployment guide assumes a production environment. For development setup, refer to the main [README.md](README.md) file.
