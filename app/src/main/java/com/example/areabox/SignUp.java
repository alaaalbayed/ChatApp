package com.example.areabox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.areabox.Model.DatabaseHandler;
import com.example.areabox.Model.Register;

public class SignUp extends AppCompatActivity {
    EditText userName, userEmail, userPassword, confirmPassword;
    CheckBox checkBox;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userName = (EditText) findViewById(R.id.editTextText3);
        userEmail = (EditText) findViewById(R.id.editTextText4);
        userPassword = (EditText) findViewById(R.id.editTextText5);
        confirmPassword = (EditText) findViewById(R.id.editTextText6);
        checkBox = findViewById(R.id.checkBox);
        db = new DatabaseHandler(this);
    }

    public void signUp(View v) {
        String name = userName.getText().toString();
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        String confirmPass = confirmPassword.getText().toString();

        if (!checkBox.isChecked()) {
            Toast.makeText(this, "Please agree to the terms and conditions!", Toast.LENGTH_SHORT).show();
        } else {
            Register register = new Register(name, email, password);

            if (!password.equals(confirmPass)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else if (!email.contains("@") || !email.contains(".")) {
                Toast.makeText(this, "Please enter a valid email!", Toast.LENGTH_SHORT).show();
            } else if (db.isUsernameExists(name)) {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            } else if (db.isEmailExists(email)) {
                Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show();
            } else {
                db.registerUser(register);
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, Login.class));
                finish();
            }

        }
    }

    public void toLogin(View v){
        Intent toLogin = new Intent(this, Login.class);
        startActivity(toLogin);
        finish();
    }
}