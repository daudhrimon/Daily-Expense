package com.daud.dailyexpensefire;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExpenseFrag extends Fragment {
    private RecyclerView recycler;
    private LinearLayout eBox;
    private ExpenseAdapter adapter;
    private List<ExpenseModel> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_expense, container, false);

        initial(view);

        getExpense();

        return view;
    }

    private void getExpense() {
        DatabaseReference dataRef = MainActivity.databaseRef;
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              if (snapshot.exists()){
                  list.clear();
                  eBox.setVisibility(View.GONE);
                  recycler.setVisibility(View.VISIBLE);
                  for(DataSnapshot dataSnap : snapshot.getChildren()){
                      if (dataSnap.exists()){
                          ExpenseModel expModel = dataSnap.getValue(ExpenseModel.class);
                          list.add(expModel);
                      }
                  }
                  adapter = new ExpenseAdapter(getContext(),list);
                  recycler.setAdapter(adapter);
                  adapter.notifyDataSetChanged();
              } else {
                  recycler.setVisibility(View.GONE);
                  eBox.setVisibility(View.VISIBLE);
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void initial(View view) {
        recycler = view.findViewById(R.id.recycler);
        eBox = view.findViewById(R.id.eBox);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();
    }
}