package com.example.myapplication6;

import android.graphics.Bitmap;

public class KitapDetayi {
    private String kitapAdi, kitapYazari, kitapYorumu;
    private Bitmap kitapResimi;

    public KitapDetayi(String kitapAdi, String kitapYazari, String kitapYorumu, Bitmap kitapResimi) {
        this.kitapAdi = kitapAdi;
        this.kitapYazari = kitapYazari;
        this.kitapYorumu = kitapYorumu;
        this.kitapResimi = kitapResimi;
    }

    public String getKitapAdi() {
        return kitapAdi;
    }

    public String getKitapYazari() {
        return kitapYazari;
    }

    public String getKitapYorumu() {
        return kitapYorumu;
    }

    public Bitmap getKitapResimi() {
        return kitapResimi;
    }
}
