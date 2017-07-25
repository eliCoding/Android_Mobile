package info.ipd9.todoapi;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    private final static String TAG = "MainActivity";
    public static final String EXTRA_ID = "id";

    ListView lvTodos;
    ArrayAdapter<TodoItem> adapter;
    public final ArrayList<TodoItem> todoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // bindings
        lvTodos = (ListView) findViewById(R.id.lvTodos);
        //
        adapter = new ArrayAdapter<TodoItem>(this, android.R.layout.simple_list_item_1, todoList);
        lvTodos.setAdapter(adapter);
        //
        lvTodos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                Log.d(TAG, "long clicked pos: " + pos);
                showDeleteDialog(pos);
                return true;
            }
        });
        //
        lvTodos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                Log.d(TAG, "clicked pos: " + pos);
                // put clicked item's position in Extra of Intent starting AddEditActivity
                Intent intent = new Intent(MainActivity.this, EditAddActivity.class);
                // pass id of the record, NOT position in the list
                intent.putExtra(EXTRA_ID, todoList.get(pos).id);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        new GetAllTodosTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mi_add_todo: {
                Log.d(TAG, "Menu item Add selected");
                Intent intent = new Intent(this, EditAddActivity.class);
                startActivity(intent);
            }
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDeleteDialog(final int pos) {
        // FIXME: all strings should come from strings.xml !!!
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete todo");
        // setup text input
        final TextView tvNote = new TextView(this);
        tvNote.setText(todoList.get(pos).toString());
        // center TextView and give it more space on top
        tvNote.setPadding(0, 25, 0, 0);
        tvNote.setGravity(Gravity.CENTER);
        // input type - regular text
        builder.setView(tvNote);
        // setup buttons
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Delete note: " + todoList.get(pos));
                new DeleteTodoTask().execute(todoList.get(pos).id);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    class DeleteTodoTask extends AsyncTask<Integer, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Integer... params) {
            int id = params[0];
            HttpURLConnection connection = null;
            try {
                Log.v(TAG, "DELETE /todos/" + id);
                URL url = new URL(Globals.BASE_API_URL + "/todos/" + id);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");
                int httpCode = connection.getResponseCode();
                if (httpCode != 200) {
                    throw new IOException("Invalid HTTP code " + httpCode);
                }
                return true;
            } catch (IOException e) {
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
                Toast.makeText(MainActivity.this, "API error deleting item", Toast.LENGTH_LONG).show();
                return;
            }
            new GetAllTodosTask().execute();
        }

    }

    class GetAllTodosTask extends AsyncTask<Void, Void, ArrayList<TodoItem>> {

        private final static String TAG = "GetAllTodosTask";

        @Override
        protected ArrayList<TodoItem> doInBackground(Void... params) {
            InputStreamReader input = null;
            JsonReader reader = null;
            try {
                Log.v(TAG, "GET /todos");
                ArrayList<TodoItem> result = new ArrayList<>();
                URL url = new URL(Globals.BASE_API_URL + "/todos");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // connection.setRequestMethod();
                input = new InputStreamReader(connection.getInputStream());
                int httpCode = connection.getResponseCode();
                if (httpCode != 200) {
                    throw new IOException("Invalid HTTP code " + httpCode);
                }
                //
                reader = new JsonReader(input);
                reader.beginArray();
                while (reader.hasNext()) {
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
                    result.add(item);
                    reader.endObject();
                }
                reader.endArray();
                return result;
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
        protected void onPostExecute(ArrayList<TodoItem> result) {
            if (result == null) {
                Toast.makeText(MainActivity.this, "API error fetching list", Toast.LENGTH_LONG).show();
                return;
            }
            todoList.clear();
            todoList.addAll(result);
            adapter.notifyDataSetChanged();
        }

    }


}
