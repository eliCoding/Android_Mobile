package com.example.ipd9.friends;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ipd on 7/13/2017.
 */

public class Friend {
    private String name;
    private int age;
    private Set<Interest> interests = new HashSet<>();
    private Gender gender;
    private boolean vegeterian;

    public Friend(String name, int age, Set<Interest> interests, Gender gender, boolean vegeterian) {
        setName(name);
        setAge(age);
        setInterests(interests);
        setGender(gender);
        setVegeterian(vegeterian);
    }

    static Friend createFromLine(String line) throws InvalidInputDataException {

        String data[] = line.split(";");
        if (data.length != 5) {
            throw new InvalidInputDataException("Invalid data");
        } else {
           String name = data[0];
           int age = Integer.parseInt(data[1]);
           String substr = data[2].substring(1,data[2].length()-1);
           String[] str = substr.split(",");

           Set<Interest> interests = new HashSet<>();
           for(int i = 0; i < str.length; i++) {
               Interest in;
               if(i==0) {
                   in = Interest.valueOf(str[i]);
               } else {
                   in = Interest.valueOf(str[i].substring(1,str[i].length()));
               }
               interests.add(in) ;
           }

           Gender gender = Gender.valueOf(data[3]);
           Boolean vegeterian = Boolean.parseBoolean(data[4]);
           return (new Friend(name, age, interests, gender, vegeterian));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Set<Interest> getInterests() {
        return interests;
    }

    public void setInterests(Set<Interest> interests) {
        this.interests = interests;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public boolean isVegeterian() {
        return vegeterian;
    }

    public void setVegeterian(boolean vegeterian) {
        this.vegeterian = vegeterian;
    }

    @Override
    public String toString() {
        return  name + ";" + age + ";" + interests +  ";" + gender + ";" + vegeterian;
    }
}

enum Gender { Male, Female, NotAvailable }
enum  Interest { Cats, Dogs, Pigs, GoldFish }
class InvalidInputDataException extends Exception {
    InvalidInputDataException(String message) {
        super(message);
    }
}


