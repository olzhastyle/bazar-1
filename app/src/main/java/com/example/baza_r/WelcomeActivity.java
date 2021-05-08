package com.example.baza_r;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class WelcomeActivity extends AppCompatActivity {
    private TextView HelpText;
    private Button LoginButton, RegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        HelpText = (TextView) findViewById(R.id.welcome_help_text);
        LoginButton = (Button) findViewById(R.id.welcome_login_button);
        RegisterButton = (Button) findViewById(R.id.welcome_register_button);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(WelcomeActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
        HelpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowStartDialog();
            }
        });
    }

    private void ShowStartDialog() {
        new AlertDialog.Builder(this)
                .setTitle("One time dialog")
                .setMessage("this sgould be one" + "/" +
                "hello")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }
}