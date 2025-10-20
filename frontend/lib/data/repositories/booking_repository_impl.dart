import 'package:dartz/dartz.dart';
import 'package:lucknow_healthcare/domain/entities/booking.dart';
import 'package:lucknow_healthcare/domain/repositories/order_repository.dart';
import 'package:lucknow_healthcare/core/error/failures.dart';
import 'package:lucknow_healthcare/data/datasources/remote/booking_remote_datasource.dart';
import 'package:lucknow_healthcare/core/network/network_info.dart';

class BookingRepositoryImpl implements OrderRepository {
  final BookingRemoteDataSource remoteDataSource;
  final NetworkInfo networkInfo;

  BookingRepositoryImpl({
    required this.remoteDataSource,
    required this.networkInfo,
  });

  @override
  Future<Either<Failure, List<Booking>>> getBookings() async {
    if (await networkInfo.isConnected) {
      try {
        // This would need user ID from auth state
        throw UnknownFailure(message: 'Use getBookingsByUser instead');
      } catch (e) {
        return Left(UnknownFailure(message: e.toString()));
      }
    } else {
      return Left(NetworkFailure(message: 'No internet connection'));
    }
  }

  @override
  Future<Either<Failure, List<Booking>>> getBookingsByUser(String userId) async {
    if (await networkInfo.isConnected) {
      try {
        final bookings = await remoteDataSource.getBookingsByUser(userId);
        return Right(bookings);
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
  Future<Either<Failure, List<Booking>>> getBookingsByProvider(String providerId) async {
    return Left(UnknownFailure(message: 'Not implemented for customer app'));
  }

  @override
  Future<Either<Failure, List<Booking>>> getBookingsByStatus(String status) async {
    if (await networkInfo.isConnected) {
      try {
        final bookings = await remoteDataSource.getBookingsByStatus(status);
        return Right(bookings);
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
  Future<Either<Failure, List<Booking>>> searchBookings(String query) async {
    if (await networkInfo.isConnected) {
      try {
        // Get all bookings and filter
        throw UnknownFailure(message: 'Search not implemented');
      } catch (e) {
        return Left(UnknownFailure(message: e.toString()));
      }
    } else {
      return Left(NetworkFailure(message: 'No internet connection'));
    }
  }

  @override
  Future<Either<Failure, Booking>> getBookingById(String bookingId) async {
    if (await networkInfo.isConnected) {
      try {
        final booking = await remoteDataSource.getBookingById(bookingId);
        return Right(booking);
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
  Future<Either<Failure, Booking>> createBooking(Booking booking) async {
    if (await networkInfo.isConnected) {
      try {
        final createdBooking = await remoteDataSource.createBooking(
          serviceId: booking.serviceId!,
          userId: booking.userId!,
          scheduledDate: booking.scheduledDate!,
          scheduledTime: booking.scheduledTime!,
          address: booking.address!,
          providerId: booking.providerId,
          notes: booking.notes,
        );
        return Right(createdBooking);
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
  Future<Either<Failure, Booking>> updateBooking(String bookingId, Booking booking) async {
    if (await networkInfo.isConnected) {
      try {
        final updatedBooking = await remoteDataSource.updateBookingStatus(
          bookingId,
          booking.status!,
        );
        return Right(updatedBooking);
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
  Future<Either<Failure, void>> cancelBooking(String bookingId) async {
    if (await networkInfo.isConnected) {
      try {
        await remoteDataSource.cancelBooking(bookingId);
        return const Right(null);
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
  Future<Either<Failure, Booking>> rescheduleBooking(
    String bookingId,
    DateTime newDate,
    String newTime,
  ) async {
    return Left(UnknownFailure(message: 'Not implemented yet'));
  }

  @override
  Future<Either<Failure, Booking>> updateBookingStatus(
    String bookingId,
    String status,
  ) async {
    if (await networkInfo.isConnected) {
      try {
        final booking = await remoteDataSource.updateBookingStatus(bookingId, status);
        return Right(booking);
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
  Future<Either<Failure, List<Booking>>> getBookingsByDateRange(
    DateTime startDate,
    DateTime endDate,
  ) async {
    return Left(UnknownFailure(message: 'Not implemented'));
  }
}

