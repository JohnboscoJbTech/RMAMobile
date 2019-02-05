package jbtechventures.com.rtma.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;

import jbtechventures.com.rtma.Data.DataAdapter;
import jbtechventures.com.rtma.Model.Complaint;

/**
 * Created by Johnbosco on 05/03/2018.
 * This class manages the results data to an from the database
 */

public class ComplaintRepository extends DataAdapter {

    private static final String TABLE_NAME = "complaint";
    private static final String KEY_ID = "_id";
    private static final String KEY_MESSAGE = "complain";
    private static final String KEY_POLLING_UNIT = "polling_unit";
    private static final String KEY_USER_ID =  "user_id";
    private static final String KEY_PROOF_IMAGE_PATH = "complain_image_path";
    private static final String KEY_SYNCED = "synced";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ELECTION_ID = "election_id";
    private static final String KEY_DATE = "date";

    public ComplaintRepository(Context _context) {
        super(_context);
    }

    /**
     * Method adds Compalin
     * returns null
     * */
    public int addComplain(Complaint complaint) {
        SQLiteDatabase db = OpenDb();
        ContentValues values = new ContentValues();
        int id = 0;

        values.put(KEY_MESSAGE, complaint.Message);
        values.put(KEY_POLLING_UNIT, complaint.PollingUnit);
        values.put(KEY_USER_ID, complaint.UserId);
        values.put(KEY_SYNCED, complaint.Synced);
        values.put(KEY_PROOF_IMAGE_PATH, complaint.ImagePath);
        values.put(KEY_TITLE, complaint.Title);
        values.put(KEY_ELECTION_ID, complaint.ElectionId);
        values.put(KEY_DATE, complaint.Date);

        id = (int)db.insert(TABLE_NAME, null, values);

        db.close();
        values.clear();
        return id;
    }

    /**
     * Method gets all non synced Complaints
     * returns null
     * */
    public ArrayList<Complaint> getNonSyncedComplaints() {
        SQLiteDatabase db = OpenDb();
        ArrayList<Complaint> complaints = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_SYNCED + " = 0", null);

        if (cursor.moveToFirst()) {
            do {
                Complaint complaint = new Complaint();

                complaint.Id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                complaint.Message = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE));
                complaint.PollingUnit = cursor.getString(cursor.getColumnIndex(KEY_POLLING_UNIT));
                complaint.Synced = cursor.getInt(cursor.getColumnIndex(KEY_SYNCED));
                complaint.ImagePath = cursor.getString(cursor.getColumnIndex(KEY_PROOF_IMAGE_PATH));
                complaint.UserId = cursor.getInt(cursor.getColumnIndex(KEY_USER_ID));
                complaint.ElectionId =  cursor.getInt(cursor.getColumnIndex(KEY_ELECTION_ID));
                complaint.Title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
                complaint.Date = cursor.getString(cursor.getColumnIndex(KEY_DATE));

                complaints.add(complaint);
            }while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return complaints;
    }

    /**
     * Method gets all Complaints
     * returns null
     * */
    public ArrayList<Complaint> getComplaints(int userId) {
        SQLiteDatabase db = OpenDb();
        ArrayList<Complaint> complaints = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_USER_ID + " = " + userId, null);

        if (cursor.moveToFirst()) {
            do {
                Complaint complaint = new Complaint();

                complaint.Id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                complaint.Message = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE));
                complaint.PollingUnit = cursor.getString(cursor.getColumnIndex(KEY_POLLING_UNIT));
                complaint.Synced = cursor.getInt(cursor.getColumnIndex(KEY_SYNCED));
                complaint.ImagePath = cursor.getString(cursor.getColumnIndex(KEY_PROOF_IMAGE_PATH));
                complaint.UserId = cursor.getInt(cursor.getColumnIndex(KEY_USER_ID));
                complaint.ElectionId =  cursor.getInt(cursor.getColumnIndex(KEY_ELECTION_ID));
                complaint.Title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
                complaint.Date = cursor.getString(cursor.getColumnIndex(KEY_DATE));

                complaints.add(complaint);
            }while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return complaints;
    }

    /**
     * Method gets all Complaints based on search query
     * Input query searches for title match
     * returns null
     * */
    public ArrayList<Complaint> getComplaints(String query, int userId) {
        SQLiteDatabase db = OpenDb();
        ArrayList<Complaint> complaints = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_USER_ID + " = " + userId + " AND " + KEY_TITLE + " LIKE '%"  + query + "%'" , null);

        if (cursor.moveToFirst()) {
            do {
                Complaint complaint = new Complaint();

                complaint.Id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                complaint.Message = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE));
                complaint.PollingUnit = cursor.getString(cursor.getColumnIndex(KEY_POLLING_UNIT));
                complaint.Synced = cursor.getInt(cursor.getColumnIndex(KEY_SYNCED));
                complaint.ImagePath = cursor.getString(cursor.getColumnIndex(KEY_PROOF_IMAGE_PATH));
                complaint.UserId = cursor.getInt(cursor.getColumnIndex(KEY_USER_ID));
                complaint.ElectionId =  cursor.getInt(cursor.getColumnIndex(KEY_ELECTION_ID));
                complaint.Title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
                complaint.Date = cursor.getString(cursor.getColumnIndex(KEY_DATE));

                complaints.add(complaint);
            }while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return complaints;
    }

    public void updateSynced(int id) {
        SQLiteDatabase db = OpenDb();
        ContentValues values = new ContentValues();

        values.put(KEY_SYNCED, 1);
        db.update(TABLE_NAME, values, KEY_ID + "=?", new String[]{String.valueOf(id)});
    }
}