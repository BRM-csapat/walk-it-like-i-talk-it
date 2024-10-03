package com.example.logintry;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

public class RegisterActivity extends AppCompatActivity {
    private EditText felhaszNevEditText, jelszoEditText, jelszoUjraEditText;
    private Button regisztracioButton;
    private DatabaseReference databaseUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Firebase adatbázis referencia
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        felhaszNevEditText = findViewById(R.id.FelhaszNev);
        jelszoEditText = findViewById(R.id.jelszo);
        jelszoUjraEditText = findViewById(R.id.jelszoujra);
        regisztracioButton = findViewById(R.id.regisztracioButton);

        regisztracioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String felhaszNev = felhaszNevEditText.getText().toString().trim();
                String jelszo = jelszoEditText.getText().toString().trim();
                String jelszoUjra = jelszoUjraEditText.getText().toString().trim();

                if (TextUtils.isEmpty(felhaszNev) || TextUtils.isEmpty(jelszo) || TextUtils.isEmpty(jelszoUjra)) {
                    Toast.makeText(RegisterActivity.this, "Minden mezőt ki kell tölteni!", Toast.LENGTH_SHORT).show();
                } else if (!jelszo.equals(jelszoUjra)) {
                    Toast.makeText(RegisterActivity.this, "A jelszavak nem egyeznek!", Toast.LENGTH_SHORT).show();
                } else {
                    checkIfUserExists(felhaszNev, jelszo);
                }
            }
        });

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void checkIfUserExists(String felhaszNev, String jelszo) {
        databaseUsers.child(felhaszNev).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(RegisterActivity.this, "A felhasználónév már létezik!", Toast.LENGTH_SHORT).show();
                } else {
                    createUser(felhaszNev, jelszo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegisterActivity.this, "Hiba történt!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void createUser(String felhaszNev, String jelszo) {
        User user = new User(felhaszNev, jelszo);
        databaseUsers.child(felhaszNev).setValue(user);
        Toast.makeText(RegisterActivity.this, "Sikeres regisztráció!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    public void belepOldal(View v) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}