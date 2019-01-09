package com.example.akshaybharadwaj.wearos;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wear.widget.BoxInsetLayout;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.Timer;
import java.lang.Math;
import java.time.Duration;
import java.util.TimerTask;

public class MainActivity extends WearableActivity {

    private TextView mTextView;
    private TextView mTextViewElapsedTime;
    private TextView mHRTextView;
    private TextView mHRDispTextView, mSPtsDispTextView, mCalDispTextView;
    private TextView mHRPercentTextView;
    private ConstraintLayout mMainBoxLayout;
    private int time_in_sec=0;
    public final int REQUEST_CODE_ASK_PERMISSIONS = 1001;
    Timer timer = new Timer("elapsed-time",true);
    int HR =0;
    int maxBPM = 192;



    boolean status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if app has sensor permission and request for Permission,
        // if permission is not granted
        if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.BODY_SENSORS)
            != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.BODY_SENSORS},REQUEST_CODE_ASK_PERMISSIONS);

        }

        mTextView = (TextView) findViewById(R.id.text);
        mTextViewElapsedTime = (TextView) findViewById(R.id.timeelapsedTE);
        mHRTextView = (TextView) findViewById(R.id.HR_count);
        mHRDispTextView = (TextView) findViewById(R.id.textView);
        mHRDispTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_rate,0,0,0);
        mSPtsDispTextView = (TextView) findViewById(R.id.textView2);
        mSPtsDispTextView = (TextView) findViewById(R.id.textView2);
        mSPtsDispTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.run_pt_edit,0,0,0);
        mCalDispTextView = (TextView) findViewById(R.id.textView3);
        mCalDispTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.calorie_img,0,0,0);
        mHRPercentTextView = (TextView) findViewById(R.id.textView6);
        mMainBoxLayout = (ConstraintLayout) findViewById(R.id.boxInsetLayout);



        // Enables Always-on
        setAmbientEnabled();
    }

    public void OnclickBtn(View StartBtn)
    {
        Button button = (Button) StartBtn;
        System.out.print("Start Button is pressed");
        if (status)
        {
            ((Button) StartBtn).setText("Start");
            timer.cancel();
            //StopReadingHeartRate();
            time_in_sec =0;
            status = false;
        }
        else
        {
            ((Button) StartBtn).setText("Stop");
            status = true;

            //Define a timer task and schedule it
            Scheduler();
            // Read Heart Rate
            ReadHeartRate();


        }
    }

    public void Scheduler()
    {
        final Handler UI_handler = new Handler();
        TimerTask task = new TimerTask()
        {
            @Override
            public void run() {
                UI_handler.post(new Runnable() {
                    @Override
                    public void run()
                    {
                        UpdateUI();
                    }
                });
            }
        };
        timer.schedule(task,1000,1000);
    }

    // Read Heart Rate (bpm)
    public void ReadHeartRate()
    {
        SensorManager mSensorManger = ((SensorManager)getSystemService(SENSOR_SERVICE));
        Sensor mHRSensor = mSensorManger.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorManger.registerListener(sensorlistner,mHRSensor,SensorManager.SENSOR_DELAY_FASTEST);
    }

  /*  public void StopReadingHeartRate()
    {
        mSensorManger.unregisterListener(sensorlistner);
    }
*/

    public SensorEventListener sensorlistner = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // CHeck if the sensor even is heart Rate
            if(event.sensor.getType() == Sensor.TYPE_HEART_RATE)
            {
                HR = Math.round(event.values[0]);
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public static String formatSeconds(int timeInSeconds)
    {
        Duration duration = Duration.ofSeconds(timeInSeconds);
        long hour = duration.toHours();
        long min = duration.toMinutes();
        long sec = duration.getSeconds() % 60;
        return hour+":"+min+":"+sec;
    }


    public void UpdateUI()
    {
        time_in_sec+=1;
        mTextViewElapsedTime.setText(formatSeconds(time_in_sec));
        mHRTextView.setText(Integer.toString(HR));
        double HRPercent = Math.floor(CalculateHRPercent(HR));
        mHRPercentTextView.setText(HRPercent+"%");
        // Set Background
        if(HRPercent <= 60)
            mMainBoxLayout.setBackgroundColor(getResources().getColor(R.color.grey));
        else if (HRPercent>60 && HRPercent<=70)
            mMainBoxLayout.setBackgroundColor(getResources().getColor(R.color.blue));
        else if(HRPercent>70 && HRPercent<=83)
            mMainBoxLayout.setBackgroundColor(getResources().getColor(R.color.green));
        else if(HRPercent>84 && HRPercent<=91)
            mMainBoxLayout.setBackgroundColor(getResources().getColor(R.color.orange));
        else
            mMainBoxLayout.setBackgroundColor(getResources().getColor(R.color.red));

    }

    public double CalculateHRPercent(int HR)
    {
        return((HR/maxBPM)*100);
    }
}
