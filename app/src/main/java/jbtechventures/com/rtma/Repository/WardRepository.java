package jbtechventures.com.rtma.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import jbtechventures.com.rtma.Data.DataAdapter;
import jbtechventures.com.rtma.Model.Lga;

public class WardRepository extends DataAdapter {

    private static final String TABLE_NAME = "party";
    private static final String KEY_ID = "party_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_CODE = "code";

    public WardRepository(Context _context) {
        super(_context);
    }

    /**
     * Method adds ward
     * returns null
     * */
    public void addWard(Lga lga) {
        SQLiteDatabase db = OpenDb();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, lga.Name);
        values.put(KEY_CODE, lga.Code);

        if (wardExists(lga.Code)) {
            db.update(TABLE_NAME, values, KEY_CODE + "=?", new String[]{lga.Code});
        }else {
            db.insert(TABLE_NAME, null, values);
        }

        db.close();
        values.clear();
    }

    /**
     * Method gets all wards
     * returns null
     * */
    public ArrayList<Lga> getWards() {
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
     * Method checks if a ward exists
     * returns
     * false => ward does not exists
     * true => ward exists
     * */
    private boolean wardExists(String code) {
        SQLiteDatabase db = OpenDb();
        Cursor cursor = db.rawQuery("SELECT " + KEY_CODE + " FROM " + TABLE_NAME + " WHERE " + KEY_CODE + " = '" + code + "'", null);
        if (cursor.moveToFirst()) {
            return true;
        }
        return false;
    }

    public void deleteWard(){
        SQLiteDatabase db = OpenDb();
        db.delete(TABLE_NAME, null ,null);
        db.close();
    }

    /**
     * Method gets all ward base on lga code
     * returns null
     * */
    public ArrayList<Lga> getWards(String lgaCode) {
        SQLiteDatabase db = OpenDb();
        ArrayList<Lga> lgas = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_CODE + " = '" + lgaCode + "'", null);

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
}
