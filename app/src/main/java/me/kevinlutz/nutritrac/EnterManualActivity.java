package me.kevinlutz.nutritrac;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EnterManualActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button submitButton;
    private EditText inputCals, inputProtein, inputFat, inputCarbs;
    private int cals, protein, carbs, fat;
    private final String TAG = "EnterManualActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_manual);

        submitButton = findViewById(R.id.submitMacrosButton);
        inputCals = findViewById(R.id.inputCals);
        inputProtein = findViewById(R.id.inputProtein);
        inputCarbs = findViewById(R.id.inputCarbs);
        inputFat = findViewById(R.id.inputFat);
    }

    public void onSubmit(View view) {
        cals = Integer.parseInt(inputCals.getText().toString());
        protein = Integer.parseInt(inputProtein.getText().toString());
        carbs = Integer.parseInt(inputCarbs.getText().toString());
        fat = Integer.parseInt(inputFat.getText().toString());

        updateDatabase();

        startActivity(new Intent(EnterManualActivity.this, MainActivity.class));
    }

    public void updateDatabase() {
        Map<String, Object> manual = new HashMap<>();
        manual.put("calsProgress", cals);
        manual.put("proteinProgress", protein);
        manual.put("carbsProgress", carbs);
        manual.put("fatProgress", fat);

        db.collection("users").document(LoginActivity.activeEmail).update(manual);
    }
}