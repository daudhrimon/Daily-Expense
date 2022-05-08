package com.daud.dailyexpensefire;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DashboardFrag extends Fragment {
    private TextInputEditText fromDateEt,toDateEt;
    private TextView totalExTv;
    private int SUM;
    private List<ExpenseModel> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_dashboard, container, false);

        initial(view);

        getAmount();

        fromDateEt.setOnClickListener(view1 -> {
            pickDate(0);
        });

        toDateEt.setOnClickListener(view1 -> {
            pickDate(1);
        });

        return view;
    }

    private void getAmount() {
        if (fromDateEt.getText().toString().isEmpty()
                || toDateEt.getText().toString().isEmpty()){
            getAllAmount();
        }else {
            getAmountByDate();
        }
    }

    private void getAllAmount() {
        MainActivity.databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.exists()){
                   SUM = 0;
                   for (DataSnapshot dataSnap : snapshot.getChildren()){
                       ExpenseModel allAmount = dataSnap.getValue(ExpenseModel.class);
                       SUM += allAmount.getAmount();
                   }
                   totalExTv.setText(String.valueOf(SUM));
               }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void  getAmountByDate() {
        MainActivity.databaseRef.orderByChild("date").startAt(fromDateEt.getText().toString())
                .endAt(toDateEt.getText().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.exists()){
                   SUM = 0;
                   for (DataSnapshot dataSnap : snapshot.getChildren()){
                       if (dataSnap.exists()){
                           ExpenseModel dateAmount = dataSnap.getValue(ExpenseModel.class);
                           SUM += dateAmount.getAmount();
                       }
                   }
                   totalExTv.setText(String.valueOf(SUM));
               }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void pickDate(int task) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker =
                new DatePickerDialog(getContext(),android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                month = month+1;
                                String Date = year+"/"+month+"/"+day;
                                if (task==0){
                                    fromDateEt.setText(Date);
                                    getAmount();
                                }else {
                                    toDateEt.setText(Date);
                                    getAmount();
                                }

                            }
                        },year,month,day);
        datePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (task==0){
            datePicker.setTitle("From Date");
        }else {
            datePicker.setTitle("To Date");
        }

        datePicker.setCancelable(false);
        datePicker.show();
    }

    private void initial(View view) {
        fromDateEt = view.findViewById(R.id.fromDateEt);
        toDateEt = view.findViewById(R.id.toDateEt);
        totalExTv = view.findViewById(R.id.totalExTv);
        list = new ArrayList<>();
    }
}