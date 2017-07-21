package info.ipd9.asyncdownloader;

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

    private final static String TAG = "MainActivity";

    EditText etUrl;
    ProgressBar pbProgress;
    TextView tvProgress;
    Button btDownload;

    // TODO: Save URL in prefernces when Button clicked and onPause
    // TODO: Restore URL from preferences in onCreate

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        etUrl = (EditText) findViewById(R.id.etUrl);
        pbProgress = (ProgressBar) findViewById(R.id.pbProgress);
        tvProgress = (TextView) findViewById(R.id.tvProgress);
        btDownload = (Button) findViewById(R.id.btDownload);
    }

    @Override
    public void onResume() {
        super.onResume();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String defaultUrl = preferences.getString("default_url", "");
        etUrl.setText(defaultUrl);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        savePreferences();
    }

    private void savePreferences() {
        Log.v(TAG, "savePreferences()");
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("default_url", etUrl.getText().toString());
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.miSetting:
            {
                Log.d(TAG, "Menu item Settings selected");
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            }
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    AsyncDownloader downloader;

    public void onDownloadClick(View v) {
        savePreferences();
        downloader = new AsyncDownloader();
        try {
            URL url = new URL(etUrl.getText().toString());
            downloader.execute(url);
        } catch (MalformedURLException e) {
            Toast.makeText(this, "URL invalid", Toast.LENGTH_SHORT).show();
        }
    }

    private class AsyncDownloader extends AsyncTask<URL, Long, String> {

        private final static String TAG = "AsyncDownloader";

        @Override
        protected String doInBackground(URL... params) {
            Log.v(TAG, "doInBackground() started");
            // generate a random filename
            String filename = new BigInteger(130, new SecureRandom()).toString();
            long totalContentLength = 0L, downloadedContentLength = 0L;
            InputStream input = null;
            OutputStream output = null;
            URL url = params[0];
            try {
                // open output file for writing
                output = new BufferedOutputStream(openFileOutput(filename, Context.MODE_PRIVATE));
                // open URL for reading
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                int httpCode = connection.getResponseCode();
                Log.d(TAG, "HTTP code " + httpCode + " GET " + url.toString());
                if (httpCode / 100 != 2) {
                    // not a 2xx http code => problem
                    return null;
                }
                input = connection.getInputStream();
                totalContentLength = connection.getContentLength();
                // read from internet, write to file
                byte [] buffer = new byte[1024];
                int singleReadCount;
                while ((singleReadCount = input.read(buffer)) != -1) {
                    downloadedContentLength += singleReadCount;
                    // FIXME: handle if total length is NOT known
                    long percentage = 100 * downloadedContentLength / totalContentLength;
                    publishProgress(percentage, downloadedContentLength / 1024);
                    output.write(buffer);
                    // internet is too fast, slow it down for this exercise
                    try {
                        Thread.sleep(500); // 500ms delay
                    } catch (InterruptedException e) {}
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                // cleanup, must catch exceptions separately to ensure we attempt to close both streams
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return filename;
        }

        @Override
        protected void onProgressUpdate(Long... progress) {
            long perc = progress[0];
            long kb = progress[1];
            Log.v(TAG, String.format("progress: %d, %d", perc, kb));
            tvProgress.setText(String.format("Downloaded %d kb (%d%%)", kb, perc));
            pbProgress.setProgress((int)perc);
        }

        @Override
        protected void onPreExecute()
        {
            Log.v(TAG, "onPreExecute()");
            btDownload.setEnabled(false);
        }

        @Override
        protected void onPostExecute(String filename) {
            Log.v(TAG, "onPostExecute()");
            btDownload.setEnabled(true);
            // TODO: show result somehow
        }

    }

}
