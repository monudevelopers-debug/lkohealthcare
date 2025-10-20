import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:dio/dio.dart';

import 'core/network/network_info.dart';
import 'data/datasources/remote/auth_remote_datasource.dart';
import 'data/datasources/remote/service_remote_datasource.dart';
import 'data/datasources/remote/booking_remote_datasource.dart';
import 'data/datasources/local/auth_local_datasource.dart';
import 'data/repositories/auth_repository_impl.dart';
import 'data/repositories/service_repository_impl.dart';
import 'data/repositories/booking_repository_impl.dart';
import 'domain/repositories/auth_repository.dart';
import 'domain/repositories/service_repository.dart';
import 'domain/repositories/order_repository.dart';
import 'presentation/bloc/auth/auth_bloc.dart';
import 'presentation/bloc/service/service_bloc.dart';
import 'presentation/bloc/order/order_bloc.dart';
import 'presentation/routes/app_router.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  
  // Initialize dependencies
  final sharedPreferences = await SharedPreferences.getInstance();
  const secureStorage = FlutterSecureStorage();
  final connectivity = Connectivity();
  final dio = Dio();
  
  // Configure Dio
  dio.options.baseUrl = 'http://localhost:8080';  // Backend API base URL
  dio.options.connectTimeout = const Duration(seconds: 30);
  dio.options.receiveTimeout = const Duration(seconds: 30);
  
  // Add token interceptor
  dio.interceptors.add(
    InterceptorsWrapper(
      onRequest: (options, handler) async {
        // Get token from secure storage
        final token = await secureStorage.read(key: 'auth_token');
        if (token != null) {
          options.headers['Authorization'] = 'Bearer $token';
        }
        return handler.next(options);
      },
      onError: (error, handler) {
        // Handle token expiration
        if (error.response?.statusCode == 401) {
          // Token expired or invalid
          secureStorage.delete(key: 'auth_token');
        }
        return handler.next(error);
      },
    ),
  );
  
  // Initialize data sources
  final networkInfo = NetworkInfoImpl(connectivity: connectivity);
  final authRemoteDataSource = AuthRemoteDataSourceImpl(dio: dio);
  final authLocalDataSource = AuthLocalDataSourceImpl(
    sharedPreferences: sharedPreferences,
    secureStorage: secureStorage,
  );
  final serviceRemoteDataSource = ServiceRemoteDataSourceImpl(dio: dio);
  final bookingRemoteDataSource = BookingRemoteDataSourceImpl(dio: dio);
  
  // Initialize repositories
  final authRepository = AuthRepositoryImpl(
    remoteDataSource: authRemoteDataSource,
    localDataSource: authLocalDataSource,
    networkInfo: networkInfo,
  );
  final serviceRepository = ServiceRepositoryImpl(
    remoteDataSource: serviceRemoteDataSource,
    networkInfo: networkInfo,
  );
  final orderRepository = BookingRepositoryImpl(
    remoteDataSource: bookingRemoteDataSource,
    networkInfo: networkInfo,
  );
  
  runApp(LucknowHealthcareApp(
    authRepository: authRepository,
    serviceRepository: serviceRepository,
    orderRepository: orderRepository,
  ));
}

class LucknowHealthcareApp extends StatelessWidget {
  final AuthRepository authRepository;
  final ServiceRepository serviceRepository;
  final OrderRepository orderRepository;
  
  const LucknowHealthcareApp({
    super.key,
    required this.authRepository,
    required this.serviceRepository,
    required this.orderRepository,
  });

  @override
  Widget build(BuildContext context) {
    return MultiBlocProvider(
      providers: [
        BlocProvider<AuthBloc>(
          create: (context) => AuthBloc(authRepository: authRepository),
        ),
        BlocProvider<ServiceBloc>(
          create: (context) => ServiceBloc(serviceRepository: serviceRepository),
        ),
        BlocProvider<OrderBloc>(
          create: (context) => OrderBloc(orderRepository: orderRepository),
        ),
      ],
      child: MaterialApp.router(
        title: 'Lucknow Healthcare Services',
        debugShowCheckedModeBanner: false,
        theme: ThemeData(
          primarySwatch: Colors.blue,
          useMaterial3: true,
          colorScheme: ColorScheme.fromSeed(
            seedColor: const Color(0xFF1976D2),
            brightness: Brightness.light,
          ),
        ),
        localizationsDelegates: const [
          GlobalMaterialLocalizations.delegate,
          GlobalWidgetsLocalizations.delegate,
          GlobalCupertinoLocalizations.delegate,
        ],
        supportedLocales: const [
          Locale('en', 'US'),
          Locale('hi', 'IN'),
        ],
        routerConfig: AppRouter.router,
      ),
    );
  }
}
