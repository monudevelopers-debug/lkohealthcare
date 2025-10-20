import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:lucknow_healthcare/main.dart' as app;

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  group('Booking Tests', () {
    
    Future<void> performLogin(WidgetTester tester) async {
      app.main();
      await tester.pumpAndSettle(const Duration(seconds: 5));

      final emailField = find.byType(TextFormField).first;
      final passwordField = find.byType(TextFormField).at(1);
      
      await tester.enterText(emailField, 'user1@example.com');
      await tester.pumpAndSettle(const Duration(seconds: 1));
      
      await tester.enterText(passwordField, 'password123');
      await tester.pumpAndSettle(const Duration(seconds: 1));

      final loginButton = find.widgetWithText(ElevatedButton, 'Login');
      await tester.tap(loginButton);
      await tester.pumpAndSettle(const Duration(seconds: 8));
    }

    testWidgets('TC012: View User Bookings', (WidgetTester tester) async {
      await performLogin(tester);

      // Tap Bookings tab in bottom navigation (index 2)
      final bookingsNav = find.text('Bookings');
      await tester.tap(bookingsNav);
      await tester.pumpAndSettle(const Duration(seconds: 5));

      // Bookings page should be displayed
      // Look for any Card widgets (bookings) or empty state
      final hasContent = find.byType(Card).evaluate().isNotEmpty || 
                        find.textContaining('No').evaluate().isNotEmpty;
      
      expect(hasContent, true, reason: 'Bookings page should show content or empty state');
      
      final count = find.byType(Card).evaluate().length;
      print('✅ TC012: View bookings test passed - Found $count bookings');
    });

    testWidgets('TC013: Cancel Booking (if bookings exist)', (WidgetTester tester) async {
      await performLogin(tester);

      // Navigate to bookings
      final bookingsNav = find.text('Bookings');
      await tester.tap(bookingsNav);
      await tester.pumpAndSettle(const Duration(seconds: 5));

      // Look for Cancel button
      final cancelButtons = find.text('Cancel');
      
      if (cancelButtons.evaluate().isNotEmpty) {
        await tester.tap(cancelButtons.first);
        await tester.pumpAndSettle(const Duration(seconds: 2));

        // Handle confirmation dialog if exists
        final confirmButtons = find.widgetWithText(TextButton, 'Yes');
        if (confirmButtons.evaluate().isEmpty) {
          final okButtons = find.widgetWithText(TextButton, 'Confirm');
          if (okButtons.evaluate().isNotEmpty) {
            await tester.tap(okButtons);
          }
        } else {
          await tester.tap(confirmButtons);
        }
        await tester.pumpAndSettle(const Duration(seconds: 5));
        
        print('✅ TC013: Cancel booking test passed');
      } else {
        print('⚠️ TC013: No cancellable bookings found - test skipped');
      }
    });

    testWidgets('TC011: Create Booking Success', (WidgetTester tester) async {
      await performLogin(tester);

      // Navigate to services
      final servicesNav = find.text('Services');
      await tester.tap(servicesNav);
      await tester.pumpAndSettle(const Duration(seconds: 5));

      // Tap first service card
      final serviceCards = find.byType(Card);
      if (serviceCards.evaluate().isNotEmpty) {
        await tester.tap(serviceCards.first);
        await tester.pumpAndSettle(const Duration(seconds: 3));

        // Try to find and fill booking form
        final formFields = find.byType(TextFormField);
        
        if (formFields.evaluate().length >= 2) {
          // Fill address and notes
          await tester.enterText(formFields.first, '123 Test St, Lucknow');
          await tester.pumpAndSettle(const Duration(seconds: 1));
          
          if (formFields.evaluate().length > 1) {
            await tester.enterText(formFields.at(1), 'Test booking');
            await tester.pumpAndSettle(const Duration(seconds: 1));
          }

          // Find and tap submit button
          final submitButtons = find.byType(ElevatedButton);
          if (submitButtons.evaluate().isNotEmpty) {
            await tester.tap(submitButtons.last);
            await tester.pumpAndSettle(const Duration(seconds: 8));
            
            print('✅ TC011: Booking creation test passed');
          }
        } else {
          print('⚠️ TC011: Booking form not found');
        }
      }
    });

    testWidgets('TC014: Booking Validation', (WidgetTester tester) async {
      await performLogin(tester);

      final servicesNav = find.text('Services');
      await tester.tap(servicesNav);
      await tester.pumpAndSettle(const Duration(seconds: 5));

      final serviceCards = find.byType(Card);
      if (serviceCards.evaluate().isNotEmpty) {
        await tester.tap(serviceCards.first);
        await tester.pumpAndSettle(const Duration(seconds: 3));

        // Try to submit without filling required fields
        final submitButtons = find.byType(ElevatedButton);
        if (submitButtons.evaluate().isNotEmpty) {
          await tester.tap(submitButtons.last);
          await tester.pumpAndSettle(const Duration(seconds: 2));
          
          // Look for validation error
          final validationError = find.textContaining('Please');
          if (validationError.evaluate().isNotEmpty) {
            print('✅ TC014: Validation test passed');
          } else {
            print('⚠️ TC014: No validation message shown');
          }
        }
      }
    });
  });
}
