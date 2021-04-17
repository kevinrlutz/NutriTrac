package me.kevinlutz.nutritrac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import kotlinx.coroutines.MainCoroutineDispatcher;

public class LoginActivity extends AppCompatActivity {
    private EditText inputEmail;
    private EditText inputPassword;
    private static final String TAG = "LoginActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static String activeEmail = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
    }

    public void loginSubmit(View view) throws IOException {
        DocumentReference docRef = db.collection("users").document(inputEmail.getText().toString());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.get("password").equals(inputPassword.getText().toString())) {
                            Log.d(TAG, "login correct!");
                            activeEmail = inputEmail.getText().toString();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            Toast incorrectPass = Toast.makeText(getApplicationContext(), "Password incorrect", Toast.LENGTH_SHORT);
                            incorrectPass.show();
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());

                }
            }
        });

    }

    public void registerSubmit(View view) throws IOException {
        Map<String, Object> user = new HashMap<>();
        user.put("email", inputEmail.getText().toString());
        user.put("password", inputPassword.getText().toString());
        user.put("proteinProgress", 0);
        user.put("carbsProgress", 0);
        user.put("fatProgress", 0);
        user.put("calsProgress", 0);

        db.collection("users").document(inputEmail.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Toast.makeText(LoginActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        db.collection("users").document(inputEmail.getText().toString())
                                .set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                        activeEmail = inputEmail.getText().toString();
                                        startActivity(new Intent(LoginActivity.this, ChangeMacrosActivity.class));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}