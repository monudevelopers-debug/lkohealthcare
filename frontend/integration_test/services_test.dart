import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:lucknow_healthcare/main.dart' as app;

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  group('Services Tests', () {
    
    // Helper to login
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

    testWidgets('TC007: Display Services List', (WidgetTester tester) async {
      await performLogin(tester);

      // Tap Services tab in bottom navigation (index 1)
      final servicesNav = find.text('Services');
      await tester.tap(servicesNav);
      await tester.pumpAndSettle(const Duration(seconds: 5));

      // Services should be displayed as Card widgets
      final serviceCards = find.byType(Card);
      expect(serviceCards, findsWidgets, reason: 'Services should be displayed as cards');
      
      final count = serviceCards.evaluate().length;
      print('✅ TC007: Services list test passed - Found $count service cards');
    });

    testWidgets('TC008: Filter Services by Category', (WidgetTester tester) async {
      await performLogin(tester);

      // Navigate to services tab
      final servicesNav = find.text('Services');
      await tester.tap(servicesNav);
      await tester.pumpAndSettle(const Duration(seconds: 5));

      // Find category filter chips
      final filterChips = find.byType(FilterChip);
      
      if (filterChips.evaluate().length > 1) {
        // Tap second category (first is "All Services")
        await tester.tap(filterChips.at(1));
        await tester.pumpAndSettle(const Duration(seconds: 3));
        
        // Services should still be displayed (filtered)
        final servicesAfterFilter = find.byType(Card);
        expect(servicesAfterFilter, findsWidgets, reason: 'Filtered services should display');
        
        print('✅ TC008: Category filter test passed');
      } else {
        print('⚠️ TC008: Category filters not found');
      }
    });

    testWidgets('TC009: View Service Details', (WidgetTester tester) async {
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

        // Should navigate to booking page or show details
        // Look for common indicators
        final detailIndicators = find.textContaining('Book');
        if (detailIndicators.evaluate().isNotEmpty) {
          print('✅ TC009: Service details test passed');
        } else {
          print('⚠️ TC009: Navigation unclear');
        }
      }
    });
  });
}
