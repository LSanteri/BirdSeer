package com.example.birdseer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class paivaSijainti extends AppCompatActivity {

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TextView dateButton;
    private EditText locationButton;
    private Button confirmButton;
    private int paiva;
    private int kuukausi;
    private int vuosi;
    private String sijainti;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paiva_sijainti);
        dateButton = findViewById(R.id.datePickerButton);
        locationButton =  findViewById(R.id.sijaintibutton);
        confirmButton =  findViewById(R.id.lisaaLaji);
        confirmButton.setEnabled(false);


        dateButton.setOnClickListener(view -> {

            //Tämä kohta poimii käyttäjän puhelimelta hänen päivämääränsä ja syöttää
            //Sen ensimmäisenä näkyviin käyttäjälle, kun hän avaa kalenterinäkymän
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            //Tämä kohta kuuntelee sitä, kun painetaan "lisää laji nappia ja
            //Ottaa sitten vasta tiedon paikasta Sijainti -Stringiin
            confirmButton.setOnClickListener(view1 -> {
                sijainti = locationButton.getText().toString().trim();
                openMain();
            });
            //Tämä aukaisee päivämäärä-näkymän erillisenä "pop-up" ikkunana
            DatePickerDialog dialog = new DatePickerDialog(paivaSijainti.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        //Tämä katsoo, minkä päivämäärän käyttäjä lopulta valitsee ja lisää
        //ne havaintoihin
        mDateSetListener = (datePicker, year, month, day) -> {
            //Kuukauteen tulee lisätä 1, sillä muuten numeroina päivämäärä on yhdellä väärä
        month = month + 1;
        String date = day + "/" + month + "/" + year;
        paiva = day;
        kuukausi = month;
        vuosi = year;
        dateButton.setText(date);
        confirmButton.setEnabled(true);



        };
    }

    private void openMain() {

        MyDatabaseHelper myDB = new MyDatabaseHelper(paivaSijainti.this);
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        Intent intent = new Intent(this, MainActivity.class);

        if (vuosi > cal.get(Calendar.YEAR)) {
            Toast.makeText(this, "Lisääminen epäonnistui (Virheellinen vuosi)", Toast.LENGTH_SHORT).show();
        } else if (vuosi <= 0){
            Toast.makeText(this, "Lisääminen epäonnistui (Virheellinen vuosi)", Toast.LENGTH_SHORT).show();
        } else if (vuosi == cal.get(Calendar.YEAR) && (kuukausi > month)) {
            Toast.makeText(this, "Lisääminen epäonnistui (Virheellinen kuukausi)", Toast.LENGTH_SHORT).show();
        } else if (vuosi == cal.get(Calendar.YEAR) && (kuukausi == month) && (paiva > cal.get(Calendar.DAY_OF_MONTH))) {
            Toast.makeText(this, "Lisääminen epäonnistui (Virheellinen päivä)", Toast.LENGTH_SHORT).show();
        } else if (sijainti.matches("^[A-Za-zÀ-ÖØ-öø-ÿ-Z0-9_ ]*")) {
            myDB.lisaaLaji(getIntent().getStringExtra("laji"), sijainti, vuosi, kuukausi, paiva);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Lisääminen epäonnistui (Virheellinen sijainti)", Toast.LENGTH_SHORT).show();
        }
    }

}