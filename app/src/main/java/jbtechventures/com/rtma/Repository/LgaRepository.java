package jbtechventures.com.rtma.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import jbtechventures.com.rtma.Data.DataAdapter;
import jbtechventures.com.rtma.Model.Lga;


public class LgaRepository extends DataAdapter {

    private static final String TABLE_NAME = "lga";
    private static final String KEY_NAME = "name";
    private static final String KEY_CODE = "code";

    public LgaRepository(Context _context) {
        super(_context);
    }

    /**
     * Method adds lga
     * returns null
     * */
    public void addLga(Lga lga) {
        SQLiteDatabase db = OpenDb();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, lga.Name);
        values.put(KEY_CODE, lga.Code);

        if (lgaExists(lga.Code)) {
            db.update(TABLE_NAME, values, KEY_CODE + "=?", new String[]{lga.Code});
        }else {
            db.insert(TABLE_NAME, null, values);
        }

        db.close();
        values.clear();
    }

    /**
     * Method gets all lgas
     * returns null
     * */
    public ArrayList<Lga> getLgas() {
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
     * Method checks if a lga exists
     * returns
     * false => lga does not exists
     * true => lga exists
     * */
    private boolean lgaExists(String code) {
        SQLiteDatabase db = OpenDb();
        Cursor cursor = db.rawQuery("SELECT " + KEY_CODE + " FROM " + TABLE_NAME + " WHERE " + KEY_CODE + " = '" + code + "'", null);
        if (cursor.moveToFirst()) {
            return true;
        }
        return false;
    }

    public void deleteLgas(){
        SQLiteDatabase db = OpenDb();
        db.delete(TABLE_NAME, null ,null);
        db.close();
    }
}
