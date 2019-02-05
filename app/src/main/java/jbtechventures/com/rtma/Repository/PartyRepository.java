package jbtechventures.com.rtma.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import jbtechventures.com.rtma.Data.DataAdapter;
import jbtechventures.com.rtma.Model.Party;


public class PartyRepository extends DataAdapter {

    private static final String TABLE_NAME = "party";
    private static final String KEY_ID = "party_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_CODE = "code";

    public PartyRepository(Context _context) {
        super(_context);
    }

    /**
     * Method adds parties
     * returns null
     * */
    public void addParty(Party party) {
        SQLiteDatabase db = OpenDb();
        ContentValues values = new ContentValues();

        values.put(KEY_ID, party.Id);
        values.put(KEY_NAME, party.Name);
        values.put(KEY_CODE, party.Code);

        if (partyExists(party.Code)) {
            db.update(TABLE_NAME, values, KEY_ID + "=?", new String[]{String.valueOf(party.Id)});
        }else {
            db.insert(TABLE_NAME, null, values);
        }

        db.close();
        values.clear();
    }

    /**
     * Method gets all Parties
     * returns null
     * */
    public ArrayList<Party> getParties() {
        SQLiteDatabase db = OpenDb();
        ArrayList<Party> parties = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                Party party = new Party();

                party.Id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                party.Name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                party.Code = cursor.getString(cursor.getColumnIndex(KEY_CODE));

                parties.add(party);
            }while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return parties;
    }

    /**
     * Method gets party by the name
     * */
    public Party getParty(String name) {
        SQLiteDatabase db = OpenDb();
        Party party = new Party();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_CODE + " = '" + name + "'", null);
                /*db.query(TABLE_NAME, new String[] { KEY_ID, KEY_NAME, KEY_CODE }, KEY_CODE,
                new String[]{ name },null,null,null,null);*/
        if (cursor.moveToFirst()) {
            party.Id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            party.Name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            party.Code = cursor.getString(cursor.getColumnIndex(KEY_CODE));
            cursor.close();
        }
        db.close();
        return party;
    }

    /**
     * Method gets party by the id
     * */
    public Party getParty(int id) {
        SQLiteDatabase db = OpenDb();
        Party party = new Party();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ID + " = " + id + "", null);
                /*db.query(TABLE_NAME, new String[] { KEY_ID, KEY_NAME, KEY_CODE }, KEY_CODE,
                new String[]{ name },null,null,null,null);*/
        if (cursor.moveToFirst()) {
            party.Id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            party.Name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            party.Code = cursor.getString(cursor.getColumnIndex(KEY_CODE));
            cursor.close();
        }
        db.close();
        return party;
    }

    /**
     * Method searches all parties that contains text
     * */
    public Cursor getParties(String searchText) {
        SQLiteDatabase db = OpenDb();
        ArrayList<Party> parties = new ArrayList<>();
        Cursor cursor = db.query(true, TABLE_NAME, new String[] { KEY_ID, KEY_NAME, KEY_CODE }, KEY_NAME + " LIKE ?",
                new String[] { searchText+"%" }, null, null, null,
                null);

        /*if (cursor.moveToFirst()) {
            do {
                Party party = new Party();

                party.Id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                party.Name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                party.Code = cursor.getString(cursor.getColumnIndex(KEY_CODE));

                parties.add(party);
            }while (cursor.moveToNext());
            cursor.close();
        }
        db.close();*/
        return cursor;
    }

    /**
     * Method checks if a party exists
     * returns
     * false => party does not exists
     * true => party exists
     * */
    private boolean partyExists(String code) {
        SQLiteDatabase db = OpenDb();
        Cursor cursor = db.rawQuery("SELECT " + KEY_CODE + " FROM " + TABLE_NAME + " WHERE " + KEY_CODE + " = '" + code + "'", null);
        if (cursor.moveToFirst()) {
            return true;
        }
        return false;
    }

    public void deleteParties(){
        SQLiteDatabase db = OpenDb();
        db.delete(TABLE_NAME, null ,null);
        db.close();
    }
}
