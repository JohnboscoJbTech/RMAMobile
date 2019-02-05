package jbtechventures.com.rtma.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class manages the creation of database and tables
 * It also manages the updating of databases
 * it is extended by all classes that will query the database in order to access the database
 * */
public class DataAdapter {

    private static DbManager dbManager;
    private Context context;

    private static final String CREATE_PERSON_TABL = "CREATE TABLE IF NOT EXISTS person (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "lastname TEXT, firstname TEXT, code TEXT, phone TEXT, userId TEXT, email TEXT, confirmed INTEGER)";
    private static final String CREATE_PERSON_TABLE = "CREATE TABLE IF NOT EXISTS person (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "username TEXT, password TEXT, userId INTEGER, logged_in INTEGER)";
    private static final String CREATE_ELECTION_TABLE = "CREATE TABLE IF NOT EXISTS election (_id INTEGER, " +
            "name TEXT, description TEXT, active INTEGER, start_date TEXT, end_date TEXT)";
    private static final String CREATE_RESULT_DATA_TABLE = "CREATE TABLE IF NOT EXISTS result (_id INTEGER PRIMARY KEY AUTOINCREMENT, module INTEGER, unit TEXT," +
            "reg_votes INTEGER, accred_votes INTEGER, cast_votes INTEGER, inv_votes INTEGER, proof_image BLOB, proof_image_path TEXT, create_date TEXT, " +
            "update_date TEXT, synced INTEGER, completed INTEGER, sync_error_text TEXT, discarded INTEGER, user_id INTEGER)";
    private static final String CREATE_VOTES_TABLE = "CREATE TABLE IF NOT EXISTS votes (_id INTEGER PRIMARY KEY AUTOINCREMENT, count INTEGER, result_id INTEGER," +
            "party_id INTEGER, module_id INTEGER)";
    private static final String CREATE_PARTY_TABLE = "CREATE TABLE IF NOT EXISTS party (party_id INTEGER, name TEXT, code TEXT)";
    private static final String CREATE_POLLING_TABLE = "CREATE TABLE IF NOT EXISTS polling (state TEXT, state_code TEXT, lga TEXT, lga_code TEXT," +
            "ward TEXT, ward_code TEXT, polling_unit TEXT, polling_unit_code TEXT)";
    private static final String CREATE_COMPLAINTS_TABLE = "CREATE TABLE IF NOT EXISTS complaint (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "polling_unit TEXT, user_id INTEGER, complain TEXT, title TEXT, election_id INTEGER, complain_image_path TEXT, synced INTEGER, " +
            "date TEXT)";

    public DataAdapter(Context _context) {
        context = _context.getApplicationContext();
    }

    protected SQLiteDatabase OpenDb() {
        if (dbManager == null) {
            dbManager = new DbManager(context);
        }
        return dbManager.getWritableDatabase();
    }

    protected SQLiteDatabase OpenReadableDb() {
        if (dbManager == null) {
            dbManager = new DbManager(context);
        }
        return dbManager.getReadableDatabase();
    }

    public void CloseDb() {
        dbManager.close();
    }

    public static class DbManager extends SQLiteOpenHelper {
        private static String DATABASE_NAME = "Rtma_Db";
        private static int DATABASE_VERSION = 1;

        private DbManager(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_PERSON_TABLE);
            db.execSQL(CREATE_ELECTION_TABLE);
            db.execSQL(CREATE_PARTY_TABLE);
            db.execSQL(CREATE_RESULT_DATA_TABLE);
            db.execSQL(CREATE_VOTES_TABLE);
            db.execSQL(CREATE_POLLING_TABLE);
            db.execSQL(CREATE_COMPLAINTS_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS person");
            db.execSQL("DROP TABLE IF EXISTS election");
            db.execSQL("DROP TABLE IF EXISTS result");
            db.execSQL("DROP TABLE IF EXISTS votes");
            db.execSQL("DROP TABLE IF EXISTS party");
            db.execSQL("DROP TABLE IF EXISTS polling");
            db.execSQL("DROP TABLE IF EXISTS complaint");

            onCreate(db);
        }
    }
}
