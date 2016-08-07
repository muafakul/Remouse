package sxccal.edu.android.remouse;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import sxccal.edu.android.remouse.sensor.SensorThread;


public class SensorFragment extends Fragment implements SensorEventListener, View.OnClickListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private Button mAcceleration;
    private Button mLinearAcceleration;
    private Button mGyroscopeButton;

    private static final int REQUEST_RW_PERMISSION = 1444;
    private static boolean alreadyClicked = false;

    public static ProgressDialog sProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_sensor, container, false);
        mAcceleration = (Button) view.findViewById(R.id.nacc_button);
        mLinearAcceleration = (Button) view.findViewById(R.id.lacc_button);
        mGyroscopeButton = (Button) view.findViewById(R.id.gyro_button);

        mAcceleration.setOnClickListener(this);
        mLinearAcceleration.setOnClickListener(this);
        mGyroscopeButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getRWPermission();
        }
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        try {
            if(view.getId() == R.id.nacc_button) {
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            } else if(view.getId() == R.id.lacc_button) {
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

            } else if(view.getId() == R.id.gyro_button) {
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

            }
            if(mSensor != null && !alreadyClicked) {
                mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
                alreadyClicked = true;
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private void unregisterSensor() {
        if(mSensorManager != null) {
            mSensorManager.unregisterListener(this);
            alreadyClicked = false;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        unregisterSensor();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterSensor();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        sProgressDialog = ProgressDialog.show(getContext(), "Sensor Data Dump",
                "Dumping in progress", false, false);

        Thread sensorThread = new Thread(new SensorThread(sensorEvent, mSensor));
        sensorThread.start();

        unregisterSensor();

        /*if(!sensorThread.isAlive()) {
            Toast.makeText(this.getContext(), "Data dumped", Toast.LENGTH_LONG).show();
        }*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RW_PERMISSION: {
                if (grantResults.length == 0 ||
                        grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this.getActivity(), "Permission denied",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void getRWPermission() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_RW_PERMISSION);
        }
    }
}
