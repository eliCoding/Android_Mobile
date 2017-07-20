package info.ipd9.asyncdownloader;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "AsyncDownloader";
    EditText etUrl;
    ProgressBar pbProgressbar;
    Button btDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //
        etUrl = (EditText)findViewById(R.id.etUrl);
        pbProgressbar = (ProgressBar) findViewById(R.id.pbProgress);
        btDownload= (Button) findViewById(R.id.btDownload);
    }

    AsyncDownloader downloader;

    public void onDownloadClick(View v) {
      downloader = new AsyncDownloader();
        try {
            URL url = new URL(etUrl.getText().toString());
            downloader.execute(url);
        }catch (MalformedURLException e) {
            Toast.makeText(this, "URL invslid", Toast.LENGTH_SHORT).show();
        }
    }

    //Inner class
    //Acynctask is a generic class the first parameter is url passing to the user to download , second parameter is how we will passing the value, the 3rd parameter is what we return
    private  class AsyncDownloader extends AsyncTask<URL, Integer,File>{


        @Override
        protected File doInBackground(URL... urls) {
            return null;
        }

        @Override
        protected  void onProgressUpdate(Integer... progress) {
               int perc  = progress[0];
               int kb = progress[1];
            Log.v(TAG, String.format("progress: %d, %d", perc, kb));

        }


        @Override
        protected  void onPreExecute() {
           btDownload.setEnabled(false);

        }

        @Override
        protected  void onPostExecute(File file) {

            btDownload.setEnabled(true);
            //TODO: show result somehow
        }


    }



}
