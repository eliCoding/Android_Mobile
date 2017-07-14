package com.example.ipd9.friends;

import android.content.Context;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import static com.example.ipd9.friends.Friend.createFromLine;

/**
 * Created by ipd on 7/13/2017.
 */

public class Globals {
    public static final ArrayList<Friend> friendsList = new ArrayList<>();


//    public static void loadData() throws IOException, InvalidInputDataException{
//
//        FileInputStream inputStream = new FileInputStream(filename);
//        Scanner input = new Scanner(inputStream);
//        friendsList.clear();
//        while (input.hasNextLine()) {
//            String str = input.nextLine();
//            Friend friend = createFromLine(str);
//            friendsList.add(friend);
//        }
//    }
//
//    public static void saveData() throws IOException {
//        String string = "";
//
//        for(Friend f : friendsList) {
//            string += f.toString() + "\n";
//        }
//
//        FileOutputStream outputStream = new FileOutputStream(filename);
//        outputStream.write(string.getBytes());
//        outputStream.close();
//
//    }
}
