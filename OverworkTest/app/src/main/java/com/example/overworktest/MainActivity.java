package com.example.overworktest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    private static final int MIN_YEAR = 1930;

    private Spinner birthdateDaySpinner, birthdateMonthSpinner, birthdateYearSpinner, genderSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        birthdateMonthSpinner = findViewById(R.id.birthdateMonthSpinner);
        birthdateYearSpinner = findViewById(R.id.birthdateYearSpinner);
        setYearsToBirthdateYearSpinner();
        birthdateDaySpinner = findViewById(R.id.birthdateDaySpinner);
        setDaysToBirthdateDaySpinner();
        genderSpinner = findViewById(R.id.genderSpinner);

        birthdateMonthSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        setDaysToBirthdateDaySpinner();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                }
        );

        birthdateYearSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        setDaysToBirthdateDaySpinner();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                }
        );
    }

    public void onNextButtonClick(View view) {
        Intent intent = new Intent(this, MeasureActivity.class);
        startActivity(intent);
    }

    private void setYearsToBirthdateYearSpinner() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        ArrayList<String> items = new ArrayList<>();
        for (int year = currentYear; year >= MIN_YEAR; year--) {
            items.add(String.valueOf(year));
        }
        setListItemsToSpinner(items, birthdateYearSpinner);
    }

    private void setDaysToBirthdateDaySpinner() {
        int year = Integer.parseInt(birthdateYearSpinner.getSelectedItem().toString());
        int month = birthdateMonthSpinner.getSelectedItemPosition();
        int daysInMonth = getDaysInMonth(year, month);

        int currentDay = birthdateDaySpinner.getSelectedItemPosition();

        ArrayList<String> items = new ArrayList<>();
        for (int dayNumber = 1; dayNumber <= daysInMonth; dayNumber++) {
            items.add(String.valueOf(dayNumber));
        }
        setListItemsToSpinner(items, birthdateDaySpinner);

        if (currentDay <= daysInMonth) {
            birthdateDaySpinner.setSelection(currentDay);
        }
    }

    private int getDaysInMonth(int year, int month) {
        final int day = 1;
        Calendar calendar = new GregorianCalendar(year, month, day);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private void setListItemsToSpinner(ArrayList<String> items, Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, items);
        spinner.setAdapter(adapter);
    }
}