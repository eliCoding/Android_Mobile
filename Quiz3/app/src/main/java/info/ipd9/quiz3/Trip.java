package info.ipd9.quiz3;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ipd on 7/25/2017.
 */

public class Trip {

    int id;
    String name;
    String destination;
    Date departure;
    TravelBy travelBy;






    enum TravelBy { Train, Car, Bus }

    @Override
    public String toString() {

        return  name + ";" + destination + ";" + departure + ";" + travelBy;
    }

}
