import 'package:dio/dio.dart';
import 'package:lucknow_healthcare/domain/entities/booking.dart';
import 'package:lucknow_healthcare/core/error/failures.dart';

abstract class BookingRemoteDataSource {
  Future<Booking> createBooking({
    required String serviceId,
    required String userId,
    required DateTime scheduledDate,
    required String scheduledTime,
    required String address,
    String? providerId,
    String? notes,
  });
  
  Future<List<Booking>> getBookingsByUser(String userId);
  Future<List<Booking>> getBookingsByStatus(String status);
  Future<Booking> getBookingById(String bookingId);
  Future<void> cancelBooking(String bookingId);
  Future<Booking> updateBookingStatus(String bookingId, String status);
}

class BookingRemoteDataSourceImpl implements BookingRemoteDataSource {
  final Dio dio;

  BookingRemoteDataSourceImpl({required this.dio});

  @override
  Future<Booking> createBooking({
    required String serviceId,
    required String userId,
    required DateTime scheduledDate,
    required String scheduledTime,
    required String address,
    String? providerId,
    String? notes,
  }) async {
    try {
      final response = await dio.post(
        '/api/bookings',
        data: {
          'serviceId': serviceId,
          'userId': userId,
          'scheduledDate': scheduledDate.toIso8601String().split('T')[0],
          'scheduledTime': scheduledTime,
          'address': address,
          if (providerId != null) 'providerId': providerId,
          if (notes != null) 'notes': notes,
        },
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        return Booking.fromJson(response.data as Map<String, dynamic>);
      } else {
        throw ServerFailure(message: 'Failed to create booking');
      }
    } on DioException catch (e) {
      if (e.type == DioExceptionType.connectionTimeout ||
          e.type == DioExceptionType.receiveTimeout) {
        throw NetworkFailure(message: 'Connection timeout');
      } else if (e.response?.statusCode == 401) {
        throw AuthenticationFailure(message: 'Please login to book');
      } else if (e.response?.statusCode == 400) {
        throw ValidationFailure(message: 'Invalid booking data');
      } else {
        throw ServerFailure(message: e.message ?? 'Failed to create booking');
      }
    } catch (e) {
      throw UnknownFailure(message: e.toString());
    }
  }

  @override
  Future<List<Booking>> getBookingsByUser(String userId) async {
    try {
      final response = await dio.get('/api/bookings/user/$userId');

      if (response.statusCode == 200) {
        final List<dynamic> data = response.data as List<dynamic>;
        return data.map((json) => Booking.fromJson(json as Map<String, dynamic>)).toList();
      } else {
        throw ServerFailure(message: 'Failed to load bookings');
      }
    } on DioException catch (e) {
      if (e.type == DioExceptionType.connectionTimeout ||
          e.type == DioExceptionType.receiveTimeout) {
        throw NetworkFailure(message: 'Connection timeout');
      } else if (e.response?.statusCode == 401) {
        throw AuthenticationFailure(message: 'Please login');
      } else {
        throw ServerFailure(message: e.message ?? 'Failed to load bookings');
      }
    } catch (e) {
      throw UnknownFailure(message: e.toString());
    }
  }

  @override
  Future<List<Booking>> getBookingsByStatus(String status) async {
    try {
      final response = await dio.get(
        '/api/bookings',
        queryParameters: {'status': status},
      );

      if (response.statusCode == 200) {
        final List<dynamic> data = response.data as List<dynamic>;
        return data.map((json) => Booking.fromJson(json as Map<String, dynamic>)).toList();
      } else {
        throw ServerFailure(message: 'Failed to load bookings');
      }
    } on DioException catch (e) {
      if (e.type == DioExceptionType.connectionTimeout ||
          e.type == DioExceptionType.receiveTimeout) {
        throw NetworkFailure(message: 'Connection timeout');
      } else {
        throw ServerFailure(message: e.message ?? 'Failed to load bookings');
      }
    } catch (e) {
      throw UnknownFailure(message: e.toString());
    }
  }

  @override
  Future<Booking> getBookingById(String bookingId) async {
    try {
      final response = await dio.get('/api/bookings/$bookingId');

      if (response.statusCode == 200) {
        return Booking.fromJson(response.data as Map<String, dynamic>);
      } else {
        throw ServerFailure(message: 'Failed to load booking');
      }
    } on DioException catch (e) {
      if (e.type == DioExceptionType.connectionTimeout ||
          e.type == DioExceptionType.receiveTimeout) {
        throw NetworkFailure(message: 'Connection timeout');
      } else if (e.response?.statusCode == 404) {
        throw NotFoundFailure(message: 'Booking not found');
      } else {
        throw ServerFailure(message: e.message ?? 'Failed to load booking');
      }
    } catch (e) {
      throw UnknownFailure(message: e.toString());
    }
  }

  @override
  Future<void> cancelBooking(String bookingId) async {
    try {
      final response = await dio.put(
        '/api/bookings/$bookingId/cancel',
      );

      if (response.statusCode != 200) {
        throw ServerFailure(message: 'Failed to cancel booking');
      }
    } on DioException catch (e) {
      if (e.type == DioExceptionType.connectionTimeout ||
          e.type == DioExceptionType.receiveTimeout) {
        throw NetworkFailure(message: 'Connection timeout');
      } else if (e.response?.statusCode == 404) {
        throw NotFoundFailure(message: 'Booking not found');
      } else {
        throw ServerFailure(message: e.message ?? 'Failed to cancel booking');
      }
    } catch (e) {
      throw UnknownFailure(message: e.toString());
    }
  }

  @override
  Future<Booking> updateBookingStatus(String bookingId, String status) async {
    try {
      final response = await dio.put(
        '/api/bookings/$bookingId',
        data: {'status': status},
      );

      if (response.statusCode == 200) {
        return Booking.fromJson(response.data as Map<String, dynamic>);
      } else {
        throw ServerFailure(message: 'Failed to update booking');
      }
    } on DioException catch (e) {
      if (e.type == DioExceptionType.connectionTimeout ||
          e.type == DioExceptionType.receiveTimeout) {
        throw NetworkFailure(message: 'Connection timeout');
      } else {
        throw ServerFailure(message: e.message ?? 'Failed to update booking');
      }
    } catch (e) {
      throw UnknownFailure(message: e.toString());
    }
  }
}

