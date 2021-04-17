package me.kevinlutz.nutritrac;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.*;
import org.jsoup.nodes.Document;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText inputBarcode;
    private TextView viewBarcode;
    private TextView viewProteinProgress;
    private TextView viewCarbsProgress;
    private TextView viewFatProgress;
    private ProgressBar progressBarProtein;
    private ProgressBar progressBarCarbs;
    private ProgressBar progressBarFat;
    private ProgressBar progressBarCals;
    private TextView viewCalsProgress;
    private TextView viewCalsMax;
    private TextView viewProteinMax;
    private TextView viewCarbsMax;
    private TextView viewFatMax;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputBarcode = findViewById(R.id.inputBarcode);
        viewBarcode = findViewById(R.id.viewBarcode);
        progressBarProtein = findViewById(R.id.progressBarProtein);
        progressBarCarbs = findViewById(R.id.progressBarCarbs);
        progressBarFat = findViewById(R.id.progressBarFat);
        viewProteinProgress = findViewById(R.id.viewProteinProgress);
        viewCarbsProgress = findViewById(R.id.viewCarbsProgress);
        viewFatProgress = findViewById(R.id.viewFatProgress);
        progressBarCals = findViewById(R.id.progressBarCals);
        viewCalsMax = findViewById(R.id.viewCalsMax);
        viewProteinMax = findViewById(R.id.viewProteinMax);
        viewCarbsMax = findViewById(R.id.viewCarbsMax);
        viewFatMax = findViewById(R.id.viewFatMax);
        viewCalsProgress = findViewById(R.id.viewCalsProgress);



        db.collection("users").document(LoginActivity.activeEmail).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    progressBarProtein.setMax(Integer.parseInt(document.get("proteinMax").toString()));
                    progressBarCals.setMax(Integer.parseInt(document.get("calsMax").toString()));
                    progressBarCarbs.setMax(Integer.parseInt(document.get("carbsMax").toString()));
                    progressBarFat.setMax(Integer.parseInt(document.get("fatMax").toString()));

                    viewProteinProgress.setText(document.get("proteinProgress").toString() + "g");
                    viewCarbsProgress.setText(document.get("carbsProgress").toString() + "g");
                    viewFatProgress.setText(document.get("fatProgress").toString() + "g");
                    viewCalsProgress.setText(document.get("calsProgress").toString());
                    viewProteinMax.setText(document.get("proteinMax").toString() + "g");
                    viewCalsMax.setText(document.get("calsMax").toString());
                    viewCarbsMax.setText(document.get("carbsMax").toString() + "g");
                    viewFatMax.setText(document.get("fatMax").toString() + "g");

                    progressBarCals.setProgress(Integer.parseInt(document.get("calsProgress").toString()));
                    progressBarProtein.setProgress(Integer.parseInt(document.get("proteinProgress").toString()));
                    progressBarCarbs.setProgress(Integer.parseInt(document.get("carbsProgress").toString()));
                    progressBarFat.setProgress(Integer.parseInt(document.get("fatProgress").toString()));
                } else {
                    Log.d(TAG, "get failed with ", task.getException());

                }
            }
        });
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

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... voids) {
            try {

                String page = "https://world.openfoodfacts.org/product/" + barcode;

                try {
                    Document doc = Jsoup.connect(page).get();

                    String proteinVal = doc.body().getElementById("nutriment_proteins_tr").child(2).html().replaceAll("[^\\d.]", "");
                    progressBarProtein.incrementProgressBy(Integer.parseInt(String.valueOf(Math.round(Float.parseFloat(proteinVal)))));

                    String carbsVal = doc.body().getElementById("nutriment_carbohydrates_tr").child(2).html().replaceAll("[^\\d.]", "");
                    progressBarCarbs.incrementProgressBy(Integer.parseInt(String.valueOf(Math.round(Float.parseFloat(carbsVal)))));

                    String fatVal = doc.body().getElementById("nutriment_fat_tr").child(2).html().replaceAll("[^\\d.]", "");
                    progressBarFat.incrementProgressBy(Integer.parseInt(String.valueOf(Math.round(Float.parseFloat(fatVal)))));

                    String calsVal = doc.body().getElementById("nutriment_energy-kcal_tr").child(2).html().replaceAll("[^\\d.]", "");
                    progressBarCals.incrementProgressBy(Integer.parseInt(String.valueOf(Math.round(Float.parseFloat(calsVal)))));

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
                viewCalsProgress.setText(String.valueOf(progressBarCals.getProgress()));
                updateDatabase();
            } else {
                viewBarcode.setText("Error in Barcode");
            }
        }

    }
    public void barcodeSubmit(View view) {
        Content content = new Content();
        content.execute();
    }

    public void updateDatabase() {
        Map<String, Object> progress = new HashMap<>();
        progress.put("calsProgress", progressBarCals.getProgress());
        progress.put("proteinProgress", progressBarProtein.getProgress());
        progress.put("carbsProgress", progressBarCarbs.getProgress());
        progress.put("fatProgress", progressBarFat.getProgress());

        db.collection("users").document(LoginActivity.activeEmail).update(progress);
    }

    public void changeMacros(View view) {
        startActivity(new Intent(MainActivity.this, ChangeMacrosActivity.class));
    }

    public void resetMacros(View view) {
        Map<String, Object> progress = new HashMap<>();
        progress.put("calsProgress", 0);
        progress.put("proteinProgress", 0);
        progress.put("carbsProgress", 0);
        progress.put("fatProgress", 0);

        progressBarCals.setProgress(0);
        progressBarProtein.setProgress(0);
        progressBarCarbs.setProgress(0);
        progressBarFat.setProgress(0);

        viewCalsProgress.setText("0");
        viewProteinProgress.setText("0g");
        viewCarbsProgress.setText("0g");
        viewFatProgress.setText("0g");

        db.collection("users").document(LoginActivity.activeEmail).update(progress);
    }
}