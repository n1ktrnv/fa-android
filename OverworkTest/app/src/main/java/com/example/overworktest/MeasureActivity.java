package com.example.overworktest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MeasureActivity extends AppCompatActivity {

    private static final long TIME_LONG = 300000;
    private static final long TIME_SHORT = 60000;
    private static final long TICK = 1000;
    private static final long VIBRATION_TIME = 2000;

    private EditText lyingPulseEditText, standingPulseEditText;
    private TextView timerView;

    private CountDownTimer countDownTimer;
    private boolean timerIsRunning;
    private long time = TIME_LONG;
    private long timeLeft = time;

    Vibrator vibrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        timerView = findViewById(R.id.timerView);
        lyingPulseEditText = findViewById(R.id.lyingPulseEdit);
        standingPulseEditText = findViewById(R.id.standingPulseEdit);
        updateTimerView();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void onTimerClick(View view) {
        if (timerIsRunning) {
            resetTimer();
        } else {
            startTimer();
        }
    }

    public void onSetTimerButtonClick(View view) {
        switch (view.getId()) {
            case R.id.longPeriodTimerButton:
                time = TIME_LONG;
                break;
            case R.id.shortPeriodTimerButton:
                time = TIME_SHORT;
                break;
        }
        resetTimer();
    }

    public void onNextButtonClick(View view) {
        if (lyingPulseEditText.getText().toString().equals("")) {
            lyingPulseEditText.requestFocus();
        } else if (standingPulseEditText.getText().toString().equals("")) {
            standingPulseEditText.requestFocus();
        } else {
            resetTimer();
            Intent launchIntent = getIntent();
            Intent intent = new Intent(this, ResultsActivity.class);
            intent.putExtra(Intent.EXTRA_INTENT, launchIntent);
            intent.putExtra("lying", lyingPulseEditText.getText().toString());
            intent.putExtra("standing", standingPulseEditText.getText().toString());
            startActivity(intent);
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeft, TICK) {
            @Override
            public void onTick(long timeUntilFinish) {
                timeLeft = timeUntilFinish;
                updateTimerView();
            }

            @Override
            public void onFinish() {
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(VIBRATION_TIME);
                }
            }
        }.start();
        timerIsRunning = true;
    }

    private void resetTimer() {
        if (timerIsRunning) {
            countDownTimer.cancel();
        }
        timeLeft = time;
        timerIsRunning = false;
        updateTimerView();
    }

    private void updateTimerView() {
        int minutes = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;
        String time = String.format("%02d:%02d", minutes, seconds);
        timerView.setText(time);
    }
}