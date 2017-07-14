package info.ipd9.quiz1garage;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by ipd on 7/14/2017.
 */

public class Globals {

    private static final String TAG = "Globals";

    private static final String filename = "data.txt";

    public static final ArrayList<Car> carsList = new ArrayList<>();

    public static void loadData(Context context) throws IOException {
        FileInputStream inputStream;

        try {
            inputStream = context.openFileInput(filename);
            Scanner input = new Scanner(inputStream);
            carsList.clear();
            while (input.hasNextLine()) {
                String data = input.nextLine();
                Car c = new Car();
                c.deserialize(data);
                carsList.add(c);
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, "loadData() data file not found");
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void saveData(Context context) throws IOException {
        String string = "";
        for (Car c : carsList) {
            string += c.serialize() + "\n";
        }

        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
