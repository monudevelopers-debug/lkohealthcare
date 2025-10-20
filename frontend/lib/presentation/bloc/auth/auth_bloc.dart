import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';

import 'package:lucknow_healthcare/domain/repositories/auth_repository.dart';
import 'package:lucknow_healthcare/domain/entities/user.dart';

// Events
abstract class AuthEvent extends Equatable {
  const AuthEvent();

  @override
  List<Object?> get props => [];
}

class LoginRequested extends AuthEvent {
  final String email;
  final String password;

  const LoginRequested({
    required this.email,
    required this.password,
  });

  @override
  List<Object> get props => [email, password];
}

class RegisterRequested extends AuthEvent {
  final String name;
  final String email;
  final String phone;
  final String password;
  final String role;

  const RegisterRequested({
    required this.name,
    required this.email,
    required this.phone,
    required this.password,
    required this.role,
  });

  @override
  List<Object> get props => [name, email, phone, password, role];
}

class LogoutRequested extends AuthEvent {}

class CheckAuthStatus extends AuthEvent {}

class RefreshToken extends AuthEvent {}

// States
abstract class AuthState extends Equatable {
  const AuthState();

  @override
  List<Object?> get props => [];
}

class AuthInitial extends AuthState {}

class AuthLoading extends AuthState {}

class AuthSuccess extends AuthState {
  final User user;
  final String token;

  const AuthSuccess({
    required this.user,
    required this.token,
  });

  @override
  List<Object> get props => [user, token];
}

class AuthFailure extends AuthState {
  final String message;

  const AuthFailure({required this.message});

  @override
  List<Object> get props => [message];
}

class AuthUnauthenticated extends AuthState {}

// BLoC
class AuthBloc extends Bloc<AuthEvent, AuthState> {
  final AuthRepository authRepository;

  AuthBloc({required this.authRepository}) : super(AuthInitial()) {
    on<LoginRequested>(_onLoginRequested);
    on<RegisterRequested>(_onRegisterRequested);
    on<LogoutRequested>(_onLogoutRequested);
    on<CheckAuthStatus>(_onCheckAuthStatus);
    on<RefreshToken>(_onRefreshToken);
  }

  Future<void> _onLoginRequested(
    LoginRequested event,
    Emitter<AuthState> emit,
  ) async {
    emit(AuthLoading());
    
    try {
      final result = await authRepository.login(
        email: event.email,
        password: event.password,
      );
      
      result.fold(
        (failure) => emit(AuthFailure(message: failure.message)),
        (user) => emit(AuthSuccess(
          user: user,
          token: user.token ?? '',
        )),
      );
    } catch (e) {
      emit(AuthFailure(message: 'Login failed: ${e.toString()}'));
    }
  }

  Future<void> _onRegisterRequested(
    RegisterRequested event,
    Emitter<AuthState> emit,
  ) async {
    emit(AuthLoading());
    
    try {
      final result = await authRepository.register(
        name: event.name,
        email: event.email,
        phone: event.phone,
        password: event.password,
        role: event.role,
      );
      
      result.fold(
        (failure) => emit(AuthFailure(message: failure.message)),
        (user) => emit(AuthSuccess(
          user: user,
          token: user.token ?? '',
        )),
      );
    } catch (e) {
      emit(AuthFailure(message: 'Registration failed: ${e.toString()}'));
    }
  }

  Future<void> _onLogoutRequested(
    LogoutRequested event,
    Emitter<AuthState> emit,
  ) async {
    emit(AuthLoading());
    
    try {
      await authRepository.logout();
      emit(AuthUnauthenticated());
    } catch (e) {
      emit(AuthFailure(message: 'Logout failed: ${e.toString()}'));
    }
  }

  Future<void> _onCheckAuthStatus(
    CheckAuthStatus event,
    Emitter<AuthState> emit,
  ) async {
    emit(AuthLoading());
    
    try {
      final result = await authRepository.getCurrentUser();
      
      result.fold(
        (failure) => emit(AuthUnauthenticated()),
        (user) => emit(AuthSuccess(
          user: user,
          token: user.token ?? '',
        )),
      );
    } catch (e) {
      emit(AuthUnauthenticated());
    }
  }

  Future<void> _onRefreshToken(
    RefreshToken event,
    Emitter<AuthState> emit,
  ) async {
    try {
      final result = await authRepository.refreshToken();
      
      result.fold(
        (failure) => emit(AuthUnauthenticated()),
        (user) => emit(AuthSuccess(
          user: user,
          token: user.token ?? '',
        )),
      );
    } catch (e) {
      emit(AuthUnauthenticated());
    }
  }
}
