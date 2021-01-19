package com.example.call_apps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class PrintActivity extends Activity implements View.OnClickListener {

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


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_print:
                tvInfo.setText("Button Clicked");
                Intent intent = new Intent();
                Log.d("Klik", "Button Print Clicked");
                intent.putExtra("printActivity", "Kembalian Activity ");
                setResult(RESULT_OK, intent);
                finish();
                break;

            default:
                // Do nothing
                break;
        }
    }


}