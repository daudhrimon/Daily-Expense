package com.daud.dailyexpensefire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLay;
    private NavigationView navDr;
    private BottomNavigationView navBtm;
    private ImageButton toggle;
    private FloatingActionButton addEx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initial();

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLay,new DashboardFrag()).commit();

        toggle.setOnClickListener(view -> {
            drawerLay.openDrawer(GravityCompat.START);
        });

        navDr.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                drawerItemSelect(item);

                return true;
            }
        });

        navBtm.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                bottomNavItemSelect(item);

                return true;
            }
        });
    }

    public void addExOnClick(View view) {
        startActivity(new Intent(MainActivity.this,AddExpenseActivity.class));
    }

    private void drawerItemSelect(MenuItem item) {

        switch (item.getItemId()){
            case R.id.eName:
                Toast.makeText(MainActivity.this, "Edit Name", Toast.LENGTH_SHORT).show();
                break;

            case R.id.sOut:
                Toast.makeText(MainActivity.this, "Sign Out", Toast.LENGTH_SHORT).show();
                break;

            case R.id.dash:
                navBtm.setSelectedItemId(R.id.dashBtm);
                drawerLay.closeDrawer(GravityCompat.START);
                break;

            case R.id.expn:
                navBtm.setSelectedItemId(R.id.expnBtm);
                drawerLay.closeDrawer(GravityCompat.START);
                break;

            default:break;
        }
    }

    private void bottomNavItemSelect(MenuItem item) {

        switch (item.getItemId()){
            case R.id.dashBtm:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLay,new DashboardFrag()).commit();
                break;

            case R.id.expnBtm:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLay,new ExpenseFrag()).commit();
                break;

            default:break;
        }
    }

    private void initial() {
        drawerLay = findViewById(R.id.drawerLay);
        navDr = findViewById(R.id.navDr);
        navBtm = findViewById(R.id.navBtm);
        toggle = findViewById(R.id.toogle);
        addEx = findViewById(R.id.addEx);
    }

}