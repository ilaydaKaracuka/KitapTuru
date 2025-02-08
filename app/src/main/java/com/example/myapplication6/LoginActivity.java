package com.example.myapplication6;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication6.data.DatabaseHelper;
import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {

    public static int currentUserID = -1;
    private EditText edtUsername, edtPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) { //boşsa
            Snackbar.make(findViewById(android.R.id.content), "Lütfen tüm alanları doldurun.", Snackbar.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("users", new String[]{"id"}, "username = ? AND password = ?",
                new String[]{username, password}, null, null, null);

        if (cursor != null && cursor.moveToFirst())
            {
            currentUserID = cursor.getInt(cursor.getColumnIndexOrThrow("id")); //giriş yapan kullanıcıya ait id sütunundaki değeri alır.uygulama boyunca o kullanıcıya özel işlemler yapmak için
            Snackbar.make(findViewById(android.R.id.content), "Giriş başarılı.", Snackbar.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else {
            Snackbar.make(findViewById(android.R.id.content), "Geçersiz kullanıcı adı veya şifre.", Snackbar.LENGTH_SHORT).show();
        }

        if (cursor != null)
        {
            cursor.close();
        }
        db.close();
    }
}
