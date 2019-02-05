package jbtechventures.com.rtma.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;

import jbtechventures.com.rtma.Data.DataAdapter;
import jbtechventures.com.rtma.Model.Result;


/**
 * Created by Johnbosco on 05/03/2018.
 * This class manages the results data to an from the database
 */

public class ResultRepository extends DataAdapter {

    private static final String TABLE_NAME = "result";
    private static final String KEY_ID = "_id";
    private static final String KEY_MODULE = "module";
    private static final String KEY_UNIT = "unit";
    private static final String KEY_REG_VOTES = "reg_votes";
    private static final String KEY_ACCRED_VOTES = "accred_votes";
    private static final String KEY_CAST_VOTES = "cast_votes";
    private static final String KEY_INV_VOTES = "inv_votes";
    private static final String KEY_CREATE_DATE = "create_date";
    private static final String KEY_UPDATE_DATE = "update_date";
    private static final String KEY_SYNCED = "synced";
    private static final String KEY_COMPLETED = "completed";
    private static final String KEY_PROOF_IMAGE = "proof_image";
    private static final String KEY_PROOF_IMAGE_PATH = "proof_image_path";
    private static final String KEY_SYNC_ERROR_TEXT = "sync_error_text";
    private static final String KEY_USER_ID = "user_id";

    public ResultRepository(Context _context) {
        super(_context);
    }

    /**
     * Method adds results
     * returns null
     * */
    public int addResult(Result result) {
        SQLiteDatabase db = OpenDb();
        ContentValues values = new ContentValues();
        int id = 0;

        values.put(KEY_MODULE, result.ElectionId);
        values.put(KEY_UNIT, result.Unit);
        values.put(KEY_REG_VOTES, result.RegVotes);
        values.put(KEY_ACCRED_VOTES, result.AccredVotes);
        values.put(KEY_CAST_VOTES, result.CastVoted);
        values.put(KEY_INV_VOTES, result.InvalidVoted);
        values.put(KEY_SYNCED, result.Synced);
        values.put(KEY_COMPLETED, result.Completed);
        values.put(KEY_PROOF_IMAGE_PATH, result.ProofImagePath);
        if(resultCompleted(result.Unit, result.ElectionId))
            return result.Id;
        if (resultExists(result.Unit, result.ElectionId)) {
            values.put(KEY_UPDATE_DATE, new Date().toString());
            id = db.update(TABLE_NAME, values, KEY_UNIT + "=? AND " + KEY_MODULE + " =?", new String[]{result.Unit, String.valueOf(result.ElectionId)});
        }else {
            values.put(KEY_USER_ID, result.UserId);
            values.put(KEY_CREATE_DATE, new Date().toString());
            id = (int)db.insert(TABLE_NAME, null, values);
        }

        db.close();
        values.clear();
        return id;
    }

    /**
     * Method gets all Results
     * returns null
     * */
    public ArrayList<Result> getNonSyncedResults() {
        SQLiteDatabase db = OpenDb();
        ArrayList<Result> results = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_SYNCED + " = 0 AND " + KEY_COMPLETED + " = 1", null);

        if (cursor.moveToFirst()) {
            do {
                Result result = new Result();

                result.Id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                result.AccredVotes = cursor.getInt(cursor.getColumnIndex(KEY_ACCRED_VOTES));
                result.CastVoted = cursor.getInt(cursor.getColumnIndex(KEY_CAST_VOTES));
                result.InvalidVoted = cursor.getInt(cursor.getColumnIndex(KEY_INV_VOTES));
                result.ElectionId = cursor.getInt(cursor.getColumnIndex(KEY_MODULE));
                result.RegVotes = cursor.getInt(cursor.getColumnIndex(KEY_REG_VOTES));
                result.Unit = cursor.getString(cursor.getColumnIndex(KEY_UNIT));
                result.Synced = cursor.getInt(cursor.getColumnIndex(KEY_SYNCED));
                result.CreateDate = cursor.getString(cursor.getColumnIndex(KEY_CREATE_DATE));
                result.UpdateDate = cursor.getString(cursor.getColumnIndex(KEY_UPDATE_DATE));
                result.ProofImagePath = cursor.getString(cursor.getColumnIndex(KEY_PROOF_IMAGE_PATH));
                result.UserId = cursor.getInt(cursor.getColumnIndex(KEY_USER_ID));

                results.add(result);
            }while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return results;
    }

    /**
     * Method checks if a result exists
     * returns
     * false => result does not exists
     * true => result exists
     * */
    private boolean resultExists(String unit, int modleId) {
        SQLiteDatabase db = OpenDb();
        Cursor cursor = db.rawQuery("SELECT " + KEY_ID + " FROM " + TABLE_NAME + " WHERE " + KEY_UNIT + " = '" + unit + "'"
                + " AND " + KEY_MODULE + " = " + modleId, null);
        if (cursor.moveToFirst()) {
            return true;
        }
        return false;
    }

    public Result getResult(int moduleId, String unitCode) {
        SQLiteDatabase db = OpenDb();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_MODULE + " = " + moduleId + " AND "
                + KEY_UNIT + " = '" + unitCode + "'", null);
        if (cursor.moveToFirst()) {
            Result result = new Result();
            result.Id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            result.AccredVotes = cursor.getInt(cursor.getColumnIndex(KEY_ACCRED_VOTES));
            result.CastVoted = cursor.getInt(cursor.getColumnIndex(KEY_CAST_VOTES));
            result.ElectionId = cursor.getInt(cursor.getColumnIndex(KEY_MODULE));
            result.RegVotes = cursor.getInt(cursor.getColumnIndex(KEY_REG_VOTES));
            result.InvalidVoted = cursor.getInt(cursor.getColumnIndex(KEY_INV_VOTES));
            result.Unit = cursor.getString(cursor.getColumnIndex(KEY_UNIT));
            result.Synced = cursor.getInt(cursor.getColumnIndex(KEY_SYNCED));
            result.Completed = cursor.getInt(cursor.getColumnIndex(KEY_COMPLETED));
            result.CreateDate = cursor.getString(cursor.getColumnIndex(KEY_CREATE_DATE));
            result.UpdateDate = cursor.getString(cursor.getColumnIndex(KEY_UPDATE_DATE));
            result.ProofImagePath = cursor.getString(cursor.getColumnIndex(KEY_PROOF_IMAGE_PATH));

            return result;
        }
        db.close();
        return null;
    }

    public boolean NonSyncResultExists() {
        SQLiteDatabase db = OpenDb();
        Cursor cursor = db.rawQuery("SELECT " + KEY_ID + " FROM " + TABLE_NAME + " WHERE " + KEY_SYNCED + " = 0", null);
        return cursor.moveToFirst();
    }

    public void updateSynced(int id) {
        SQLiteDatabase db = OpenDb();
        ContentValues values = new ContentValues();

        values.put(KEY_SYNCED, 1);
        db.update(TABLE_NAME, values, KEY_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void updateCompleted(int id) {
        SQLiteDatabase db = OpenDb();
        ContentValues values = new ContentValues();

        values.put(KEY_COMPLETED, 1);
        db.update(TABLE_NAME, values, KEY_ID + "=?", new String[]{String.valueOf(id)});
    }


    public void updateSyncError(int id, String value) {
        SQLiteDatabase db = OpenDb();
        ContentValues values = new ContentValues();

        values.put(KEY_SYNC_ERROR_TEXT, value);
        db.update(TABLE_NAME, values, KEY_ID + "=?", new String[]{String.valueOf(id)});
    }

    /**
     * Method checks if a result has been completed
     * returns
     * false => result has not been completed
     * true => result completed
     * */
    public boolean resultCompleted(String unit, int modleId) {
        SQLiteDatabase db = OpenDb();
        Cursor cursor = db.rawQuery("SELECT " + KEY_ID + " FROM " + TABLE_NAME + " WHERE " + KEY_UNIT + " = '" +
                unit + "' AND " + KEY_MODULE + " = " + modleId + " AND " + KEY_COMPLETED + " = 1", null);
        if (cursor.moveToFirst()) {
            return true;
        }
        return false;
    }

    /**
     * Method gets all Results
     * returns null or ArrayList of Results
     * */
    public ArrayList<Result> getModuleResults(int moduleId) {
        SQLiteDatabase db = OpenDb();
        ArrayList<Result> results = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_MODULE + " = " + moduleId, null);

        if (cursor.moveToFirst()) {
            do {
                Result result = new Result();

                result.Id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                result.AccredVotes = cursor.getInt(cursor.getColumnIndex(KEY_ACCRED_VOTES));
                result.CastVoted = cursor.getInt(cursor.getColumnIndex(KEY_CAST_VOTES));
                result.InvalidVoted = cursor.getInt(cursor.getColumnIndex(KEY_INV_VOTES));
                result.ElectionId = cursor.getInt(cursor.getColumnIndex(KEY_MODULE));
                result.RegVotes = cursor.getInt(cursor.getColumnIndex(KEY_REG_VOTES));
                result.Unit = cursor.getString(cursor.getColumnIndex(KEY_UNIT));
                result.Synced = cursor.getInt(cursor.getColumnIndex(KEY_SYNCED));
                result.CreateDate = cursor.getString(cursor.getColumnIndex(KEY_CREATE_DATE));
                result.UpdateDate = cursor.getString(cursor.getColumnIndex(KEY_UPDATE_DATE));
                result.ProofImagePath = cursor.getString(cursor.getColumnIndex(KEY_PROOF_IMAGE_PATH));
                result.Completed = cursor.getInt(cursor.getColumnIndex(KEY_COMPLETED));
                result.SyncErrorText = cursor.getString(cursor.getColumnIndex(KEY_SYNC_ERROR_TEXT));

                //add votes
                /*Cursor voteCursor = db.rawQuery("SELECT * FROM votes WHERE " + result_id + " = " + result.Id, null);*/

                results.add(result);
            }while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return results;
    }

    /**
     * Method gets all Submitted Results
     * returns null or ArrayList of Results
     * */
    public ArrayList<Result> getSubmittedResults(int userId) {
        SQLiteDatabase db = OpenDb();
        ArrayList<Result> results = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_USER_ID + " = " + userId + " ORDER BY " + KEY_ID + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                Result result = new Result();

                result.Id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                result.AccredVotes = cursor.getInt(cursor.getColumnIndex(KEY_ACCRED_VOTES));
                result.CastVoted = cursor.getInt(cursor.getColumnIndex(KEY_CAST_VOTES));
                result.InvalidVoted = cursor.getInt(cursor.getColumnIndex(KEY_INV_VOTES));
                result.ElectionId = cursor.getInt(cursor.getColumnIndex(KEY_MODULE));
                result.RegVotes = cursor.getInt(cursor.getColumnIndex(KEY_REG_VOTES));
                result.Unit = cursor.getString(cursor.getColumnIndex(KEY_UNIT));
                result.Synced = cursor.getInt(cursor.getColumnIndex(KEY_SYNCED));
                result.CreateDate = cursor.getString(cursor.getColumnIndex(KEY_CREATE_DATE));
                result.UpdateDate = cursor.getString(cursor.getColumnIndex(KEY_UPDATE_DATE));
                result.ProofImagePath = cursor.getString(cursor.getColumnIndex(KEY_PROOF_IMAGE_PATH));
                result.Completed = cursor.getInt(cursor.getColumnIndex(KEY_COMPLETED));
                result.SyncErrorText = cursor.getString(cursor.getColumnIndex(KEY_SYNC_ERROR_TEXT));

                //add votes
                /*Cursor voteCursor = db.rawQuery("SELECT * FROM votes WHERE " + result_id + " = " + result.Id, null);*/

                results.add(result);
            }while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return results;
    }

    /**
     * Method gets all Submitted Results
     * returns null or ArrayList of Results
     * */
    public ArrayList<Result> getSubmittedResults(String query, int userId) {
        SQLiteDatabase db = OpenDb();
        ArrayList<Result> results = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_USER_ID + " = " + userId + " AND " + KEY_UNIT  + " LIKE ? %" + query + "% ORDER BY " + KEY_ID + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                Result result = new Result();

                result.Id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                result.AccredVotes = cursor.getInt(cursor.getColumnIndex(KEY_ACCRED_VOTES));
                result.CastVoted = cursor.getInt(cursor.getColumnIndex(KEY_CAST_VOTES));
                result.InvalidVoted = cursor.getInt(cursor.getColumnIndex(KEY_INV_VOTES));
                result.ElectionId = cursor.getInt(cursor.getColumnIndex(KEY_MODULE));
                result.RegVotes = cursor.getInt(cursor.getColumnIndex(KEY_REG_VOTES));
                result.Unit = cursor.getString(cursor.getColumnIndex(KEY_UNIT));
                result.Synced = cursor.getInt(cursor.getColumnIndex(KEY_SYNCED));
                result.CreateDate = cursor.getString(cursor.getColumnIndex(KEY_CREATE_DATE));
                result.UpdateDate = cursor.getString(cursor.getColumnIndex(KEY_UPDATE_DATE));
                result.ProofImagePath = cursor.getString(cursor.getColumnIndex(KEY_PROOF_IMAGE_PATH));
                result.Completed = cursor.getInt(cursor.getColumnIndex(KEY_COMPLETED));
                result.SyncErrorText = cursor.getString(cursor.getColumnIndex(KEY_SYNC_ERROR_TEXT));

                //add votes
                /*Cursor voteCursor = db.rawQuery("SELECT * FROM votes WHERE " + result_id + " = " + result.Id, null);*/

                results.add(result);
            }while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return results;
    }
}