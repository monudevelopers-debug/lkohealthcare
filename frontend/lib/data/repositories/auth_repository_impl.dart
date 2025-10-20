import 'package:dartz/dartz.dart';
import 'package:lucknow_healthcare/domain/entities/user.dart';
import 'package:lucknow_healthcare/domain/repositories/auth_repository.dart';
import 'package:lucknow_healthcare/core/error/failures.dart';
import 'package:lucknow_healthcare/data/datasources/remote/auth_remote_datasource.dart';
import 'package:lucknow_healthcare/data/datasources/local/auth_local_datasource.dart';
import 'package:lucknow_healthcare/core/network/network_info.dart';

class AuthRepositoryImpl implements AuthRepository {
  final AuthRemoteDataSource remoteDataSource;
  final AuthLocalDataSource localDataSource;
  final NetworkInfo networkInfo;

  AuthRepositoryImpl({
    required this.remoteDataSource,
    required this.localDataSource,
    required this.networkInfo,
  });

  @override
  Future<Either<Failure, User>> login({
    required String email,
    required String password,
  }) async {
    if (await networkInfo.isConnected) {
      try {
        final user = await remoteDataSource.login(
          email: email,
          password: password,
        );
        
        // Cache user and token
        await localDataSource.cacheUser(user);
        if (user.token != null) {
          await localDataSource.saveToken(user.token!);
        }
        
        return Right(user);
      } on ServerFailure catch (e) {
        return Left(e);
      } on NetworkFailure catch (e) {
        return Left(e);
      } on AuthenticationFailure catch (e) {
        return Left(e);
      } on ValidationFailure catch (e) {
        return Left(e);
      } on NotFoundFailure catch (e) {
        return Left(e);
      } on TimeoutFailure catch (e) {
        return Left(e);
      } on UnknownFailure catch (e) {
        return Left(e);
      } catch (e) {
        return Left(UnknownFailure(message: e.toString()));
      }
    } else {
      return const Left(NetworkFailure(message: 'No internet connection'));
    }
  }

  @override
  Future<Either<Failure, User>> register({
    required String name,
    required String email,
    required String phone,
    required String password,
    required String role,
  }) async {
    if (await networkInfo.isConnected) {
      try {
        final user = await remoteDataSource.register(
          name: name,
          email: email,
          phone: phone,
          password: password,
          role: role,
        );
        
        // Cache user and token
        await localDataSource.cacheUser(user);
        if (user.token != null) {
          await localDataSource.saveToken(user.token!);
        }
        
        return Right(user);
      } on ServerFailure catch (e) {
        return Left(e);
      } on NetworkFailure catch (e) {
        return Left(e);
      } on ValidationFailure catch (e) {
        return Left(e);
      } on UnknownFailure catch (e) {
        return Left(e);
      } catch (e) {
        return Left(UnknownFailure(message: e.toString()));
      }
    } else {
      return const Left(NetworkFailure(message: 'No internet connection'));
    }
  }

  @override
  Future<Either<Failure, User>> getCurrentUser() async {
    if (await networkInfo.isConnected) {
      try {
        final user = await remoteDataSource.getCurrentUser();
        
        // Update cache
        await localDataSource.cacheUser(user);
        
        return Right(user);
      } on ServerFailure catch (e) {
        return Left(e);
      } on NetworkFailure catch (e) {
        return Left(e);
      } on AuthenticationFailure catch (e) {
        return Left(e);
      } on UnknownFailure catch (e) {
        return Left(e);
      } catch (e) {
        return Left(UnknownFailure(message: e.toString()));
      }
    } else {
      // Try to get cached user when offline
      try {
        final cachedUser = await localDataSource.getCachedUser();
        if (cachedUser != null) {
          return Right(cachedUser);
        } else {
          return const Left(NetworkFailure(message: 'No internet connection and no cached user'));
        }
      } catch (e) {
        return const Left(NetworkFailure(message: 'No internet connection and no cached user'));
      }
    }
  }

  @override
  Future<Either<Failure, User>> refreshToken() async {
    if (await networkInfo.isConnected) {
      try {
        final user = await remoteDataSource.refreshToken();
        
        // Update cache
        await localDataSource.cacheUser(user);
        if (user.token != null) {
          await localDataSource.saveToken(user.token!);
        }
        
        return Right(user);
      } on ServerFailure catch (e) {
        return Left(e);
      } on NetworkFailure catch (e) {
        return Left(e);
      } on AuthenticationFailure catch (e) {
        return Left(e);
      } on UnknownFailure catch (e) {
        return Left(e);
      } catch (e) {
        return Left(UnknownFailure(message: e.toString()));
      }
    } else {
      return const Left(NetworkFailure(message: 'No internet connection'));
    }
  }

  @override
  Future<Either<Failure, void>> logout() async {
    try {
      if (await networkInfo.isConnected) {
        await remoteDataSource.logout();
      }
      
      // Clear local cache regardless of network status
      await localDataSource.clearCache();
      await localDataSource.clearToken();
      
      return const Right(null);
    } catch (e) {
      // Even if logout fails on server, clear local cache
      await localDataSource.clearCache();
      await localDataSource.clearToken();
      return const Right(null);
    }
  }

  @override
  Future<Either<Failure, void>> forgotPassword({
    required String email,
  }) async {
    if (await networkInfo.isConnected) {
      try {
        await remoteDataSource.forgotPassword(email: email);
        return const Right(null);
      } on ServerFailure catch (e) {
        return Left(e);
      } on NetworkFailure catch (e) {
        return Left(e);
      } on ValidationFailure catch (e) {
        return Left(e);
      } on NotFoundFailure catch (e) {
        return Left(e);
      } on UnknownFailure catch (e) {
        return Left(e);
      } catch (e) {
        return Left(UnknownFailure(message: e.toString()));
      }
    } else {
      return const Left(NetworkFailure(message: 'No internet connection'));
    }
  }

  @override
  Future<Either<Failure, void>> resetPassword({
    required String token,
    required String password,
  }) async {
    if (await networkInfo.isConnected) {
      try {
        await remoteDataSource.resetPassword(
          token: token,
          password: password,
        );
        return const Right(null);
      } on ServerFailure catch (e) {
        return Left(e);
      } on NetworkFailure catch (e) {
        return Left(e);
      } on ValidationFailure catch (e) {
        return Left(e);
      } on UnknownFailure catch (e) {
        return Left(e);
      } catch (e) {
        return Left(UnknownFailure(message: e.toString()));
      }
    } else {
      return const Left(NetworkFailure(message: 'No internet connection'));
    }
  }

  @override
  Future<Either<Failure, User>> updateProfile({
    required String userId,
    String? name,
    String? phone,
    String? address,
  }) async {
    if (await networkInfo.isConnected) {
      try {
        final user = await remoteDataSource.updateProfile(
          userId: userId,
          name: name,
          phone: phone,
          address: address,
        );
        
        // Update cache
        await localDataSource.cacheUser(user);
        
        return Right(user);
      } on ServerFailure catch (e) {
        return Left(e);
      } on NetworkFailure catch (e) {
        return Left(e);
      } on AuthenticationFailure catch (e) {
        return Left(e);
      } on AuthorizationFailure catch (e) {
        return Left(e);
      } on ValidationFailure catch (e) {
        return Left(e);
      } on UnknownFailure catch (e) {
        return Left(e);
      } catch (e) {
        return Left(UnknownFailure(message: e.toString()));
      }
    } else {
      return const Left(NetworkFailure(message: 'No internet connection'));
    }
  }

  @override
  Future<Either<Failure, void>> changePassword({
    required String userId,
    required String currentPassword,
    required String newPassword,
  }) async {
    if (await networkInfo.isConnected) {
      try {
        await remoteDataSource.changePassword(
          userId: userId,
          currentPassword: currentPassword,
          newPassword: newPassword,
        );
        return const Right(null);
      } on ServerFailure catch (e) {
        return Left(e);
      } on NetworkFailure catch (e) {
        return Left(e);
      } on AuthenticationFailure catch (e) {
        return Left(e);
      } on AuthorizationFailure catch (e) {
        return Left(e);
      } on ValidationFailure catch (e) {
        return Left(e);
      } on UnknownFailure catch (e) {
        return Left(e);
      } catch (e) {
        return Left(UnknownFailure(message: e.toString()));
      }
    } else {
      return const Left(NetworkFailure(message: 'No internet connection'));
    }
  }
}
