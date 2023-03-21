import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:current_location/current_location.dart';
import 'dart:developer' as devtools show log;

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  final _currentLocationPlugin = CurrentLocation();
  double? _latitude = 0.0;
  double? _longitude = 0.0;

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  void getCoordinates() async {
    final coordinates = await _currentLocationPlugin.getCoordinates();
    devtools.log("New Latitude is: ${coordinates.toString()}");
    if (coordinates is Map<String, double>) {
      setState(() {
        _latitude = coordinates['latitude'];
        _longitude = coordinates['longitude'];
      });
    }
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion = await _currentLocationPlugin.getPlatformVersion() ??
          'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: [
              Text('Running on: $_platformVersion\n'),
              Text('Latitude is: $_latitude'),
              Text('Longitude is: $_longitude'),
              TextButton(
                onPressed: () {
                  getCoordinates();
                },
                child: const Text('Get coordinates'),
              )
            ],
          ),
        ),
      ),
    );
  }
}
