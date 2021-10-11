package com.example.simpleplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ToDoList extends AppCompatActivity {
    int layout = R.layout.activity_my_day;
    int listViewId = R.id.myDayListView;
    int editTextId = R.id.myDayEditText;
    String key = "my_day";


    ListView toDoListView;
    ArrayAdapter toDoListAdapter;
    ArrayList<String> toDoList = new ArrayList<String>();
    EditText toDoEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout);

        toDoListView = findViewById(listViewId);
        toDoListAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                toDoList);
        toDoListView.setAdapter(toDoListAdapter);
        loadToDos();
        toDoEditText = findViewById(editTextId);

        setResult(RESULT_OK, getIntent());
    }

    public void onClick (View view) {
        String item = toDoEditText.getText().toString();
        toDoList.add(item);
        toDoListAdapter.notifyDataSetChanged();
        Intent intent = getIntent();
        intent.putStringArrayListExtra(key, toDoList);
    }

    private void loadToDos() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ArrayList<String> items = extras.getStringArrayList(key);
            if (items != null) {
                for (String item : items) {
                    toDoList.add(item);
                }
            }
        }
        toDoListAdapter.notifyDataSetChanged();
    }

}
