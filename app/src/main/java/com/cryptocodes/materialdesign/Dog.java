package com.cryptocodes.materialdesign;

import io.realm.RealmObject;

/**
 * Created by jonathanf on 11/3/2015.
 */
public class Dog extends RealmObject {
    private String Name;
    private int Age;

    public void setName(String name)
    {
        Name = name;
    }
    public void setAge(int age)
    {
        Age = age;
    }
    public String getName() { return Name; }
    public int getAge() { return Age; }
}
