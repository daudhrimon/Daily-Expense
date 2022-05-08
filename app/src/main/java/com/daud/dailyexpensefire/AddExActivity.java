package com.daud.dailyexpensefire;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddExActivity extends AppCompatActivity {
    private Spinner spinner;
    private String exType, imageUrl = "",STATE,KEY;
    private TextInputEditText exAmount,exDate,exTime;
    private Button exAdd;
    private ImageView exImage;
    private Uri imageUri = null;
    private RelativeLayout exImageLay;
    private String[] exTypeList = {"Breakfast","Lunch","Dinner","Transport","Bill","Shopping","Medical","Payment","Insurance","Others"};
    private StorageReference storageReference;
    private ProgressBar addExProg;
    private int Hour,Minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ex);

        initial();

        //setAdapter Spinner
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(AddExActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,exTypeList);
        spinner.setAdapter(adapter);

        checkState(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                exType = spinner.getItemAtPosition(pos).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        exAdd.setOnClickListener(view -> {
            if (exAmount.getText().toString().isEmpty()){
                exAmount.setError("Empty");
                exAmount.requestFocus();
                return;
            }

            if (exDate.getText().toString().isEmpty()){
                exDate.setError("Empty");
                return;
            }

            addExpenseFirebase();

        });
    }

    private void checkState(ArrayAdapter<String> adapter) {
        Intent intent = getIntent();
        STATE = intent.getStringExtra("STATE");
        if (STATE.equals("UP")){
            KEY = intent.getStringExtra("KEY");
            String TYPE = intent.getStringExtra("TYPE");
            String AMOUNT = intent.getStringExtra("AMOUNT");
            String DATE = intent.getStringExtra("DATE");
            String TIME = intent.getStringExtra("TIME");
            String DOC = intent.getStringExtra("DOC");

            if (!TYPE.isEmpty()) {
                int spinnerPosition = adapter.getPosition(TYPE);
                spinner.setSelection(spinnerPosition);
            }

            exAmount.setText(AMOUNT);
            exDate.setText(DATE);

            if (!TIME.isEmpty()){
                exTime.setText(TIME);
            }

            if (!DOC.isEmpty()){
                exImageLay.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(DOC)
                        .into(exImage);
            }
        }
    }

    public void pickDate (View view){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker =
                new DatePickerDialog(AddExActivity.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month+1;
                        String Date = year+"/"+month+"/"+day;
                        exDate.setText(Date);
                    }
                },year,month,day);
        datePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePicker.setTitle("Select Date");
        datePicker.setCancelable(false);
        datePicker.show();
    }

    public void pickTime(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View timeView = getLayoutInflater().inflate(R.layout.custom_timepicker,null);
        final TimePicker timePicker = timeView.findViewById(R.id.timepicker);
        Button doneBtn = timeView.findViewById(R.id.doneBtn);
        builder.setView(timeView);
        final Dialog dialog = builder.create();
        dialog.show();

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm ss aa");
                @SuppressLint({"NewApi", "LocalSuppress"}) int hour = timePicker.getHour();
                @SuppressLint({"NewApi", "LocalSuppress"}) int min = timePicker.getMinute();

                Time time = new Time(hour,min,0);
                exTime.setText(timeFormat.format(time));
                dialog.dismiss();
            }
        });
    }

    public void addDocOnClick(View view) {
        if (ActivityCompat.checkSelfPermission(AddExActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(AddExActivity.this,"Camera permission not Granted",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(AddExActivity.this, new String[]{Manifest.permission.CAMERA}, 2);
        }else{
            if (checkInternet()){
                imageSourseSelectorDialog();
            }else{
                Toast.makeText(this, "Check Internet Connection First", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void imageSourseSelectorDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        View view1 = LayoutInflater.from(this).inflate(R.layout.image_source_selector, null);
        alertDialog.setView(view1);
        ImageView camera = view1.findViewById(R.id.camera);
        ImageView gallery = view1.findViewById(R.id.gallery);

        camera.setOnClickListener(view2 -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,1);
            alertDialog.dismiss();
        });

        gallery.setOnClickListener(view2 -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
            startActivityForResult(intent,2);
            alertDialog.dismiss();
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public void exPopupOnClick(View view) {
        PopupMenu popupMenu = new PopupMenu(AddExActivity.this,exImage,GravityCompat.END);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.upPop:
                        addDocOnClick(view);
                        break;
                    case R.id.dltPop:
                        if (STATE.equals("UP")){
                            if (checkInternet()){
                                deleteImageFromFirebase();
                            }
                        }else {
                            imageUri = null;
                            exImageLay.setVisibility(View.GONE);
                        }
                }
                return true;
            }
        });
        popupMenu.show();

    }

    private void deleteImageFromFirebase() {
        exImageLay.setVisibility(View.GONE);
        addExProg.setVisibility(View.VISIBLE);
        StorageReference storageRef = storageReference.child(KEY);
        storageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    addExProg.setVisibility(View.GONE);
                    imageUri = null;
                    exImageLay.setVisibility(View.GONE);
                }
            }
        });

    }

    private void addExpenseFirebase() {
        String Key;
        if (STATE.equals("UP")){
            Key = KEY;
        }else{
            DatabaseReference dataRef = MainActivity.databaseRef.push();
            Key = dataRef.getKey().toString();
        }

        if (imageUri !=null){
            exImageLay.setVisibility(View.GONE);
            addExProg.setVisibility(View.VISIBLE);

            StorageReference storageRef = storageReference.child(Key);
            storageRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                if (uri!=null){
                                    addExProg.setVisibility(View.GONE);
                                    imageUrl = uri.toString();
                                    setValueToFirebase(Key);
                                }else{
                                    addExProg.setVisibility(View.GONE);
                                    Toast.makeText(AddExActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        addExProg.setVisibility(View.GONE);
                        exImageLay.setVisibility(View.VISIBLE);
                        Toast.makeText(AddExActivity.this, "Image Upload Fail", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            setValueToFirebase(Key);
        }

    }

    private void setValueToFirebase(String key){
        ExpenseModel exModel = new ExpenseModel(key,exType,
                Integer.parseInt(exAmount.getText().toString().trim()),
                exDate.getText().toString(),
                exTime.getText().toString(),
                imageUrl);
        DatabaseReference dataRef = MainActivity.databaseRef.child(key);
        dataRef.setValue(exModel);
        Toast.makeText(this, "Expense Added", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) AddExActivity.this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageUri = getImageUri(bitmap);
            exImageLay.setVisibility(View.VISIBLE);
            exImage.setImageURI(imageUri);
        } else if (resultCode == RESULT_OK && requestCode == 2) {
            imageUri = data.getData();
            exImageLay.setVisibility(View.VISIBLE);
            exImage.setImageURI(imageUri);
        }
    }

    public Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
        String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),bitmap,"val",null);
        return Uri.parse(path);
    }

    private void initial() {
        spinner = findViewById(R.id.spinner);
        exAmount = findViewById(R.id.exAmount);
        exDate = findViewById(R.id.exDate);
        exTime = findViewById(R.id.exTime);
        exAdd = findViewById(R.id.exAdd);
        exImage = findViewById(R.id.exImage);
        exImageLay = findViewById(R.id.exImageLay);
        String Phone = MainActivity.sharedPreferences.getString("Phone", "");
        storageReference = FirebaseStorage.getInstance().getReference(Phone);
        //Key = AuthActivity.sharedPreferences.getString("Key","");
        addExProg = findViewById(R.id.addExProg);
    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        if (TASK==0){
            MainActivity.databaseRef.child(Key).removeValue();
        }
    }*/
}