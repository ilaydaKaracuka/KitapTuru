package com.example.myapplication6.data;

import android.provider.BaseColumns;

public class KitapTuruContract {
    public static final class KitaplarEntry implements BaseColumns {
        public static final String TABLE_NAME = "kitaplar";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_KITAP_ADI = "kitapAdi";
        public static final String COLUMN_KITAP_YAZARI = "kitapYazari";
        public static final String COLUMN_KITAP_YORUM = "kitapYorum";
        public static final String COLUMN_KITAP_RESMI = "kitapResmi";
        public static final String COLUMN_USER_ID = "user_id";
    }


}