package com.biosens.biosens;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {
    EditText EditLog,EditPass;
    private TextView TextViewWrong;
    SQLiteDatabase db;
    SQLiteOpenHelper biosensDatabaseHelper;
    Cursor cursor;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManagement session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        session = new SessionManagement(this);
        EditLog = (EditText) findViewById(R.id.editLog);
        EditPass =(EditText) findViewById(R.id.editPass);
        Toast.makeText(this, "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();
        try {
            biosensDatabaseHelper = new BioSensDatabaseHelper(this);
            db = biosensDatabaseHelper.getReadableDatabase();
            cursor = db.query("Test",
                    new String[] {"Field"},
                    null,
                    null,
                    null, null,null);
//Код работы с курсором
        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    public void onClickEnter(View v) {
        String username = EditLog.getText().toString();
        String password = EditPass.getText().toString();

        try {
            biosensDatabaseHelper = new BioSensDatabaseHelper(this);
            db = biosensDatabaseHelper.getReadableDatabase();
            cursor = db.query("User",
                    new String[] {"_id"},
                    "LOGIN= ? AND PASSWORD= ?",
                    new String[] {username,password},
                    null, null,null);
//Код работы с курсором
        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }


        // Check if username, password is filled
        if (username.trim().length() > 0 && password.trim().length() > 0) {
            // For testing puspose username, password is checked with sample data

            if (cursor.moveToFirst()) {

                // Creating user login session
                // For testing i am stroing name, email as follow
                // Use user real data
                session.createLoginSession(cursor.getInt(0));

                // Staring MainActivity
                Intent i = new Intent(StartActivity.this, MainActivity.class);
                startActivity(i);
                finish();

            } else {
                // username / password doesn't match
                alert.showAlertDialog(StartActivity.this, "Login failed..", "Username/Password is incorrect", false);
            }
        } else {
            // user didn't entered username or password
            // Show alert asking him to enter the details
            alert.showAlertDialog(StartActivity.this, "Login failed..", "Please enter username and password", false);
        }




    cursor.close();
        db.close();
    }
}
