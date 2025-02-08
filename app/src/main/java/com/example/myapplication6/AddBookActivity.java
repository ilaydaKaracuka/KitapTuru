package com.example.myapplication6;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.myapplication6.data.DatabaseHelper;
import com.example.myapplication6.data.KitapTuruContract.KitaplarEntry;
import com.google.android.material.snackbar.Snackbar;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddBookActivity extends AppCompatActivity {
    private EditText editTextKitapIsmi, editTextKitapYazari, editTextKitapYorum;
    private ImageView imgKitapResim;
    private Button btnKaydet;
    private String kitapIsmi, kitapYazari, kitapYorum;
    private Bitmap secilenResim;

    private ActivityResultLauncher<Intent> resimSecici; //Resim seçme işlemini başlatmak ve sonucunu almak için.
    private ActivityResultLauncher<String> izinIstemeci;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        editTextKitapIsmi = findViewById(R.id.add_book_activity_editTextKitapIsmi);
        editTextKitapYorum = findViewById(R.id.add_book_activity_editTextKitapYorum);
        editTextKitapYazari = findViewById(R.id.add_book_activity_editTextKitapYazari);
        imgKitapResim = findViewById(R.id.add_book_activity_imageViewKitapResmi);
        btnKaydet = findViewById(R.id.add_book_activity_btnKaydet);

        resimSecici = registerForActivityResult(  //Resim seçme işlemi için başlatılır.
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri resimUri = result.getData().getData(); //result.getData(), aktivitenin sonucunda dönen veriyi alır. getData().getData(): spesifik seçilen öğenin URI'sini alır.

                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                ImageDecoder.Source resimSource = ImageDecoder.createSource(this.getContentResolver(), resimUri);

                                secilenResim = ImageDecoder.decodeBitmap(resimSource);
                                imgKitapResim.setImageBitmap(secilenResim);
                            } else {
                                secilenResim = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resimUri);
                                imgKitapResim.setImageBitmap(secilenResim);
                            }
                            btnKaydet.setEnabled(true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        izinIstemeci = registerForActivityResult( //izin isteme işlemi için başlatılır.
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        resimSec();
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), "Resim seçmek için izin gerekli!", Snackbar.LENGTH_SHORT).show();
                    }
                });

        btnKaydet.setOnClickListener(this::kitapKaydet);
    }

    public void kitapKaydet(View v) {
        kitapIsmi = editTextKitapIsmi.getText().toString().trim();
        kitapYazari = editTextKitapYazari.getText().toString().trim();
        kitapYorum = editTextKitapYorum.getText().toString().trim();

        if (TextUtils.isEmpty(kitapIsmi) || TextUtils.isEmpty(kitapYazari) || TextUtils.isEmpty(kitapYorum)) {
            Snackbar.make(findViewById(android.R.id.content), "Tüm alanları doldurun.", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (secilenResim == null) {
            Snackbar.make(findViewById(android.R.id.content), "Lütfen bir resim seçin.", Snackbar.LENGTH_SHORT).show();
            return;
        }

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); //Resmi byte dizisine dönüştürmek için
            secilenResim.compress(Bitmap.CompressFormat.PNG, 70, outputStream);
            byte[] kayitEdilecekResim = outputStream.toByteArray(); //sıkıştırılmış resmi byte dizisine dönüştürür ve kayitEdilecekResim değişkenine atar.

            DatabaseHelper helper = new DatabaseHelper(this);
            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KitaplarEntry.COLUMN_KITAP_ADI, kitapIsmi);
            values.put(KitaplarEntry.COLUMN_KITAP_YAZARI, kitapYazari);
            values.put(KitaplarEntry.COLUMN_KITAP_YORUM, kitapYorum);
            values.put(KitaplarEntry.COLUMN_KITAP_RESMI, kayitEdilecekResim);
            values.put(KitaplarEntry.COLUMN_USER_ID, LoginActivity.currentUserID);

            long yeniSatirId = db.insert(KitaplarEntry.TABLE_NAME, null, values);
            if (yeniSatirId != -1) {
                Snackbar.make(findViewById(android.R.id.content), "Kitap başarıyla kaydedildi.", Snackbar.LENGTH_SHORT).show();
                editTextKitapIsmi.setText("");
                editTextKitapYazari.setText("");
                editTextKitapYorum.setText("");
                imgKitapResim.setImageResource(R.drawable.resim5);
            } else {
                Snackbar.make(findViewById(android.R.id.content), "Kaydetme sırasında bir hata oluştu.", Snackbar.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Snackbar.make(findViewById(android.R.id.content), "Bir hata oluştu: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
        }
    }

    public void resimSec(View v) { //addbookactivity.xml de ki görsel seçmek için olan imageviewa tıklanırsa
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                //cihazın READ_MEDIA_IMAGES izni olup olmadığını kontrol eder. Eğer izin verilmemişse, izinIstemeci.launch(Manifest.permission.READ_MEDIA_IMAGES) ile izin istemi başlatılır.
                izinIstemeci.launch(Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                resimSec();
            }
        }
        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                izinIstemeci.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                resimSec();
            }
        }
    }

    private void resimSec() {
        Intent resimiAl = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        resimSecici.launch(resimiAl);
    }
}
