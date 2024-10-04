package com.example.logintry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ellenőrizzük, hogy a felhasználó be van-e jelentkezve
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // Ha a felhasználó már be van jelentkezve, navigáljunk a WalkieTalkieActivity-be
            Intent intent = new Intent(MainActivity.this, WalieTalkieActivity.class);
            startActivity(intent);
            finish();  // Bezárjuk a MainActivity-t
        }
        else {
            // Ha nincs bejelentkezve, jelenítsük meg a bejelentkezési képernyőt
            setContentView(R.layout.activity_main);
            EdgeToEdge.enable(this);
            FirebaseApp.initializeApp(this);}
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Firebase adatbázis referencia
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        felhaszNevEditText = findViewById(R.id.FelhaszNev);
        jelszoEditText = findViewById(R.id.jelszo);
        bejelentkezesButton = findViewById(R.id.bejelentkezesButton);

        bejelentkezesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String felhaszNev = felhaszNevEditText.getText().toString().trim();
                String jelszo = jelszoEditText.getText().toString().trim();

                if (TextUtils.isEmpty(felhaszNev) || TextUtils.isEmpty(jelszo)) {
                    Toast.makeText(MainActivity.this, "Minden mezőt ki kell tölteni!", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(felhaszNev, jelszo);
                }
            }
        });


    }
    private DatabaseReference databaseUsers;
    private EditText felhaszNevEditText, jelszoEditText;
    private Button bejelentkezesButton;
    public void register(View v) {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }
    private void loginUser(String felhaszNev, String jelszo) {
        databaseUsers.child(felhaszNev).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user.jelszo.equals(jelszo)) {
                        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();  // Alkalmazzuk a változást

                        // Navigálás a WalkieTalkieActivity-be
                        Toast.makeText(MainActivity.this, "Sikeres bejelentkezés!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, WalieTalkieActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Hibás jelszó!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Nincs ilyen felhasználó!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Hiba történt!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}