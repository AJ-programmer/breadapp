package com.example.breadapp;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHelper {
    private static final String TAG = "ConnectionHelper";
   
    private static final String IP = "192.168.1.106";
    private static final String DATABASE = "ProgrammingDB";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "sql12345678"; 
    private static final String PORT = "1433";

    private static final String CONNECTION_URL =
            "jdbc:jtds:sqlserver://" + IP + ":" + PORT + ";" +
                    "databasename=" + DATABASE + ";user=" + USERNAME +
                    ";password=" + PASSWORD + ";";

   
    public void getConnection(ConnectionCallback callback) {
        new DatabaseConnectionTask(callback).execute();
    }

    
    public interface ConnectionCallback {
        void onConnectionSuccess(Connection connection);
        void onConnectionFailure(Exception e);
    }

  
    private static class DatabaseConnectionTask extends AsyncTask<Void, Void, Connection> {
        private Exception exception = null;
        private ConnectionCallback callback;

        DatabaseConnectionTask(ConnectionCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Connection doInBackground(Void... voids) {
            Connection connection = null;
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                connection = DriverManager.getConnection(CONNECTION_URL);
            } catch (ClassNotFoundException | SQLException e) {
                Log.e(TAG, "Database connection error: " + e.getMessage());
                exception = e;
            }
            return connection;
        }

        @Override
        protected void onPostExecute(Connection connection) {
            if (connection != null && exception == null) {
                callback.onConnectionSuccess(connection);
            } else {
                callback.onConnectionFailure(exception);
            }
        }
    }
}
