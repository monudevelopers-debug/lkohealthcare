# Project File Structure - MVP
## Lucknow Nursing Healthcare Services

```
lucknow-nursing-healthcare/
├── README.md
├── docker-compose.yml
├── .env.example
├── .gitignore
├── docs/
│   ├── api/
│   │   ├── authentication.md
│   │   ├── user-management.md
│   │   ├── booking-management.md
│   │   ├── provider-management.md
│   │   ├── payment-integration.md
│   │   └── notification-system.md
│   ├── deployment/
│   │   ├── docker-setup.md
│   │   ├── database-migration.md
│   │   └── production-deployment.md
│   └── architecture/
│       ├── system-design.md
│       ├── database-schema.md
│       └── security-guidelines.md
│
├── backend/
│   ├── pom.xml
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── lucknow/
│   │   │   │           └── healthcare/
│   │   │   │               ├── LucknowHealthcareApplication.java
│   │   │   │               ├── config/
│   │   │   │               │   ├── SecurityConfig.java
│   │   │   │               │   ├── DatabaseConfig.java
│   │   │   │               │   ├── RedisConfig.java
│   │   │   │               │   ├── EmailConfig.java
│   │   │   │               │   ├── FileUploadConfig.java
│   │   │   │               │   └── PaymentConfig.java
│   │   │   │               ├── entity/
│   │   │   │               │   ├── User.java
│   │   │   │               │   ├── ServiceCategory.java
│   │   │   │               │   ├── Service.java
│   │   │   │               │   ├── Booking.java
│   │   │   │               │   ├── Provider.java
│   │   │   │               │   ├── Payment.java
│   │   │   │               │   └── Review.java
│   │   │   │               ├── repository/
│   │   │   │               │   ├── UserRepository.java
│   │   │   │               │   ├── ServiceCategoryRepository.java
│   │   │   │               │   ├── ServiceRepository.java
│   │   │   │               │   ├── BookingRepository.java
│   │   │   │               │   ├── ProviderRepository.java
│   │   │   │               │   ├── PaymentRepository.java
│   │   │   │               │   └── ReviewRepository.java
│   │   │   │               ├── service/
│   │   │   │               │   ├── interfaces/
│   │   │   │               │   │   ├── IUserService.java
│   │   │   │               │   │   ├── IBookingService.java
│   │   │   │               │   │   ├── IProviderService.java
│   │   │   │               │   │   ├── IPaymentService.java
│   │   │   │               │   │   └── INotificationService.java
│   │   │   │               │   ├── impl/
│   │   │   │               │   │   ├── UserServiceImpl.java
│   │   │   │               │   │   ├── BookingServiceImpl.java
│   │   │   │               │   │   ├── ProviderServiceImpl.java
│   │   │   │               │   │   ├── PaymentServiceImpl.java
│   │   │   │               │   │   └── NotificationServiceImpl.java
│   │   │   │               │   └── external/
│   │   │   │               │       ├── EmailService.java
│   │   │   │               │       ├── RazorpayService.java
│   │   │   │               │       └── StripeService.java
│   │   │   │               ├── controller/
│   │   │   │               │   ├── AuthController.java
│   │   │   │               │   ├── UserController.java
│   │   │   │               │   ├── BookingController.java
│   │   │   │               │   ├── ProviderController.java
│   │   │   │               │   ├── ServiceController.java
│   │   │   │               │   ├── PaymentController.java
│   │   │   │               │   ├── NotificationController.java
│   │   │   │               │   └── AdminController.java
│   │   │   │               ├── dto/
│   │   │   │               │   ├── request/
│   │   │   │               │   │   ├── UserRegistrationDto.java
│   │   │   │               │   │   ├── LoginDto.java
│   │   │   │               │   │   ├── CreateBookingDto.java
│   │   │   │               │   │   ├── CreateProviderDto.java
│   │   │   │               │   │   ├── PaymentRequestDto.java
│   │   │   │               │   │   └── RescheduleBookingDto.java
│   │   │   │               │   ├── response/
│   │   │   │               │   │   ├── UserDto.java
│   │   │   │               │   │   ├── AuthResponseDto.java
│   │   │   │               │   │   ├── BookingDto.java
│   │   │   │               │   │   ├── ProviderDto.java
│   │   │   │               │   │   ├── PaymentResponseDto.java
│   │   │   │               │   │   └── ReviewDto.java
│   │   │   │               │   └── common/
│   │   │   │               │       ├── ApiResponse.java
│   │   │   │               │       ├── ErrorResponse.java
│   │   │   │               │       └── PaginationDto.java
│   │   │   │               ├── security/
│   │   │   │               │   ├── JwtTokenProvider.java
│   │   │   │               │   ├── JwtAuthenticationFilter.java
│   │   │   │               │   ├── CustomUserDetailsService.java
│   │   │   │               │   └── SecurityUtils.java
│   │   │   │               ├── exception/
│   │   │   │               │   ├── GlobalExceptionHandler.java
│   │   │   │               │   ├── UserNotFoundException.java
│   │   │   │               │   ├── OrderNotFoundException.java
│   │   │   │               │   ├── PaymentProcessingException.java
│   │   │   │               │   └── ValidationException.java
│   │   │   │               ├── util/
│   │   │   │               │   ├── DateTimeUtils.java
│   │   │   │               │   ├── ValidationUtils.java
│   │   │   │               │   ├── FileUtils.java
│   │   │   │               │   ├── EncryptionUtils.java
│   │   │   │               │   └── EmailTemplateUtils.java
│   │   │   │               └── enums/
│   │   │   │                   ├── UserRole.java
│   │   │   │                   ├── UserStatus.java
│   │   │   │                   ├── OrderStatus.java
│   │   │   │                   ├── PaymentStatus.java
│   │   │   │                   ├── ServiceCategory.java
│   │   │   │                   ├── StaffRole.java
│   │   │   │                   ├── AvailabilityStatus.java
│   │   │   │                   ├── NotificationType.java
│   │   │   │                   └── PaymentMethod.java
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       ├── application-dev.yml
│   │   │       ├── application-prod.yml
│   │   │       ├── db/
│   │   │       │   └── migration/
│   │   │       │       ├── V1__Create_users_table.sql
│   │   │       │       ├── V2__Create_services_table.sql
│   │   │       │       ├── V3__Create_staff_table.sql
│   │   │       │       ├── V4__Create_orders_table.sql
│   │   │       │       ├── V5__Create_payments_table.sql
│   │   │       │       ├── V6__Create_medical_equipment_table.sql
│   │   │       │       ├── V7__Create_notifications_table.sql
│   │   │       │       ├── V8__Create_ambulance_services_table.sql
│   │   │       │       ├── V9__Create_patient_details_table.sql
│   │   │       │       ├── V10__Create_dealers_table.sql
│   │   │       │       └── V11__Create_indexes_and_constraints.sql
│   │   │       ├── templates/
│   │   │       │   ├── email/
│   │   │       │   │   ├── welcome.html
│   │   │       │   │   ├── email-verification.html
│   │   │       │   │   ├── password-reset.html
│   │   │       │   │   ├── order-confirmation.html
│   │   │       │   │   ├── payment-confirmation.html
│   │   │       │   │   └── appointment-reminder.html
│   │   │       │   └── sms/
│   │   │       │       ├── order-confirmation.txt
│   │   │       │       ├── payment-confirmation.txt
│   │   │       │       └── appointment-reminder.txt
│   │   │       └── static/
│   │   │           ├── images/
│   │   │           ├── css/
│   │   │           └── js/
│   │   └── test/
│   │       ├── java/
│   │       │   └── com/
│   │       │       └── lucknow/
│   │       │           └── healthcare/
│   │       │               ├── controller/
│   │       │               │   ├── AuthControllerTest.java
│   │       │               │   ├── UserControllerTest.java
│   │       │               │   ├── OrderControllerTest.java
│   │       │               │   └── PaymentControllerTest.java
│   │       │               ├── service/
│   │       │               │   ├── UserServiceTest.java
│   │       │               │   ├── OrderServiceTest.java
│   │       │               │   ├── StaffServiceTest.java
│   │       │               │   └── PaymentServiceTest.java
│   │       │               ├── repository/
│   │       │               │   ├── UserRepositoryTest.java
│   │       │               │   ├── OrderRepositoryTest.java
│   │       │               │   └── StaffRepositoryTest.java
│   │       │               └── integration/
│   │       │                   ├── AuthIntegrationTest.java
│   │       │                   ├── OrderFlowIntegrationTest.java
│   │       │                   └── PaymentIntegrationTest.java
│   │       └── resources/
│   │           ├── application-test.yml
│   │           └── test-data/
│   │               ├── users.sql
│   │               ├── services.sql
│   │               └── staff.sql
│   └── Dockerfile
│
├── frontend/
│   ├── pubspec.yaml
│   ├── analysis_options.yaml
│   ├── android/
│   │   ├── app/
│   │   │   ├── build.gradle
│   │   │   └── src/
│   │   │       └── main/
│   │   │           ├── AndroidManifest.xml
│   │   │           └── kotlin/
│   │   └── build.gradle
│   ├── ios/
│   │   ├── Runner/
│   │   │   ├── Info.plist
│   │   │   └── Runner.xcodeproj/
│   │   └── Podfile
│   ├── web/
│   │   ├── index.html
│   │   ├── manifest.json
│   │   └── favicon.png
│   ├── lib/
│   │   ├── main.dart
│   │   ├── app.dart
│   │   ├── core/
│   │   │   ├── constants/
│   │   │   │   ├── app_constants.dart
│   │   │   │   ├── api_endpoints.dart
│   │   │   │   ├── colors.dart
│   │   │   │   ├── strings.dart
│   │   │   │   └── dimensions.dart
│   │   │   ├── network/
│   │   │   │   ├── api_client.dart
│   │   │   │   ├── network_info.dart
│   │   │   │   └── interceptors/
│   │   │   │       ├── auth_interceptor.dart
│   │   │   │       └── logging_interceptor.dart
│   │   │   ├── storage/
│   │   │   │   ├── local_storage.dart
│   │   │   │   └── secure_storage.dart
│   │   │   ├── utils/
│   │   │   │   ├── validators.dart
│   │   │   │   ├── date_utils.dart
│   │   │   │   ├── currency_formatter.dart
│   │   │   │   └── notification_utils.dart
│   │   │   └── error/
│   │   │       ├── exceptions.dart
│   │   │       └── failures.dart
│   │   ├── data/
│   │   │   ├── models/
│   │   │   │   ├── user_model.dart
│   │   │   │   ├── order_model.dart
│   │   │   │   ├── service_model.dart
│   │   │   │   ├── staff_model.dart
│   │   │   │   ├── payment_model.dart
│   │   │   │   └── notification_model.dart
│   │   │   ├── repositories/
│   │   │   │   ├── auth_repository.dart
│   │   │   │   ├── user_repository.dart
│   │   │   │   ├── order_repository.dart
│   │   │   │   ├── service_repository.dart
│   │   │   │   ├── staff_repository.dart
│   │   │   │   └── payment_repository.dart
│   │   │   └── datasources/
│   │   │       ├── remote/
│   │   │       │   ├── auth_remote_datasource.dart
│   │   │       │   ├── user_remote_datasource.dart
│   │   │       │   ├── order_remote_datasource.dart
│   │   │       │   └── payment_remote_datasource.dart
│   │   │       └── local/
│   │   │           ├── auth_local_datasource.dart
│   │   │           └── user_local_datasource.dart
│   │   ├── domain/
│   │   │   ├── entities/
│   │   │   │   ├── user.dart
│   │   │   │   ├── order.dart
│   │   │   │   ├── service.dart
│   │   │   │   ├── staff.dart
│   │   │   │   └── payment.dart
│   │   │   ├── repositories/
│   │   │   │   ├── auth_repository.dart
│   │   │   │   ├── user_repository.dart
│   │   │   │   ├── order_repository.dart
│   │   │   │   └── payment_repository.dart
│   │   │   └── usecases/
│   │   │       ├── auth/
│   │   │       │   ├── login_usecase.dart
│   │   │       │   ├── register_usecase.dart
│   │   │       │   └── logout_usecase.dart
│   │   │       ├── order/
│   │   │       │   ├── create_order_usecase.dart
│   │   │       │   ├── get_orders_usecase.dart
│   │   │       │   └── cancel_order_usecase.dart
│   │   │       └── payment/
│   │   │           ├── process_payment_usecase.dart
│   │   │           └── get_payment_history_usecase.dart
│   │   ├── presentation/
│   │   │   ├── bloc/
│   │   │   │   ├── auth/
│   │   │   │   │   ├── auth_bloc.dart
│   │   │   │   │   ├── auth_event.dart
│   │   │   │   │   └── auth_state.dart
│   │   │   │   ├── order/
│   │   │   │   │   ├── order_bloc.dart
│   │   │   │   │   ├── order_event.dart
│   │   │   │   │   └── order_state.dart
│   │   │   │   ├── service/
│   │   │   │   │   ├── service_bloc.dart
│   │   │   │   │   ├── service_event.dart
│   │   │   │   │   └── service_state.dart
│   │   │   │   └── payment/
│   │   │   │       ├── payment_bloc.dart
│   │   │   │       ├── payment_event.dart
│   │   │   │       └── payment_state.dart
│   │   │   ├── pages/
│   │   │   │   ├── auth/
│   │   │   │   │   ├── login_page.dart
│   │   │   │   │   ├── register_page.dart
│   │   │   │   │   ├── forgot_password_page.dart
│   │   │   │   │   └── email_verification_page.dart
│   │   │   │   ├── home/
│   │   │   │   │   ├── home_page.dart
│   │   │   │   │   ├── dashboard_page.dart
│   │   │   │   │   └── emergency_page.dart
│   │   │   │   ├── services/
│   │   │   │   │   ├── service_list_page.dart
│   │   │   │   │   ├── service_detail_page.dart
│   │   │   │   │   └── booking_page.dart
│   │   │   │   ├── orders/
│   │   │   │   │   ├── order_list_page.dart
│   │   │   │   │   ├── order_detail_page.dart
│   │   │   │   │   └── order_tracking_page.dart
│   │   │   │   ├── profile/
│   │   │   │   │   ├── profile_page.dart
│   │   │   │   │   ├── edit_profile_page.dart
│   │   │   │   │   ├── patient_management_page.dart
│   │   │   │   │   └── payment_history_page.dart
│   │   │   │   ├── payment/
│   │   │   │   │   ├── payment_page.dart
│   │   │   │   │   ├── payment_success_page.dart
│   │   │   │   │   └── payment_failure_page.dart
│   │   │   │   └── admin/
│   │   │   │       ├── admin_dashboard_page.dart
│   │   │   │       ├── staff_management_page.dart
│   │   │   │       ├── order_management_page.dart
│   │   │   │       └── analytics_page.dart
│   │   │   ├── widgets/
│   │   │   │   ├── common/
│   │   │   │   │   ├── custom_button.dart
│   │   │   │   │   ├── custom_text_field.dart
│   │   │   │   │   ├── loading_widget.dart
│   │   │   │   │   ├── error_widget.dart
│   │   │   │   │   └── custom_app_bar.dart
│   │   │   │   ├── service/
│   │   │   │   │   ├── service_card.dart
│   │   │   │   │   ├── service_category_grid.dart
│   │   │   │   │   └── booking_form.dart
│   │   │   │   ├── order/
│   │   │   │   │   ├── order_card.dart
│   │   │   │   │   ├── order_status_indicator.dart
│   │   │   │   │   └── order_timeline.dart
│   │   │   │   └── payment/
│   │   │   │       ├── payment_method_selector.dart
│   │   │   │       ├── payment_summary.dart
│   │   │   │       └── invoice_widget.dart
│   │   │   └── routes/
│   │   │       ├── app_routes.dart
│   │   │       └── route_generator.dart
│   │   └── l10n/
│   │       ├── app_en.arb
│   │       ├── app_hi.arb
│   │       └── l10n.yaml
│   ├── test/
│   │   ├── unit/
│   │   │   ├── bloc/
│   │   │   ├── repositories/
│   │   │   └── usecases/
│   │   ├── widget/
│   │   │   ├── auth/
│   │   │   ├── home/
│   │   │   └── order/
│   │   └── integration/
│   │       ├── auth_flow_test.dart
│   │       ├── booking_flow_test.dart
│   │       └── payment_flow_test.dart
│   └── assets/
│       ├── images/
│       │   ├── logo.png
│       │   ├── icons/
│       │   └── illustrations/
│       ├── fonts/
│       └── translations/
│
├── provider-dashboard/
│   ├── package.json
│   ├── tsconfig.json
│   ├── tailwind.config.js
│   ├── vite.config.ts
│   ├── index.html
│   ├── src/
│   │   ├── main.tsx
│   │   ├── App.tsx
│   │   ├── components/
│   │   │   ├── ui/
│   │   │   │   ├── button.tsx
│   │   │   │   ├── input.tsx
│   │   │   │   ├── card.tsx
│   │   │   │   └── table.tsx
│   │   │   ├── layout/
│   │   │   │   ├── Sidebar.tsx
│   │   │   │   ├── Header.tsx
│   │   │   │   └── Layout.tsx
│   │   │   ├── dashboard/
│   │   │   │   ├── MetricsCard.tsx
│   │   │   │   └── RecentBookings.tsx
│   │   │   ├── bookings/
│   │   │   │   ├── BookingTable.tsx
│   │   │   │   ├── BookingDetails.tsx
│   │   │   │   └── StatusUpdate.tsx
│   │   │   └── profile/
│   │   │       ├── ProfileForm.tsx
│   │   │       └── AvailabilityForm.tsx
│   │   ├── pages/
│   │   │   ├── Dashboard.tsx
│   │   │   ├── Bookings.tsx
│   │   │   ├── Earnings.tsx
│   │   │   └── Profile.tsx
│   │   ├── hooks/
│   │   │   ├── useAuth.ts
│   │   │   ├── useBookings.ts
│   │   │   └── useProvider.ts
│   │   ├── services/
│   │   │   ├── api.ts
│   │   │   ├── auth.ts
│   │   │   ├── bookings.ts
│   │   │   └── provider.ts
│   │   ├── types/
│   │   │   ├── auth.ts
│   │   │   ├── booking.ts
│   │   │   └── provider.ts
│   │   └── utils/
│   │       ├── constants.ts
│   │       ├── formatters.ts
│   │       └── validators.ts
│   └── public/
│       ├── favicon.ico
│       └── images/
│
├── admin-dashboard/
│   ├── package.json
│   ├── tsconfig.json
│   ├── tailwind.config.js
│   ├── vite.config.ts
│   ├── index.html
│   ├── src/
│   │   ├── main.tsx
│   │   ├── App.tsx
│   │   ├── components/
│   │   │   ├── ui/
│   │   │   │   ├── button.tsx
│   │   │   │   ├── input.tsx
│   │   │   │   ├── card.tsx
│   │   │   │   └── table.tsx
│   │   │   ├── layout/
│   │   │   │   ├── Sidebar.tsx
│   │   │   │   ├── Header.tsx
│   │   │   │   └── Layout.tsx
│   │   │   ├── dashboard/
│   │   │   │   ├── MetricsCard.tsx
│   │   │   │   └── RecentBookings.tsx
│   │   │   ├── bookings/
│   │   │   │   ├── BookingTable.tsx
│   │   │   │   ├── BookingDetails.tsx
│   │   │   │   └── BookingFilters.tsx
│   │   │   ├── providers/
│   │   │   │   ├── ProviderTable.tsx
│   │   │   │   ├── ProviderForm.tsx
│   │   │   │   └── ProviderSchedule.tsx
│   │   │   └── analytics/
│   │   │       ├── BasicCharts.tsx
│   │   │       └── ReportsTable.tsx
│   │   ├── pages/
│   │   │   ├── Dashboard.tsx
│   │   │   ├── Bookings.tsx
│   │   │   ├── Providers.tsx
│   │   │   ├── Services.tsx
│   │   │   └── Analytics.tsx
│   │   ├── hooks/
│   │   │   ├── useAuth.ts
│   │   │   ├── useBookings.ts
│   │   │   ├── useProviders.ts
│   │   │   └── useAnalytics.ts
│   │   ├── services/
│   │   │   ├── api.ts
│   │   │   ├── auth.ts
│   │   │   ├── bookings.ts
│   │   │   ├── providers.ts
│   │   │   └── analytics.ts
│   │   ├── types/
│   │   │   ├── auth.ts
│   │   │   ├── booking.ts
│   │   │   ├── provider.ts
│   │   │   └── analytics.ts
│   │   └── utils/
│   │       ├── constants.ts
│   │       ├── formatters.ts
│   │       └── validators.ts
│   └── public/
│       ├── favicon.ico
│       └── images/
│
├── database/
│   ├── migrations/
│   │   ├── 001_create_users_table.sql
│   │   ├── 002_create_service_categories_table.sql
│   │   ├── 003_create_services_table.sql
│   │   ├── 004_create_providers_table.sql
│   │   ├── 005_create_bookings_table.sql
│   │   ├── 006_create_payments_table.sql
│   │   └── 007_create_reviews_table.sql
│   ├── seeds/
│   │   ├── 001_seed_users.sql
│   │   ├── 002_seed_service_categories.sql
│   │   ├── 003_seed_services.sql
│   │   └── 004_seed_providers.sql
│   └── scripts/
│       ├── backup.sh
│       ├── restore.sh
│       └── migrate.sh
│
├── scripts/
│   ├── setup.sh
│   ├── build.sh
│   ├── deploy.sh
│   └── test.sh
│
└── .github/
    └── workflows/
        ├── backend-ci.yml
        ├── frontend-ci.yml
        └── deploy-production.yml
```

## Key Directory Explanations - MVP

### Backend Structure (Simplified)
- **Entity Layer**: JPA entities representing 7 core database tables with relationships
- **Repository Layer**: Data access layer with Spring Data JPA repositories
- **Service Layer**: Business logic implementation with interface-based design
- **Controller Layer**: REST API endpoints with proper HTTP status codes
- **Security Layer**: JWT authentication and authorization implementation
- **Configuration**: Database, security, email, and payment gateway configurations

### Frontend Structure (Flutter)
- **Core**: Shared utilities, constants, network, and storage management
- **Data Layer**: Models, repositories, and data sources (remote/local)
- **Domain Layer**: Business entities, repository interfaces, and use cases
- **Presentation Layer**: UI components, state management (BLoC), and pages
- **Localization**: English language support for MVP

### Provider Dashboard (React/TypeScript)
- **Components**: Reusable UI components for provider interface
- **Pages**: Provider dashboard screens (bookings, earnings, profile)
- **Services**: API integration and data fetching logic
- **Types**: TypeScript type definitions for type safety
- **Hooks**: Custom React hooks for state management and API calls

### Admin Dashboard (React/TypeScript)
- **Components**: Reusable UI components for admin interface
- **Pages**: Admin dashboard screens (users, bookings, providers, analytics)
- **Services**: API integration and data fetching logic
- **Types**: TypeScript type definitions for type safety
- **Hooks**: Custom React hooks for state management and API calls

### Database Structure (MVP)
- **Migrations**: Version-controlled database schema changes for 7 core tables
- **Seeds**: Initial data for development and testing
- **Scripts**: Database maintenance and backup utilities

### Deployment (Simplified for MVP)
- **Docker**: Basic containerization for development
- **CI/CD**: Basic automated testing and deployment pipelines
- **Cloud**: Simple deployment to Railway/Render/AWS

This MVP file structure focuses on core functionality with simplified architecture for rapid development and deployment.