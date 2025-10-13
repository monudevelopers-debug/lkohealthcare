import 'package:dartz/dartz.dart';
import '../entities/booking.dart';
import '../../core/error/failures.dart';

abstract class OrderRepository {
  Future<Either<Failure, List<Booking>>> getBookings();

  Future<Either<Failure, List<Booking>>> getBookingsByUser(String userId);

  Future<Either<Failure, List<Booking>>> getBookingsByProvider(String providerId);

  Future<Either<Failure, List<Booking>>> getBookingsByStatus(String status);

  Future<Either<Failure, List<Booking>>> searchBookings(String query);

  Future<Either<Failure, Booking>> getBookingById(String bookingId);

  Future<Either<Failure, Booking>> createBooking(Booking booking);

  Future<Either<Failure, Booking>> updateBooking(String bookingId, Booking booking);

  Future<Either<Failure, void>> cancelBooking(String bookingId);

  Future<Either<Failure, Booking>> rescheduleBooking(
    String bookingId,
    DateTime newDate,
    String newTime,
  );

  Future<Either<Failure, Booking>> updateBookingStatus(
    String bookingId,
    String status,
  );

  Future<Either<Failure, List<Booking>>> getBookingsByDateRange(
    DateTime startDate,
    DateTime endDate,
  );
}
