package com.example.androidgameproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    private EditText username;
    private EditText email;
    private EditText password;
    private Button signupButton;
    private TextView loginText;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private String TAG = "Firebase";

    public void saveUser(View v){
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information

                        String name = username.getText().toString();
                        String mail = email.getText().toString();
                        String encodedEmail = mail.replace(".", "_dot_").replace("@", "_at_");
                        // String decodedEmail = encodedEmail.replace("_dot_", ".").replace("_at_", "@");

                        // Save the username to the Realtime Database
                        DatabaseReference userRef = database.getReference("users").child(encodedEmail);
                        userRef.child("best_score").setValue(0);
                        userRef.child("username").setValue(name);
                    }
                    else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        FirebaseApp.initializeApp(this); // Make sure this is done.

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signupButton = findViewById(R.id.signup_button);
        loginText = findViewById(R.id.login_text);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle registration logic here (e.g., validate inputs)
                String user = username.getText().toString();
                String mail = email.getText().toString();
                String pass = password.getText().toString();

                if (user.isEmpty() || mail.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
                else if (pass.length() < 6) {
                    // Password should be at least 6 characters
                    Toast.makeText(SignupActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                    // Email has to be in the correct format
                    Toast.makeText(SignupActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                }
                else {
                    saveUser(v);

                    // Proceed with registration logic (e.g., save user data, etc.)
                    Toast.makeText(SignupActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();

                    // Optionally navigate to the main activity or login
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Optionally finish this activity
                }
            }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the login screen
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        Button backToMenuButton = findViewById(R.id.back_to_menu_button);
        backToMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to MenuActivity
                Intent intent = new Intent(SignupActivity.this, MenuActivity.class);
                startActivity(intent);
                finish(); // Close LoginActivity to avoid stacking activities
            }
        });
    }
}
