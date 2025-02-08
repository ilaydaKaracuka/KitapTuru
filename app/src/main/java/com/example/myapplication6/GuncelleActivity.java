package com.example.myapplication6;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication6.data.DatabaseHelper;
import com.example.myapplication6.data.KitapTuruContract;
import com.google.android.material.snackbar.Snackbar;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class GuncelleActivity extends AppCompatActivity {
    private EditText editTextKitapAdi, editTextKitapYazari, editTextKitapYorumu;
    private ImageView imgKitapResim;
    private Button btnKaydet;
    private Bitmap yeniGorsel;

    private ActivityResultLauncher<Intent> resimSecici;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guncelle);

        editTextKitapAdi = findViewById(R.id.guncelle_activity_editTextKitapAdi);
        editTextKitapYazari = findViewById(R.id.guncelle_activity_editTextKitapYazari);
        editTextKitapYorumu = findViewById(R.id.guncelle_activity_editTextKitapYorumu);
        imgKitapResim = findViewById(R.id.guncelle_activity_imageViewKitapResim);
        btnKaydet = findViewById(R.id.guncelle_activity_buttonKaydet);

        editTextKitapAdi.setText(MainActivity.kitapDetayi.getKitapAdi());
        editTextKitapYazari.setText(MainActivity.kitapDetayi.getKitapYazari());
        editTextKitapYorumu.setText(MainActivity.kitapDetayi.getKitapYorumu());
        imgKitapResim.setImageBitmap(MainActivity.kitapDetayi.getKitapResimi());

        resimSecici = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri resimUri = result.getData().getData();
                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), resimUri);//Resmin uri'sini ImageDecoder.Source'a çevirir
                                yeniGorsel = ImageDecoder.decodeBitmap(source);
                            } else {
                                yeniGorsel = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resimUri); //URI ile belirtilen görselden Bitmap objesi oluşturur.
                            }
                            imgKitapResim.setImageBitmap(yeniGorsel);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });


        imgKitapResim.setOnClickListener(v -> {
            Intent resimSecIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);   //ACTION_PICK intent türü, kullanıcıya bir öğe seçmesini sağlar. EXTERNAL_CONTENT_URI, resimlere erişim için kullanılan bir URI(cihazın depolama alanındaki resim dosyalarını işaret eder.)
            resimSecici.launch(resimSecIntent); //resimSecici ile resim seçme işlemini başlatır.
        });

        btnKaydet.setOnClickListener(v -> kitapGuncelle());
    }

    private void kitapGuncelle() {
        String yeniAdi = editTextKitapAdi.getText().toString().trim();
        String yeniYazari = editTextKitapYazari.getText().toString().trim();
        String yeniYorum = editTextKitapYorumu.getText().toString().trim();

        if (TextUtils.isEmpty(yeniAdi) || TextUtils.isEmpty(yeniYazari) || TextUtils.isEmpty(yeniYorum)) {
            Snackbar.make(findViewById(android.R.id.content), "Tüm alanları doldurunuz.", Snackbar.LENGTH_SHORT)
                    .show();
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KitapTuruContract.KitaplarEntry.COLUMN_KITAP_ADI, yeniAdi);
        values.put(KitapTuruContract.KitaplarEntry.COLUMN_KITAP_YAZARI, yeniYazari);
        values.put(KitapTuruContract.KitaplarEntry.COLUMN_KITAP_YORUM, yeniYorum);

        if (yeniGorsel != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); //resmi byte dizisi olarak depolamak için
            yeniGorsel.compress(Bitmap.CompressFormat.PNG, 70, outputStream);
            values.put(KitapTuruContract.KitaplarEntry.COLUMN_KITAP_RESMI, outputStream.toByteArray());
        }

        int guncellenenSatirlar = db.update(
                KitapTuruContract.KitaplarEntry.TABLE_NAME,
                values,
                KitapTuruContract.KitaplarEntry.COLUMN_KITAP_ADI + " = ?",
                new String[]{MainActivity.kitapDetayi.getKitapAdi()}
        );

        if (guncellenenSatirlar > 0) {
            Snackbar.make(findViewById(android.R.id.content), "Kitap başarıyla güncellendi", Snackbar.LENGTH_SHORT)
                    .show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Snackbar.make(findViewById(android.R.id.content), "Güncelleme başarısız oldu.", Snackbar.LENGTH_SHORT)
                    .show();
        }
    }
}
