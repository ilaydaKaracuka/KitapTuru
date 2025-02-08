package com.example.myapplication6;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication6.data.DatabaseHelper;
import com.example.myapplication6.data.KitapTuruContract;
import com.google.android.material.snackbar.Snackbar;

public class DetayActivity extends AppCompatActivity {
    private ImageView imgKitapResimi;
    private TextView txtKitapAdi, txtKitapYazari, txtKitapYorumu;
    private Button btnUpdate, btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detay);

        imgKitapResimi = findViewById(R.id.detay_activity_imageViewKitapResim);
        txtKitapAdi = findViewById(R.id.detay_activity_textViewKitapAdi);
        txtKitapYazari = findViewById(R.id.detay_activity_textViewKitapYazari);
        txtKitapYorumu = findViewById(R.id.detay_activity_textViewKitapYorumu);
        btnUpdate = findViewById(R.id.detay_activity_buttonUpdate);
        btnDelete = findViewById(R.id.detay_activity_buttonDelete);


        txtKitapAdi.setText(MainActivity.kitapDetayi.getKitapAdi());
        txtKitapYazari.setText(MainActivity.kitapDetayi.getKitapYazari());
        txtKitapYorumu.setText(MainActivity.kitapDetayi.getKitapYorumu());
        imgKitapResimi.setImageBitmap(MainActivity.kitapDetayi.getKitapResimi());


        btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(this, GuncelleActivity.class);
            startActivity(intent);
        });


        btnDelete.setOnClickListener(v -> silKitabi());
    }

    private void silKitabi() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int silinenSatir = db.delete(
                KitapTuruContract.KitaplarEntry.TABLE_NAME,
                KitapTuruContract.KitaplarEntry.COLUMN_KITAP_ADI + " = ?",
                new String[]{MainActivity.kitapDetayi.getKitapAdi()}
        );

        if (silinenSatir > 0) {

            Snackbar.make(findViewById(android.R.id.content), "Kitap başarıyla silindi.", Snackbar.LENGTH_SHORT)
                    .show();
        } else {

            Snackbar.make(findViewById(android.R.id.content), "Silme işlemi başarısız oldu.", Snackbar.LENGTH_SHORT)
                    .show();
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
