package com.example.simpleplanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> myDayToDoList;
    ArrayList<String> importantsToDoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.myDayButton:
                intent = new Intent(this, MyDayActivity.class);

                break;
            case R.id.importantsButton:
                intent = new Intent(this, ImportantsActivity.class);
                break;
        }
        if (myDayToDoList != null) {
            intent.putStringArrayListExtra("my_day", myDayToDoList);
        }
        if (importantsToDoList != null) {
            intent.putStringArrayListExtra("importants", importantsToDoList);
        }
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle extras = data.getExtras();
        if (extras != null) {
            myDayToDoList = data.getExtras().getStringArrayList("my_day");
            importantsToDoList = data.getExtras().getStringArrayList("importants");
        }
    }
}