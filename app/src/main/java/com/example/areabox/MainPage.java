package com.example.areabox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.areabox.Model.DatabaseHandler;
import com.example.areabox.Model.User;

public class MainPage extends AppCompatActivity {
    EditText searchEditText;
    Button searchButton;
    TextView resultTextView;
    Button chatButton;
    DatabaseHandler db;
    User searchedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        resultTextView = findViewById(R.id.resultTextView);
        chatButton = findViewById(R.id.chatButton);
        db = new DatabaseHandler(this);
    }

    public void searchButton(View view) {
        searchUser();
    }

    public void chatButton(View view) {
        startChatActivity();
    }

    public void searchUser() {
        String searchQuery = searchEditText.getText().toString().trim();
        if (!searchQuery.isEmpty()) {
            User user = db.getUserInfo(searchQuery);
            if (user != null) {
                searchedUser = user;
                resultTextView.setVisibility(View.VISIBLE);
                chatButton.setVisibility(View.VISIBLE);
                resultTextView.setText(user.getUserName());
            } else {
                resultTextView.setVisibility(View.GONE);
                chatButton.setVisibility(View.GONE);
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            resultTextView.setVisibility(View.GONE);
            chatButton.setVisibility(View.GONE);
            Toast.makeText(this, "Please enter a username or email!", Toast.LENGTH_SHORT).show();
        }
    }
    private void startChatActivity() {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("RECIPIENT_NAME", searchedUser.getUserName());
        String userName = getIntent().getStringExtra("USER_NAME");
        intent.putExtra("USER_NAME", userName);
        startActivity(intent);
    }

    public void exit(View v){
        Intent exit = new Intent(this, Login.class);
        startActivity(exit);
        finish();
    }
}
