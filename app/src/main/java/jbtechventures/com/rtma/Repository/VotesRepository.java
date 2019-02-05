package jbtechventures.com.rtma.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import jbtechventures.com.rtma.Data.DataAdapter;
import jbtechventures.com.rtma.Model.Vote;


public class VotesRepository extends DataAdapter {

    private static final String TABLE_NAME = "votes";
    private static final String KEY_ID = "_id";
    private static final String KEY_COUNT = "count";
    private static final String KEY_RESULT_ID = "result_id";
    private static final String KEY_PARTY_ID = "party_id";
    private static final String KEY_MODULE_ID = "module_id";

    public VotesRepository(Context _context) {
        super(_context);
    }

    /**
     * Method adds votes
     * returns null
     * */
    public void addVote(Vote vote) {
        SQLiteDatabase db = OpenDb();
        ContentValues values = new ContentValues();

        //values.put(KEY_ID, vote.Id);
        values.put(KEY_COUNT, vote.Count);
        values.put(KEY_RESULT_ID, vote.Result);
        values.put(KEY_PARTY_ID, vote.Party);
        values.put(KEY_MODULE_ID, vote.ElectionId);

        if (voteExists(vote.Party, vote.ElectionId)) {
            db.update(TABLE_NAME, values, KEY_PARTY_ID + " = ? AND " + KEY_MODULE_ID + "= ?", new String[]{String.valueOf(vote.Party),String.valueOf(vote.ElectionId)});
        }else {
            db.insert(TABLE_NAME, null, values);
        }

        db.close();
        values.clear();
    }

    /**
     * Method gets all votes
     * returns null
     * */
    public ArrayList<Vote> getVotes() {
        SQLiteDatabase db = OpenDb();
        ArrayList<Vote> votes = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                Vote vote = new Vote();

                vote.Id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                vote.Count = cursor.getInt(cursor.getColumnIndex(KEY_COUNT));
                vote.ElectionId = cursor.getInt(cursor.getColumnIndex(KEY_MODULE_ID));
                vote.Party = cursor.getInt(cursor.getColumnIndex(KEY_PARTY_ID));
                vote.Result = cursor.getInt(cursor.getColumnIndex(KEY_RESULT_ID));

                votes.add(vote);
            }while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return votes;
    }

    /**
     * Method checks if a vote exists
     * returns
     * false => vote does not exists
     * true => vote exists
     * */
    private boolean voteExists(int partyId, int moduleId) {
        SQLiteDatabase db = OpenDb();
        Cursor cursor = db.rawQuery("SELECT " + KEY_ID + " FROM " + TABLE_NAME + " WHERE " + KEY_PARTY_ID + " = " + partyId + " AND " +
                KEY_MODULE_ID + " = " + moduleId, null);
        if (cursor.moveToFirst()) {
            return true;
        }
        return false;
    }

    public ArrayList<Vote> getModuleVotes(int id, int resultId) {
        SQLiteDatabase db = OpenDb();
        ArrayList<Vote> votes = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_MODULE_ID + " = " + id + " AND " +
                KEY_RESULT_ID + " = " + resultId, null);

        if (cursor.moveToFirst()) {
            do {
                Vote vote = new Vote();

                vote.Id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                vote.Count = cursor.getInt(cursor.getColumnIndex(KEY_COUNT));
                vote.ElectionId = cursor.getInt(cursor.getColumnIndex(KEY_MODULE_ID));
                vote.Party = cursor.getInt(cursor.getColumnIndex(KEY_PARTY_ID));
                vote.Result = cursor.getInt(cursor.getColumnIndex(KEY_RESULT_ID));

                votes.add(vote);
            }while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return votes;
    }
}
