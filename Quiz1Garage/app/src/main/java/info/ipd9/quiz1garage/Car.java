package info.ipd9.quiz1garage;

import android.util.Log;

import java.io.IOException;

/**
 * Created by ipd on 7/14/2017.
 */

public class Car {
    private static final String TAG = "Friend";

    String makeModel;
    int odometer; // in thousands of kilometers 0 - 500
    int yearOfProduction; // integer 1900-2100
    BodyType bodyType; // Enum { Van, Coupe, Convertible, FullSize, Compact }
    boolean isElectric;





    @Override
    public String toString() {
        return  makeModel + ";odometer is " + odometer + ";Year of Prodocution is" + yearOfProduction +  "; Body Type is:" + bodyType + ";" + isElectric + "\n";
    }
    enum BodyType {

        Van, Coupe, Convertible, FullSize, Compact
    }


    public String serialize() throws IOException {


        String data = String.format("%s;%d;%d;%s;%s", makeModel, odometer, yearOfProduction, bodyType.toString(),
                Boolean.toString(isElectric));
        Log.v(TAG, "Car seralized to: " + data);
        return data;

    }
    public void deserialize(String data) throws IOException {
        String [] dataArray = data.split(";");
        if (dataArray.length != 5) {
            Log.wtf(TAG, "deseralize error - wrong data structure");
            throw new IOException("deseralize error - wrong data structure");
        }
        makeModel = dataArray[0];
        odometer = Integer.parseInt(dataArray[1]);
        yearOfProduction = Integer.parseInt(dataArray[2]);

        try {
            // Note: valueOf() throws IllegalArgumentException if no match is found
            bodyType = BodyType.valueOf(dataArray[3]);
        } catch (IllegalArgumentException ex) {
            throw new IOException("deserialize gender parse error on " + dataArray[3], ex);
        }
        //
        isElectric = Boolean.parseBoolean(dataArray[4]);
    }

}
