import 'package:shared_preferences/shared_preferences.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'dart:convert';
import 'package:lucknow_healthcare/domain/entities/user.dart';

abstract class AuthLocalDataSource {
  Future<User?> getCachedUser();
  Future<void> cacheUser(User user);
  Future<void> clearCache();
  Future<String?> getToken();
  Future<void> saveToken(String token);
  Future<void> clearToken();
}

class AuthLocalDataSourceImpl implements AuthLocalDataSource {
  final SharedPreferences sharedPreferences;
  final FlutterSecureStorage secureStorage;

  AuthLocalDataSourceImpl({
    required this.sharedPreferences,
    required this.secureStorage,
  });

  static const String _userKey = 'cached_user';
  static const String _tokenKey = 'auth_token';

  @override
  Future<User?> getCachedUser() async {
    try {
      final userJson = sharedPreferences.getString(_userKey);
      if (userJson != null) {
        final userMap = json.decode(userJson) as Map<String, dynamic>;
        return User.fromJson(userMap);
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  @override
  Future<void> cacheUser(User user) async {
    try {
      final userJson = json.encode(user.toJson());
      await sharedPreferences.setString(_userKey, userJson);
    } catch (e) {
      // Handle cache error silently
    }
  }

  @override
  Future<void> clearCache() async {
    try {
      await sharedPreferences.remove(_userKey);
      await secureStorage.delete(key: _tokenKey);
    } catch (e) {
      // Handle cache error silently
    }
  }

  @override
  Future<String?> getToken() async {
    try {
      return await secureStorage.read(key: _tokenKey);
    } catch (e) {
      return null;
    }
  }

  @override
  Future<void> saveToken(String token) async {
    try {
      await secureStorage.write(key: _tokenKey, value: token);
    } catch (e) {
      // Handle token save error silently
    }
  }

  @override
  Future<void> clearToken() async {
    try {
      await secureStorage.delete(key: _tokenKey);
    } catch (e) {
      // Handle token clear error silently
    }
  }
}
