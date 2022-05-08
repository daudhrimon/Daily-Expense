package com.daud.dailyexpensefire;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
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
    private ExpenseAdapter adapter;
    private List<ExpenseModel> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_expense, container, false);

        initial(view);

        getExpense();

        MainActivity.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }
            @Override
            public boolean onQueryTextChange(String newText) {
                searchMethod(newText);
                return true;
            }
        });

        return view;
    }

    private void searchMethod(String newText) {
        List<ExpenseModel> searchList = new ArrayList<>();
        for (int i = 0; i<list.size(); i++){
            if (list.get(i).getType().toLowerCase().contains(newText) ||
                    list.get(i).getDate().contains(newText) ||
                    list.get(i).getTime().contains(newText) ||
                    String.valueOf(list.get(i).getAmount()).contains(newText)){

                searchList.add(list.get(i));
            }
        }
        adapter = new ExpenseAdapter(getContext(),searchList);
        recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getExpense() {
        DatabaseReference dataRef = MainActivity.databaseRef;
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              if (snapshot.exists()){
                  list.clear();
                  for(DataSnapshot dataSnap : snapshot.getChildren()){
                      if (dataSnap.exists()){
                          ExpenseModel expModel = dataSnap.getValue(ExpenseModel.class);
                          list.add(expModel);
                      }
                  }
                  adapter = new ExpenseAdapter(getContext(),list);
                  recycler.setAdapter(adapter);
                  adapter.notifyDataSetChanged();
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void initial(View view) {
        recycler = view.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();
    }
}