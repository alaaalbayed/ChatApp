package com.example.areabox.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "chat.db";
    private static final int DATABASE_VERSION = 1;

    // User table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    // Message table
    private static final String TABLE_MESSAGES = "messages";
    public static final String COLUMN_MESSAGE_ID = "message_id";
    public static final String COLUMN_SENDER = "sender";
    private static final String COLUMN_RECEIVER = "receiver";
    public static final String COLUMN_MESSAGE = "message";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTableQuery = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createUserTableQuery);

        String createMessageTableQuery = "CREATE TABLE " + TABLE_MESSAGES + " (" +
                COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SENDER + " TEXT, " +
                COLUMN_RECEIVER + " TEXT, " +
                COLUMN_MESSAGE + " TEXT)";
        db.execSQL(createMessageTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public void registerUser(Register register) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, register.getUsername());
        values.put(COLUMN_EMAIL, register.getEmail());
        values.put(COLUMN_PASSWORD, register.getPassword());
        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public boolean loginUser(UserLogin login) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USERNAME + " = ? OR " + COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {login.getUsername(), login.getUsername()};
        Cursor cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null);

        boolean result = false;

        if (cursor.moveToFirst()) {
            int passwordIndex = cursor.getColumnIndex(COLUMN_PASSWORD);
            if (passwordIndex != -1) {
                String storedPassword = cursor.getString(passwordIndex);
                if (login.getPassword().equals(storedPassword)) {
                    result = true;
                }
            }
        }
        cursor.close();
        db.close();
        return result;
    }

    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public User getUserInfo(String usernameOrEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        String selection = COLUMN_USERNAME + " = ? OR " + COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {usernameOrEmail, usernameOrEmail};
        Cursor cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_USER_ID);
            int usernameIndex = cursor.getColumnIndex(COLUMN_USERNAME);
            int emailIndex = cursor.getColumnIndex(COLUMN_EMAIL);

            int userId = cursor.getInt(idIndex);
            String username = cursor.getString(usernameIndex);
            String email = cursor.getString(emailIndex);

            user = new User(userId, username, email);
        }

        cursor.close();
        db.close();

        return user;
    }

    public void insertMessage(String sender, String receiver, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SENDER, sender);
        values.put(COLUMN_RECEIVER, receiver);
        values.put(COLUMN_MESSAGE, message);
        db.insert(TABLE_MESSAGES, null, values);
        db.close();
    }

    public Cursor getMessages(String sender, String recipient) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = "(" + COLUMN_SENDER + "=? AND " + COLUMN_RECEIVER + "=?) OR (" +
                COLUMN_SENDER + "=? AND " + COLUMN_RECEIVER + "=?)";
        String[] selectionArgs = {sender, recipient, recipient, sender};

        return db.query(TABLE_MESSAGES, null, selection, selectionArgs, null, null, null);
    }

    public void updateMessage(int messageId, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MESSAGE, message);
        String whereClause = COLUMN_MESSAGE_ID + " = ?";
        String[] whereArgs = {String.valueOf(messageId)};
        db.update(TABLE_MESSAGES, values, whereClause, whereArgs);
        db.close();
    }

    public void deleteMessage(int messageId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_MESSAGE_ID + " = ?";
        String[] whereArgs = {String.valueOf(messageId)};
        db.delete(TABLE_MESSAGES, whereClause, whereArgs);
        db.close();
    }
}
