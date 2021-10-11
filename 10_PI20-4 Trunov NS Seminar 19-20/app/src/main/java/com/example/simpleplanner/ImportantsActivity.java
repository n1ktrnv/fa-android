package com.example.simpleplanner;

import android.os.Bundle;

public class ImportantsActivity extends ToDoList {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layout = R.layout.activity_importants;
        listViewId = R.id.importantsListView;
        editTextId = R.id.importantsEditText;
        key = "importants";

        super.onCreate(savedInstanceState);
    }
}