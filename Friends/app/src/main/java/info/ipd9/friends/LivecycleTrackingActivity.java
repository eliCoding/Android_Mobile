package info.ipd9.friends;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
/**
 * Created by ipd on 7/17/2017.
 */

public class LivecycleTrackingActivity extends AppCompatActivity {

    private void log(String event) {
        String tag = this.getClass().getSimpleName();
        Log.v(tag, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("AAA onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        log("AAA onStart");
    }

    protected void onRestart() {
        super.onRestart();
        log("AAA onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        log("AAA onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        log("AAA onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        log("AAA onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log("AAA onDestroy");
    }
}
