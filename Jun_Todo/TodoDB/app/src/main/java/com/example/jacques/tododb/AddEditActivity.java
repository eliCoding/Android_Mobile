package com.example.jacques.tododb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class AddEditActivity extends LivecycleTrackingActivity {

    private static final String TAG = "AddEditActivity";

    private EditText etAction;
    private Switch swIsDone;
    private DatePicker dpDueDate;

    private TodoItem currentTodoItem;

    Button btAddSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        //
        etAction = (EditText) findViewById(R.id.etAction);
        dpDueDate = (DatePicker) findViewById(R.id.dpDueDate);
        swIsDone = (Switch) findViewById(R.id.swIsDone);

        // Example on how to add onClick handler programmatically
        btAddSave = (Button) findViewById(R.id.btAddSave);
        btAddSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddSaveClick(v);
            }
        });
    }

    private Database database;

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        database = Database.openDatabase(this);
        // load data here, not in onCreate
        // Check if Extra with index was provided, if yes, load data from ArrayList[index] into UI
        Intent intent = getIntent();
        // if extra is absent then assume it was -1
        int currentId = intent.getIntExtra(MainActivity.EXTRA_ID, -1);

        if (currentId != -1) {
            try {
                // pull translation from strings.xml in current language of the device
                String saveString = getResources().getString(R.string.save);
                btAddSave.setText(saveString);
                // load the data of edited item
                currentTodoItem = database.getTodoItemById(this, currentId);
                etAction.setText(currentTodoItem.action);
                Calendar cal = Calendar.getInstance();
                cal.setTime(currentTodoItem.dueDate);
                dpDueDate.updateDate(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
                swIsDone.setChecked(Boolean.valueOf(currentTodoItem.isDone));

            } catch (SQLException e) {
                e.printStackTrace();
                String errorString = getResources().getString(R.string.error_loading_data);
                Toast.makeText(this, errorString, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
        Database.closeDatabase();
    }

    public void onAddSaveClick(View v) {
        Log.d(TAG, "onAddSaveClick()");
        // extract data from UI, put in TodoItem instance
        TodoItem TodoItem = (currentTodoItem == null) ? new TodoItem(this) : currentTodoItem;
        TodoItem.action = etAction.getText().toString();
        TodoItem.dueDate = getDateFromDatePicker(dpDueDate);
        TodoItem.isDone = swIsDone.isChecked();
        // add or save data
        try {
            if (currentTodoItem == null) {
                database.addTodoItem(TodoItem);
                // show toast
                String TodoItemAddedString = getResources().getString(R.string.todoItem_added);
                Toast.makeText(this, TodoItemAddedString, Toast.LENGTH_SHORT).show();
            } else {
                database.updateTodoItem(TodoItem);
                // show toast
                String TodoItemUpdatedString = getResources().getString(R.string.todoItem_updated);
                Toast.makeText(this, TodoItemUpdatedString, Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException e) {
            String errorString = getResources().getString(R.string.error_saving_data);
            Toast.makeText(this, errorString, Toast.LENGTH_LONG).show();
        }

        // finish(), do NOT use Intent to go back to MainActivity
        finish();
    }

    public static Date getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return new java.sql.Date(calendar.getTimeInMillis());
    }

}