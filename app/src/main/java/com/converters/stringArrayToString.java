package com.converters;

import androidx.room.TypeConverter;

import java.util.List;
//TODO: check if still needed
public class stringArrayToString {
    @TypeConverter
    public static List<String> toArray(String value) {
        String [] arrayString=value.split(",");
        List<String> myarray=null;
        for(int i=0;i<arrayString.length;i++){
            myarray.add(arrayString[i]);
        }
        return value == null ? null : myarray;
    }

    @TypeConverter
    public static String toString(String[] value) {
        String myString="";
        if(value.length>0&&value!=null){
            for(int i=0;i<value.length;i++){
                myString=myString+value[i];
            }
        }
        return value == null ? null : myString;
    }
}
