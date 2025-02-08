package com.example.myapplication6;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication6.data.DatabaseHelper;
import com.example.myapplication6.data.KitapTuruContract;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public Button btn;
    private RecyclerView mRecyclerView;
    private KitapAdapter adapter;
    static public KitapDetayi kitapDetayi;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_menu_add_book) {
            Intent addBookIntent = new Intent(this, AddBookActivity.class);
            startActivity(addBookIntent);
            return true;
        } else if (item.getItemId() == R.id.action_search) {
            Intent searchIntent = new Intent(this, SearchActivity.class);
            startActivity(searchIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.main_activity_recyclerView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshBookList();
    }

    private void setupRecyclerView() {
        adapter = new KitapAdapter(getUserBooks(), this);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager manager = new GridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(new GridManagerDecoration());
        mRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new KitapAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Kitap kitap) {
                kitapDetayi = new KitapDetayi(kitap.getKitapAdi(), kitap.getKitapYazari(), kitap.getKitapYorumu(), kitap.getKitapResim()); //Kitap classından kitabın bilgileri alınır
                Intent detayIntent = new Intent(MainActivity.this, DetayActivity.class);
                startActivity(detayIntent);
            }
        });
    }

    private void refreshBookList() { //kitap listesini günceller
        ArrayList<Kitap> updatedBooks = getUserBooks(); //kullanıcıya ait tüm kitapları alır.
        adapter.updateList(updatedBooks);
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
                    int kitapAdiIndex = cursor.getColumnIndexOrThrow(KitapTuruContract.KitaplarEntry.COLUMN_KITAP_ADI);
                    int kitapYazariIndex = cursor.getColumnIndexOrThrow(KitapTuruContract.KitaplarEntry.COLUMN_KITAP_YAZARI);
                    int kitapYorumuIndex = cursor.getColumnIndexOrThrow(KitapTuruContract.KitaplarEntry.COLUMN_KITAP_YORUM);
                    int kitapResmiIndex = cursor.getColumnIndexOrThrow(KitapTuruContract.KitaplarEntry.COLUMN_KITAP_RESMI);

                    String kitapAdi = cursor.getString(kitapAdiIndex);
                    String kitapYazari = cursor.getString(kitapYazariIndex);
                    String kitapYorumu = cursor.getString(kitapYorumuIndex);
                    byte[] resimBytes = cursor.getBlob(kitapResmiIndex);
                    Bitmap kitapResim = BitmapFactory.decodeByteArray(resimBytes, 0, resimBytes.length);
                    kitapList.add(new Kitap(kitapAdi, kitapYazari, kitapYorumu, kitapResim));
                }
            } catch (Exception e) {
                Log.e("DatabaseError", "Error reading data: ", e);
            } finally {
                cursor.close();
            }
        }

        return kitapList;
    }

    private class GridManagerDecoration extends RecyclerView.ItemDecoration {
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.bottom = 25;//25 piksel
        }
    }
}
