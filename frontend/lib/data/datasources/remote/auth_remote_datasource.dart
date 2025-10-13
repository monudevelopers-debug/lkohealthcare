import 'package:dio/dio.dart';
import '../../core/error/failures.dart';
import '../../domain/entities/user.dart';

abstract class AuthRemoteDataSource {
  Future<User> login({
    required String email,
    required String password,
  });

  Future<User> register({
    required String name,
    required String email,
    required String phone,
    required String password,
    required String role,
  });

  Future<User> getCurrentUser();

  Future<User> refreshToken();

  Future<void> logout();

  Future<void> forgotPassword({required String email});

  Future<void> resetPassword({
    required String token,
    required String password,
  });

  Future<User> updateProfile({
    required String userId,
    String? name,
    String? phone,
    String? address,
  });

  Future<void> changePassword({
    required String userId,
    required String currentPassword,
    required String newPassword,
  });
}

class AuthRemoteDataSourceImpl implements AuthRemoteDataSource {
  final Dio dio;

  AuthRemoteDataSourceImpl({required this.dio});

  @override
  Future<User> login({
    required String email,
    required String password,
  }) async {
    try {
      final response = await dio.post(
        '/api/users/authenticate',
        data: {
          'email': email,
          'password': password,
        },
      );

      if (response.statusCode == 200) {
        return User.fromJson(response.data);
      } else {
        throw ServerFailure(message: 'Login failed');
      }
    } on DioException catch (e) {
      if (e.type == DioExceptionType.connectionTimeout ||
          e.type == DioExceptionType.receiveTimeout) {
        throw NetworkFailure(message: 'Connection timeout');
      } else if (e.response?.statusCode == 401) {
        throw AuthenticationFailure(message: 'Invalid credentials');
      } else if (e.response?.statusCode == 404) {
        throw NotFoundFailure(message: 'User not found');
      } else {
        throw ServerFailure(message: e.message ?? 'Login failed');
      }
    } catch (e) {
      throw UnknownFailure(message: e.toString());
    }
  }

  @override
  Future<User> register({
    required String name,
    required String email,
    required String phone,
    required String password,
    required String role,
  }) async {
    try {
      final response = await dio.post(
        '/api/users/register',
        data: {
          'name': name,
          'email': email,
          'phone': phone,
          'password': password,
          'role': role,
        },
      );

      if (response.statusCode == 201) {
        return User.fromJson(response.data);
      } else {
        throw ServerFailure(message: 'Registration failed');
      }
    } on DioException catch (e) {
      if (e.type == DioExceptionType.connectionTimeout ||
          e.type == DioExceptionType.receiveTimeout) {
        throw NetworkFailure(message: 'Connection timeout');
      } else if (e.response?.statusCode == 400) {
        throw ValidationFailure(message: 'Invalid registration data');
      } else if (e.response?.statusCode == 409) {
        throw ValidationFailure(message: 'Email already exists');
      } else {
        throw ServerFailure(message: e.message ?? 'Registration failed');
      }
    } catch (e) {
      throw UnknownFailure(message: e.toString());
    }
  }

  @override
  Future<User> getCurrentUser() async {
    try {
      final response = await dio.get('/api/users/me');

      if (response.statusCode == 200) {
        return User.fromJson(response.data);
      } else {
        throw ServerFailure(message: 'Failed to get current user');
      }
    } on DioException catch (e) {
      if (e.type == DioExceptionType.connectionTimeout ||
          e.type == DioExceptionType.receiveTimeout) {
        throw NetworkFailure(message: 'Connection timeout');
      } else if (e.response?.statusCode == 401) {
        throw AuthenticationFailure(message: 'Not authenticated');
      } else {
        throw ServerFailure(message: e.message ?? 'Failed to get current user');
      }
    } catch (e) {
      throw UnknownFailure(message: e.toString());
    }
  }

  @override
  Future<User> refreshToken() async {
    try {
      final response = await dio.post('/api/users/refresh-token');

      if (response.statusCode == 200) {
        return User.fromJson(response.data);
      } else {
        throw ServerFailure(message: 'Token refresh failed');
      }
    } on DioException catch (e) {
      if (e.type == DioExceptionType.connectionTimeout ||
          e.type == DioExceptionType.receiveTimeout) {
        throw NetworkFailure(message: 'Connection timeout');
      } else if (e.response?.statusCode == 401) {
        throw AuthenticationFailure(message: 'Invalid refresh token');
      } else {
        throw ServerFailure(message: e.message ?? 'Token refresh failed');
      }
    } catch (e) {
      throw UnknownFailure(message: e.toString());
    }
  }

  @override
  Future<void> logout() async {
    try {
      await dio.post('/api/users/logout');
    } on DioException catch (e) {
      // Logout should not throw errors even if the server call fails
      // The local token should be cleared regardless
    }
  }

  @override
  Future<void> forgotPassword({required String email}) async {
    try {
      await dio.post(
        '/api/users/forgot-password',
        data: {'email': email},
      );
    } on DioException catch (e) {
      if (e.type == DioExceptionType.connectionTimeout ||
          e.type == DioExceptionType.receiveTimeout) {
        throw NetworkFailure(message: 'Connection timeout');
      } else if (e.response?.statusCode == 404) {
        throw NotFoundFailure(message: 'Email not found');
      } else {
        throw ServerFailure(message: e.message ?? 'Failed to send reset email');
      }
    } catch (e) {
      throw UnknownFailure(message: e.toString());
    }
  }

  @override
  Future<void> resetPassword({
    required String token,
    required String password,
  }) async {
    try {
      await dio.post(
        '/api/users/reset-password',
        data: {
          'token': token,
          'password': password,
        },
      );
    } on DioException catch (e) {
      if (e.type == DioExceptionType.connectionTimeout ||
          e.type == DioExceptionType.receiveTimeout) {
        throw NetworkFailure(message: 'Connection timeout');
      } else if (e.response?.statusCode == 400) {
        throw ValidationFailure(message: 'Invalid reset token');
      } else {
        throw ServerFailure(message: e.message ?? 'Password reset failed');
      }
    } catch (e) {
      throw UnknownFailure(message: e.toString());
    }
  }

  @override
  Future<User> updateProfile({
    required String userId,
    String? name,
    String? phone,
    String? address,
  }) async {
    try {
      final response = await dio.put(
        '/api/users/$userId',
        data: {
          if (name != null) 'name': name,
          if (phone != null) 'phone': phone,
          if (address != null) 'address': address,
        },
      );

      if (response.statusCode == 200) {
        return User.fromJson(response.data);
      } else {
        throw ServerFailure(message: 'Profile update failed');
      }
    } on DioException catch (e) {
      if (e.type == DioExceptionType.connectionTimeout ||
          e.type == DioExceptionType.receiveTimeout) {
        throw NetworkFailure(message: 'Connection timeout');
      } else if (e.response?.statusCode == 401) {
        throw AuthenticationFailure(message: 'Not authenticated');
      } else if (e.response?.statusCode == 403) {
        throw AuthorizationFailure(message: 'Not authorized');
      } else {
        throw ServerFailure(message: e.message ?? 'Profile update failed');
      }
    } catch (e) {
      throw UnknownFailure(message: e.toString());
    }
  }

  @override
  Future<void> changePassword({
    required String userId,
    required String currentPassword,
    required String newPassword,
  }) async {
    try {
      await dio.put(
        '/api/users/$userId/change-password',
        data: {
          'currentPassword': currentPassword,
          'newPassword': newPassword,
        },
      );
    } on DioException catch (e) {
      if (e.type == DioExceptionType.connectionTimeout ||
          e.type == DioExceptionType.receiveTimeout) {
        throw NetworkFailure(message: 'Connection timeout');
      } else if (e.response?.statusCode == 401) {
        throw AuthenticationFailure(message: 'Current password is incorrect');
      } else if (e.response?.statusCode == 403) {
        throw AuthorizationFailure(message: 'Not authorized');
      } else {
        throw ServerFailure(message: e.message ?? 'Password change failed');
      }
    } catch (e) {
      throw UnknownFailure(message: e.toString());
    }
  }
}
