package com.daud.dailyexpensefire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLay;
    private NavigationView navDr;
    private BottomNavigationView navBtm;
    private ImageButton toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLay = findViewById(R.id.drawerLay);
        navDr = findViewById(R.id.navDr);
        navBtm = findViewById(R.id.navBtm);
        toggle = findViewById(R.id.toogle);

        toggle.setOnClickListener(view -> {
            drawerLay.openDrawer(GravityCompat.START);
        });

        navDr.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.eName:
                        Toast.makeText(MainActivity.this, "Edit Name", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.sOut:
                        Toast.makeText(MainActivity.this, "Sign Out", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.dash:
                        Toast.makeText(MainActivity.this, "Dash Board", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.addex:
                        Toast.makeText(MainActivity.this, "Add Expense", Toast.LENGTH_SHORT).show();
                        break;

                        default:break;
                }
                return true;
            }
        });

        navBtm.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.dashBtm:
                        Toast.makeText(MainActivity.this, "Dash Board", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.addexBtm:
                        Toast.makeText(MainActivity.this, "Add Expense", Toast.LENGTH_SHORT).show();
                        break;

                        default:break;
                }
                return true;
            }
        });
    }
}