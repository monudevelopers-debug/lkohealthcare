import 'package:dartz/dartz.dart';
import '../entities/service.dart';
import '../../core/error/failures.dart';

abstract class ServiceRepository {
  Future<Either<Failure, List<Service>>> getServices();

  Future<Either<Failure, List<Service>>> getActiveServices();

  Future<Either<Failure, List<Service>>> getServicesByCategory(String categoryId);

  Future<Either<Failure, List<Service>>> searchServices(String query);

  Future<Either<Failure, Service>> getServiceById(String serviceId);

  Future<Either<Failure, Service>> createService(Service service);

  Future<Either<Failure, Service>> updateService(String serviceId, Service service);

  Future<Either<Failure, void>> deleteService(String serviceId);

  Future<Either<Failure, List<Service>>> getServicesByProvider(String providerId);

  Future<Either<Failure, Service>> updateServiceStatus(String serviceId, bool isActive);
}
