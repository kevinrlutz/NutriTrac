package me.kevinlutz.nutritrac;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.*;
import java.io.*;
import java.util.Scanner;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class MainActivity extends AppCompatActivity {

    private EditText inputBarcode;
    private TextView viewBarcode;
    private TextView viewProtein;
    private TextView viewProteinProgress;
    private TextView viewCarbsProgress;
    private TextView viewFatProgress;
    private ProgressBar progressBarProtein;
    private ProgressBar progressBarCarbs;
    private ProgressBar progressBarFat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputBarcode = findViewById(R.id.inputBarcode);
        viewBarcode = findViewById(R.id.viewBarcode);
        progressBarProtein = findViewById(R.id.progressBarProtein);
        progressBarCarbs = findViewById(R.id.progressBarCarbs);
        progressBarFat = findViewById(R.id.progressBarFat);
        viewProteinProgress = findViewById(R.id.viewProteinProgress);
        viewCarbsProgress = findViewById(R.id.viewCarbsProgress);
        viewFatProgress = findViewById(R.id.viewFatProgress);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private class Content extends AsyncTask<Void, Void, Void> {
        String barcode = inputBarcode.getText().toString();
        String productName = "";
        boolean error = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {

                String page = "https://world.openfoodfacts.org/product/" + barcode;

                try {
                    Document doc = Jsoup.connect(page).get();

                    String proteinVal = doc.body().getElementById("nutriment_proteins_tr").child(2).html().replaceAll("[^0-9]", "");
                    progressBarProtein.incrementProgressBy(Integer.parseInt(proteinVal));

                    String carbsVal = doc.body().getElementById("nutriment_carbohydrates_tr").child(2).html().replaceAll("[^0-9]", "");
                    progressBarCarbs.incrementProgressBy(Integer.parseInt(carbsVal));

                    String fatVal = doc.body().getElementById("nutriment_fat_tr").child(2).html().replaceAll("[^0-9]", "");
                    progressBarFat.incrementProgressBy(Integer.parseInt(fatVal));

                    productName = doc.body().getElementsByAttributeValue("property", "food:name").text();
                } catch (org.jsoup.HttpStatusException e) {
                    System.out.println(e.getStatusCode());
                    error = true;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!error) {
                super.onPostExecute(aVoid);
                viewBarcode.setText(productName);
                viewProteinProgress.setText(progressBarProtein.getProgress() + "g");
                viewCarbsProgress.setText(progressBarCarbs.getProgress() + "g");
                viewFatProgress.setText(progressBarFat.getProgress() + "g");
            } else {
                viewBarcode.setText("Error in Barcode");
            }
        }

    }
    public void barcodeSubmit(View view) throws IOException {
        Content content = new Content();
        content.execute();
    }
}