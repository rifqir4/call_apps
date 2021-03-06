package com.example.call_apps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.Log;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;

public class MainActivity extends FlutterActivity implements MethodCallHandler {
    private static final int REQUEST_PERMISSION = 100;

    private String CHANNEL = "android_channel";
    private MethodChannel mChannel = null;

    private MethodChannel.Result mResult = null;

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        requestRuntimePermission();

        mChannel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL);
        mChannel.setMethodCallHandler(this);

    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        Map<String, Object> args = call.arguments();
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("getCheckMessage")) {
            result.success("Hello from Java!!");
        } else if (call.method.equals("discoverPrinter")) {
            Intent intent = new Intent(this, DisocverActivity.class);
            startActivityForResult(intent, 0);
        } else if (call.method.equals("printReceipt")) {
            this.mResult = result;
            Intent intent = new Intent(this, EpsonActivity.class);
            startActivityForResult(intent, 123);
        } else if(call.method.equals("callWithArgs")) {
            String test = (String) args.get("test");
            result.success("This is test: " + test );
        } else {
            result.notImplemented();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == 123){
                String res = data.getStringExtra("printActivity");
                mResult.success(res);
            }
        }
    }

    private void requestRuntimePermission() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            return;
        }

        int permissionStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //int permissionLocationCoarse= ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionLocationFine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> requestPermissions = new ArrayList<>();

        if (permissionStorage == PackageManager.PERMISSION_DENIED) {
            requestPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionLocationFine == PackageManager.PERMISSION_DENIED) {
            requestPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
//        if (permissionLocationCoarse == PackageManager.PERMISSION_DENIED) {
//            requestPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
//        }

        if (!requestPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, requestPermissions.toArray(new String[requestPermissions.size()]), REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode != REQUEST_PERMISSION || grantResults.length == 0) {
            return;
        }

        List<String> requestPermissions = new ArrayList<>();

        for (int i = 0; i < permissions.length; i++) {
            if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && grantResults[i] == PackageManager.PERMISSION_DENIED) {
                requestPermissions.add(permissions[i]);
            }
            if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)
                    && grantResults[i] == PackageManager.PERMISSION_DENIED) {
                requestPermissions.add(permissions[i]);
            }

            // If your app targets Android 9 or lower, you can declare ACCESS_COARSE_LOCATION instead.
//            if (permissions[i].equals(Manifest.permission.ACCESS_COARSE_LOCATION)
//                    && grantResults[i] == PackageManager.PERMISSION_DENIED) {
//                requestPermissions.add(permissions[i]);
//            }
        }

        if (!requestPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, requestPermissions.toArray(new String[requestPermissions.size()]), REQUEST_PERMISSION);
        }
    }

}
