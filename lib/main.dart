import 'package:call_apps/callNativeHandler.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  String _message = 'Unknown';
  var _dono = "Ready";
  var _printStat = "Ready";
  var _dataTest = "No Data";

  @override
  void initState() {
    super.initState();
    initPlatformState();
    checkMessage();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await CallNativeHandler.platformVersion;
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

  Future<void> checkMessage() async {
    String message;

    try {
      message = await CallNativeHandler.checkMessage;
    } on PlatformException {
      message = 'Failed to get messages';
    }

    if (!mounted) return;

    setState(() {
      _message = message;
    });
  }

  Future<void> discoverPrinter() async {
    var dono;

    setState(() {
      _dono = "Starting";
    });

    try {
      dono = await CallNativeHandler.dicoverPrinter;
    } on PlatformException {
      dono = 'Failed to get printer';
    }

    if (!mounted) return;

    setState(() {
      _dono = dono;
    });
  }

  Future<void> printReceipt() async {
    var status;

    setState(() {
      status = "Starting";
    });

    try {
      status = await CallNativeHandler.printReceipt;
    } on PlatformException {
      status = 'Failed to print';
    }

    if (!mounted) return;

    setState(() {
      _printStat = status;
    });
  }

  Future<void> getDataTest() async {
    var response;

    try {
      response = await CallNativeHandler.callWithArgs;
    } on PlatformException {
      response = "Failed to get test";
    }

    if (!mounted) return;

    setState(() {
      _dataTest = response;
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
              Text('Running on: $_platformVersion\n Message : $_message'),
              RaisedButton(
                onPressed: discoverPrinter,
                child: Text('discover'),
              ),
              Text('${_dono.toString()}'),
              RaisedButton(
                onPressed: printReceipt,
                child: Text('print'),
              ),
              Text('${_printStat.toString()}'),
              RaisedButton(
                onPressed: () {
                  getDataTest();
                },
                child: Text("Data With Args"),
              ),
              Text('$_dataTest'),
            ],
          ),
        ),
      ),
    );
  }
}
