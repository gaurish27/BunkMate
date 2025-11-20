package com.example.bunkmate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bunkmate.database.UserDAO;

public class RegistrationActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private Button btnRegister;
    private TextView tvLoginRedirect;

    private UserDAO userDAO;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration); // make sure your XML name matches

        userDAO = new UserDAO(this);
        session = new SessionManager(this);

        etName = findViewById(R.id.nameInput);
        etEmail = findViewById(R.id.emailInput);
        etPassword = findViewById(R.id.passwordInput);
        btnRegister = findViewById(R.id.registerBtn);
        tvLoginRedirect = findViewById(R.id.loginRedirect);

        btnRegister.setOnClickListener(v -> register());
        tvLoginRedirect.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class))
        );
    }

    private void register() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) { etName.setError("Required"); return; }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Valid email required"); return;
        }
        if (TextUtils.isEmpty(pass) || pass.length() < 6) {
            etPassword.setError("Min 6 chars"); return;
        }

        long newId = userDAO.registerUser(name, email, pass);
        if (newId == -1) {
            Toast.makeText(this, "Email already registered", Toast.LENGTH_LONG).show();
            return;
        }

        // Auto-login
        session.saveLogin(newId);
        Toast.makeText(this, "Welcome, " + name + "!", Toast.LENGTH_SHORT).show();

        // Go to Main
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
