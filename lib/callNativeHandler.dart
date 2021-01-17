import 'dart:async';

import 'package:flutter/services.dart';

class CallNativeHandler {
  static const MethodChannel _channel = const MethodChannel('android_channel');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String> get checkMessage async {
    final String message = await _channel.invokeMethod('getCheckMessage');
    return message;
  }

  static Future<dynamic> get dicoverPrinter async {
    var dono = await _channel.invokeMethod('discoverPrinter');
    return dono;
  }

  static Future<dynamic> get printReceipt async {
    var status = await _channel.invokeMethod('printReceipt');
    return status;
  }

  static Future<dynamic> get callWithArgs async {
    var sendMap = <String, dynamic>{
      "test": "Ayam Goreng\nsapi bakar\n",
    };
    var response = await _channel.invokeMethod('callWithArgs', sendMap);
    return response;
  }
}
