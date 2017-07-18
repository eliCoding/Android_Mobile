package info.ipd9.friendsdb;

import android.content.Context;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.IOError;
import java.io.IOException;
import java.util.HashSet;
import java.util.IllegalFormatException;
import java.util.Set;

import info.ipd9.friendsdb.R;

public class Friend {

    public static final String TABLE_NAME = "friends";
    public static final String COLUMN_ID = BaseColumns._ID;
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_INTERESTS = "interests";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_VEGETARIAN = "vegetarian";

    private static final String TAG = "Friend";

    int id;
    String name;
    int age;
    Set<Interest> interestSet = new HashSet<>();
    Gender gender;
    boolean vegetarian;

    String formatString;
    String male, female, notAvailable;

    public Friend(Context context) {
        formatString = context.getResources().getString(R.string.friend_format);
        male = context.getResources().getString(R.string.male);
        female = context.getResources().getString(R.string.female);
        notAvailable = context.getResources().getString(R.string.not_available);
    }

    @Override
    public String toString() {
        return String.format(formatString, name, age, genderToLocalizedString(gender));
        //return String.format("%s is %d is %s", name, age, gender.toString());
    }

    private String genderToLocalizedString(Gender g) {
        switch (g) {
            case Male:
                return male;
            case Female:
                return female;
            case Undeclared:
                return notAvailable;
            default:
                Log.wtf(TAG, "Gender translation failed");
                return "?";
        }
    }

    public String serialize() {
        String interestString = android.text.TextUtils.join(",", interestSet);
        String data = String.format("%s;%d;%s;%s;%s", name, age, interestString, gender.toString(),
                Boolean.toString(vegetarian));
        Log.v(TAG, "Friend seralized to: " + data);
        return data;
    }

    public void deserialize(String data) throws IOException {
        String [] dataArray = data.split(";");
        if (dataArray.length != 5) {
            Log.wtf(TAG, "deseralize error - wrong data structure");
            throw new IOException("deseralize error - wrong data structure");
        }
        name = dataArray[0];
        age = Integer.parseInt(dataArray[1]);
        interestSet.clear();
        String [] interestArray = dataArray[2].split(",");
        try {
            for (String s : interestArray) {
                // Note: valueOf() throws IllegalArgumentException if no match is found
                Interest iii = Interest.valueOf(s);
                interestSet.add(iii);
            }
        } catch (IllegalArgumentException ex) {
            throw new IOException("deserialize interest parse error on " + dataArray[2], ex);
        }
        try {
            // Note: valueOf() throws IllegalArgumentException if no match is found
            gender = Gender.valueOf(dataArray[3]);
        } catch (IllegalArgumentException ex) {
            throw new IOException("deserialize gender parse error on " + dataArray[3], ex);
        }
        //
        vegetarian = Boolean.parseBoolean(dataArray[4]);
    }

    //
    enum Interest { Cats, Dogs, Goldfish, Pigs }
    enum Gender { Male, Female, Undeclared }
}

