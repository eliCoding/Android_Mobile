package info.ipd9.tododb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class EditAddActivity extends AppCompatActivity {

    private final static String TAG = "EditAddActivity";

    // bindings
    EditText etTask;
    DatePicker dpDueDate;
    Switch swIsDone;
    Button btSaveAdd;

    private Database database;
    private TodoItem currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_add);
        // bindings
        etTask = (EditText) findViewById(R.id.etTask);
        dpDueDate = (DatePicker) findViewById(R.id.dpDueDate);
        swIsDone = (Switch) findViewById(R.id.swIsDone);
        btSaveAdd = (Button) findViewById(R.id.btSaveAdd);
    }

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
                btSaveAdd.setText(saveString);
                // load the data of edited item
                currentItem = database.getTodoItemById(this, currentId);
                etTask.setText(currentItem.task);
                //
                Calendar cal = Calendar.getInstance();
                cal.setTime(currentItem.dueDate);

                dpDueDate.updateDate(cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                swIsDone.setChecked(currentItem.isDone);
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
        // extract data from UI, put in Friend instance
        TodoItem item = (currentItem == null) ? new TodoItem(this) : currentItem;
        item.task = etTask.getText().toString();
        int year = dpDueDate.getYear() - 1900;
        int month = dpDueDate.getMonth();
        int dom = dpDueDate.getDayOfMonth();
        item.dueDate = new Date(year, month, dom);
        item.isDone = swIsDone.isChecked();
        // add or save data
        try {
            if (currentItem == null) {
                database.addTodoItem(item);
                // show toast
                String friendAddedString = getResources().getString(R.string.todo_added);
                Toast.makeText(this, friendAddedString, Toast.LENGTH_SHORT).show();
            } else {
                database.updateTodoItem(item);
                // show toast
                String friendUpdatedString = getResources().getString(R.string.todo_updated);
                Toast.makeText(this, friendUpdatedString, Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException e) {
            String errorString = getResources().getString(R.string.error_saving_data);
            Toast.makeText(this, errorString, Toast.LENGTH_LONG).show();
        }

        // finish(), do NOT use Intent to go back to MainActivity
        finish();
    }

}
