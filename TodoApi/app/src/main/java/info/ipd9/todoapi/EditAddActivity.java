package info.ipd9.todoapi;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EditAddActivity extends AppCompatActivity {

    private final static String TAG = "EditAddActivity";

    private EditText etTask;
    private DatePicker dpDueDate;
    private Switch swIsDone;
    private Button btSaveAdd;

    private TodoItem currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_add);
        etTask = (EditText) findViewById(R.id.etTask);
        dpDueDate = (DatePicker) findViewById(R.id.dpDueDate);
        swIsDone = (Switch) findViewById(R.id.swIsDone);
        btSaveAdd = (Button) findViewById(R.id.btSaveAdd);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        Intent intent = getIntent();
        // if extra is absent then assume it was -1
        int currentId = intent.getIntExtra(MainActivity.EXTRA_ID, -1);
        if (currentId != -1) {
            // pull translation from strings.xml in current language of the device
            String saveString = getResources().getString(R.string.save);
            btSaveAdd.setText(saveString);
            new LoadTodoItemByIdTask().execute(currentId);
        }
    }

    public void onEditAddClick(View v) {
        // add or edit
        TodoItem item = (currentItem == null) ? new TodoItem() : currentItem;
        item.task = etTask.getText().toString();
        // convert DatePicker selection to Date object
        int year = dpDueDate.getYear() - 1900;
        int month = dpDueDate.getMonth();
        int dom = dpDueDate.getDayOfMonth();
        item.dueDate = new Date(year, month, dom);
        //
        item.isDone = swIsDone.isChecked();
        //
        if (currentItem == null) {
            new AddTodoItemTask().execute(item);
        } else {
            new UpdateTodoItemTask().execute(item);
        }
    }

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    class AddTodoItemTask extends AsyncTask<TodoItem, Void, Boolean> {

        private final static String TAG = "AddTodoItemTask";

        @Override
        protected Boolean doInBackground(TodoItem... params) {
            TodoItem item = params[0];
            HttpURLConnection connection = null;
            try {
                JSONObject jsonData = new JSONObject();
                jsonData.put("task", item.task);
                // format dueDate as YYYY-MM-DD
                jsonData.put("dueDate", dateFormat.format(item.dueDate));
                jsonData.put("isDone", item.isDone ? 1 : 0);
                //
                Log.v(TAG, "POST /todos DATA: " + item.toString());
                URL url = new URL(Globals.BASE_API_URL + "/todos");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                // send data, do not read data
                connection.setDoInput(false);
                connection.setDoOutput(true);
                // TODO: get output stream, encode and send the data
                // Send POST output.
                PrintWriter printWriter = new PrintWriter(connection.getOutputStream());
                printWriter.print(jsonData.toString());
                printWriter.flush();
                printWriter.close();
                //
                int httpCode = connection.getResponseCode();
                if (httpCode != 201) {
                    throw new IOException("Invalid HTTP code " + httpCode);
                }
                return true;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return false;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                Toast.makeText(EditAddActivity.this, "API error creating todo item", Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(EditAddActivity.this, "Success adding todo item", Toast.LENGTH_LONG).show();
            EditAddActivity.this.finish();
        }

    }

    class LoadTodoItemByIdTask extends AsyncTask<Integer, Void, TodoItem> {
        private final static String TAG = "GetAllTodosTask";

        @Override
        protected TodoItem doInBackground(Integer... params) {
            InputStreamReader input = null;
            JsonReader reader = null;
            int id = params[0];
            try {
                Log.v(TAG, "GET /todos/" + id);
                URL url = new URL(Globals.BASE_API_URL + "/todos/" + id);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // connection.setRequestMethod();
                input = new InputStreamReader(connection.getInputStream());
                int httpCode = connection.getResponseCode();
                if (httpCode != 200) {
                    throw new IOException("Invalid HTTP code " + httpCode);
                }
                //
                reader = new JsonReader(input);
                TodoItem item = new TodoItem();
                reader.beginObject();
                String key;
                // id
                key = reader.nextName();
                if (!key.equals("id")) throw new ParseException("id expected", 0);
                item.id = reader.nextInt();
                // task
                key = reader.nextName();
                if (!key.equals("task")) throw new ParseException("task expected", 0);
                item.task = reader.nextString();
                // dueDate
                key = reader.nextName();
                if (!key.equals("dueDate")) throw new ParseException("dueDate expected", 0);
                String dueDateString = reader.nextString();
                item.dueDate = new SimpleDateFormat("yyyy-MM-dd").parse(dueDateString);
                // isDone
                key = reader.nextName();
                if (!key.equals("isDone")) throw new ParseException("isDone expected", 0);
                item.isDone = (reader.nextInt() != 0);
                //
                reader.endObject();
                return item;
            } catch (IOException | ParseException e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    if (reader != null) reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(TodoItem result) {
            if (result == null) {
                Toast.makeText(EditAddActivity.this, "API error fetching todo item", Toast.LENGTH_LONG).show();
                return;
            }
            currentItem = result;
            etTask.setText(currentItem.task);
            //
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentItem.dueDate);
            dpDueDate.updateDate(cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            swIsDone.setChecked(currentItem.isDone);
        }


    }

    class UpdateTodoItemTask extends AsyncTask<TodoItem, Void, Boolean> {
        private final static String TAG = "AddTodoItemTask";

        @Override
        protected Boolean doInBackground(TodoItem... params) {
            TodoItem item = params[0];
            HttpURLConnection connection = null;
            try {
                JSONObject jsonData = new JSONObject();
                jsonData.put("task", item.task);
                // format dueDate as YYYY-MM-DD
                jsonData.put("dueDate", dateFormat.format(item.dueDate));
                jsonData.put("isDone", item.isDone ? 1 : 0);
                //
                Log.v(TAG, "PUT /todos DATA: " + item.toString());
                URL url = new URL(Globals.BASE_API_URL + "/todos/" + item.id);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                // send data, do not read data
                connection.setDoInput(false);
                connection.setDoOutput(true);
                // TODO: get output stream, encode and send the data
                // Send POST output.
                PrintWriter printWriter = new PrintWriter(connection.getOutputStream());
                printWriter.print(jsonData.toString());
                printWriter.flush();
                printWriter.close();
                //
                int httpCode = connection.getResponseCode();
                if (httpCode != 200) {
                    throw new IOException("Invalid HTTP code " + httpCode);
                }
                return true;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return false;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                Toast.makeText(EditAddActivity.this, "API error updating todo item", Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(EditAddActivity.this, "Success saving todo item", Toast.LENGTH_LONG).show();
            EditAddActivity.this.finish();
        }

    }

}
