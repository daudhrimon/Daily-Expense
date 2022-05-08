package com.daud.dailyexpensefire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
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
        Intent intent = new Intent(MainActivity.this, AddExActivity.class);
        intent.putExtra("STATE","Add");
        startActivity(intent);
    }

    private void drawerItemSelect(MenuItem item) {

        switch (item.getItemId()){
            case R.id.eName:
                editNameAlert();
                break;

            case R.id.sOut:
                showSignOutAlert();
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

    private void editNameAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = LayoutInflater.from(this).inflate(R.layout.name_input_lay,null);
        final TextInputEditText nameEt = view.findViewById(R.id.nameEt);
        final MaterialButton saveBtn = view.findViewById(R.id.saveBtn);
        builder.setView(view);
        final Dialog dialog = builder.create();
        dialog.show();

        saveBtn.setOnClickListener(view1 -> {
            if (nameEt.getText().toString().isEmpty()){
                nameEt.setError("Empty");
                nameEt.requestFocus();
                return;
            }
            String name = nameEt.getText().toString();
            editor.putString("Name",name);
            nameTv.setText(name);
            Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

    private void showSignOutAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setIcon(R.drawable.logout);
        dialog.setTitle("SignOut Alert !");
        dialog.setMessage("Are you sere, you want to SignOut ?");

        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "SignOut", Toast.LENGTH_SHORT).show();
                MainActivity.editor.putInt("STATE", 0).commit();
                startActivity(new Intent(MainActivity.this,AuthActivity.class));
                finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
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

    @Override
    public void onBackPressed() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setIcon(R.drawable.exit);
        dialog.setTitle("Exit Alert !");
        dialog.setMessage("Are you sere, you want to Exit ?");

        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
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