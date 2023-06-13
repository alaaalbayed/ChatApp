package com.example.areabox;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.areabox.Model.DatabaseHandler;
import com.example.areabox.Model.User;

public class ChatActivity extends AppCompatActivity {

    private LinearLayout chatLayout;
    private EditText messageEditText;
    private Button sendButton;

    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatLayout = findViewById(R.id.chatLayout);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        db = new DatabaseHandler(this);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        // Load the existing messages from the database
        loadMessages();
    }

    private void sendMessage() {
        final String message = messageEditText.getText().toString().trim();
        if (!message.isEmpty()) {
            String recipient = getIntent().getStringExtra("RECIPIENT_NAME");
            String userName = getIntent().getStringExtra("USER_NAME");
            db.insertMessage(userName, recipient, message);

            Cursor cursor = db.getMessages(recipient, userName);
            if (cursor != null && cursor.moveToLast()) {
                int messageIdIndex = cursor.getColumnIndex(DatabaseHandler.COLUMN_MESSAGE_ID);
                int senderIndex = cursor.getColumnIndex(DatabaseHandler.COLUMN_SENDER);
                String sender = cursor.getString(senderIndex);
                int messageId = cursor.getInt(messageIdIndex);

                String displayMessage = sender + ": " + message;
                addMessage(messageId, displayMessage);

                cursor.close();
            }

            messageEditText.setText("");
        } else {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadMessages() {
        String recipient = getIntent().getStringExtra("RECIPIENT_NAME");
        String userName = getIntent().getStringExtra("USER_NAME");
        Cursor cursor = db.getMessages(recipient, userName);

        if (cursor != null && cursor.moveToFirst()) {
            int senderIndex = cursor.getColumnIndex(DatabaseHandler.COLUMN_SENDER);
            int messageIndex = cursor.getColumnIndex(DatabaseHandler.COLUMN_MESSAGE);
            int messageIdIndex = cursor.getColumnIndex(DatabaseHandler.COLUMN_MESSAGE_ID);

            do {
                int messageId = -1;
                if (messageIdIndex != -1) {
                    messageId = cursor.getInt(messageIdIndex);
                }
                String sender = "";
                if (senderIndex != -1) {
                    sender = cursor.getString(senderIndex);
                }
                String message = cursor.getString(messageIndex);
                String displayMessage = sender + ": " + message;

                addMessage(messageId, displayMessage);
            } while (cursor.moveToNext());

            cursor.close();
        }
    }

    private void addMessage(final int messageId, final String message) {
        LinearLayout messageLayout = new LinearLayout(this);
        messageLayout.setOrientation(LinearLayout.HORIZONTAL);
        messageLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        messageLayout.setPadding(16, 16, 16, 16);

        TextView messageTextView = new TextView(this);
        messageTextView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1));
        messageTextView.setText(message);
        messageTextView.setTextSize(16);
        messageLayout.addView(messageTextView);

        String currentUser = getIntent().getStringExtra("USER_NAME");
        String sender = message.substring(0, message.indexOf(":"));
        boolean isCurrentUserSender = sender.equals(currentUser);

        if (isCurrentUserSender) {
            Button editButton = new Button(this);
            editButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            editButton.setText("Edit");
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editMessage(messageId, message);
                }
            });
            messageLayout.addView(editButton);

            Button deleteButton = new Button(this);
            deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            deleteButton.setText("Delete");
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteMessage(messageId, message);
                }
            });
            messageLayout.addView(deleteButton);
        }

        chatLayout.addView(messageLayout);
    }

    private void deleteMessage(final int messageId, final String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit or Delete Message")
                .setMessage("Choose an action:")
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMessage(messageId);
                        Toast.makeText(ChatActivity.this, "Message deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void editMessage(final int messageId, final String message) {
        final EditText editMessageEditText = new EditText(this);
        final String originalMessage = message.substring(message.indexOf(":") + 2); // Extract the message content
        editMessageEditText.setText(originalMessage);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Message")
                .setView(editMessageEditText)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String editedMessage = editMessageEditText.getText().toString().trim();
                        if (!editedMessage.isEmpty()) {
                            String updatedMessage = message.substring(0, message.indexOf(":") + 2) + editedMessage;
                            updateMessage(messageId, updatedMessage);
                            Toast.makeText(ChatActivity.this, "Message edited", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChatActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void updateMessage(int messageId, String message) {
        String originalMessage = message.substring(message.indexOf(":") + 2);
        db.updateMessage(messageId, originalMessage);
        chatLayout.removeAllViews();
        loadMessages();
    }

    private void deleteMessage(int messageId) {
        db.deleteMessage(messageId);
        chatLayout.removeAllViews();
        loadMessages();
    }

    public void backToMain(View v){
        Intent backToMain = new Intent(this,MainPage.class);
        startActivity(backToMain);
        finish();
    }
}
