package jbtechventures.com.rtma.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import jbtechventures.com.rtma.Data.DataAdapter;
import jbtechventures.com.rtma.Model.Lga;

public class PuRepository extends DataAdapter {

    private static final String TABLE_NAME = "party";
    private static final String KEY_ID = "party_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_CODE = "code";

    public PuRepository(Context _context) {
        super(_context);
    }

    /**
     * Method adds pu
     * returns null
     * */
    public void addPu(Lga lga) {
        SQLiteDatabase db = OpenDb();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, lga.Name);
        values.put(KEY_CODE, lga.Code);

        if (puExists(lga.Code)) {
            db.update(TABLE_NAME, values, KEY_CODE + "=?", new String[]{lga.Code});
        }else {
            db.insert(TABLE_NAME, null, values);
        }

        db.close();
        values.clear();
    }

    /**
     * Method gets all pu
     * returns null
     * */
    public ArrayList<Lga> getPu() {
        SQLiteDatabase db = OpenDb();
        ArrayList<Lga> lgas = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                Lga lga = new Lga();

                lga.Name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                lga.Code = cursor.getString(cursor.getColumnIndex(KEY_CODE));

                lgas.add(lga);
            }while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return lgas;
    }

    /**
     * Method checks if a pu exists
     * returns
     * false => pu does not exists
     * true => pu exists
     * */
    private boolean puExists(String code) {
        SQLiteDatabase db = OpenDb();
        Cursor cursor = db.rawQuery("SELECT " + KEY_CODE + " FROM " + TABLE_NAME + " WHERE " + KEY_CODE + " = '" + code + "'", null);
        if (cursor.moveToFirst()) {
            return true;
        }
        return false;
    }

    public void deletePus(){
        SQLiteDatabase db = OpenDb();
        db.delete(TABLE_NAME, null ,null);
        db.close();
    }
}
