import 'package:dartz/dartz.dart';
import 'package:lucknow_healthcare/domain/entities/user.dart';
import 'package:lucknow_healthcare/core/error/failures.dart';

abstract class AuthRepository {
  Future<Either<Failure, User>> login({
    required String email,
    required String password,
  });

  Future<Either<Failure, User>> register({
    required String name,
    required String email,
    required String phone,
    required String password,
    required String role,
  });

  Future<Either<Failure, User>> getCurrentUser();

  Future<Either<Failure, User>> refreshToken();

  Future<Either<Failure, void>> logout();

  Future<Either<Failure, void>> forgotPassword({
    required String email,
  });

  Future<Either<Failure, void>> resetPassword({
    required String token,
    required String password,
  });

  Future<Either<Failure, User>> updateProfile({
    required String userId,
    String? name,
    String? phone,
    String? address,
  });

  Future<Either<Failure, void>> changePassword({
    required String userId,
    required String currentPassword,
    required String newPassword,
  });
}
