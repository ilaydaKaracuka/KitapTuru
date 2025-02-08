package com.example.myapplication6;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.example.myapplication6.data.DatabaseHelper;
import com.example.myapplication6.data.KitapTuruContract.KitaplarEntry;
import java.util.ArrayList;

public class Kitap {
    //bu class uygulamada ki kitap verilerini temsil etmek için kullanılmaktadır. Veritabanından kitapları çekip, bu kitapların özelliklerine (adı, yazarı, yorumu, resmi) göre bir liste oluşturur.
    private String kitapAdi, kitapYazari, kitapYorumu;
    private Bitmap kitapResim;

    public Kitap(){}

    public Kitap(String kitapAdi, String kitapYazari, String kitapYorumu, Bitmap kitapResim) {
        this.kitapAdi = kitapAdi;
        this.kitapYazari = kitapYazari;
        this.kitapYorumu = kitapYorumu;
        this.kitapResim = kitapResim;
    }

    public String getKitapAdi() {
        return kitapAdi;
    }

    public void setKitapAdi(String kitapAdi) {
        this.kitapAdi = kitapAdi;
    }

    public String getKitapYazari() {
        return kitapYazari;
    }

    public void setKitapYazari(String kitapYazari) {
        this.kitapYazari = kitapYazari;
    }

    public String getKitapYorumu() {
        return kitapYorumu;
    }

    public void setKitapYorumu(String kitapYorumu) {
        this.kitapYorumu = kitapYorumu;
    }

    public Bitmap getKitapResim() {
        return kitapResim;
    }

    public void setKitapResim(Bitmap kitapResim) {
        this.kitapResim = kitapResim;
    }

    static public ArrayList<Kitap> getData(Context context)
    {
        ArrayList<Kitap> kitapList = new ArrayList<>();

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM " + KitaplarEntry.TABLE_NAME, null);

        int kitapAdiIndex = cursor.getColumnIndex(KitaplarEntry.COLUMN_KITAP_ADI);
        int kitapYazariIndex = cursor.getColumnIndex(KitaplarEntry.COLUMN_KITAP_YAZARI);
        int kitapYorumuIndex = cursor.getColumnIndex(KitaplarEntry.COLUMN_KITAP_YORUM);
        int kitapResimIndex = cursor.getColumnIndex(KitaplarEntry.COLUMN_KITAP_RESMI);

        while (cursor.moveToNext()) {
            String kitapAdi = cursor.getString(kitapAdiIndex);
            String kitapYazari = cursor.getString(kitapYazariIndex);
            String kitapYorumu = cursor.getString(kitapYorumuIndex);

            byte[] gelenResimByte = cursor.getBlob(kitapResimIndex);                  // Resim verisini byte dizisi olarak alıyoruz ve Android de kullanabilmek için Bitmap'e çeviriyoruz
            Bitmap kitapResim = BitmapFactory.decodeByteArray(gelenResimByte, 0, gelenResimByte.length);

            Kitap kitap = new Kitap();
            kitap.setKitapAdi(kitapAdi);
            kitap.setKitapYazari(kitapYazari);
            kitap.setKitapYorumu(kitapYorumu);
            kitap.setKitapResim(kitapResim);

            kitapList.add(kitap); //kitap nesnesini listeye ekledikten sonra cursor.moveToNext() old. için diğer satıra geçer aynı işlemleri yapar
        }

        cursor.close();

        return kitapList;
    }

}


