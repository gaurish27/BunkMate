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

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegisterRedirect;

    private UserDAO userDAO;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(this);
        if (session.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        userDAO = new UserDAO(this);

        etEmail = findViewById(R.id.emailInput);
        etPassword = findViewById(R.id.passwordInput);
        btnLogin = findViewById(R.id.loginBtn);
        tvRegisterRedirect = findViewById(R.id.registerRedirect);

        btnLogin.setOnClickListener(v -> login());
        tvRegisterRedirect.setOnClickListener(v ->
                startActivity(new Intent(this, RegistrationActivity.class))
        );
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Valid email required"); return;
        }
        if (TextUtils.isEmpty(pass)) {
            etPassword.setError("Password required"); return;
        }

        long userId = userDAO.login(email, pass);
        if (userId > 0) {
            session.saveLogin(userId);
            Toast.makeText(this, "Login success", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_LONG).show();
        }
    }
}
