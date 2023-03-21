import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'current_location_method_channel.dart';

abstract class CurrentLocationPlatform extends PlatformInterface {
  /// Constructs a CurrentLocationPlatform.
  CurrentLocationPlatform() : super(token: _token);

  static final Object _token = Object();

  static CurrentLocationPlatform _instance = MethodChannelCurrentLocation();

  /// The default instance of [CurrentLocationPlatform] to use.
  ///
  /// Defaults to [MethodChannelCurrentLocation].
  static CurrentLocationPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [CurrentLocationPlatform] when
  /// they register themselves.
  static set instance(CurrentLocationPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<Map<String, double>?> getCoordinates() {
    throw UnimplementedError('getCoordinates() has not been implemented.');
  }
}
