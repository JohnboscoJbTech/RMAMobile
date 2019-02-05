package jbtechventures.com.rtma.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import jbtechventures.com.rtma.Data.DataAdapter;
import jbtechventures.com.rtma.Model.Election;


public class ElectionRepository extends DataAdapter {

    private static final String TABLE_NAME = "election";
    private static final String KEY_ID = "_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_ACTIVE = "active";
    private static final String KEY_START_DATE = "start_date";
    private static final String KEY_END_DATE = "end_date";

    public ElectionRepository(Context _context) {
        super(_context);
    }

    /*
    * Add an election to the database if not already exists
    **/
    public int addElection(Election election) {
        SQLiteDatabase db = OpenDb();
        ContentValues values = new ContentValues();
        int id = 0;

        values.put(KEY_ID, election.Id);
        values.put(KEY_NAME, election.Name);
        values.put(KEY_DESCRIPTION, election.Description);
        values.put(KEY_ACTIVE, election.Active);
        values.put(KEY_START_DATE, election.StartDate);
        values.put(KEY_END_DATE, election.EndDate);

        if (electionExists(election.Id)) {
            id = db.update(TABLE_NAME, values, KEY_ID + "=?", new String[]{String.valueOf(election.Id)});
        }else {
            id = (int)db.insert(TABLE_NAME, null, values);
        }

        db.close();
        values.clear();
        return id;
    }

    /*
    * Checks if election exists by the id
    * */
    private boolean electionExists(int id) {
        SQLiteDatabase db = OpenDb();
        Cursor cursor = db.rawQuery("SELECT " + KEY_NAME + " FROM " + TABLE_NAME + " WHERE " + KEY_ID + " = " + id, null);
        if (cursor.moveToFirst()) {
            //db.close();
            cursor.close();
            return true;
        }
        cursor.close();
        //db.close();
        return false;
    }

    /* *
    * Get All Active Elections
    * returs the cursor
    * */
    public Cursor getActiveElections(){
        SQLiteDatabase db = OpenReadableDb();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ACTIVE + " = " + 1, null);
        //db.close();
        return cursor;
    }

    /* *
     * Get a particular Elections
     * returns election
     * */
    public Election getElection(int id){
        SQLiteDatabase db = OpenReadableDb();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ID + " = " + id, null);
        Election eLection = new Election();
        if (cursor.moveToFirst()) {
            eLection.Id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            eLection.Name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            eLection.StartDate = cursor.getString(cursor.getColumnIndex(KEY_START_DATE));
            eLection.EndDate = cursor.getString(cursor.getColumnIndex(KEY_END_DATE));
            cursor.close();
        }
        db.close();
        return eLection;
    }

    /* *
     * Get All Active Elections
     * returns elections as arrayList
     * */
    public ArrayList<Election> getAllActiveElections(){
        SQLiteDatabase db = OpenReadableDb();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ACTIVE + " = " + 1, null);
        ArrayList<Election> elections = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Election eLection = new Election();

                eLection.Id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                eLection.Name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                eLection.StartDate = cursor.getString(cursor.getColumnIndex(KEY_START_DATE));
                eLection.EndDate = cursor.getString(cursor.getColumnIndex(KEY_END_DATE));

                elections.add(eLection);
            }while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return elections;
    }

    /**
     * Deletes all Election data
     * */
    public void deleteElection(){
        SQLiteDatabase db = OpenDb();
        db.delete(TABLE_NAME, null ,null);
        db.close();
    }
}
