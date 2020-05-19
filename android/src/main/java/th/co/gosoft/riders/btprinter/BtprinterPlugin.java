package th.co.gosoft.riders.btprinter;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
// todo: add event channel here
import io.flutter.plugin.common.EventChannel;
// todo: add Handler here
import android.os.Handler;
// todo: add StreamChannel here
import app.loup.streams_channel.StreamsChannel;

import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** BtprinterPlugin */
public class BtprinterPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "btprinter");
    channel.setMethodCallHandler(this);

    final StreamsChannel streamChannel = new StreamsChannel(flutterPluginBinding.getBinaryMessenger(), "bt_printer_channel");
    streamChannel.setStreamHandlerFactory(new StreamsChannel.StreamHandlerFactory() {
      @Override
      public EventChannel.StreamHandler create(Object arguments) {
        return new StreamHandler();
      }
    });
  }

  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "btprinter");
    channel.setMethodCallHandler(new BtprinterPlugin());

    // todo: add streamChannel setup here
    final StreamsChannel streamChannel = new StreamsChannel(registrar.messenger(), "bt_printer_channel");
    streamChannel.setStreamHandlerFactory(new StreamsChannel.StreamHandlerFactory() {
      @Override
      public EventChannel.StreamHandler create(Object arguments) {
        return new StreamHandler();
      }
    });

  }

  // todo: add implementation of EventChannel.StreamHandler here.
  // Send "Hello" 10 times, every second, then ends the stream
  public static class StreamHandler implements EventChannel.StreamHandler {

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
      @Override
      public void run() {
        if (count > 10) {
          eventSink.endOfStream();
        } else {
          eventSink.success("Hello " + count + "/10");
        }
        count++;
        handler.postDelayed(this, 1000);
      }
    };

    private EventChannel.EventSink eventSink;
    private int count = 1;

    @Override
    public void onListen(Object o, final EventChannel.EventSink eventSink) {
      this.eventSink = eventSink;
      runnable.run();
    }

    @Override
    public void onCancel(Object o) {
      handler.removeCallbacks(runnable);
    }
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
