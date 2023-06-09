package com.example.current_location

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry

/** CurrentLocationPlugin */
class CurrentLocationPlugin : FlutterPlugin, MethodCallHandler,
    PluginRegistry.RequestPermissionsResultListener, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    private lateinit var activity: Activity
    private var mLocationManager: LocationManager? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var methodChannelResult: Result


    @RequiresApi(Build.VERSION_CODES.M)
    private fun getCoordinates(result: Result): Map<String, Double> {
        var lat = 0.0
        var long = 0.0
        print("start getlatitude")

        mLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        val isGpsEnabled = mLocationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)
        println("isGpsEnabled: $isGpsEnabled")
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (isGpsEnabled != null) {
                if (isGpsEnabled) {
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                        lat = location?.latitude ?: 0.0
                        long = location?.longitude ?: 0.0
                        println("fused location got coordinates of: $lat, $long")
                        val coordinates = mapOf<String, Double>(
                            "latitude" to lat,
                            "longitude" to long,
                        )
                        result.success(coordinates)
                    }
//                    val locationGps =
//                        mLocationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//                    if (locationGps != null) {
//                        lat = locationGps.latitude
//                        long = locationGps.longitude
//                        println("location manager got coords of: $lat, $long")
//                    }
                } else {
                    println("TURN ON GPS")
                }
            }
        } else {
            // ask user for permission to use fine location
            val permissions: Array<String> =
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
            activity.requestPermissions(permissions, 1)
        }
        val coordinates = mapOf<String, Double>(
            "latitude" to lat,
            "longitude" to long,
        )

        return coordinates
    }

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "current_location")
        channel.setMethodCallHandler(this)
        // access context
        context = flutterPluginBinding.applicationContext
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMethodCall(call: MethodCall, result: Result) {
        if (call.method == "getPlatformVersion") {
            result.success("Android ${android.os.Build.VERSION.RELEASE}")
        } else if (call.method == "getCoordinates") {
            methodChannelResult = result
            val reading: Map<String, Double> = getCoordinates(result)
//            result.success(reading)
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        // access activity
        // used when requesting user permission to access location
        activity = binding.activity
        binding.addRequestPermissionsResultListener(this)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        TODO("Not yet implemented")
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        TODO("Not yet implemented")
    }

    override fun onDetachedFromActivity() {
        TODO("Not yet implemented")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        var lat = 0.0
        var long = 0.0
        if (grantResults.first() == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                lat = location?.latitude ?: 0.0
                long = location?.longitude ?: 0.0
                println("onRequestPermissionsResult => fused location got coordinates of: $lat, $long")
                val coordinates = mapOf<String, Double>(
                    "latitude" to lat,
                    "longitude" to long,
                )
                methodChannelResult.success(coordinates)
            }
            return true
        }
        return false
    }
}
