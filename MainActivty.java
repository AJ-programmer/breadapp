package com.example.breadapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView textView1;
    private TextView textView2;
    private Button btnGetData;
    private Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView1 = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        btnGetData = findViewById(R.id.buttonGetData);

        btnGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTextFromSQL();
            }
        });
    }

    private void getTextFromSQL() {
        
        textView1.setText("Loading...");
        textView2.setText("Loading...");

        
        ConnectionHelper connectionHelper = new ConnectionHelper();
        connectionHelper.getConnection(new ConnectionHelper.ConnectionCallback() {
            @Override
            public void onConnectionSuccess(Connection conn) {
                connection = conn;

                try {
                    String query = "SELECT * FROM sign_up";
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query);

                    if (resultSet.next()) {
                        final String value1 = resultSet.getString(1);
                        final String value2 = resultSet.getString(2);

                        
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView1.setText(value1);
                                textView2.setText(value2);
                            }
                        });
                    } else {
                        
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView1.setText("No data found");
                                textView2.setText("No data found");
                                Toast.makeText(MainActivity.this,
                                        "No records in the database",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    resultSet.close();
                    statement.close();
                    connection.close();

                } catch (SQLException e) {
                    Log.e(TAG, "Database query error: " + e.getMessage());
                    showErrorOnUiThread("Query failed: " + e.getMessage());
                }
            }

            @Override
            public void onConnectionFailure(Exception e) {
                Log.e(TAG, "Connection failed: " + e.getMessage());
                showErrorOnUiThread("Connection failed");
            }
        });
    }

    private void showErrorOnUiThread(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView1.setText("Error");
                textView2.setText(message);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void GetTextFromSQL(View v) {
        getTextFromSQL();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                Log.e(TAG, "Error closing connection: " + e.getMessage());
            }
        }
    }
}
