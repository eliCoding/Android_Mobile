package info.ipd9.asyncdownloader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;

public class MainActivity extends AppCompatActivity {

    EditText etUrl;
    ProgressBar pbProgress;
    TextView tvProgress;
    Button btDownload;
    private static String TAG = "MainActivity";

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUrl = (EditText) findViewById(R.id.etUrl);
        pbProgress = (ProgressBar) findViewById(R.id.pbProgress);
        tvProgress = (TextView) findViewById(R.id.tvProgress);
        btDownload = (Button) findViewById(R.id.btDownload);
    }

    @Override
    public void onResume(){
        super.onResume();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String defaultUrl = preferences.getString("default_url", "");
        etUrl.setText(defaultUrl);
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause()");
        savePreference();
    }

    private void savePreference(){
        Log.d(TAG, "savePreference()");
        //SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("default_url", etUrl.getText().toString());
        editor.commit();
    }

    AsyncDowloader dowloader;

    public void onDownloadClick(View v){
        savePreference();
        dowloader = new AsyncDowloader();
        try{
            URL url = new URL(etUrl.getText().toString());
            dowloader.execute(url);
        } catch (MalformedURLException e){
            Toast.makeText(this, "URL invalid", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.miSettings:
            {
                Log.d(TAG, "Menu item Settings selected");
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
            }
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private class AsyncDowloader extends AsyncTask<URL, Long, String> {
        private  final static String TAG = "AsyncDowloader" ;

        @Override
        protected String doInBackground(URL...params){
            Log.v(TAG, "doInBackground() started");
            String filename = new BigInteger(130, new SecureRandom()).toString();
            long totalContentLength = 0L, downloadedContentLength = 0L;
            InputStream input = null;
            OutputStream output = null;
            URL url = params[0];

            try{
                output = new BufferedOutputStream(openFileOutput(filename, Context.MODE_PRIVATE));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                int httpCode = connection.getResponseCode();
                Log.v(TAG, "HTTP code " + httpCode + " GET " + url.toString());
                if (httpCode / 100 != 2){
                    return null;
                }
                input = connection.getInputStream();
                totalContentLength = connection.getContentLength();
                byte [] buffer = new byte[1024];
                int singleReadCount;
                while ((singleReadCount = input.read(buffer))!= -1){
                    downloadedContentLength += singleReadCount;
                    long percentage = 100 * downloadedContentLength / totalContentLength;
                    publishProgress(percentage, downloadedContentLength / 1024);
                    output.write(buffer);
                    try {
                        Thread.sleep(500);
                    }catch (InterruptedException e){

                    }
                }
            }catch (IOException e){
                e.printStackTrace();
                return null;
            }finally {
                if(input != null){
                    try {
                        input.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                if(output != null){
                    try {
                        output.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
            return filename;
        }

        @Override
        protected void onProgressUpdate(Long...progress){
            long perc = progress[0];
            long kb = progress[1];

            Log.v(TAG, String.format("progress: %d, %d", perc, kb));
            tvProgress.setText(String.format("Downloaded %d kb (%d%%)", kb, perc));
            pbProgress.setProgress((int)perc);
        }

        @Override
        protected void onPreExecute(){
            Log.v(TAG, "onPreExecute()");
            btDownload.setEnabled(false);
        }

        @Override
        protected void onPostExecute(String filename){
            Log.v(TAG, "onPostExecute()");
            btDownload.setEnabled(true);
        }

    }
}
