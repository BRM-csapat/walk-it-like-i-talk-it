package com.example.logintry;

import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.HttpCookie;
import java.util.List;

public class WalieTalkieActivity extends AppCompatActivity {

    private ActionMode navigationView;
    private DrawerLayout drawerLayout;
    private DatabaseReference channelRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_walie_talkie);
        // Láthatatlan beszélő gomb inicializálása
        View talkButton = findViewById(R.id.btn_talk);

        // Firebase adatbázis referencia a 'channel1' csatornára
        channelRef = FirebaseDatabase.getInstance().getReference("channels/channel1/currentSpeaker");


        // 4.2 pont szerinti kód (érintéses eseménykezelő)
        talkButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // A gomb megnyomása - beszéd indítása
                    startTalking();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // A gomb felengedése - beszéd leállítása
                    stopTalking();
                }
                return true;
            }
        });
        channelRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Ellenőrizzük, hogy valaki beszél-e
                String currentSpeaker = snapshot.getValue(String.class);
                if (currentSpeaker != null) {
                    // Ha van aktuális beszélő, mutassuk meg ki az
                    Toast.makeText(WalieTalkieActivity.this, currentSpeaker + " beszél", Toast.LENGTH_SHORT).show();
                } else {
                    // Ha senki sem beszél
                    Toast.makeText(WalieTalkieActivity.this, "Senki sem beszél", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WalieTalkieActivity.this, "Hiba történt a csatorna frissítésekor", Toast.LENGTH_SHORT).show();
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_drawer:
                drawerLayout.openDrawer(GravityCompat.START); // Oldalsó navigáció megnyitása
                return true;
            case R.id.action_settings:
                // Beállítások megnyitása
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateChannelMembers(String channelName, List<String> members) {
        MenuItem channelItem = navigationView.getMenu().findItem(R.id.channel1); // Csatorna id-je
        SubMenu subMenu = channelItem.getSubMenu();
        subMenu.clear();  // Tagok törlése
        for (String member : members) {
            subMenu.add(member);  // Új tagok hozzáadása
        }
    }
    private void startTalking() {
        // Mikrofon aktiválása (itt valószínűleg egy valódi audio capture is lesz)
        Toast.makeText(this, "Beszélés indítása", Toast.LENGTH_SHORT).show();

        // A Firebase adatbázis frissítése, jelezve hogy ez a felhasználó beszél
        String currentUser = "John Doe";  // Ezt valószínűleg a bejelentkezett felhasználóból kéred le
        channelRef.setValue(currentUser);  // A csatorna most megjegyzi, hogy ki beszél
    }

    private void stopTalking() {
        // Mikrofon deaktiválása
        Toast.makeText(this, "Beszélés leállítása", Toast.LENGTH_SHORT).show();

        // Firebase adatbázis frissítése, eltávolítva a beszélőt
        channelRef.setValue(null);  // Eltávolítja a beszélőt a csatornából, ha már nem beszél
    }
}