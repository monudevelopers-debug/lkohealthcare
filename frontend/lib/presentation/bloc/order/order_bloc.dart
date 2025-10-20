import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';

import 'package:lucknow_healthcare/domain/repositories/order_repository.dart';
import 'package:lucknow_healthcare/domain/entities/booking.dart';

// Events
abstract class OrderEvent extends Equatable {
  const OrderEvent();

  @override
  List<Object?> get props => [];
}

class LoadBookings extends OrderEvent {}

class LoadBookingsByStatus extends OrderEvent {
  final String status;

  const LoadBookingsByStatus({required this.status});

  @override
  List<Object> get props => [status];
}

class LoadBookingsByUser extends OrderEvent {
  final String userId;

  const LoadBookingsByUser({required this.userId});

  @override
  List<Object> get props => [userId];
}

class CreateBooking extends OrderEvent {
  final Booking booking;

  const CreateBooking({required this.booking});

  @override
  List<Object> get props => [booking];
}

class UpdateBooking extends OrderEvent {
  final String bookingId;
  final Booking booking;

  const UpdateBooking({
    required this.bookingId,
    required this.booking,
  });

  @override
  List<Object> get props => [bookingId, booking];
}

class CancelBooking extends OrderEvent {
  final String bookingId;

  const CancelBooking({required this.bookingId});

  @override
  List<Object> get props => [bookingId];
}

class RescheduleBooking extends OrderEvent {
  final String bookingId;
  final DateTime newDate;
  final String newTime;

  const RescheduleBooking({
    required this.bookingId,
    required this.newDate,
    required this.newTime,
  });

  @override
  List<Object> get props => [bookingId, newDate, newTime];
}

class SearchBookings extends OrderEvent {
  final String query;

  const SearchBookings({required this.query});

  @override
  List<Object> get props => [query];
}

// States
abstract class OrderState extends Equatable {
  const OrderState();

  @override
  List<Object?> get props => [];
}

class OrderInitial extends OrderState {}

class OrderLoading extends OrderState {}

class OrderSuccess extends OrderState {
  final List<Booking> bookings;

  const OrderSuccess({required this.bookings});

  @override
  List<Object> get props => [bookings];
}

class OrderFailure extends OrderState {
  final String message;

  const OrderFailure({required this.message});

  @override
  List<Object> get props => [message];
}

class BookingCreated extends OrderState {
  final Booking booking;

  const BookingCreated({required this.booking});

  @override
  List<Object> get props => [booking];
}

class BookingUpdated extends OrderState {
  final Booking booking;

  const BookingUpdated({required this.booking});

  @override
  List<Object> get props => [booking];
}

class BookingCancelled extends OrderState {
  final String bookingId;

  const BookingCancelled({required this.bookingId});

  @override
  List<Object> get props => [bookingId];
}

// BLoC
class OrderBloc extends Bloc<OrderEvent, OrderState> {
  final OrderRepository orderRepository;

  OrderBloc({required this.orderRepository}) : super(OrderInitial()) {
    on<LoadBookings>(_onLoadBookings);
    on<LoadBookingsByStatus>(_onLoadBookingsByStatus);
    on<LoadBookingsByUser>(_onLoadBookingsByUser);
    on<CreateBooking>(_onCreateBooking);
    on<UpdateBooking>(_onUpdateBooking);
    on<CancelBooking>(_onCancelBooking);
    on<RescheduleBooking>(_onRescheduleBooking);
    on<SearchBookings>(_onSearchBookings);
  }

  Future<void> _onLoadBookings(
    LoadBookings event,
    Emitter<OrderState> emit,
  ) async {
    emit(OrderLoading());
    
    try {
      final result = await orderRepository.getBookings();
      
      result.fold(
        (failure) => emit(OrderFailure(message: failure.message)),
        (bookings) => emit(OrderSuccess(bookings: bookings)),
      );
    } catch (e) {
      emit(OrderFailure(message: 'Failed to load bookings: ${e.toString()}'));
    }
  }

  Future<void> _onLoadBookingsByStatus(
    LoadBookingsByStatus event,
    Emitter<OrderState> emit,
  ) async {
    emit(OrderLoading());
    
    try {
      final result = await orderRepository.getBookingsByStatus(event.status);
      
      result.fold(
        (failure) => emit(OrderFailure(message: failure.message)),
        (bookings) => emit(OrderSuccess(bookings: bookings)),
      );
    } catch (e) {
      emit(OrderFailure(message: 'Failed to load bookings: ${e.toString()}'));
    }
  }

  Future<void> _onLoadBookingsByUser(
    LoadBookingsByUser event,
    Emitter<OrderState> emit,
  ) async {
    emit(OrderLoading());
    
    try {
      final result = await orderRepository.getBookingsByUser(event.userId);
      
      result.fold(
        (failure) => emit(OrderFailure(message: failure.message)),
        (bookings) => emit(OrderSuccess(bookings: bookings)),
      );
    } catch (e) {
      emit(OrderFailure(message: 'Failed to load bookings: ${e.toString()}'));
    }
  }

  Future<void> _onCreateBooking(
    CreateBooking event,
    Emitter<OrderState> emit,
  ) async {
    emit(OrderLoading());
    
    try {
      final result = await orderRepository.createBooking(event.booking);
      
      result.fold(
        (failure) => emit(OrderFailure(message: failure.message)),
        (booking) => emit(BookingCreated(booking: booking)),
      );
    } catch (e) {
      emit(OrderFailure(message: 'Failed to create booking: ${e.toString()}'));
    }
  }

  Future<void> _onUpdateBooking(
    UpdateBooking event,
    Emitter<OrderState> emit,
  ) async {
    emit(OrderLoading());
    
    try {
      final result = await orderRepository.updateBooking(
        event.bookingId,
        event.booking,
      );
      
      result.fold(
        (failure) => emit(OrderFailure(message: failure.message)),
        (booking) => emit(BookingUpdated(booking: booking)),
      );
    } catch (e) {
      emit(OrderFailure(message: 'Failed to update booking: ${e.toString()}'));
    }
  }

  Future<void> _onCancelBooking(
    CancelBooking event,
    Emitter<OrderState> emit,
  ) async {
    emit(OrderLoading());
    
    try {
      final result = await orderRepository.cancelBooking(event.bookingId);
      
      result.fold(
        (failure) => emit(OrderFailure(message: failure.message)),
        (success) => emit(BookingCancelled(bookingId: event.bookingId)),
      );
    } catch (e) {
      emit(OrderFailure(message: 'Failed to cancel booking: ${e.toString()}'));
    }
  }

  Future<void> _onRescheduleBooking(
    RescheduleBooking event,
    Emitter<OrderState> emit,
  ) async {
    emit(OrderLoading());
    
    try {
      final result = await orderRepository.rescheduleBooking(
        event.bookingId,
        event.newDate,
        event.newTime,
      );
      
      result.fold(
        (failure) => emit(OrderFailure(message: failure.message)),
        (booking) => emit(BookingUpdated(booking: booking)),
      );
    } catch (e) {
      emit(OrderFailure(message: 'Failed to reschedule booking: ${e.toString()}'));
    }
  }

  Future<void> _onSearchBookings(
    SearchBookings event,
    Emitter<OrderState> emit,
  ) async {
    emit(OrderLoading());
    
    try {
      final result = await orderRepository.searchBookings(event.query);
      
      result.fold(
        (failure) => emit(OrderFailure(message: failure.message)),
        (bookings) => emit(OrderSuccess(bookings: bookings)),
      );
    } catch (e) {
      emit(OrderFailure(message: 'Failed to search bookings: ${e.toString()}'));
    }
  }
}
