import 'package:dartz/dartz.dart';
import 'package:lucknow_healthcare/domain/entities/service.dart';
import 'package:lucknow_healthcare/domain/repositories/service_repository.dart';
import 'package:lucknow_healthcare/core/error/failures.dart';
import 'package:lucknow_healthcare/data/datasources/remote/service_remote_datasource.dart';
import 'package:lucknow_healthcare/core/network/network_info.dart';

class ServiceRepositoryImpl implements ServiceRepository {
  final ServiceRemoteDataSource remoteDataSource;
  final NetworkInfo networkInfo;

  ServiceRepositoryImpl({
    required this.remoteDataSource,
    required this.networkInfo,
  });

  @override
  Future<Either<Failure, List<Service>>> getServices() async {
    if (await networkInfo.isConnected) {
      try {
        final services = await remoteDataSource.getServices();
        return Right(services);
      } on Failure catch (failure) {
        return Left(failure);
      } catch (e) {
        return Left(UnknownFailure(message: e.toString()));
      }
    } else {
      return Left(NetworkFailure(message: 'No internet connection'));
    }
  }

  @override
  Future<Either<Failure, List<Service>>> getActiveServices() async {
    if (await networkInfo.isConnected) {
      try {
        final services = await remoteDataSource.getServices();
        // Filter active services
        final activeServices = services.where((s) => s.isActive == true).toList();
        return Right(activeServices);
      } on Failure catch (failure) {
        return Left(failure);
      } catch (e) {
        return Left(UnknownFailure(message: e.toString()));
      }
    } else {
      return Left(NetworkFailure(message: 'No internet connection'));
    }
  }

  @override
  Future<Either<Failure, List<Service>>> getServicesByCategory(String categoryId) async {
    if (await networkInfo.isConnected) {
      try {
        final services = await remoteDataSource.getServicesByCategory(categoryId);
        return Right(services);
      } on Failure catch (failure) {
        return Left(failure);
      } catch (e) {
        return Left(UnknownFailure(message: e.toString()));
      }
    } else {
      return Left(NetworkFailure(message: 'No internet connection'));
    }
  }

  @override
  Future<Either<Failure, List<Service>>> searchServices(String query) async {
    if (await networkInfo.isConnected) {
      try {
        final services = await remoteDataSource.getServices();
        // Filter services by search query
        final filtered = services.where((service) {
          final nameLower = service.name?.toLowerCase() ?? '';
          final descLower = service.description?.toLowerCase() ?? '';
          final categoryLower = service.categoryName?.toLowerCase() ?? '';
          final queryLower = query.toLowerCase();
          
          return nameLower.contains(queryLower) ||
                 descLower.contains(queryLower) ||
                 categoryLower.contains(queryLower);
        }).toList();
        return Right(filtered);
      } on Failure catch (failure) {
        return Left(failure);
      } catch (e) {
        return Left(UnknownFailure(message: e.toString()));
      }
    } else {
      return Left(NetworkFailure(message: 'No internet connection'));
    }
  }

  @override
  Future<Either<Failure, Service>> getServiceById(String serviceId) async {
    if (await networkInfo.isConnected) {
      try {
        final service = await remoteDataSource.getServiceById(serviceId);
        return Right(service);
      } on Failure catch (failure) {
        return Left(failure);
      } catch (e) {
        return Left(UnknownFailure(message: e.toString()));
      }
    } else {
      return Left(NetworkFailure(message: 'No internet connection'));
    }
  }

  @override
  Future<Either<Failure, Service>> createService(Service service) async {
    return Left(UnknownFailure(message: 'Not implemented - Admin only'));
  }

  @override
  Future<Either<Failure, Service>> updateService(String serviceId, Service service) async {
    return Left(UnknownFailure(message: 'Not implemented - Admin only'));
  }

  @override
  Future<Either<Failure, void>> deleteService(String serviceId) async {
    return Left(UnknownFailure(message: 'Not implemented - Admin only'));
  }

  @override
  Future<Either<Failure, List<Service>>> getServicesByProvider(String providerId) async {
    return Left(UnknownFailure(message: 'Not implemented'));
  }

  @override
  Future<Either<Failure, Service>> updateServiceStatus(String serviceId, bool isActive) async {
    return Left(UnknownFailure(message: 'Not implemented - Admin only'));
  }
}

