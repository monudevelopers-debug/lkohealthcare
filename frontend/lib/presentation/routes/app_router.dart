import 'package:go_router/go_router.dart';
import 'package:flutter/material.dart';

import '../pages/auth/login_page.dart';
import '../pages/auth/register_page.dart';
import '../pages/home/home_page.dart';
import '../pages/services/services_page.dart';
import '../pages/orders/orders_page.dart';
import '../pages/profile/profile_page.dart';

class AppRouter {
  static const String login = '/login';
  static const String register = '/register';
  static const String home = '/home';
  static const String services = '/services';
  static const String orders = '/orders';
  static const String profile = '/profile';
  static const String forgotPassword = '/forgot-password';

  static final GoRouter router = GoRouter(
    initialLocation: login,
    routes: [
      // Auth Routes
      GoRoute(
        path: login,
        name: 'login',
        builder: (context, state) => const LoginPage(),
      ),
      GoRoute(
        path: register,
        name: 'register',
        builder: (context, state) => const RegisterPage(),
      ),
      GoRoute(
        path: forgotPassword,
        name: 'forgot-password',
        builder: (context, state) => const ForgotPasswordPage(),
      ),

      // Main App Routes
      GoRoute(
        path: home,
        name: 'home',
        builder: (context, state) => const HomePage(),
        routes: [
          GoRoute(
            path: 'services',
            name: 'services',
            builder: (context, state) => const ServicesPage(),
          ),
          GoRoute(
            path: 'orders',
            name: 'orders',
            builder: (context, state) => const OrdersPage(),
          ),
          GoRoute(
            path: 'profile',
            name: 'profile',
            builder: (context, state) => const ProfilePage(),
          ),
        ],
      ),

      // Direct Routes
      GoRoute(
        path: services,
        name: 'services-direct',
        builder: (context, state) => const ServicesPage(),
      ),
      GoRoute(
        path: orders,
        name: 'orders-direct',
        builder: (context, state) => const OrdersPage(),
      ),
      GoRoute(
        path: profile,
        name: 'profile-direct',
        builder: (context, state) => const ProfilePage(),
      ),
    ],
    errorBuilder: (context, state) => const ErrorPage(),
  );
}

class ForgotPasswordPage extends StatelessWidget {
  const ForgotPasswordPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Reset Password'),
        backgroundColor: const Color(0xFF1976D2),
        foregroundColor: Colors.white,
      ),
      body: const Center(
        child: Text('Forgot Password functionality coming soon'),
      ),
    );
  }
}

class ErrorPage extends StatelessWidget {
  const ErrorPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Error'),
        backgroundColor: const Color(0xFF1976D2),
        foregroundColor: Colors.white,
      ),
      body: const Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.error_outline,
              size: 64,
              color: Colors.red,
            ),
            SizedBox(height: 16),
            Text(
              'Page not found',
              style: TextStyle(fontSize: 18),
            ),
            SizedBox(height: 8),
            Text(
              'The page you are looking for does not exist.',
              style: TextStyle(color: Colors.grey),
            ),
          ],
        ),
      ),
    );
  }
}
