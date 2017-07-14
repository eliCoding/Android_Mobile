package info.ipd9.quiz1garage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;

public class AddCarActivity extends AppCompatActivity {


    private static final String TAG = "AddCarActivity";

    private EditText etModel;
    private SeekBar sbOdometer;
    private EditText etYear;
    private RadioGroup rgBody;
    private Switch swElectrical;

    private Car currentCar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        etModel = (EditText) findViewById(R.id.etModel);
        sbOdometer = (SeekBar) findViewById(R.id.sbOdometer);
        etYear  = (EditText) findViewById(R.id.etYear);
        swElectrical = (Switch) findViewById(R.id.swElectrical);

        rgBody = (RadioGroup) findViewById(R.id.rgBody);

        // Example on how to add onClick handler programmatically
        Button button = (Button) findViewById(R.id.btAdd);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddCarClick(v);
            }
        });
    }


    public void onAddCarClick(View v) {
        Log.d(TAG, "onAddCarClick()");
        // extract data from UI, put in Friend instance
        Car car = (currentCar == null) ? new Car() : currentCar;
        car.makeModel = etModel.getText().toString();
        car.odometer = sbOdometer.getProgress();
        car.bodyType = getSelectedBodyType();
        car.yearOfProduction = Integer.parseInt( etYear.getText().toString() );

        car.isElectric = swElectrical.isChecked();
        // add or save data
        if (currentCar == null) {
            Globals.carsList.add(car);
        }
        // save changes to file
        try {
            Globals.saveData(this);
        } catch (IOException e) {
            Toast.makeText(this, "Error saving data", Toast.LENGTH_LONG).show();
        }
        // show toast
        Toast.makeText(this, "car added", Toast.LENGTH_SHORT).show();
        // finish(), do NOT use Intent to go back to MainActivity
        finish();
    }


    private Car.BodyType getSelectedBodyType() {
        int id = rgBody.getCheckedRadioButtonId();
        switch (id) {
            case R.id.rbVan:
                return Car.BodyType.Van;
            case R.id.rbCoupe:
                return Car.BodyType.Coupe;
            case R.id.rbConvertible:
                return Car.BodyType.Convertible;
            case R.id.rbFullsize:
                return Car.BodyType.FullSize;
            case R.id.rbCompact:
                return Car.BodyType.Compact;
            default:
                Log.wtf(TAG, "setSelectedBodyTyper() unknown ID");
                return Car.BodyType.Van;
        }
    }
}
