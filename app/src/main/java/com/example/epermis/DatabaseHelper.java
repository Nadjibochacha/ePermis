package com.example.epermis;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "ePermis.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_REQUESTS = "requests";

    // Common Column Names
    public static final String KEY_ID = "id";

    // Users Table Columns
    public static final String KEY_USER_NAME = "full_name";
    public static final String KEY_USER_EMAIL = "email";
    public static final String KEY_USER_PASSWORD = "password";
    public static final String KEY_USER_ROLE = "role"; // Citoyen, Agent, Admin

    // Requests Table Columns
    public static final String KEY_REQ_USER_ID = "user_id"; // Foreign Key
    public static final String KEY_REQ_TYPE = "project_type"; // Maison, Immeuble, etc.
    public static final String KEY_REQ_ADDRESS = "address";
    public static final String KEY_REQ_STATUS = "status"; // En attente, Acceptée, Refusée

    // Table Create Statements
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USER_NAME + " TEXT,"
            + KEY_USER_EMAIL + " TEXT UNIQUE,"
            + KEY_USER_PASSWORD + " TEXT,"
            + KEY_USER_ROLE + " TEXT" + ")";

    private static final String CREATE_TABLE_REQUESTS = "CREATE TABLE " + TABLE_REQUESTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_REQ_USER_ID + " INTEGER,"
            + KEY_REQ_TYPE + " TEXT,"
            + KEY_REQ_ADDRESS + " TEXT,"
            + KEY_REQ_STATUS + " TEXT DEFAULT 'En attente',"
            + "FOREIGN KEY(" + KEY_REQ_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_ID + ")" + ")";

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
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REQUESTS);
        // Create tables again
        onCreate(db);
    }
}