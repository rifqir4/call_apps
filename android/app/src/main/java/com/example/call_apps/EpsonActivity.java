package com.example.call_apps;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.Log;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;
import java.util.List;

public class EpsonActivity extends Activity implements View.OnClickListener, ReceiveListener {

    private static final int REQUEST_PERMISSION = 100;
    private static final int DISCONNECT_INTERVAL = 500;//millseconds

    private Context mContext = null;
    public static EditText mEditTarget = null;
    public static Spinner mSpnSeries = null;
    public static Spinner mSpnLang = null;
    public static Printer  mPrinter = null;
    public static ToggleButton mDrawer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epson);

        //requestRuntimePermission();

//        enableLocationSetting();

        mContext = this;

        int[] target = {
                R.id.btnSampleReceipt,
        };

        for (int i = 0; i < target.length; i++) {
            Button button = (Button)findViewById(target[i]);
            button.setOnClickListener(this);
        }


        initializeObject();

        try {
            Log.setLogSettings(mContext, Log.PERIOD_TEMPORARY, Log.OUTPUT_STORAGE, null, 0, 1, Log.LOGLEVEL_LOW);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "setLogSettings", mContext);
        }
    }

    @Override
    protected void onDestroy() {

        finalizeObject();

        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch (v.getId()) {

            case R.id.btnSampleReceipt:
                updateButtonState(false);
                if (!runPrintReceiptSequence()) {
                    updateButtonState(true);
                }
                break;

            default:
                // Do nothing
                break;
        }
    }

    private boolean runPrintReceiptSequence() {

        if (!createReceiptData()) {
            return false;
        }

        if (!printData()) {
            return false;
        }

        return true;
    }

    private String customJustify(String left, String right){
        String justifyText;
        int space = 33 - left.length() - right.length();
        justifyText = left;
        for (int i = 0; i < space; i++) {
            justifyText  += ' ';
        }
        justifyText += right;
        return justifyText;
    }

    private boolean createReceiptData() {
        String method = "";
        StringBuilder textData = new StringBuilder();
        final String divider = "--------------------------------\n";

        if (mPrinter == null) {
            return false;
        }

        try {


            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);

            method = "addTextSize";
            mPrinter.addTextSize(2, 2);
            method = "addText";
            mPrinter.addText("TOKO\nSOLO MAKMUR\n");

            method = "addTextSize";
            mPrinter.addTextSize(1, 1);
            method = "addFeedLine";
            mPrinter.addFeedLine(1);
            textData.append("Agen Gas, Beras, dan\n");
            textData.append("Minuman Kemasan\n\n");
            textData.append("Jl. Kasuari Raya No. 256 Perumnas 1, Kayurungin Jaya, Bekasi Selatan, Kota Bekasi, 17144\n");
            textData.append("Telp. 021-8868068\n");
            textData.append("\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            textData.append(divider);
            textData.append("Aqua Galon (Eceran)\n");
            textData.append(customJustify("3x @37.000", "101.000") + "\n\n");

            textData.append("Raja Lele (Eceran)\n");
            textData.append(customJustify("1x @50.000", "50.000") + "\n\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addText";
            mPrinter.addText(divider);
            mPrinter.addText(customJustify("TOTAL", "Rp. 151.000"));

            method = "addFeedLine";
            mPrinter.addFeedLine(1);
            method = "addText";
            textData.append(divider);
            textData.append("Alamat Pelanggan :\n");
            textData.append("Jl. Piranha Atas\n");
            textData.append("Keterangan :\n");
            textData.append("Rifqi Radifan\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addFeedLine";
            mPrinter.addFeedLine(1);
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            method = "addText";
            mPrinter.addText("-Terima Kasih-");

            method = "addFeedLine";
            mPrinter.addFeedLine(2);

            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);

        }
        catch (Exception e) {
            mPrinter.clearCommandBuffer();
            ShowMsg.showException(e, method, mContext);
            return false;
        }

        textData = null;

        return true;
    }

    private boolean runPrintCouponSequence() {

        if (!createCouponData()) {
            return false;
        }

        if (!printData()) {
            return false;
        }

        return true;
    }

    private boolean createCouponData() {
        String method = "";
//        Bitmap coffeeData = BitmapFactory.decodeResource(getResources(), R.drawable.coffee);
//        Bitmap wmarkData = BitmapFactory.decodeResource(getResources(), R.drawable.wmark);
//
//        final int barcodeWidth = 2;
//        final int barcodeHeight = 64;
//
//        if (mPrinter == null) {
//            return false;
//        }
//
//        try {
//            method = "addImage";
//            mPrinter.addImage(wmarkData, 0, 0, wmarkData.getWidth(), wmarkData.getHeight(), Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT);
//
//            method = "addImage";
//            mPrinter.addImage(coffeeData, 0, 0, coffeeData.getWidth(), coffeeData.getHeight(), Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, 3, Printer.PARAM_DEFAULT);
//
//            method = "addBarcode";
//            mPrinter.addBarcode("01234567890", Printer.BARCODE_UPC_A, Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, barcodeWidth, barcodeHeight);
//
//            method = "addCut";
//            mPrinter.addCut(Printer.CUT_FEED);
//        }
//        catch (Exception e) {
//            mPrinter.clearCommandBuffer();
//            ShowMsg.showException(e, method, mContext);
//            return false;
//        }

        return true;
    }

    private boolean printData() {
        if (mPrinter == null) {
            return false;
        }

        if (!connectPrinter()) {
            mPrinter.clearCommandBuffer();
            return false;
        }

        try {
            mPrinter.sendData(Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {
            mPrinter.clearCommandBuffer();
            ShowMsg.showException(e, "sendData", mContext);
            try {
                mPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        return true;
    }

    private boolean initializeObject() {
        try {
            mPrinter = new Printer(Printer.TM_U220,
                    Printer.MODEL_ANK,
                    mContext);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "Printer", mContext);
            return false;
        }

        mPrinter.setReceiveEventListener(this);

        return true;
    }

    private void finalizeObject() {
        if (mPrinter == null) {
            return;
        }

        mPrinter.setReceiveEventListener(null);

        mPrinter = null;
    }

    private boolean connectPrinter() {
        if (mPrinter == null) {
            return false;
        }

        try {
            mPrinter.connect("USB:/dev/bus/usb/001/002", Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "connect", mContext);
            return false;
        }

        return true;
    }

    private void disconnectPrinter() {
        if (mPrinter == null) {
            return;
        }

        while (true) {
            try {
                mPrinter.disconnect();
                break;
            } catch (final Exception e) {
                if (e instanceof Epos2Exception) {
                    //Note: If printer is processing such as printing and so on, the disconnect API returns ERR_PROCESSING.
                    if (((Epos2Exception) e).getErrorStatus() == Epos2Exception.ERR_PROCESSING) {
                        try {
                            Thread.sleep(DISCONNECT_INTERVAL);
                        } catch (Exception ex) {
                        }
                    }else{
                        runOnUiThread(new Runnable() {
                            public synchronized void run() {
                                ShowMsg.showException(e, "disconnect", mContext);
                            }
                        });
                        break;
                    }
                }else{
                    runOnUiThread(new Runnable() {
                        public synchronized void run() {
                            ShowMsg.showException(e, "disconnect", mContext);
                        }
                    });
                    break;
                }
            }
        }

        mPrinter.clearCommandBuffer();
    }

    private String makeErrorMessage(PrinterStatusInfo status) {
        String msg = "";

        if (status.getOnline() == Printer.FALSE) {
            msg += getString(R.string.handlingmsg_err_offline);
        }
        if (status.getConnection() == Printer.FALSE) {
            msg += getString(R.string.handlingmsg_err_no_response);
        }
        if (status.getCoverOpen() == Printer.TRUE) {
            msg += getString(R.string.handlingmsg_err_cover_open);
        }
        if (status.getPaper() == Printer.PAPER_EMPTY) {
            msg += getString(R.string.handlingmsg_err_receipt_end);
        }
        if (status.getPaperFeed() == Printer.TRUE || status.getPanelSwitch() == Printer.SWITCH_ON) {
            msg += getString(R.string.handlingmsg_err_paper_feed);
        }
        if (status.getErrorStatus() == Printer.MECHANICAL_ERR || status.getErrorStatus() == Printer.AUTOCUTTER_ERR) {
            msg += getString(R.string.handlingmsg_err_autocutter);
            msg += getString(R.string.handlingmsg_err_need_recover);
        }
        if (status.getErrorStatus() == Printer.UNRECOVER_ERR) {
            msg += getString(R.string.handlingmsg_err_unrecover);
        }
        if (status.getErrorStatus() == Printer.AUTORECOVER_ERR) {
            if (status.getAutoRecoverError() == Printer.HEAD_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_head);
            }
            if (status.getAutoRecoverError() == Printer.MOTOR_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_motor);
            }
            if (status.getAutoRecoverError() == Printer.BATTERY_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_battery);
            }
            if (status.getAutoRecoverError() == Printer.WRONG_PAPER) {
                msg += getString(R.string.handlingmsg_err_wrong_paper);
            }
        }
        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_0) {
            msg += getString(R.string.handlingmsg_err_battery_real_end);
        }

        return msg;
    }

//    private void dispPrinterWarnings(PrinterStatusInfo status) {
//        EditText edtWarnings = (EditText)findViewById(R.id.edtWarnings);
//        String warningsMsg = "";
//
//        if (status == null) {
//            return;
//        }
//
//        if (status.getPaper() == Printer.PAPER_NEAR_END) {
//            warningsMsg += getString(R.string.handlingmsg_warn_receipt_near_end);
//        }
//
//        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_1) {
//            warningsMsg += getString(R.string.handlingmsg_warn_battery_near_end);
//        }
//
//        edtWarnings.setText(warningsMsg);
//    }

    private void updateButtonState(boolean state) {
        Button btnReceipt = (Button)findViewById(R.id.btnSampleReceipt);

        btnReceipt.setEnabled(state);
    }

    @Override
    public void onPtrReceive(final Printer printerObj, final int code, final PrinterStatusInfo status, final String printJobId) {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                ShowMsg.showResult(code, makeErrorMessage(status), mContext);

                //dispPrinterWarnings(status);

                updateButtonState(true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectPrinter();
                    }
                }).start();
            }
        });
    }

    private void requestRuntimePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
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

    //When searching for a device running on Android 10 or later as a Bluetooth-capable device, enable access to location information of the device.
//    private void enableLocationSetting() {
//        LocationRequest locationRequest = LocationRequest.create();
//        locationRequest.setInterval(10000);
//        locationRequest.setFastestInterval(5000);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(locationRequest);;
//
//        SettingsClient client = LocationServices.getSettingsClient(this);
//        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
//
//        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
//            @Override
//            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
//                // All location settings are satisfied. The client can initialize
//                // location requests here.
//                // ...
//            }
//        });
//
//        task.addOnFailureListener(this, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                if (e instanceof ResolvableApiException) {
//                    // Location settings are not satisfied, but this can be fixed
//                    // by showing the user a dialog.
//                    try {
//                        // Show the dialog by calling startResolutionForResult(),
//                        // and check the result in onActivityResult().
//                        ResolvableApiException resolvable = (ResolvableApiException) e;
//                        resolvable.startResolutionForResult(MainActivity.this,
//                                CommonStatusCodes.RESOLUTION_REQUIRED);
//                    } catch (IntentSender.SendIntentException sendEx) {
//                        // Ignore the error.
//                    }
//                }
//            }
//        });
//    }
}
