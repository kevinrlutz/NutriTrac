package me.kevinlutz.nutritrac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChangeMacrosActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "ChangeMacrosActivity";

    private EditText proteinMax, calsMax, carbsMax, fatMax;
    private int proteinMaxVal, calsMaxVal, carbsMaxVal, fatMaxVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_macros);

        proteinMax = findViewById(R.id.inputProteinGoal);
        calsMax = findViewById(R.id.inputCalGoal);
        carbsMax = findViewById(R.id.inputCarbGoal);
        fatMax = findViewById(R.id.inputFatGoal);
    }

    private class Update extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            calsMaxVal = Integer.parseInt(calsMax.getText().toString());
            proteinMaxVal = Integer.parseInt(proteinMax.getText().toString());
            carbsMaxVal = Integer.parseInt(carbsMax.getText().toString());
            fatMaxVal = Integer.parseInt(fatMax.getText().toString());
            updateDatabase();
        }
    }

    public void submitGoals(View view) {
        if (!(calsMax.getText().toString().equals("") || proteinMax.getText().toString().equals("") || carbsMax.getText().toString().equals("") || fatMax.getText().toString().equals(""))) {
            Update updateDb = new Update();
            updateDb.execute();

            startActivity(new Intent(ChangeMacrosActivity.this, MainActivity.class));
        } else {
            Toast.makeText(this, "Please enter all data", Toast.LENGTH_SHORT).show();
        }
    }

    void updateDatabase() {
        Map<String, Object> goals = new HashMap<>();
        goals.put("proteinMax", proteinMaxVal);
        goals.put("calsMax", calsMaxVal);
        goals.put("carbsMax", carbsMaxVal);
        goals.put("fatMax", fatMaxVal);

        Log.d(TAG, goals.toString());

        db.collection("users").document(LoginActivity.activeEmail).update(goals);
        Log.d(TAG, "end of updateDatabase()");
    }
}