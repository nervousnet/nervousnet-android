package ch.ethz.coss.nervousnet.hub.ui;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ch.ethz.coss.nervousnet.hub.Application;
import ch.ethz.coss.nervousnet.hub.R;
import ch.ethz.coss.nervousnet.lib.InfoReading;
import ch.ethz.coss.nervousnet.lib.LibConstants;
import ch.ethz.coss.nervousnet.lib.NervousnetServiceConnectionListener;
import ch.ethz.coss.nervousnet.lib.NervousnetServiceController;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.NNLog;
import ch.ethz.coss.nervousnet.vm.NervousnetVMConstants;
import ch.ethz.coss.nervousnet.vm.events.NNEvent;

import static ch.ethz.coss.nervousnet.lib.LibConstants.SENSOR_GYROSCOPE;

public class SensorStatisticsViewActivity extends BaseActivity implements NervousnetServiceConnectionListener{

    NervousnetServiceController nervousnetServiceController;
    Handler m_handler = new Handler();
    Runnable m_statusChecker;
    WebView webView;
    private int sensorId;
    private int m_interval = 300; // 100 milliseconds by default, can be changed later

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_statistics_view);

        sensorId = getIntent().getIntExtra("sensorId", -1);

        ((Application) getApplication()).startService(this);
        initServiceConnection();
        EventBus.getDefault().post(new NNEvent(NervousnetVMConstants.EVENT_START_NERVOUSNET_REQUEST));

        initSensorview(sensorId);
    }

    private void initServiceConnection() {
        nervousnetServiceController = new NervousnetServiceController(SensorStatisticsViewActivity.this, this);
        nervousnetServiceController.connect();
    }

    private void initSensorview(int sensorId) {
        String urlPath = "file:///android_asset/Highcharts/webview_charts_3_lines_live_data_over_time.html";
        String javascript_global_variables = "";

        webView = (WebView)findViewById(R.id.highchartsWebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(urlPath);

        switch (sensorId) {
            case 0:
                javascript_global_variables =
                        "var unit_of_meas = " + "'m/s^2';" +
                                "var first_curve_name = " + "'x';" +
                                "var second_curve_name = " + "'y';" +
                                "var third_curve_name = " + "'z';" +
                                "var x_axis_title = " + "'Date';" +
                                "var y_axis_title = " + "'acceleration';" +
                                "var plot_title = " + "'acceleration';" +
                                "var plot_subtitle = " + "'current acceleration';";
                break;
            case 2:
                javascript_global_variables =
                        "var unit_of_meas = " + "'rads';" +
                                "var first_curve_name = " + "'x';" +
                                "var second_curve_name = " + "'y';" +
                                "var third_curve_name = " + "'z';" +
                                "var x_axis_title = " + "'Date';" +
                                "var y_axis_title = " + "'rads';" +
                                "var plot_title = " + "'rotation';" +
                                "var plot_subtitle = " + "'current rotation';";
                break;
            case 3:
                javascript_global_variables =
                        "var unit_of_meas = " + "'latlongms';" +
                                "var first_curve_name = " + "'lat';" +
                                "var second_curve_name = " + "'long';" +
                                "var third_curve_name = " + "'speed';" +
                                "var x_axis_title = " + "'Date';" +
                                "var y_axis_title = " + "'latlongms';" +
                                "var plot_title = " + "'lat,long,m/s';" +
                                "var plot_subtitle = " + "'current location and speed';";
                break;
        }

        webView.evaluateJavascript(javascript_global_variables, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {

            }
        });
        updateGraph();
    }

    private void updateGraph() {
        try {
            Log.d("HODORORORORO", "DOROOOsdfsdsdsdfsfdfsdfsdfDOORODRORODOORODORODROODRODR");

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);

            SensorReading lastLoc = nervousnetServiceController.getLatestReading(LibConstants.SENSOR_ACCELEROMETER);

            switch (sensorId) {
                case 0:
                    lastLoc = nervousnetServiceController.getLatestReading(LibConstants.SENSOR_ACCELEROMETER);
                    break;
                case 2:
                    lastLoc = nervousnetServiceController.getLatestReading(LibConstants.SENSOR_GYROSCOPE);
                    break;
                case 3:
                    lastLoc = nervousnetServiceController.getLatestReading(6);
                    break;
            }

            ArrayList values = lastLoc.getValues();

            String point = "point0 = [Date.UTC(" + year + "," + month + "," + day + "," + hour + "," + minute + "," + second + ")," + values.get(0) + "];"
                + "point1 = [Date.UTC(" + year + "," + month + "," + day + "," + hour + "," + minute + "," + second + ")," + values.get(1) + "];"
                + "point2 = [Date.UTC(" + year + "," + month + "," + day + "," + hour + "," + minute + "," + second + ")," + 0 + "];";

            webView.evaluateJavascript(point, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                }
            });

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void startStopSensorService(boolean on) {
        if (on) {
            ((Application) getApplication()).startService(this);
            initServiceConnection();
            EventBus.getDefault().post(new NNEvent(NervousnetVMConstants.EVENT_START_NERVOUSNET_REQUEST));
        } else {
            nervousnetServiceController.disconnect();
            ((Application) getApplication()).stopService(this);
//            stopRepeatingTask();
        }

    }

    @Override
    public void updateActionBar() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.ab_nn, null);
        ActionBar actionBar;
        Switch mainSwitch;

        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(v);
        mainSwitch = (Switch) findViewById(R.id.mainSwitch);

        byte state = ((Application) getApplication()).getState();
        NNLog.d("SensorDisplayActivity", "state = " + state);
        mainSwitch.setChecked(state == 0 ? false : true);

        mainSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                startStopSensorService(isChecked);
            }
        });
    }

    void startRepeatingTask() {

        m_statusChecker = new Runnable() {
            @Override
            public void run() {
                boolean errorFlag;
                NNLog.d("SensorStatisticsViewActivity", "before updating");
                updateGraph();
                m_handler.postDelayed(m_statusChecker, m_interval);
            }
        };

        m_statusChecker.run();
    }

    void stopRepeatingTask() {
        m_handler.removeCallbacks(m_statusChecker);
        m_statusChecker = null;
    }

    @Override
    public void onServiceConnected() {
        startRepeatingTask();
    }

    @Override
    public void onServiceDisconnected() {
        stopRepeatingTask();
    }

    @Override
    public void onServiceConnectionFailed(InfoReading infoReading) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        nervousnetServiceController.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        nervousnetServiceController.disconnect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        nervousnetServiceController.connect();
    }

    @Override
    protected void onStop() {
        nervousnetServiceController.disconnect();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopRepeatingTask();
        finish();
    }

}
