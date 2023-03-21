import 'package:flutter_test/flutter_test.dart';
import 'package:current_location/current_location.dart';
import 'package:current_location/current_location_platform_interface.dart';
import 'package:current_location/current_location_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockCurrentLocationPlatform
    with MockPlatformInterfaceMixin
    implements CurrentLocationPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');

  // @override
  // Future<double?> getLatitude() => Future.value(12.34);
}

void main() {
  final CurrentLocationPlatform initialPlatform = CurrentLocationPlatform.instance;

  test('$MethodChannelCurrentLocation is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelCurrentLocation>());
  });

  test('getPlatformVersion', () async {
    CurrentLocation currentLocationPlugin = CurrentLocation();
    MockCurrentLocationPlatform fakePlatform = MockCurrentLocationPlatform();
    CurrentLocationPlatform.instance = fakePlatform;

    expect(await currentLocationPlugin.getPlatformVersion(), '42');
  });
}
