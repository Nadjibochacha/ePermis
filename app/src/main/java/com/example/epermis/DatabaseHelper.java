package com.example.epermis;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ePermis.db";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_USERS = "users";
    public static final String TABLE_REQUESTS = "requests";
    public static final String KEY_ID = "id";
    public static final String KEY_USER_NAME = "full_name";
    public static final String KEY_USER_EMAIL = "email";
    public static final String KEY_USER_PASSWORD = "password";
    public static final String KEY_USER_ROLE = "role";
    public static final String KEY_REQ_USER_ID = "user_id";
    public static final String KEY_REQ_TYPE = "project_type";
    public static final String KEY_REQ_ADDRESS = "address";
    public static final String KEY_REQ_STATUS = "status";
    public static final String KEY_REQ_NAME = "applicant_name";
    public static final String KEY_REQ_DATE = "birth_date";
    public static final String KEY_REQ_PHONE = "phone";
    public static final String KEY_REQ_FILE = "file_path";


    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USER_NAME + " TEXT,"
            + KEY_USER_EMAIL + " TEXT UNIQUE,"
            + KEY_USER_PASSWORD + " TEXT,"
            + KEY_USER_ROLE + " TEXT" + ")";
    private static final String CREATE_TABLE_REQUESTS = "CREATE TABLE " + TABLE_REQUESTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_REQ_NAME + " TEXT,"
            + KEY_REQ_DATE + " TEXT,"
            + KEY_REQ_PHONE + " TEXT,"
            + KEY_REQ_TYPE + " TEXT,"
            + KEY_REQ_ADDRESS + " TEXT,"
            + KEY_REQ_FILE + " TEXT,"
            + KEY_REQ_STATUS + " TEXT DEFAULT 'En attente',"
            + KEY_REQ_USER_ID + " INTEGER" + ")";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating required tables
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_REQUESTS);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REQUESTS);
        onCreate(db);
    }
    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT " + KEY_ID + " AS _id, " + KEY_USER_NAME + ", " + KEY_USER_EMAIL + ", " + KEY_USER_ROLE + " FROM " + TABLE_USERS, null);
    }
    public void deleteUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, KEY_ID + " = ?", new String[]{String.valueOf(id)});
    }
    public void deleteRequest(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REQUESTS, KEY_ID + " = ?", new String[]{String.valueOf(id)});
    }
    public boolean updateUser(int id, String name, String email, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, name);
        values.put(KEY_USER_EMAIL, email);
        values.put(KEY_USER_ROLE, role);
        return db.update(TABLE_USERS, values, KEY_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }
}