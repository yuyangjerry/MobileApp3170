package com.FIT3170.HealthMonitor;

import android.content.Intent;
import android.os.Bundle;

import com.google.zxing.Result;

import androidx.appcompat.app.AppCompatActivity;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    public static final String RESPONSE_INTENT_URL_KEY = "QRCODE";

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register ourselves as a handler for scan results.
        mScannerView.setResultHandler(this);
        // Start camera on resume
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop camera on pause
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Intent intent = new Intent();
        intent.putExtra(RESPONSE_INTENT_URL_KEY, rawResult.getText());
        setResult(RESULT_OK, intent);
        finish();
    }
}