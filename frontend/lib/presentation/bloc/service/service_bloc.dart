import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';

import 'package:lucknow_healthcare/domain/repositories/service_repository.dart';
import 'package:lucknow_healthcare/domain/entities/service.dart';

// Events
abstract class ServiceEvent extends Equatable {
  const ServiceEvent();

  @override
  List<Object?> get props => [];
}

class LoadServices extends ServiceEvent {}

class LoadServicesByCategory extends ServiceEvent {
  final String categoryId;

  const LoadServicesByCategory({required this.categoryId});

  @override
  List<Object> get props => [categoryId];
}

class LoadActiveServices extends ServiceEvent {}

class SearchServices extends ServiceEvent {
  final String query;

  const SearchServices({required this.query});

  @override
  List<Object> get props => [query];
}

class LoadServiceById extends ServiceEvent {
  final String serviceId;

  const LoadServiceById({required this.serviceId});

  @override
  List<Object> get props => [serviceId];
}

class CreateService extends ServiceEvent {
  final Service service;

  const CreateService({required this.service});

  @override
  List<Object> get props => [service];
}

class UpdateService extends ServiceEvent {
  final String serviceId;
  final Service service;

  const UpdateService({
    required this.serviceId,
    required this.service,
  });

  @override
  List<Object> get props => [serviceId, service];
}

class DeleteService extends ServiceEvent {
  final String serviceId;

  const DeleteService({required this.serviceId});

  @override
  List<Object> get props => [serviceId];
}

// States
abstract class ServiceState extends Equatable {
  const ServiceState();

  @override
  List<Object?> get props => [];
}

class ServiceInitial extends ServiceState {}

class ServiceLoading extends ServiceState {}

class ServiceSuccess extends ServiceState {
  final List<Service> services;

  const ServiceSuccess({required this.services});

  @override
  List<Object> get props => [services];
}

class ServiceFailure extends ServiceState {
  final String message;

  const ServiceFailure({required this.message});

  @override
  List<Object> get props => [message];
}

class ServiceCreated extends ServiceState {
  final Service service;

  const ServiceCreated({required this.service});

  @override
  List<Object> get props => [service];
}

class ServiceUpdated extends ServiceState {
  final Service service;

  const ServiceUpdated({required this.service});

  @override
  List<Object> get props => [service];
}

class ServiceDeleted extends ServiceState {
  final String serviceId;

  const ServiceDeleted({required this.serviceId});

  @override
  List<Object> get props => [serviceId];
}

// BLoC
class ServiceBloc extends Bloc<ServiceEvent, ServiceState> {
  final ServiceRepository serviceRepository;

  ServiceBloc({required this.serviceRepository}) : super(ServiceInitial()) {
    on<LoadServices>(_onLoadServices);
    on<LoadServicesByCategory>(_onLoadServicesByCategory);
    on<LoadActiveServices>(_onLoadActiveServices);
    on<SearchServices>(_onSearchServices);
    on<LoadServiceById>(_onLoadServiceById);
    on<CreateService>(_onCreateService);
    on<UpdateService>(_onUpdateService);
    on<DeleteService>(_onDeleteService);
  }

  Future<void> _onLoadServices(
    LoadServices event,
    Emitter<ServiceState> emit,
  ) async {
    emit(ServiceLoading());
    
    try {
      final result = await serviceRepository.getServices();
      
      result.fold(
        (failure) => emit(ServiceFailure(message: failure.message)),
        (services) => emit(ServiceSuccess(services: services)),
      );
    } catch (e) {
      emit(ServiceFailure(message: 'Failed to load services: ${e.toString()}'));
    }
  }

  Future<void> _onLoadServicesByCategory(
    LoadServicesByCategory event,
    Emitter<ServiceState> emit,
  ) async {
    emit(ServiceLoading());
    
    try {
      final result = await serviceRepository.getServicesByCategory(event.categoryId);
      
      result.fold(
        (failure) => emit(ServiceFailure(message: failure.message)),
        (services) => emit(ServiceSuccess(services: services)),
      );
    } catch (e) {
      emit(ServiceFailure(message: 'Failed to load services: ${e.toString()}'));
    }
  }

  Future<void> _onLoadActiveServices(
    LoadActiveServices event,
    Emitter<ServiceState> emit,
  ) async {
    emit(ServiceLoading());
    
    try {
      final result = await serviceRepository.getActiveServices();
      
      result.fold(
        (failure) => emit(ServiceFailure(message: failure.message)),
        (services) => emit(ServiceSuccess(services: services)),
      );
    } catch (e) {
      emit(ServiceFailure(message: 'Failed to load services: ${e.toString()}'));
    }
  }

  Future<void> _onSearchServices(
    SearchServices event,
    Emitter<ServiceState> emit,
  ) async {
    emit(ServiceLoading());
    
    try {
      final result = await serviceRepository.searchServices(event.query);
      
      result.fold(
        (failure) => emit(ServiceFailure(message: failure.message)),
        (services) => emit(ServiceSuccess(services: services)),
      );
    } catch (e) {
      emit(ServiceFailure(message: 'Failed to search services: ${e.toString()}'));
    }
  }

  Future<void> _onLoadServiceById(
    LoadServiceById event,
    Emitter<ServiceState> emit,
  ) async {
    emit(ServiceLoading());
    
    try {
      final result = await serviceRepository.getServiceById(event.serviceId);
      
      result.fold(
        (failure) => emit(ServiceFailure(message: failure.message)),
        (service) => emit(ServiceSuccess(services: [service])),
      );
    } catch (e) {
      emit(ServiceFailure(message: 'Failed to load service: ${e.toString()}'));
    }
  }

  Future<void> _onCreateService(
    CreateService event,
    Emitter<ServiceState> emit,
  ) async {
    emit(ServiceLoading());
    
    try {
      final result = await serviceRepository.createService(event.service);
      
      result.fold(
        (failure) => emit(ServiceFailure(message: failure.message)),
        (service) => emit(ServiceCreated(service: service)),
      );
    } catch (e) {
      emit(ServiceFailure(message: 'Failed to create service: ${e.toString()}'));
    }
  }

  Future<void> _onUpdateService(
    UpdateService event,
    Emitter<ServiceState> emit,
  ) async {
    emit(ServiceLoading());
    
    try {
      final result = await serviceRepository.updateService(
        event.serviceId,
        event.service,
      );
      
      result.fold(
        (failure) => emit(ServiceFailure(message: failure.message)),
        (service) => emit(ServiceUpdated(service: service)),
      );
    } catch (e) {
      emit(ServiceFailure(message: 'Failed to update service: ${e.toString()}'));
    }
  }

  Future<void> _onDeleteService(
    DeleteService event,
    Emitter<ServiceState> emit,
  ) async {
    emit(ServiceLoading());
    
    try {
      final result = await serviceRepository.deleteService(event.serviceId);
      
      result.fold(
        (failure) => emit(ServiceFailure(message: failure.message)),
        (success) => emit(ServiceDeleted(serviceId: event.serviceId)),
      );
    } catch (e) {
      emit(ServiceFailure(message: 'Failed to delete service: ${e.toString()}'));
    }
  }
}
