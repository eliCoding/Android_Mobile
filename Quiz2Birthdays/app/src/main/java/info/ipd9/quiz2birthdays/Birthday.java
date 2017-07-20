package info.ipd9.quiz2birthdays;

import android.provider.BaseColumns;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ipd on 7/19/2017.
 */

public class Birthday {

    public static final String TABLE_NAME = "birthdays";
    public static final String COLUMN_ID = BaseColumns._ID;
    public static final String COLUMN_NAME = "name";
    public final static String COLUMN_DATEOFBIRTH = "dateofbirth";


    int id;
    String name;
    Date dob; //(Date of Birth, year/month/day)


    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String d = sdf.format(dob).toString();
        return  name + ";" + d;
    }

}
