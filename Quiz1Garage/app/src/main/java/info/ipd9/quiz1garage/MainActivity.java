package info.ipd9.quiz1garage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";


    ListView lvCars;

    ArrayAdapter<Car> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_main);
        lvCars = (ListView) findViewById(R.id.lvCars);
        //
        adapter = new ArrayAdapter<Car>(this, android.R.layout.simple_list_item_1, Globals.carsList);
        lvCars.setAdapter(adapter);
        //





    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        try {
            Globals.loadData(this);
        } catch (IOException e) {
            Toast.makeText(this, "Error loading data", Toast.LENGTH_LONG).show();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
        try {
            Globals.saveData(this);
        } catch (IOException e) {
            Toast.makeText(this, "Error saving data", Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mi_add_car:
            {
                Log.d(TAG, "Menu item Add selected");
                Intent intent = new Intent(this, AddCarActivity.class);
                startActivity(intent);
            }
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
