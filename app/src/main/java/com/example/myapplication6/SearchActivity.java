package com.example.myapplication6;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication6.data.DatabaseHelper;
import com.example.myapplication6.data.KitapTuruContract;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private KitapAdapter adapter;
    private ArrayList<Kitap> kitapList;
    private ArrayList<Kitap> filteredList;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.search_activity_recyclerView);
        searchEditText = findViewById(R.id.search_activity_editTextSearch);

        kitapList = getUserBooks();
        filteredList = new ArrayList<>(kitapList);

        adapter = new KitapAdapter(filteredList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); //verileri dikey liste şeklinde görüntülemek için
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new KitapAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Kitap kitap) {
                MainActivity.kitapDetayi = new KitapDetayi( //MainActivity'nin kitapDetayi nesnesine bu searchde tıklanan kitabın verilerini atıyor sonra detay activite de bu main aktivitede ki kitapDetayi nesnesinin verileri görüntüleniyor
                        kitap.getKitapAdi(),
                        kitap.getKitapYazari(),
                        kitap.getKitapYorumu(),
                        kitap.getKitapResim()
                );
                Intent intent = new Intent(SearchActivity.this, DetayActivity.class);
                startActivity(intent);
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { //Kullanıcı her harf yazdığında çalışır,metni değiştirdiğinde
                filteredList.clear();                                                                                                   // Önceki filtrelenmiş listeyi temizler.
                for (Kitap kitap : kitapList) { // Tüm kitapları tek tek dolaşır.
                    if (kitap.getKitapAdi().toLowerCase().contains(s.toString().toLowerCase())) {
                        filteredList.add(kitap);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private ArrayList<Kitap> getUserBooks() {
        ArrayList<Kitap> kitapList = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = KitapTuruContract.KitaplarEntry.COLUMN_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(LoginActivity.currentUserID)};

        Cursor cursor = db.query(
                KitapTuruContract.KitaplarEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    String kitapAdi = cursor.getString(cursor.getColumnIndexOrThrow(KitapTuruContract.KitaplarEntry.COLUMN_KITAP_ADI)); //Sütundan kitap adı alır. o indexe ait hücredeki değeri alır.
                    String kitapYazari = cursor.getString(cursor.getColumnIndexOrThrow(KitapTuruContract.KitaplarEntry.COLUMN_KITAP_YAZARI));
                    String kitapYorumu = cursor.getString(cursor.getColumnIndexOrThrow(KitapTuruContract.KitaplarEntry.COLUMN_KITAP_YORUM));
                    byte[] resimBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(KitapTuruContract.KitaplarEntry.COLUMN_KITAP_RESMI));
                    Bitmap kitapResim = BitmapFactory.decodeByteArray(resimBytes, 0, resimBytes.length);
                    kitapList.add(new Kitap(kitapAdi, kitapYazari, kitapYorumu, kitapResim));
                }
            } finally {
                cursor.close();
            }
        }

        return kitapList;
    }
}
