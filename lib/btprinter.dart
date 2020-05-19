import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';
import 'package:streams_channel/streams_channel.dart';

class Btprinter {
  static const MethodChannel _channel =
      const MethodChannel('btprinter');

  static  StreamsChannel _streamChannel =  StreamsChannel('bt_printer_channel');
  static Stream<dynamic> s;
  static StreamSubscription ss;
  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  // change this to list of string when implement real plugin
  static void getBtPrinterStream(String argument) {
    print('begin: getBtPrinterStream');
    WidgetsFlutterBinding.ensureInitialized();
    s = _streamChannel.receiveBroadcastStream(argument);
    ss= s.listen((i) { print('the stream said $i'); });
    print('after listening');
  }
}
