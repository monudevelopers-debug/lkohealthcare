import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:flutter/material.dart';
import 'package:lucknow_healthcare/main.dart' as app;

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  group('Authentication Integration Tests', () {
    testWidgets('User can register successfully', (WidgetTester tester) async {
      // Start the app
      app.main();
      await tester.pumpAndSettle();

      // Navigate to register screen
      await tester.tap(find.text('Register'));
      await tester.pumpAndSettle();

      // Fill registration form
      await tester.enterText(find.byKey(const Key('name_field')), 'Test User');
      await tester.enterText(find.byKey(const Key('email_field')), 'test@example.com');
      await tester.enterText(find.byKey(const Key('phone_field')), '+91-9876543210');
      await tester.enterText(find.byKey(const Key('password_field')), 'password123');
      await tester.enterText(find.byKey(const Key('confirm_password_field')), 'password123');

      // Submit registration
      await tester.tap(find.byKey(const Key('register_button')));
      await tester.pumpAndSettle();

      // Verify success message or navigation
      expect(find.text('Registration successful'), findsOneWidget);
    });

    testWidgets('User can login successfully', (WidgetTester tester) async {
      // Start the app
      app.main();
      await tester.pumpAndSettle();

      // Fill login form
      await tester.enterText(find.byKey(const Key('email_field')), 'test@example.com');
      await tester.enterText(find.byKey(const Key('password_field')), 'password123');

      // Submit login
      await tester.tap(find.byKey(const Key('login_button')));
      await tester.pumpAndSettle();

      // Verify navigation to home screen
      expect(find.byKey(const Key('home_screen')), findsOneWidget);
    });

    testWidgets('User can logout successfully', (WidgetTester tester) async {
      // Start the app and login first
      app.main();
      await tester.pumpAndSettle();

      // Login
      await tester.enterText(find.byKey(const Key('email_field')), 'test@example.com');
      await tester.enterText(find.byKey(const Key('password_field')), 'password123');
      await tester.tap(find.byKey(const Key('login_button')));
      await tester.pumpAndSettle();

      // Navigate to profile
      await tester.tap(find.byKey(const Key('profile_tab')));
      await tester.pumpAndSettle();

      // Logout
      await tester.tap(find.byKey(const Key('logout_button')));
      await tester.pumpAndSettle();

      // Verify navigation to login screen
      expect(find.byKey(const Key('login_screen')), findsOneWidget);
    });

    testWidgets('Invalid login shows error message', (WidgetTester tester) async {
      // Start the app
      app.main();
      await tester.pumpAndSettle();

      // Fill invalid login form
      await tester.enterText(find.byKey(const Key('email_field')), 'invalid@example.com');
      await tester.enterText(find.byKey(const Key('password_field')), 'wrongpassword');

      // Submit login
      await tester.tap(find.byKey(const Key('login_button')));
      await tester.pumpAndSettle();

      // Verify error message
      expect(find.text('Invalid credentials'), findsOneWidget);
    });

    testWidgets('Registration validation works', (WidgetTester tester) async {
      // Start the app
      app.main();
      await tester.pumpAndSettle();

      // Navigate to register screen
      await tester.tap(find.text('Register'));
      await tester.pumpAndSettle();

      // Try to submit empty form
      await tester.tap(find.byKey(const Key('register_button')));
      await tester.pumpAndSettle();

      // Verify validation messages
      expect(find.text('Name is required'), findsOneWidget);
      expect(find.text('Email is required'), findsOneWidget);
      expect(find.text('Password is required'), findsOneWidget);
    });
  });

  group('Service Booking Integration Tests', () {
    testWidgets('User can browse services', (WidgetTester tester) async {
      // Start the app and login
      app.main();
      await tester.pumpAndSettle();

      // Login
      await tester.enterText(find.byKey(const Key('email_field')), 'test@example.com');
      await tester.enterText(find.byKey(const Key('password_field')), 'password123');
      await tester.tap(find.byKey(const Key('login_button')));
      await tester.pumpAndSettle();

      // Navigate to services
      await tester.tap(find.byKey(const Key('services_tab')));
      await tester.pumpAndSettle();

      // Verify services are displayed
      expect(find.byKey(const Key('service_card')), findsWidgets);
    });

    testWidgets('User can book a service', (WidgetTester tester) async {
      // Start the app and login
      app.main();
      await tester.pumpAndSettle();

      // Login
      await tester.enterText(find.byKey(const Key('email_field')), 'test@example.com');
      await tester.enterText(find.byKey(const Key('password_field')), 'password123');
      await tester.tap(find.byKey(const Key('login_button')));
      await tester.pumpAndSettle();

      // Navigate to services
      await tester.tap(find.byKey(const Key('services_tab')));
      await tester.pumpAndSettle();

      // Select a service
      await tester.tap(find.byKey(const Key('service_card')).first);
      await tester.pumpAndSettle();

      // Fill booking form
      await tester.enterText(find.byKey(const Key('booking_date_field')), '2024-12-25');
      await tester.enterText(find.byKey(const Key('booking_time_field')), '10:00');
      await tester.enterText(find.byKey(const Key('booking_notes_field')), 'Test booking');

      // Submit booking
      await tester.tap(find.byKey(const Key('book_service_button')));
      await tester.pumpAndSettle();

      // Verify success message
      expect(find.text('Booking created successfully'), findsOneWidget);
    });

    testWidgets('User can view their bookings', (WidgetTester tester) async {
      // Start the app and login
      app.main();
      await tester.pumpAndSettle();

      // Login
      await tester.enterText(find.byKey(const Key('email_field')), 'test@example.com');
      await tester.enterText(find.byKey(const Key('password_field')), 'password123');
      await tester.tap(find.byKey(const Key('login_button')));
      await tester.pumpAndSettle();

      // Navigate to bookings
      await tester.tap(find.byKey(const Key('bookings_tab')));
      await tester.pumpAndSettle();

      // Verify bookings are displayed
      expect(find.byKey(const Key('booking_card')), findsWidgets);
    });
  });

  group('Provider Dashboard Integration Tests', () {
    testWidgets('Provider can view their bookings', (WidgetTester tester) async {
      // Start the provider dashboard
      app.main();
      await tester.pumpAndSettle();

      // Login as provider
      await tester.enterText(find.byKey(const Key('email_field')), 'provider@test.com');
      await tester.enterText(find.byKey(const Key('password_field')), 'password123');
      await tester.tap(find.byKey(const Key('login_button')));
      await tester.pumpAndSettle();

      // Verify provider dashboard
      expect(find.byKey(const Key('provider_dashboard')), findsOneWidget);
      expect(find.byKey(const Key('provider_bookings')), findsOneWidget);
    });

    testWidgets('Provider can update booking status', (WidgetTester tester) async {
      // Start the provider dashboard and login
      app.main();
      await tester.pumpAndSettle();

      // Login as provider
      await tester.enterText(find.byKey(const Key('email_field')), 'provider@test.com');
      await tester.enterText(find.byKey(const Key('password_field')), 'password123');
      await tester.tap(find.byKey(const Key('login_button')));
      await tester.pumpAndSettle();

      // Navigate to bookings
      await tester.tap(find.byKey(const Key('bookings_tab')));
      await tester.pumpAndSettle();

      // Select a booking
      await tester.tap(find.byKey(const Key('booking_card')).first);
      await tester.pumpAndSettle();

      // Update status
      await tester.tap(find.byKey(const Key('accept_booking_button')));
      await tester.pumpAndSettle();

      // Verify status update
      expect(find.text('Booking accepted'), findsOneWidget);
    });
  });
}
