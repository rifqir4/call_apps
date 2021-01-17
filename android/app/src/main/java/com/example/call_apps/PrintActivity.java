package com.example.call_apps;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.epson.epos2.Epos2CallbackCode;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;

import java.util.ArrayList;
import java.util.List;

public class PrintActivity extends Activity implements View.OnClickListener, ReceiveListener {

    private static final int REQUEST_PERMISSION = 100;
    private static final int DISCONNECT_INTERVAL = 500;//millseconds

    private Context mContext = null;
    public static Printer mPrinter = null;
    public static TextView tvInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);

        mContext = this;

        tvInfo = findViewById(R.id.tv_info);
        tvInfo.setText("Start");

        Button mButton = findViewById(R.id.button_print);
        mButton.setOnClickListener(this);

        tvInfo.setText("Init Printer");
        if(!initPrinter()){
            tvInfo.setText("Gagal Init");
        } else {
            tvInfo.setText("Berhasil Init");
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_print:
                tvInfo.setText("Button Clicked");
                Log.d("Klik", "Button Print Clicked");
                if(createPrintData()){
                    if(connectPrinter()){
                        printData();
                    };
                };
                break;

            default:
                // Do nothing
                break;
        }
    }

    private boolean initPrinter() {

        try {
            mPrinter = new Printer(Printer.TM_U220, Printer.MODEL_ANK, mContext);
        } catch (Epos2Exception e){
            Log.d("Epos2Exception", e.toString());
            return false;
        }
        mPrinter.setReceiveEventListener((ReceiveListener) mContext);
        return true;
    }

    private boolean createPrintData(){
        try {
            tvInfo.setText("Create Data");
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addText("Hello World");
        } catch (Epos2Exception e) {
            tvInfo.setText("Error Create Data");
            Log.d("Epos2Exception", e.toString());
            return false;
        }
        tvInfo.setText("Data Created");
        return true;
    }

    private boolean connectPrinter(){
        try {
            tvInfo.setText("Connect Printer");
            mPrinter.connect("USB:/dev/bus/usb/001/002",Printer.PARAM_DEFAULT);
        } catch (Epos2Exception e) {
            Log.d("Epos2Exception", e.toString());
            tvInfo.setText("Error Connect");
            mPrinter.clearCommandBuffer();
            return false;
        }
        tvInfo.setText("Printer Connected");
        return  true;
    }

    private boolean printData(){
        try {
            tvInfo.setText("Start Print");
            mPrinter.sendData(Printer.PARAM_DEFAULT);
        } catch (Epos2Exception e) {
            tvInfo.setText("Error Print");
            mPrinter.clearCommandBuffer();
            Log.d("Epos2Exception", e.toString());
            disconnectPrinter();
            return false;
        }
        mPrinter.clearCommandBuffer();
        tvInfo.setText("Success Print");
        return true;
    }

    private boolean disconnectPrinter(){
        try {
            mPrinter.disconnect();
        }
        catch (Epos2Exception e) {
            Log.d("Epos2Exception", e.toString());
            return false;
        }

        tvInfo.setText("Printer Disconnected");
        mPrinter.clearCommandBuffer();

        return true;
    }

    public void onPtrReceive(final Printer printerObj, final int code, final PrinterStatusInfo status,
                             final String printJobId) {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                if (code == Epos2CallbackCode.CODE_SUCCESS) {
                    tvInfo.setText("Success Receive");
                    Log.d("EpsonPrinter", "Success Receive");
                }
                else {
                    Log.d("EpsonPrinter", "Gagal Receive");
                    tvInfo.setText("Gagal Receive");

                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public synchronized void run() {
            disconnectPrinter();
            }
        }).start();
    }
}