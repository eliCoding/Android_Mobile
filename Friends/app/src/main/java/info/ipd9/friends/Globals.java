package info.ipd9.friends;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Globals {

    private static final String TAG = "Globals";

    private static final String filename = "data.txt";

    public static final ArrayList<Friend> friendsList = new ArrayList<>();

    public static void loadData(Context context) throws IOException {
        FileInputStream inputStream;

        try {
            inputStream = context.openFileInput(filename);
            Scanner input = new Scanner(inputStream);
            friendsList.clear();
            while (input.hasNextLine()) {
                String data = input.nextLine();
                Friend f = new Friend();
                f.deserialize(data);
                friendsList.add(f);
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
        for (Friend f : friendsList) {
            string += f.serialize() + "\n";
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
