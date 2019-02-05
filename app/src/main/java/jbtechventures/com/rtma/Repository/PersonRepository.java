package jbtechventures.com.rtma.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import jbtechventures.com.rtma.Data.DataAdapter;
import jbtechventures.com.rtma.Model.Person;


/**
 * Created by Johnbosco on 05/03/2018.
 * This class manages the person data to an from the database
 */

public class PersonRepository extends DataAdapter {

    private static final String TABLE_NAME = "person";
    private static final String KEY_ID = "_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_LOGGED_IN = "logged_in";

    public PersonRepository(Context _context) {
        super(_context);
    }

    /**
     * Method adds person
     * returns null
     * */
    public int addPerson(Person person) {
        SQLiteDatabase db = OpenDb();
        ContentValues values = new ContentValues();
        int id = 0;

        values.put(KEY_USERNAME, person.Username);
        values.put(KEY_PASSWORD, person.Password);
        values.put(KEY_USER_ID, person.UserId);
        values.put(KEY_LOGGED_IN, person.LoggedIn);

        if (personExists(person.Username)) {
            id = db.update(TABLE_NAME, values, KEY_USER_ID + "=?", new String[]{String.valueOf(person.UserId)});
        }else {
            id = (int)db.insert(TABLE_NAME, null, values);
        }

        db.close();
        values.clear();
        return id;
    }

    /**
     * Method checks if a person exists
     * returns
     * false => person does not exists
     * true => person exists
     * */
    public boolean personExists(String userName) {
        SQLiteDatabase db = OpenDb();
        Cursor cursor = db.rawQuery("SELECT " + KEY_USER_ID + " FROM " + TABLE_NAME + " WHERE " + KEY_USERNAME + " = '" + userName + "'", null);
        if (cursor.moveToFirst()) {
            return true;
        }
        return false;
    }

    /**
     * Method checks if a person is currently logged in
     * returns
     * false => person does not exists
     * true => person exists
     * */
    public boolean personCurrentlyLoggedIn(String userName, String password) {
        SQLiteDatabase db = OpenDb();
        Cursor cursor = db.rawQuery("SELECT " + KEY_USER_ID + " FROM " + TABLE_NAME + " WHERE " + KEY_USERNAME + " = '" + userName + "' AND " +
                KEY_LOGGED_IN + " = 1 AND " + KEY_PASSWORD + " = '" + password + "'", null);
        if (cursor.moveToFirst()) {
            return true;
        }
        return false;
    }

    /**
     * Method checks if a person is currently logged in
     * returns
     * false => person does not exists
     * true => person exists
     * */
    public Person getPersonCurrentlyLoggedIn() {
        SQLiteDatabase db = OpenDb();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_LOGGED_IN + " = 1 ", null);
        if (cursor.moveToFirst()) {
            Person person = new Person();
            person.UserId = cursor.getInt(cursor.getColumnIndex(KEY_USER_ID));
            person.Username = cursor.getString(cursor.getColumnIndex(KEY_USERNAME));
            person.Password = cursor.getString(cursor.getColumnIndex(KEY_PASSWORD));
            person.LoggedIn = cursor.getInt(cursor.getColumnIndex(KEY_LOGGED_IN));
            person.Id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            return person;
        }
        cursor.close();
        db.close();
        return null;
    }

    /**
     * Method updates the last time a user logs in
     * returns null
     * */
    public void updatePersonLogin() {
        SQLiteDatabase db = OpenDb();
        ContentValues values = new ContentValues();

        values.put(KEY_LOGGED_IN, 0);
        db.update(TABLE_NAME, values, null, null);
    }

    /**
     * Method gets the person currently logged in
     * returns
     * null => person does not exists
     * Person => person exists
     * */
    public Person getPerson() {
        SQLiteDatabase db = OpenDb();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_LOGGED_IN + " = 1 ", null);
        if (cursor.moveToFirst()) {
            Person person = new Person();
            person.UserId = cursor.getInt(cursor.getColumnIndex(KEY_USER_ID));
            person.Username = cursor.getString(cursor.getColumnIndex(KEY_USERNAME));
            person.Password = cursor.getString(cursor.getColumnIndex(KEY_PASSWORD));
            person.LoggedIn = cursor.getInt(cursor.getColumnIndex(KEY_LOGGED_IN));
            person.Id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            return person;
        }
        cursor.close();
        db.close();
        return null;
    }
}
