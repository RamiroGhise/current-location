package com.example.current_location

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** CurrentLocationPlugin */
class CurrentLocationPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    private lateinit var activity: Activity
    private var mLocationManager: LocationManager? = null

    private fun getCoordinates(): Map<String, Double> {
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
                    val locationGps =
                        mLocationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (locationGps != null) {
                        lat = locationGps.latitude
                        long = locationGps.longitude
                        println("location manager got latitude of: $lat")
                    }

                } else {
                    println("TURN ON GPS")
                }
            }
        } else {
            // ask user for permission to use fine location
            val permissions: Array<String> =
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(activity, permissions, 1)
        }
        val coordinates = mapOf<String, Double>(
            "latitude" to lat,
            "longitude" to long,
        )

        return coordinates
    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "current_location")
        channel.setMethodCallHandler(this)
        // access context
        context = flutterPluginBinding.applicationContext
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (call.method == "getPlatformVersion") {
            result.success("Android ${android.os.Build.VERSION.RELEASE}")
        } else if (call.method == "getCoordinates") {
            val reading: Map<String, Double> = getCoordinates()
            result.success(reading)
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        // access activity
        // used when requesting user permission to access location
        activity = binding.activity;
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
}
