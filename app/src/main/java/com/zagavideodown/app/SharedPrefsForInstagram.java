/*
 * *
 *  * Created by Syed Usama Ahmad on 3/14/23, 5:04 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 3/14/23, 4:56 PM
 *
 */

package com.zagavideodown.app;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.zagavideodown.app.models.instawithlogin.ModelInstagramPref;
import com.zagavideodown.app.utils.Utils;
import com.google.gson.Gson;

public class SharedPrefsForInstagram {
    public static String PREFERENCE = "Allvideodownloaderinstaprefs";
    public static String PREFERENCE_item = "instadata";

    public static Context context;

    private static SharedPrefsForInstagram instance;
    SharedPreferences sharedPreference;
    SharedPreferences.Editor editor;

    public SharedPrefsForInstagram() {
    }

    public SharedPrefsForInstagram(Context context) {
        SharedPrefsForInstagram.context = context;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        sharedPreference = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedPreference.edit();
    }

    public static SharedPrefsForInstagram getInstance() {
        return instance;


    }

    public ModelInstagramPref getPreference() {
        try {
            Gson gson = new Gson();
            String storedHashMapString = sharedPreference.getString(PREFERENCE_item, null);

            boolean isValid = Utils.isValidJson(storedHashMapString);
            if (isValid) {
                return gson.fromJson(storedHashMapString, ModelInstagramPref.class);
            } else {
                return new ModelInstagramPref("", "", "", "", "false");

            }
        } catch (Exception e) {
//            e.printStackTrace();
            return new ModelInstagramPref("", "", "", "", "false");
        }
    }

    public void setPreference(ModelInstagramPref map) {

        Gson gson = new Gson();

        String hashMapString = gson.toJson(map);

        editor = sharedPreference.edit();
        editor.putString(PREFERENCE_item, hashMapString);
        editor.apply();
    }

    public void clearSharePrefs() {
        Gson gson = new Gson();
        String hashMapString = gson.toJson(new ModelInstagramPref("", "", "", "", "false"));
        editor = sharedPreference.edit();
        editor.putString(PREFERENCE_item, hashMapString);
        editor.apply();

    }


}

