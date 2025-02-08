package com.example.myapplication6.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Kitaplar.db";
    private static final int DATABASE_VERSION = 4;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Kitaplar tablosu
        db.execSQL("CREATE TABLE " + KitapTuruContract.KitaplarEntry.TABLE_NAME + " ("
                + KitapTuruContract.KitaplarEntry._ID + " INTEGER PRIMARY KEY, "
                + KitapTuruContract.KitaplarEntry.COLUMN_KITAP_ADI + " TEXT, "
                + KitapTuruContract.KitaplarEntry.COLUMN_KITAP_YAZARI + " TEXT, "
                + KitapTuruContract.KitaplarEntry.COLUMN_KITAP_YORUM + " TEXT, "
                + KitapTuruContract.KitaplarEntry.COLUMN_KITAP_RESMI + " BLOB, "
                + KitapTuruContract.KitaplarEntry.COLUMN_USER_ID + " INTEGER)"
        );

        db.execSQL("CREATE TABLE users ("

                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT UNIQUE, "
                + "password TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + KitapTuruContract.KitaplarEntry.TABLE_NAME + " ADD COLUMN " + KitapTuruContract.KitaplarEntry.COLUMN_USER_ID + " INTEGER"); //Eski sürümlerde kitaplar tablosu user_id sütununa sahip değilse veritabanını güncellemek için çalışır.

        }
    }
}
