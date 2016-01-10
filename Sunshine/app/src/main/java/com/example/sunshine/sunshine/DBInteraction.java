package com.example.sunshine.sunshine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by dell pc on 24/12/2015.
 * /**
 * DBInteraction Class is used for Local Database. Used to perform Database
 * connectivity, Queries to the database, close database connection.
 */


public class DBInteraction extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SmartN.db";
    public Context context = null;

    public DBInteraction(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table User " +
                        "(user_id integer primary key,user_name text,password text,email text)"
        );
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        //db.execSQL(SQL_DELETE_ENTRIES);
        //onCreate(db);
    }


    public boolean insertUserDetail(ModelUser user) {

        SQLiteDatabase db = this.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        Random rand = new Random();

        values.put("user_name", user.username);
        values.put("email", user.email);
        values.put("password", user.password);
        values.put("user_id", rand.nextInt(1000000));

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert("User", null, values);
        return true;
    }

    public ModelUser checkUserDetails(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from User where user_name = '" + username + "'", null);
        if (cursor != null & cursor.getColumnCount() > 0) {
            cursor.moveToFirst();

            String uname = cursor.getString(cursor.getColumnIndex("user_name"));
            String pass = cursor.getString(cursor.getColumnIndex("password"));

            if(uname.equals(username) && pass.equals(password)){
                ModelUser user = new ModelUser();
                user.id = cursor.getInt(cursor.getColumnIndex("user_id"));
                user.username = cursor.getString(cursor.getColumnIndex("user_name"));
                user.password = cursor.getString(cursor.getColumnIndex("password"));
                user.email = cursor.getString(cursor.getColumnIndex("email"));

                return user;
            }

        }
        return null;
    }

}
