import Flutter
import UIKit
import CoreLocation

public class CurrentLocationPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "current_location", binaryMessenger: registrar.messenger())
    let instance = CurrentLocationPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
      if (call.method == "getCoordinates") {
          self.getCoordinates(result: result)
      } else if (call.method == "getPlatformVersion")  {
          result("iOS " + UIDevice.current.systemVersion)
      } else {
          result(FlutterMethodNotImplemented)
      }
  }
    
    private func getCoordinates(result: FlutterResult) {
        var locationManager = CLLocationManager()
        locationManager.requestAlwaysAuthorization()
        var locValue: CLLocationCoordinate2D? = locationManager.location?.coordinate
        var latitude:Double = locValue?.latitude ?? 0.0
        var longitude:Double = locValue?.longitude ?? 0.0
        print("locations = \(latitude) \(longitude)")
        let coordinates: [String: Double] = ["latitude": latitude, "longitude": longitude]
        
        result(coordinates)
    }
}
