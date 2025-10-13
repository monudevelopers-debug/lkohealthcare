# Lucknow Nursing Healthcare Services - MVP

A comprehensive healthcare services platform enabling customers to book nursing, elderly care, physiotherapy, child care, and ambulance services through a cross-platform mobile application and web dashboards.

## 🏥 Project Overview

This MVP focuses on core service booking, provider management, and secure transactions for the Lucknow region. The platform connects healthcare service providers with customers through a seamless digital experience.

## 🚀 MVP Features

### Customer Features (Mobile App)
- **User Registration & Authentication** - Secure JWT-based authentication
- **Service Booking** - Book nursing, elderly care, physiotherapy, child care, and ambulance services
- **Booking Management** - View, reschedule, and cancel bookings
- **Payment Integration** - Secure online payments via Razorpay/Stripe
- **Real-time Updates** - Track booking status and provider updates

### Provider Features (Web Dashboard)
- **Provider Dashboard** - Manage assigned bookings and availability
- **Booking Updates** - Update booking status and add notes
- **Earnings Tracking** - View earnings and performance metrics
- **Profile Management** - Update qualifications and availability

### Admin Features (Web Dashboard)
- **User Management** - Manage customers and providers
- **Booking Oversight** - Monitor and assign bookings
- **Provider Management** - Verify and manage healthcare providers
- **Basic Analytics** - Revenue and booking insights

## 🛠 Technology Stack

### Backend
- **Framework**: Spring Boot 3.x (Monolithic)
- **Database**: PostgreSQL 15
- **Authentication**: JWT with Spring Security
- **Payment Gateway**: Razorpay/Stripe
- **Notifications**: SMTP Email

### Frontend
- **Mobile App**: Flutter 3.x with BLoC pattern
- **Provider Dashboard**: React 18 with TypeScript
- **Admin Dashboard**: React 18 with TypeScript
- **UI Framework**: Tailwind CSS

### Deployment
- **Containerization**: Docker
- **Cloud Provider**: Railway/Render/AWS
- **CI/CD**: GitHub Actions

## 📁 Project Structure

```
lucknow-nursing-healthcare/
├── backend/                 # Spring Boot REST API
├── frontend/               # Flutter mobile app
├── provider-dashboard/    # React provider dashboard
├── admin-dashboard/        # React admin dashboard
├── database/              # Database migrations and seeds
├── docs/                  # API and deployment documentation
└── scripts/               # Build and deployment scripts
```

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- Flutter 3.x
- PostgreSQL 15
- Docker (optional)

### Development Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd lucknow-nursing-healthcare
   ```

2. **Backend Setup**
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

3. **Frontend Setup**
   ```bash
   cd frontend
   flutter pub get
   flutter run
   ```

4. **Provider Dashboard**
   ```bash
   cd provider-dashboard
   npm install
   npm run dev
   ```

5. **Admin Dashboard**
   ```bash
   cd admin-dashboard
   npm install
   npm run dev
   ```

6. **Database Setup**
   ```bash
   cd database
   ./scripts/migrate.sh
   ```

## 📋 MVP Timeline (8 Weeks)

| Phase | Duration | Deliverables |
|-------|----------|-------------|
| **Phase 1: Core Setup** | Week 1–2 | DB schema, backend boilerplate, API skeleton |
| **Phase 2: Authentication + Booking APIs** | Week 3–4 | User registration, booking CRUD, provider assignment |
| **Phase 3: Flutter App UI & Integration** | Week 5–6 | Mobile app screens + API integration |
| **Phase 4: Admin Dashboard + QA** | Week 7 | Dashboard build + testing |
| **Phase 5: Launch & Feedback Loop** | Week 8 | Deploy on AWS / Play Store beta |

## 🔧 Core APIs

- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User authentication
- `GET /api/users/profile` - User profile management
- `GET /api/services/list` - Service catalog
- `POST /api/bookings/create` - Create booking
- `GET /api/bookings/user/{id}` - User bookings
- `GET /api/bookings/provider/{id}` - Provider bookings
- `POST /api/payments/initiate` - Payment processing

## 🗄️ Database Schema

7 core tables:
- `users` - User accounts (customers, providers, admins)
- `service_categories` - Service categories
- `services` - Individual services
- `bookings` - Service bookings
- `providers` - Healthcare providers
- `payments` - Payment records
- `reviews` - Customer reviews

## 📚 Documentation

- [Product Requirements Document](./prd.md)
- [System Design](./system_design.md)
- [Architecture Diagram](./architect.plantuml)
- [Database Schema](./er_diagram.plantuml)
- [API Documentation](./docs/api/)
- [Deployment Guide](./docs/deployment/)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support and questions, please contact the development team or create an issue in the repository.

---

*This MVP serves as the foundation for the Lucknow Nursing Healthcare Services platform, focusing on core functionality that can be delivered in 8 weeks to validate market demand and operational workflows.*
