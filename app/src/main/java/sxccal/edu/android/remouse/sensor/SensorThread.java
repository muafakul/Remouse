package sxccal.edu.android.remouse.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import sxccal.edu.android.remouse.SensorFragment;
import sxccal.edu.android.remouse.io.DumpData;

public class SensorThread implements Runnable {

    private SensorEvent mSensorEvent;
    private Sensor mSensor;

    private static final String DIR= Environment.getExternalStorageDirectory().getAbsolutePath();

    public SensorThread(SensorEvent sensorEvent, Sensor sensor) {
        mSensorEvent = sensorEvent;
        mSensor = sensor;
    }
    @Override
    public void run() {
        boolean isLinear = false;
        if(mSensor.getName().contains("Linear")) {
            isLinear = true;
        }

        DumpData dumpData = new DumpData(DIR, isLinear);
        long startTime = System.nanoTime(), currentTime;

        do {
            currentTime = System.nanoTime() - startTime;
            dumpData.dumpToFile(currentTime, mSensorEvent.values);
        } while (currentTime/1e+9 < 30.);

        dumpData.closeFile();
        handler.sendEmptyMessage(0);
    }

    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            SensorFragment.sProgressDialog.dismiss();
        }
    };
}