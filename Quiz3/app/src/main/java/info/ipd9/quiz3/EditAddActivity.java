package info.ipd9.quiz3;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditAddActivity extends AppCompatActivity {

    private final static String TAG = "EditAddActivity";

    private EditText etName;
    private EditText etDestination;
    private DatePicker dpDeparture;
    private RadioGroup rgTrip;


    private Trip currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_add);

        etName = (EditText) findViewById(R.id.etName);
        etDestination = (EditText) findViewById(R.id.etDestination);
        dpDeparture = (DatePicker) findViewById(R.id.dpDeparture);
        rgTrip = (RadioGroup) findViewById(R.id.rgTrip);

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
          //  String saveString = getResources().getString(R.string.save);
          //  btSaveAdd.setText(saveString);
            new LoadTtipItemById().execute(currentId);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mi_save_trip: {
                Log.d(TAG, "Menu item Save selected");
                Trip trip = (currentItem == null) ? new Trip() : currentItem;
                trip.name = etName.getText().toString();
                trip.destination = etDestination.getText().toString();
                // convert DatePicker selection to Date object
                int year = dpDeparture.getYear() - 1900;
                int month = dpDeparture.getMonth();
                int dom = dpDeparture.getDayOfMonth();
                trip.departure = new Date(year, month, dom);
                //
                trip.travelBy = getSelectedTrip();
                if (currentItem == null) {
                    new AddTrip().execute(trip);
                } else {

                    new UpdateTrip().execute(trip);

                }

            }
            finish();
            return true;

            default:
                return super.onOptionsItemSelected(item);

        }


    }

    private Trip.TravelBy getSelectedTrip() {
        int id = rgTrip.getCheckedRadioButtonId();
        switch (id) {
            case R.id.rbTrain:
                return Trip.TravelBy.Train;
            case R.id.rbCar:
                return Trip.TravelBy.Car;
            case R.id.rbBus:
                return Trip.TravelBy.Bus;
            default:
                Log.wtf(TAG, "setSelectedTrip() unknown ID");
                return Trip.TravelBy.Car;
        }
    }

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    class AddTrip extends AsyncTask<Trip, Void, Boolean> {

        private final static String TAG = "AddTtips";

        @Override
        protected Boolean doInBackground(Trip... params) {
            Trip trip= params[0];
            HttpURLConnection connection = null;
            try {
                JSONObject jsonData = new JSONObject();
                jsonData.put("name", trip.name);
                jsonData.put("destination", trip.destination);
                // format dueDate as YYYY-MM-DD
                jsonData.put("departure", dateFormat.format(trip.departure));
                jsonData.put("travelBy", trip.travelBy);
                //
                Log.v(TAG, "POST /trips DATA: " + trip.toString());
                URL url = new URL(Globals.BASE_API_URL + "/trips");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                // send data, do not read data
                connection.setDoInput(false);
                connection.setDoOutput(true);

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
                Toast.makeText(EditAddActivity.this, "API error creating trips", Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(EditAddActivity.this, "Success adding trips", Toast.LENGTH_LONG).show();
            EditAddActivity.this.finish();
        }

    }

    class LoadTtipItemById extends AsyncTask<Integer, Void, Trip> {
        private final static String TAG = "GetAllTrips";

        @Override
        protected Trip doInBackground(Integer... params) {
            InputStreamReader input = null;
            JsonReader reader = null;
            int id = params[0];
            try {
                Log.v(TAG, "GET /trips/" + id);
                URL url = new URL(Globals.BASE_API_URL + "/trips/" + id);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // connection.setRequestMethod();
                input = new InputStreamReader(connection.getInputStream());
                int httpCode = connection.getResponseCode();
                if (httpCode != 200) {
                    throw new IOException("Invalid HTTP code " + httpCode);
                }
                //
                reader = new JsonReader(input);
                Trip trip = new Trip();
                reader.beginObject();
                String key;
                // id
                key = reader.nextName();
                if (!key.equals("id")) throw new ParseException("id expected", 0);
                trip.id = reader.nextInt();
                // name
                key = reader.nextName();
                if (!key.equals("name")) throw new ParseException("name expected", 0);
                trip.name = reader.nextString();
                // destination
                key = reader.nextName();
                if (!key.equals("destination")) throw new ParseException("destination expected", 0);
                trip.destination = reader.nextString();
                // dueDate
                key = reader.nextName();
                if (!key.equals("departure")) throw new ParseException("departure expected", 0);
                String departureString = reader.nextString();
                trip.departure = new SimpleDateFormat("yyyy-MM-dd").parse(departureString);
                // travelBy
                key = reader.nextName();
                if (!key.equals("travelBy")) throw new ParseException("travelBy expected", 0);
                trip.travelBy = Trip.TravelBy.valueOf(reader.nextString());
                //
                reader.endObject();
                return trip;
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
        protected void onPostExecute(Trip result) {
            if (result == null) {
                Toast.makeText(EditAddActivity.this, "API error fetching trip", Toast.LENGTH_LONG).show();
                return;
            }
            currentItem = result;
            etName.setText(currentItem.name);
            etDestination.setText(currentItem.destination);
            //
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentItem.departure);
            dpDeparture.updateDate(cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            // radio buttons for gender
            switch (currentItem.travelBy) {
                case Train:
                    rgTrip.check(R.id.rbTrain);
                    break;
                case Car:
                    rgTrip.check(R.id.rbCar);
                    break;
                case Bus:
                    rgTrip.check(R.id.rbBus);
                    break;
                default:
                    Log.wtf(TAG, "currentItem.travelBy Car");
            }
        }


    }

    class UpdateTrip extends AsyncTask<Trip, Void, Boolean> {
        private final static String TAG = "AddTrip";

        @Override
        protected Boolean doInBackground(Trip... params) {
            Trip  trip= params[0];
            HttpURLConnection connection = null;
            try {
                JSONObject jsonData = new JSONObject();

                jsonData.put("name", trip.name);
                jsonData.put("destination", trip.destination);
                // format dueDate as YYYY-MM-DD
                jsonData.put("departure", dateFormat.format(trip.departure));
                jsonData.put("travelBy", trip.travelBy);
                //
                //
                Log.v(TAG, "PUT /trips DATA: " + trip.toString());
                URL url = new URL(Globals.BASE_API_URL + "/trips/" + trip.id);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                // send data, do not read data
                connection.setDoInput(false);
                connection.setDoOutput(true);

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
                Toast.makeText(EditAddActivity.this, "API error updating Trip item", Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(EditAddActivity.this, "Success saving Trip item", Toast.LENGTH_LONG).show();
            EditAddActivity.this.finish();
        }

    }

}
