import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'current_location_platform_interface.dart';

/// An implementation of [CurrentLocationPlatform] that uses method channels.
class MethodChannelCurrentLocation extends CurrentLocationPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('current_location');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<Map<String, double>?> getCoordinates() async {
    final coordinates = await methodChannel.invokeMethod<Map<dynamic, dynamic>?>('getCoordinates');
    if (coordinates != null) {
      final coordinatesMap = (coordinates as Map).cast<String, double>();
      return coordinatesMap;
    }
    return null;
  }
}
