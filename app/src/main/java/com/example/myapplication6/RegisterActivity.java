package com.example.myapplication6;

import android.content.ContentValues;
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

public class RegisterActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        if (username.isEmpty() || password.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), "Lütfen tüm alanları doldurun.", Snackbar.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("users", null, "username = ?", new String[]{username}, null, null, null);

        if (cursor.moveToFirst()) {
            Snackbar.make(findViewById(android.R.id.content), "Bu kullanıcı zaten kayıtlı.", Snackbar.LENGTH_SHORT).show();
            cursor.close();
            db.close();
            return;
        }

        cursor.close();

        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);

        long newRowId = db.insert("users", null, values);

        if (newRowId != -1)
        {
            Snackbar.make(findViewById(android.R.id.content), "Kayıt başarılı.", Snackbar.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else
        {
            Snackbar.make(findViewById(android.R.id.content), "Kayıt sırasında bir hata oluştu.", Snackbar.LENGTH_SHORT).show();
        }

        db.close();
    }
}
