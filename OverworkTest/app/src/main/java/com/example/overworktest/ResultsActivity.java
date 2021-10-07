package com.example.overworktest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ResultsActivity extends AppCompatActivity {

    private static final int PROGRESS_BAR_MAX = 37;
    private static final int GOOD_RESULT_MAX = 12;
    private static final int MEDIUM_RESULT_MAX = 25;

    TextView resultsText;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        resultsText = findViewById(R.id.resultsText);
        progressBar = findViewById(R.id.progressBar);

        Intent intent = getIntent();
        int lyingPulse = Integer.parseInt(intent.getStringExtra("lying"));
        int standingPulse = Integer.parseInt(intent.getStringExtra("standing"));
        int differencePulse = standingPulse - lyingPulse;

        setTextResult(differencePulse);
        setProgressBarResult(differencePulse);
    }

    private void setTextResult (int differencePulse) {
        if (differencePulse <= GOOD_RESULT_MAX) {
            resultsText.setText(R.string.good_result_text);
        } else if (differencePulse <= MEDIUM_RESULT_MAX) {
            resultsText.setText(R.string.medium_result_text);
        } else {
            resultsText.setText(R.string.bad_result_text);
        }
    }

    private void setProgressBarResult(int differencePulse) {
        progressBar.setMax(PROGRESS_BAR_MAX);
        int progress = PROGRESS_BAR_MAX - differencePulse;
        progressBar.setProgress(progress);
    }
}