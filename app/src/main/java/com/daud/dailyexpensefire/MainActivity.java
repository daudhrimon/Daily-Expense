package com.daud.dailyexpensefire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLay;
    private NavigationView navDr;
    private BottomNavigationView navBtm;
    private TextView nameTv,phoneTv;
    private ImageButton toggle;
    private FloatingActionButton addEx;
    public static DatabaseReference databaseRef;
    private String Name,Phone;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

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
        /*DatabaseReference dataRef = databaseRef.push();
        String Key = dataRef.getKey().toString();
        AuthActivity.editor.putString("Key",Key).commit();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("Type","");
        hashMap.put("Amount",0);
        hashMap.put("Note","");
        hashMap.put("Date","");
        hashMap.put("Time","");
        hashMap.put("Doc","");
        dataRef.setValue(hashMap);*/
        Intent intent = new Intent(MainActivity.this, AddExActivity.class);
        intent.putExtra("STATE","Add");
        startActivity(intent);
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
        navBtm = findViewById(R.id.navBtm);
        toggle = findViewById(R.id.toogle);
        addEx = findViewById(R.id.addEx);
        // get user name and phone from SharedPreference
        sharedPreferences = getSharedPreferences("MySp", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Phone = sharedPreferences.getString("Phone", "");
        Name = sharedPreferences.getString("Name", "");
        ///Navigation View And Header Initial
        navDr = findViewById(R.id.navDr);
        View header = navDr.getHeaderView(0);
        nameTv = header.findViewById(R.id.nameTv);
        nameTv.setText(Name);
        phoneTv =header.findViewById(R.id.phoneTv);
        phoneTv.setText(Phone);
        ////////////////////////////////////////
        databaseRef = FirebaseDatabase.getInstance().getReference(Phone);
        databaseRef.keepSynced(true);
    }

}