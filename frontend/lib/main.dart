import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:dio/dio.dart';

import 'app.dart';
import 'core/network/network_info.dart';
import 'data/datasources/remote/auth_remote_datasource.dart';
import 'data/datasources/local/auth_local_datasource.dart';
import 'data/repositories/auth_repository_impl.dart';
import 'domain/repositories/auth_repository.dart';
import 'presentation/bloc/auth/auth_bloc.dart';
import 'presentation/bloc/order/order_bloc.dart';
import 'presentation/bloc/service/service_bloc.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  
  // Initialize dependencies
  final sharedPreferences = await SharedPreferences.getInstance();
  final secureStorage = const FlutterSecureStorage();
  final connectivity = Connectivity();
  final dio = Dio();
  
  // Configure Dio
  dio.options.baseUrl = 'http://localhost:8080';
  dio.options.connectTimeout = const Duration(seconds: 30);
  dio.options.receiveTimeout = const Duration(seconds: 30);
  
  // Initialize data sources
  final networkInfo = NetworkInfoImpl(connectivity: connectivity);
  final authRemoteDataSource = AuthRemoteDataSourceImpl(dio: dio);
  final authLocalDataSource = AuthLocalDataSourceImpl(
    sharedPreferences: sharedPreferences,
    secureStorage: secureStorage,
  );
  
  // Initialize repositories
  final authRepository = AuthRepositoryImpl(
    remoteDataSource: authRemoteDataSource,
    localDataSource: authLocalDataSource,
    networkInfo: networkInfo,
  );
  
  runApp(LucknowHealthcareApp(
    authRepository: authRepository,
  ));
}

class LucknowHealthcareApp extends StatelessWidget {
  final AuthRepository authRepository;
  
  const LucknowHealthcareApp({
    super.key,
    required this.authRepository,
  });

  @override
  Widget build(BuildContext context) {
    return MultiBlocProvider(
      providers: [
        BlocProvider<AuthBloc>(
          create: (context) => AuthBloc(authRepository: authRepository),
        ),
        // TODO: Add other BLoC providers when repositories are implemented
        // BlocProvider<OrderBloc>(
        //   create: (context) => OrderBloc(orderRepository: orderRepository),
        // ),
        // BlocProvider<ServiceBloc>(
        //   create: (context) => ServiceBloc(serviceRepository: serviceRepository),
        // ),
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
