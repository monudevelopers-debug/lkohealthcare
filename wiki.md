# Lucknow Nursing Healthcare Services - MVP Project Wiki

## Project Summary

This project is a **Minimum Viable Product (MVP)** for a healthcare services platform designed to provide nursing, elderly care, physiotherapy, child care, and ambulance services through a cross-platform mobile application and web dashboards. The MVP focuses on core functionality that can be delivered in **8 weeks** to validate market demand and operational workflows.

## MVP Functional Modules

### 1. User Management
- **Customer Registration & Authentication**: Secure user registration with email verification
- **Provider Registration**: Healthcare provider onboarding with document verification
- **Profile Management**: Basic user and provider profiles
- **Role-based Access Control**: Customer, Provider, and Admin access levels

### 2. Service Management
- **Service Catalog**: Basic listing of healthcare services (Nursing, Child Care, Elderly Care, Physiotherapy, Ambulance)
- **Service Categories**: Organized service types with descriptions
- **Basic Pricing**: Fixed pricing for services

### 3. Booking Management
- **Service Booking**: Simple booking workflow with provider assignment
- **Booking Tracking**: Basic status updates (Pending, Confirmed, In-Progress, Completed)
- **Rescheduling & Cancellation**: Basic booking management options

### 4. Provider Management
- **Provider Registration**: Basic provider onboarding
- **Availability Management**: Simple availability tracking
- **Provider Dashboard**: Web interface for providers to manage bookings

### 5. Payment Integration (Phase 1.5)
- **Payment Gateway**: Razorpay/Stripe integration for online payments
- **Basic Transactions**: Simple payment processing
- **Payment History**: Basic payment tracking

### 6. Notification System
- **Email Notifications**: Basic email notifications for booking confirmations
- **Status Updates**: Simple booking status notifications

### 7. Admin Dashboard
- **Basic Analytics**: Simple revenue and booking analytics
- **User Management**: Basic user and provider management
- **Booking Management**: Simple booking oversight
- **Provider Management**: Basic provider verification and management

## MVP Directory Tree

The complete MVP project structure is defined in [filetree.md](./filetree.md).

## MVP File Description Inventory

### Backend (Spring Boot - Monolithic)
- **Entity Classes**: 7 core JPA entities (User, ServiceCategory, Service, Booking, Provider, Payment, Review)
- **Repository Layer**: Simple data access with Spring Data JPA
- **Service Layer**: Basic business logic implementation
- **Controller Layer**: REST API endpoints for MVP functionality
- **Security Configuration**: JWT authentication and authorization
- **External Integrations**: Basic email service and payment gateway

### Frontend (Flutter)
- **Core Module**: Basic utilities and network management
- **Data Layer**: Simple models and data sources
- **Domain Layer**: Basic business entities
- **Presentation Layer**: UI components and state management (BLoC)
- **Localization**: English language support

### Provider Dashboard (React/TypeScript)
- **Component Library**: Basic UI components
- **Page Components**: Provider dashboard screens (bookings, earnings, profile)
- **Service Integration**: API integration for provider functionality
- **State Management**: Basic state management

### Admin Dashboard (React/TypeScript)
- **Component Library**: Basic UI components
- **Page Components**: Admin dashboard screens (users, bookings, providers, analytics)
- **Service Integration**: API integration for admin functionality
- **State Management**: Basic state management

### Database (PostgreSQL)
- **Migrations**: 7 core table migrations
- **Seeds**: Basic initial data
- **Scripts**: Simple database maintenance

### Deployment (Simplified)
- **Docker Configuration**: Basic containerization
- **Cloud Deployment**: Simple deployment to Railway/Render/AWS
- **CI/CD**: Basic automated testing and deployment

## MVP Technology Stack

### Backend
- **Framework**: Spring Boot 3.x (Monolithic)
- **Database**: PostgreSQL 15
- **Authentication**: JWT with Spring Security
- **Payment Gateway**: Razorpay/Stripe (Phase 1.5)
- **Notifications**: SMTP Email

### Frontend
- **Framework**: Flutter 3.x
- **State Management**: BLoC Pattern
- **HTTP Client**: Dio
- **Localization**: English

### Provider Dashboard
- **Framework**: React 18 with TypeScript
- **UI Library**: Basic components with Tailwind CSS
- **State Management**: Basic React hooks

### Admin Dashboard
- **Framework**: React 18 with TypeScript
- **UI Library**: Basic components with Tailwind CSS
- **State Management**: Basic React hooks

### Deployment
- **Containerization**: Docker (Basic)
- **Cloud Provider**: Railway/Render/AWS
- **CI/CD**: GitHub Actions (Basic)

## MVP Usage Instructions

### Development Setup
1. **Clone Repository**: `git clone <repository-url>`
2. **Backend Setup**: Navigate to `backend/` and run `./mvnw spring-boot:run`
3. **Frontend Setup**: Navigate to `frontend/` and run `flutter run`
4. **Provider Dashboard**: Navigate to `provider-dashboard/` and run `npm run dev`
5. **Admin Dashboard**: Navigate to `admin-dashboard/` and run `npm run dev`
6. **Database Setup**: Run migrations using `./scripts/migrate.sh`

### Production Deployment
1. **Build Images**: `docker-compose build`
2. **Deploy to Cloud**: Deploy to Railway/Render/AWS
3. **Configure Domain**: Set up domain and SSL
4. **Monitor Deployment**: Basic monitoring and logging

### API Documentation
- **Swagger UI**: Available at `/swagger-ui.html` when running backend
- **Authentication**: Use JWT tokens for API access
- **Core APIs**: 8 essential APIs for MVP functionality

### Testing
- **Unit Tests**: Run `./mvnw test` for backend tests
- **Integration Tests**: Run `flutter test` for frontend tests
- **Basic E2E Tests**: Simple end-to-end testing

## MVP Timeline (8 Weeks)

| Phase | Duration | Deliverables |
|-------|----------|-------------|
| **Phase 1: Core Setup** | Week 1–2 | DB schema, backend boilerplate, API skeleton |
| **Phase 2: Authentication + Booking APIs** | Week 3–4 | User registration, booking CRUD, provider assignment |
| **Phase 3: Flutter App UI & Integration** | Week 5–6 | Mobile app screens + API integration |
| **Phase 4: Admin Dashboard + QA** | Week 7 | Dashboard build + testing |
| **Phase 5: Launch & Feedback Loop** | Week 8 | Deploy on AWS / Play Store beta |

## MVP Core APIs

- `/api/auth/register`
- `/api/auth/login`
- `/api/users/profile`
- `/api/services/list`
- `/api/bookings/create`
- `/api/bookings/user/{id}`
- `/api/bookings/provider/{id}`
- `/api/payments/initiate` *(Phase 1.5)*

## MVP Database Schema

7 core tables:
- `users` - User accounts (customers, providers, admins)
- `service_categories` - Service categories
- `services` - Individual services
- `bookings` - Service bookings
- `providers` - Healthcare providers
- `payments` - Payment records (Phase 1.5)
- `reviews` - Customer reviews (Phase 1.5)

## Documentation Links

- **[PRD](./prd.md)** - Product Requirements Document (MVP focused)
- **[System Design](./system_design.md)** - Technical implementation approach
- **[File Tree](./filetree.md)** - Project structure and organization
- **[Architecture Diagram](./architect.plantuml)** - System architecture visualization
- **[Class Diagram](./classDiagram.plantuml)** - Core entity relationships
- **[ER Diagram](./er_diagram.plantuml)** - Database schema
- **[Sequence Diagram](./sequence_diagram.plantuml)** - Key user flows
- **[UI Navigation](./ui_navigation.plantuml)** - User interface navigation flows

---

*This MVP wiki serves as the central documentation hub for the Lucknow Nursing Healthcare Services platform. The focus is on core functionality that can be delivered in 8 weeks to validate market demand and operational workflows.*