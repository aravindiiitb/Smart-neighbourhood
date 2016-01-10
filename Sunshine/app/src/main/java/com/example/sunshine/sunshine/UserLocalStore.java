package com.example.sunshine.sunshine;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by dell pc on 25/12/2015.
 */
public class UserLocalStore {

    public static final String SP_NAME = "userDetails";

    SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context) {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(ModelUser user) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putString("username", user.username);
        userLocalDatabaseEditor.putString("password", user.password);
        userLocalDatabaseEditor.putString("email", user.email);
        userLocalDatabaseEditor.putInt("user_id", user.id);
        userLocalDatabaseEditor.putBoolean("is_admin",user.is_admin);
        userLocalDatabaseEditor.putString("occupation",user.occupation);
        userLocalDatabaseEditor.putInt("is_verified",user.is_verified);

        userLocalDatabaseEditor.commit();
    }

    public void setUserLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putBoolean("loggedIn", loggedIn);
        userLocalDatabaseEditor.commit();
    }

    public void clearUserData() {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.clear();
        userLocalDatabaseEditor.commit();
    }

    public ModelUser getLoggedInUser() {
        if (userLocalDatabase.getBoolean("loggedIn", false) == false) {
            return null;
        }
        ModelUser user = new ModelUser();

        user.username = userLocalDatabase.getString("username", "");
        user.password = userLocalDatabase.getString("password", "");
        user.email = userLocalDatabase.getString("email", "");
        user.id = userLocalDatabase.getInt("user_id", -1);
        user.is_admin = userLocalDatabase.getBoolean("is_admin", false);
        user.occupation = userLocalDatabase.getString("occupation", "");
        user.is_verified = userLocalDatabase.getInt("is_verified", -1);

        return user;
    }

}
