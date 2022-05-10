package com.daud.dailyexpensefire;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private Context context;
    private List<ExpenseModel> list;

    public ExpenseAdapter(Context context, List<ExpenseModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.expense_vholder,parent,false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        holder.expType.setText(list.get(position).getType());
        holder.expAmount.setText(""+list.get(position).getAmount());
        holder.expDate.setText(list.get(position).getDate());

        if (!list.get(position).getTime().isEmpty()){
            holder.expTime.setVisibility(View.VISIBLE);
            holder.expTime.setText(list.get(position).getTime());
        }

        if (!list.get(position).getDoc().isEmpty()){
            holder.expandBtn.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(list.get(position).getDoc())
                    .into(holder.expImage);
        }

        holder.expPopup.setOnClickListener(view -> {
            popUpMenuOnClick(holder,position);
        });

        holder.expandBtn.setOnClickListener(view -> {
            expandBtnOnClick(holder,position);
        });

        holder.itemView.setOnClickListener(view -> {
            popUpMenuOnClick(holder,position);
        });
    }

    private void expandBtnOnClick(ExpenseViewHolder holder, int position) {
        switch (holder.expImage.getVisibility()) {
            case View.GONE:
                holder.expImage.setVisibility(View.VISIBLE);
                holder.expandBtn.setImageResource(R.drawable.expand_less);
                break;

            case View.VISIBLE:
                holder.expImage.setVisibility(View.GONE);
                holder.expandBtn.setImageResource(R.drawable.expand_more);
                break;
        }
    }

    private void popUpMenuOnClick(ExpenseViewHolder holder, int position) {
        PopupMenu popupMenu = new PopupMenu(context,holder.expPopup,GravityCompat.END);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.upPop:
                        Intent intent = new Intent(context,AddExActivity.class);
                        intent.putExtra("STATE","UP");
                        intent.putExtra("KEY",list.get(position).getKey());
                        intent.putExtra("TYPE",list.get(position).getType());
                        intent.putExtra("AMOUNT",""+list.get(position).getAmount());
                        intent.putExtra("DATE",list.get(position).getDate());
                        intent.putExtra("TIME",list.get(position).getTime());
                        intent.putExtra("DOC",list.get(position).getDoc());
                        context.startActivity(intent);
                        break;
                    case R.id.dltPop:
                        DatabaseReference dataRef = MainActivity.databaseRef.child(list.get(position).getKey());
                        dataRef.removeValue();
                }
                return true;
            }
        });
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private TextView expType,expDate,expTime,expAmount;
        private ImageButton expPopup,expandBtn;
        private ImageView expImage;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            expType = itemView.findViewById(R.id.expType);
            expDate = itemView.findViewById(R.id.expDate);
            expTime = itemView.findViewById(R.id.expTime);
            expAmount = itemView.findViewById(R.id.expAmount);
            expPopup = itemView.findViewById(R.id.expPopup);
            expandBtn = itemView.findViewById(R.id.expandBtn);
            expImage = itemView.findViewById(R.id.expImage);
        }
    }
}
