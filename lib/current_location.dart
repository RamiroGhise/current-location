
import 'current_location_platform_interface.dart';

class CurrentLocation {
  Future<String?> getPlatformVersion() {
    return CurrentLocationPlatform.instance.getPlatformVersion();
  }

  Future<Map<String, double>?> getCoordinates() {
    return CurrentLocationPlatform.instance.getCoordinates();
  }
}
