package jbtechventures.com.rtma.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import jbtechventures.com.rtma.Data.DataAdapter;
import jbtechventures.com.rtma.Model.Lga;
import jbtechventures.com.rtma.Model.PollingUnit;

public class PollingRepository extends DataAdapter {

    private static final String TABLE_NAME = "polling";
    private static final String KEY_STATE = "state";
    private static final String KEY_STATE_CODE= "state_code";
    private static final String KEY_LGA = "lga";
    private static final String KEY_LGA_CODE = "lga_code";
    private static final String KEY_WARD = "ward";
    private static final String KEY_WARD_CODE = "ward_code";
    private static final String KEY_POLLING_UNIT = "polling_unit";
    private static final String KEY_POLLING_UNIT_CODE = "polling_unit_code";

    public PollingRepository(Context _context) {
        super(_context);
    }

    /**
     * Method adds polling unit
     * returns null
     * */
    public void addPollingUnit(PollingUnit pollingUnit) {
        SQLiteDatabase db = OpenDb();;
        ContentValues values = new ContentValues();

        values.put(KEY_STATE, pollingUnit.State);
        values.put(KEY_STATE_CODE, pollingUnit.StateCode);
        values.put(KEY_LGA, pollingUnit.Lga);
        values.put(KEY_LGA_CODE, pollingUnit.LgaCode);
        values.put(KEY_WARD, pollingUnit.Ward);
        values.put(KEY_WARD_CODE, pollingUnit.WardCode);
        values.put(KEY_POLLING_UNIT, pollingUnit.PollingUnit);
        values.put(KEY_POLLING_UNIT_CODE, pollingUnit.PollingUnitCode);

        /*Cursor cursor = db.rawQuery("SELECT " + KEY_POLLING_UNIT_CODE + " FROM " + TABLE_NAME + " WHERE " + KEY_POLLING_UNIT_CODE + " = '" + pollingUnit.PollingUnitCode + "'", null);
        if (cursor.moveToFirst()) {
            db.update(TABLE_NAME, values, KEY_POLLING_UNIT_CODE + "=?", new String[]{pollingUnit.PollingUnitCode});
        }
        else{*/
            db.insert(TABLE_NAME, null, values);

        //}
        if(db.isOpen())
            db.close();
        values.clear();
    }

    public void addPollingUnits(ArrayList<PollingUnit> pollingUnits) {
        SQLiteDatabase db = OpenDb();;
        ContentValues values = new ContentValues();

        for (PollingUnit pollingUnit: pollingUnits) {
            values.put(KEY_STATE, pollingUnit.State);
            values.put(KEY_STATE_CODE, pollingUnit.StateCode);
            values.put(KEY_LGA, pollingUnit.Lga);
            values.put(KEY_LGA_CODE, pollingUnit.LgaCode);
            values.put(KEY_WARD, pollingUnit.Ward);
            values.put(KEY_WARD_CODE, pollingUnit.WardCode);
            values.put(KEY_POLLING_UNIT, pollingUnit.PollingUnit);
            values.put(KEY_POLLING_UNIT_CODE, pollingUnit.PollingUnitCode);

            db.insert(TABLE_NAME, null, values);
        }
        if(db.isOpen())
            db.close();
        values.clear();
    }

    /**
     * Method gets all polling units
     * returns null
     * */
    public ArrayList<Lga> getPollingUnits() {
        SQLiteDatabase db = OpenDb();
        ArrayList<Lga> lgas = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                Lga lga = new Lga();

                /*lga.Name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                lga.Code = cursor.getString(cursor.getColumnIndex(KEY_CODE));*/

                lgas.add(lga);
            }while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return lgas;
    }

    /**
     * Method checks if a polling unit exists
     * returns
     * false => polling unit does not exists
     * true => polling unit exists
     * */
    private boolean pollingUnitExists(String code) {
        SQLiteDatabase db = OpenDb();
        Cursor cursor = db.rawQuery("SELECT " + KEY_POLLING_UNIT_CODE + " FROM " + TABLE_NAME + " WHERE " + KEY_POLLING_UNIT_CODE + " = '" + code + "'", null);
        if (cursor.moveToFirst()) {
            db.close();
            return true;
        }
        db.close();
        return false;
    }

    public void deletePollingUnits(){
        SQLiteDatabase db = OpenDb();
        db.delete(TABLE_NAME, null ,null);
        db.close();
    }

    /**
     * Method gets all lgas in the current state
     * returns null
     * */
    public ArrayList<Lga> getLgas() {
        SQLiteDatabase db = OpenDb();
        ArrayList<Lga> lgas = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + KEY_LGA + "," + KEY_LGA_CODE + " FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                Lga lga = new Lga();

                lga.Name = cursor.getString(cursor.getColumnIndex(KEY_LGA));
                lga.Code = cursor.getString(cursor.getColumnIndex(KEY_LGA_CODE));

                lgas.add(lga);
            }while (cursor.moveToNext());
            cursor.close();
        }
        //db.close();
        return lgas;
    }

    /**
     * Method gets all wards in the current lga
     * returns null
     * */
    public ArrayList<Lga> getWards(String lgaCode) {
        SQLiteDatabase db = OpenDb();
        ArrayList<Lga> lgas = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + KEY_WARD + "," + KEY_WARD_CODE + " FROM " + TABLE_NAME + " WHERE " + KEY_LGA_CODE
                + " = '" + lgaCode + "'", null);

        if (cursor.moveToFirst()) {
            do {
                Lga lga = new Lga();

                lga.Name = cursor.getString(cursor.getColumnIndex(KEY_WARD));
                lga.Code = cursor.getString(cursor.getColumnIndex(KEY_WARD_CODE));

                lgas.add(lga);
            }while (cursor.moveToNext());
            cursor.close();
        }
        //db.close();
        return lgas;
    }

    /**
     * Method gets all wards in the current lga
     * returns null
     * */
    public ArrayList<Lga> getPus(String wardCode) {
        SQLiteDatabase db = OpenDb();
        ArrayList<Lga> lgas = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + KEY_POLLING_UNIT + "," + KEY_POLLING_UNIT_CODE + " FROM " + TABLE_NAME + " WHERE " + KEY_WARD_CODE
                + " = '" + wardCode + "'", null);

        if (cursor.moveToFirst()) {
            do {
                Lga lga = new Lga();

                lga.Name = cursor.getString(cursor.getColumnIndex(KEY_POLLING_UNIT));
                lga.Code = cursor.getString(cursor.getColumnIndex(KEY_POLLING_UNIT_CODE));

                lgas.add(lga);
            }while (cursor.moveToNext());
            cursor.close();
        }
        //db.close();
        return lgas;
    }

    /**
     * Method gets polling units based on the code pass as parameter
     * returns null
     * */
    public PollingUnit getPollingUnit(String code) {
        SQLiteDatabase db = OpenDb();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_POLLING_UNIT_CODE + " = '" + code + "'", null);

        if (cursor.moveToFirst()) {
            PollingUnit pollingUnit = new PollingUnit();
            pollingUnit.State = cursor.getString(cursor.getColumnIndex(KEY_STATE));
            pollingUnit.StateCode = cursor.getString(cursor.getColumnIndex(KEY_STATE_CODE));
            pollingUnit.Lga = cursor.getString(cursor.getColumnIndex(KEY_LGA));
            pollingUnit.LgaCode = cursor.getString(cursor.getColumnIndex(KEY_LGA_CODE));
            pollingUnit.Ward = cursor.getString(cursor.getColumnIndex(KEY_WARD));
            pollingUnit.WardCode = cursor.getString(cursor.getColumnIndex(KEY_WARD_CODE));
            pollingUnit.PollingUnit = cursor.getString(cursor.getColumnIndex(KEY_POLLING_UNIT));
            pollingUnit.PollingUnitCode = cursor.getString(cursor.getColumnIndex(KEY_POLLING_UNIT_CODE));

            cursor.close();
            db.close();
            return pollingUnit;
        }
        cursor.close();
        db.close();
        return null;
    }

}
