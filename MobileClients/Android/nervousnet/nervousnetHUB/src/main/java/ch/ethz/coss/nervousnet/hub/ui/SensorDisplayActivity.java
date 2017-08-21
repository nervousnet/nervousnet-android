/*******************************************************************************
 * *     Nervousnet - a distributed middleware software for social sensing.
 * *      It is responsible for collecting and managing data in a fully de-centralised fashion
 * *
 * *     Copyright (C) 2016 ETH Zürich, COSS
 * *
 * *     This file is part of Nervousnet Framework
 * *
 * *     Nervousnet is free software: you can redistribute it and/or modify
 * *     it under the terms of the GNU General Public License as published by
 * *     the Free Software Foundation, either version 3 of the License, or
 * *     (at your option) any later version.
 * *
 * *     Nervousnet is distributed in the hope that it will be useful,
 * *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 * *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * *     GNU General Public License for more details.
 * *
 * *     You should have received a copy of the GNU General Public License
 * *     along with NervousNet. If not, see <http://www.gnu.org/licenses/>.
 * *
 * *
 * * 	Contributors:
 * * 	Prasad Pulikal - prasad.pulikal@gess.ethz.ch  -  Initial API and implementation
 *******************************************************************************/
package ch.ethz.coss.nervousnet.hub.ui;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Field;
import java.util.ArrayList;

import ch.ethz.coss.nervousnet.hub.Application;
import ch.ethz.coss.nervousnet.hub.Constants;
import ch.ethz.coss.nervousnet.hub.R;
import ch.ethz.coss.nervousnet.hub.ui.fragments.AccelFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.BaseFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.BatteryFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.DummyFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.GyroFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.LightFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.LocationFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.NoiseFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.ProximityFragment;
import ch.ethz.coss.nervousnet.lib.InfoReading;
import ch.ethz.coss.nervousnet.lib.LibConstants;
import ch.ethz.coss.nervousnet.lib.NervousnetServiceConnectionListener;
import ch.ethz.coss.nervousnet.lib.NervousnetServiceController;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.lib.Utils;
import ch.ethz.coss.nervousnet.vm.NNLog;
import ch.ethz.coss.nervousnet.vm.NervousnetVMConstants;
import ch.ethz.coss.nervousnet.vm.events.NNEvent;

public class SensorDisplayActivity extends FragmentActivity implements ActionBarImplementation, NervousnetServiceConnectionListener {
    private static BaseFragment fragment;
    int m_interval = 300; // 100 milliseconds by default, can be changed later
    Handler m_handler = new Handler();
    Runnable m_statusChecker;
    NervousnetServiceController nervousnetServiceController;
    private Boolean bindFlag;
    private SensorDisplayPagerAdapter sapAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateActionBar();
        setContentView(R.layout.activity_sensor_display);


        sapAdapter = new SensorDisplayPagerAdapter(getApplicationContext(), getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(sapAdapter);
//        if (savedInstanceState == null) {
        initServiceConnection();
//        }
    }

    private void initServiceConnection() {
        nervousnetServiceController = new NervousnetServiceController(SensorDisplayActivity.this, this);
        nervousnetServiceController.connect();
    }

    @Subscribe
    public void onNNEvent(NNEvent event) {
        NNLog.d("SensorDisplayActivityon", "onNNEvent called ");

        if (event.eventType == NervousnetVMConstants.EVENT_SENSOR_STATE_UPDATED || event.eventType == NervousnetVMConstants.EVENT_NERVOUSNET_STATE_UPDATED) {
//            getSupportFragmentManager().beginTransaction().detach(sapAdapter.getItem(viewPager.getCurrentItem())).commit();
            finish();
            startActivity(getIntent());
            NNLog.d("SensorDisplayActivityon", "onNNEvent 2 called ");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
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


    protected void updateStatus(SensorReading reading, int index) {

        BaseFragment fragment = (BaseFragment) sapAdapter.getFragment(index);
        NNLog.d("SensorDisplayActivity", "Inside updateStatus, index =  " + index);

        if (reading != null) {
            if (reading instanceof InfoReading) {
                fragment.handleError((InfoReading) reading);
            } else {
                fragment.updateReadings(reading);
            }

        }

    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        nervousnetServiceController.connect();
    }

    @Override
    public void onStop() {
        nervousnetServiceController.disconnect();
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        nervousnetServiceController.disconnect();

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        nervousnetServiceController.connect();

    }

    @Override
    public void onBackPressed() {

        nervousnetServiceController.disconnect();
        stopRepeatingTask();
        finish();
    }

    void startRepeatingTask() {

        m_statusChecker = new Runnable() {
            @Override
            public void run() {
                boolean errorFlag;
                NNLog.d("SensorDisplayActivity", "before updating");
                update(); // this function can change value of m_interval.
                m_handler.postDelayed(m_statusChecker, m_interval);
            }
        };

        m_statusChecker.run();
    }

    void stopRepeatingTask() {
        m_handler.removeCallbacks(m_statusChecker);
        m_statusChecker = null;

    }

    protected void update() {
        try {
            int index = viewPager.getCurrentItem();
            NNLog.d("SensorDisplayActivity", "Inside update : index  = " + index);
            boolean errorFlag;
            switch (index) {
                case 0:
                    updateStatus(nervousnetServiceController.getLatestReading(LibConstants.SENSOR_ACCELEROMETER), index);
                    break;
                case 1:
                    updateStatus(nervousnetServiceController.getLatestReading(LibConstants.SENSOR_BATTERY), index);
                    break;
                case 2:
                    updateStatus(nervousnetServiceController.getLatestReading(LibConstants.SENSOR_GYROSCOPE), index);
                    break;
                case 3:
                    updateStatus(nervousnetServiceController.getLatestReading(LibConstants.SENSOR_LOCATION), index);
                    break;
                case 4:
                    updateStatus(nervousnetServiceController.getLatestReading(LibConstants.SENSOR_LIGHT), index);
                    break;
                case 5:
                    updateStatus(nervousnetServiceController.getLatestReading(LibConstants.SENSOR_NOISE), index);
                    break;
                case 6:
                    updateStatus(nervousnetServiceController.getLatestReading(LibConstants.SENSOR_PROXIMITY), index);
                    break;

                default:
                    break;
            }

            viewPager.getAdapter().notifyDataSetChanged();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceConnected() {

        startRepeatingTask();

    }

    @Override
    public void onServiceDisconnected() {
        fragment.handleError(Utils.getInfoReading(101));

        stopRepeatingTask();
    }

    @Override
    public void onServiceConnectionFailed(InfoReading infoReading) {

    }

    public void showInfo(View view) {
        String title = "Sensor Frequency:";

        // Includes the updates as well so users know what changed.
        String message = "\n\n- Settings to control the frequency of Sensors." +
                "\nClick on the options to switch off or change the frequency." +
                "\n- Various levels of frequency can be selected" +
                "\n          - HIGH, MEDIUM, LOW or OFF" +
                "\n Please note if the Nervousnet Service is Paused, this control is disabled.";


        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new Dialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();

                    }
                });
        builder.setCancelable(false);

        AlertDialog alert = builder.create();
        alert.show();

        alert.getWindow().getAttributes();

        TextView textView = (TextView) alert.findViewById(android.R.id.message);
        textView.setTextSize(12);
    }

    public static class SensorDisplayPagerAdapter extends FragmentStatePagerAdapter {
        Context context;

        public SensorDisplayPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int i) {

            switch (i) {
                case 0:
                    fragment = new AccelFragment();
                    break;
                case 1:
                    fragment = new BatteryFragment();
                    break;
                case 2:
                    fragment = new GyroFragment();
                    break;
                case 3:
                    fragment = new LocationFragment();
                    break;
                case 4:
                    fragment = new LightFragment();
                    break;
                case 5:
                    fragment = new NoiseFragment();
                    break;
                case 6:
                    fragment = new ProximityFragment();
                    break;
                default:
                    fragment = new DummyFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return NervousnetVMConstants.sensor_labels.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Drawable drawable;
            ImageSpan span;
            SpannableStringBuilder sb;
            sb = new SpannableStringBuilder("  " + NervousnetVMConstants.sensor_labels[position]);

            drawable = context.getResources().getDrawable(Constants.icon_array_sensors[position]);
            drawable.setBounds(0, 0, 40, 40);
            span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // sb.setSpan(new ClickableSpan() {
            // @Override
            // public void onClick(View widget) {
            // Toast.makeText(context, "Clicked Span",
            // Toast.LENGTH_LONG).show();
            // }
            // }, 0, sb.length(),
            // Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        }

        @SuppressWarnings("unchecked")
        public Fragment getFragment(int position) {
            try {
                Field f = FragmentStatePagerAdapter.class.getDeclaredField("mFragments");
                f.setAccessible(true);
                ArrayList<Fragment> fragments = (ArrayList<Fragment>) f.get(this);
                if (fragments.size() > position) {
                    return fragments.get(position);
                }
                return null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


}
