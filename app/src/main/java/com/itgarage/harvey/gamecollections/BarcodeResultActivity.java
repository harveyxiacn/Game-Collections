package com.itgarage.harvey.gamecollections;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class BarcodeResultActivity extends ActionBarActivity {

    private TextView resultTextView;
    private String resultStr;
    public static final String BARCODE_SCAN_RESULT_SAVED_TAG = "BARCODE_SCAN_RESULT";
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_result);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.barcode_scan_result_activity_title));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        resultTextView = (TextView) findViewById(R.id.barcodeScanResultTextView);
        Intent intent = getIntent();
        resultStr = intent.getStringExtra(NaviDrawerActivity.BARCODE_SCAN_RESULT);
        resultTextView.setText(resultStr);
        if (savedInstanceState != null) {
            String barcodeScanResult = savedInstanceState.getString(BARCODE_SCAN_RESULT_SAVED_TAG);
            resultTextView.setText(barcodeScanResult);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(BARCODE_SCAN_RESULT_SAVED_TAG, resultStr);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_barcode_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
