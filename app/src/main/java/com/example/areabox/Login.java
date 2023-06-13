package com.example.areabox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.areabox.Model.DatabaseHandler;
import com.example.areabox.Model.UserLogin;

public class Login extends AppCompatActivity {
    EditText userName, userPassword;
    TextView singUp;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        userName = (EditText) findViewById(R.id.editTextText);
        userPassword = (EditText) findViewById(R.id.editTextText2);
        singUp = (TextView) findViewById(R.id.textView3);
        db = new DatabaseHandler(this);
    }

    public void login(View v) {
        String name = userName.getText().toString();
        String password = userPassword.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter username or email", Toast.LENGTH_SHORT).show();
        }
        else if (password.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        }
        else {
            UserLogin login = new UserLogin(name,password);
            if (db.loginUser(login)) {
                Intent loginPage = new Intent(this, MainPage.class);
                loginPage.putExtra("USER_NAME", login.getUsername());
                startActivity(loginPage);
            } else {
                Toast.makeText(this, "Invalid username/email or password", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void signUp(View v){
        Intent singUp = new Intent(this, SignUp.class);
        startActivity(singUp);
        finish();
    }
}