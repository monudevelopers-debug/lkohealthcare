import 'package:dio/dio.dart';
import 'package:lucknow_healthcare/domain/entities/service.dart';
import 'package:lucknow_healthcare/core/error/failures.dart';

abstract class ServiceRemoteDataSource {
  Future<List<Service>> getServices();
  Future<List<Service>> getServicesByCategory(String categoryId);
  Future<Service> getServiceById(String serviceId);
}

class ServiceRemoteDataSourceImpl implements ServiceRemoteDataSource {
  final Dio dio;

  ServiceRemoteDataSourceImpl({required this.dio});

  @override
  Future<List<Service>> getServices() async {
    try {
      final response = await dio.get('/api/services');

      if (response.statusCode == 200) {
        final List<dynamic> data = response.data as List<dynamic>;
        return data.map((json) => Service.fromJson(json as Map<String, dynamic>)).toList();
      } else {
        throw ServerFailure(message: 'Failed to load services');
      }
    } on DioException catch (e) {
      if (e.type == DioExceptionType.connectionTimeout ||
          e.type == DioExceptionType.receiveTimeout) {
        throw NetworkFailure(message: 'Connection timeout');
      } else if (e.response?.statusCode == 401) {
        throw AuthenticationFailure(message: 'Please login to view services');
      } else if (e.response?.statusCode == 403) {
        throw AuthorizationFailure(message: 'Access denied');
      } else {
        throw ServerFailure(message: e.message ?? 'Failed to load services');
      }
    } catch (e) {
      throw UnknownFailure(message: e.toString());
    }
  }

  @override
  Future<List<Service>> getServicesByCategory(String categoryId) async {
    try {
      final response = await dio.get('/api/services', 
        queryParameters: {'categoryId': categoryId}
      );

      if (response.statusCode == 200) {
        final List<dynamic> data = response.data as List<dynamic>;
        return data.map((json) => Service.fromJson(json as Map<String, dynamic>)).toList();
      } else {
        throw ServerFailure(message: 'Failed to load services');
      }
    } on DioException catch (e) {
      if (e.type == DioExceptionType.connectionTimeout ||
          e.type == DioExceptionType.receiveTimeout) {
        throw NetworkFailure(message: 'Connection timeout');
      } else if (e.response?.statusCode == 401) {
        throw AuthenticationFailure(message: 'Please login to view services');
      } else {
        throw ServerFailure(message: e.message ?? 'Failed to load services');
      }
    } catch (e) {
      throw UnknownFailure(message: e.toString());
    }
  }

  @override
  Future<Service> getServiceById(String serviceId) async {
    try {
      final response = await dio.get('/api/services/$serviceId');

      if (response.statusCode == 200) {
        return Service.fromJson(response.data as Map<String, dynamic>);
      } else {
        throw ServerFailure(message: 'Failed to load service');
      }
    } on DioException catch (e) {
      if (e.type == DioExceptionType.connectionTimeout ||
          e.type == DioExceptionType.receiveTimeout) {
        throw NetworkFailure(message: 'Connection timeout');
      } else if (e.response?.statusCode == 401) {
        throw AuthenticationFailure(message: 'Please login');
      } else if (e.response?.statusCode == 404) {
        throw NotFoundFailure(message: 'Service not found');
      } else {
        throw ServerFailure(message: e.message ?? 'Failed to load service');
      }
    } catch (e) {
      throw UnknownFailure(message: e.toString());
    }
  }
}

