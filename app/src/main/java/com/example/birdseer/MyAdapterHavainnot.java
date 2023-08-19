package com.example.birdseer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;

public class MyAdapterHavainnot extends RecyclerView.Adapter<MyAdapterHavainnot.MyViewHolder> {

    private Context context;
    private ArrayList<String> lajiNimiArrayList, sijaintiArrayList;
    private ArrayList<Integer> idArrayList, dayArrayList, monthArrayList, yearArrayList;
    private ArrayList<Boolean> poistaLajiVisibilityList;


    public MyAdapterHavainnot(Context context,
                              ArrayList<String> lajiNimiArrayList,
                              ArrayList<String> sijaintiArrayList,
                              ArrayList<Integer> idArrayList,
                              ArrayList<Integer> dayArrayList,
                              ArrayList<Integer> monthArrayList,
                              ArrayList<Integer> yearArrayList) {
        this.context = context;
        this.lajiNimiArrayList = lajiNimiArrayList;
        this.sijaintiArrayList = sijaintiArrayList;
        this.idArrayList = idArrayList;
        this.dayArrayList = dayArrayList;
        this.monthArrayList = monthArrayList;
        this.yearArrayList = yearArrayList;
        poistaLajiVisibilityList = new ArrayList<>(Collections.nCopies(idArrayList.size(), false));

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_row_havainnot, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bind(position);

        // Set the visibility of the poistaLaji button based on the visibility state
        if (poistaLajiVisibilityList.get(position)) {
            holder.poistaLaji.setVisibility(View.VISIBLE);
        } else {
            holder.poistaLaji.setVisibility(View.INVISIBLE);
        }

        holder.mainLayout.setOnClickListener(v -> {
            // Update the visibility state and notify data changes
            poistaLajiVisibilityList.set(position, !poistaLajiVisibilityList.get(position));
            notifyDataSetChanged();
        });

        holder.poistaLaji.setOnClickListener(v -> havainnonPoistoDialogi(position));
    }


    @Override
    public int getItemCount() {
        return idArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView havaittuLaji_txt, paiva_txt, kuukausi_txt, vuosi_txt, sijainti_txt;
        ImageView poistaLaji;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            havaittuLaji_txt = itemView.findViewById(R.id.havaittuLaji_txt);
            paiva_txt = itemView.findViewById(R.id.paiva_txt);
            kuukausi_txt = itemView.findViewById(R.id.Kuukausi_txt);
            vuosi_txt = itemView.findViewById(R.id.vuosi_txt);
            sijainti_txt = itemView.findViewById(R.id.sijainti_txt);
            poistaLaji = itemView.findViewById(R.id.roskakori);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            poistaLaji.setVisibility(View.INVISIBLE);

            poistaLaji.setOnClickListener(v -> havainnonPoistoDialogi(getAdapterPosition()));
        }

        public void bind(int position) {
            havaittuLaji_txt.setText(String.valueOf(lajiNimiArrayList.get(position)));
            paiva_txt.setText(String.valueOf(dayArrayList.get(position)));
            kuukausi_txt.setText(String.valueOf(monthArrayList.get(position)));
            vuosi_txt.setText(String.valueOf(yearArrayList.get(position)));
            sijainti_txt.setText(String.valueOf(sijaintiArrayList.get(position)));
        }
    }

    private void havainnonPoistoDialogi(int position) {
        String lajinimi = String.valueOf(lajiNimiArrayList.get(position));
        String sijainti = String.valueOf(sijaintiArrayList.get(position));
        int vuosi = yearArrayList.get(position);
        int kuukausi = monthArrayList.get(position);
        int paiva = dayArrayList.get(position);
        int id = idArrayList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle("Havainnon poistaminen");
        String viesti = "Haluatko varmasti poistaa havainnon: " + "<br>" + "<b>"
                + lajinimi +
                ", " + paiva +
                "." + kuukausi +
                "." + vuosi +
                " " + sijainti +
                "?" + "</b>";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setMessage(Html.fromHtml(viesti, Html.FROM_HTML_MODE_COMPACT));
        } else {
            builder.setMessage(Html.fromHtml(viesti));
        }
        builder.setPositiveButton("Poista",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyDatabaseHelper myDB = new MyDatabaseHelper(context);
                        myDB.poistaHavainto(id);

                        lajiNimiArrayList.remove(position);
                        sijaintiArrayList.remove(position);
                        idArrayList.remove(position);
                        dayArrayList.remove(position);
                        monthArrayList.remove(position);
                        yearArrayList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // This is the cancel action
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
