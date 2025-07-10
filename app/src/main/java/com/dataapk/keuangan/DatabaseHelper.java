package com.dataapk.keuangan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "finance.db";
    private static final int DATABASE_VERSION = 1;

    // Table structure
    private static final String TABLE_TRANSACTIONS = "transactions";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_DATE = "date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_TRANSACTIONS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_TITLE + " TEXT," +
                COLUMN_AMOUNT + " REAL," +
                COLUMN_TYPE + " TEXT," +
                COLUMN_CATEGORY + " TEXT," +
                COLUMN_DATE + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        onCreate(db);
    }

    // CRUD Operations
    public long addTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, transaction.getTitle());
        values.put(COLUMN_AMOUNT, transaction.getAmount());
        values.put(COLUMN_TYPE, transaction.getType());
        values.put(COLUMN_CATEGORY, transaction.getCategory());
        values.put(COLUMN_DATE, transaction.getDate());

        long id = db.insert(TABLE_TRANSACTIONS, null, values);
        db.close();
        return id;
    }

    // Method untuk update transaction yang hilang
    public boolean updateTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, transaction.getTitle());
        values.put(COLUMN_AMOUNT, transaction.getAmount());
        values.put(COLUMN_TYPE, transaction.getType());
        values.put(COLUMN_CATEGORY, transaction.getCategory());
        values.put(COLUMN_DATE, transaction.getDate());

        // Update the row, returns the number of affected rows
        int result = db.update(TABLE_TRANSACTIONS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(transaction.getId())});
        db.close();

        // Return true if update was successful (affected rows > 0)
        return result > 0;
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_TRANSACTIONS + " ORDER BY " + COLUMN_ID + " DESC";

        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery(query, null)) {

            if (cursor.moveToFirst()) {
                do {
                    Transaction transaction = new Transaction();
                    transaction.setId(cursor.getInt(0));
                    transaction.setTitle(cursor.getString(1));
                    transaction.setAmount(cursor.getDouble(2));
                    transaction.setType(cursor.getString(3));
                    transaction.setCategory(cursor.getString(4));
                    transaction.setDate(cursor.getString(5));
                    transactions.add(transaction);
                } while (cursor.moveToNext());
            }
        }
        return transactions;
    }

    // Method untuk mendapatkan single transaction berdasarkan ID
    public Transaction getTransaction(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Transaction transaction = null;

        try (Cursor cursor = db.query(TABLE_TRANSACTIONS, null, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                transaction = new Transaction();
                transaction.setId(cursor.getInt(0));
                transaction.setTitle(cursor.getString(1));
                transaction.setAmount(cursor.getDouble(2));
                transaction.setType(cursor.getString(3));
                transaction.setCategory(cursor.getString(4));
                transaction.setDate(cursor.getString(5));
            }
        }
        db.close();
        return transaction;
    }

    public double getTotalIncome() {
        String query = "SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_TRANSACTIONS +
                " WHERE " + COLUMN_TYPE + " = 'Income'";
        return getSumFromQuery(query);
    }

    public double getTotalExpense() {
        String query = "SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_TRANSACTIONS +
                " WHERE " + COLUMN_TYPE + " = 'Expense'";
        return getSumFromQuery(query);
    }

    private double getSumFromQuery(String query) {
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery(query, null)) {
            if (cursor.moveToFirst()) {
                return cursor.getDouble(0);
            }
        }
        return 0;
    }

    public boolean deleteTransaction(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_TRANSACTIONS, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    // Method untuk mendapatkan total count transactions
    public int getTransactionCount() {
        String query = "SELECT COUNT(*) FROM " + TABLE_TRANSACTIONS;
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery(query, null)) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }
        return 0;
    }

    // Method untuk mendapatkan transactions berdasarkan type
    public List<Transaction> getTransactionsByType(String type) {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_TRANSACTIONS +
                " WHERE " + COLUMN_TYPE + " = ? ORDER BY " + COLUMN_ID + " DESC";

        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery(query, new String[]{type})) {

            if (cursor.moveToFirst()) {
                do {
                    Transaction transaction = new Transaction();
                    transaction.setId(cursor.getInt(0));
                    transaction.setTitle(cursor.getString(1));
                    transaction.setAmount(cursor.getDouble(2));
                    transaction.setType(cursor.getString(3));
                    transaction.setCategory(cursor.getString(4));
                    transaction.setDate(cursor.getString(5));
                    transactions.add(transaction);
                } while (cursor.moveToNext());
            }
        }
        return transactions;
    }

    // Method untuk mendapatkan transactions berdasarkan kategori
    public List<Transaction> getTransactionsByCategory(String category) {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_TRANSACTIONS +
                " WHERE " + COLUMN_CATEGORY + " = ? ORDER BY " + COLUMN_ID + " DESC";

        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery(query, new String[]{category})) {

            if (cursor.moveToFirst()) {
                do {
                    Transaction transaction = new Transaction();
                    transaction.setId(cursor.getInt(0));
                    transaction.setTitle(cursor.getString(1));
                    transaction.setAmount(cursor.getDouble(2));
                    transaction.setType(cursor.getString(3));
                    transaction.setCategory(cursor.getString(4));
                    transaction.setDate(cursor.getString(5));
                    transactions.add(transaction);
                } while (cursor.moveToNext());
            }
        }
        return transactions;
    }
}