package info.ipd9.helloage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;



public class SayHelloActivity extends AppCompatActivity {
   private TextView   tvHelloMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_say_hello);

    //binding
       tvHelloMessage = (TextView) findViewById(R.id. tvHelloMessage);

    }

    @Override
    protected void onStart() {

        super.onStart();

        Intent intent = getIntent();
        String name = intent.getStringExtra(MainActivity.EXTRA_NAME);
        String age = intent.getStringExtra(MainActivity.EXTRA_AGE);

        String msg = String.format("hello %s,you are %s y/o", name,age);
        tvHelloMessage.setText(msg);


    }

}
