package info.ipd9.helloage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;




public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_AGE = "age";
    private EditText etName, etAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
           //Asign binding
        etName = (EditText) findViewById(R.id.etName);
        etAge = (EditText) findViewById(R.id.etAge);

    }

    public void onSayHelloClick(View view) {
        //0. Before all - Create new activity SayHelloActivity
         // 1. Extract name and age strings
        String name = etName.getText().toString();
        String age = etAge.getText().toString();
        //ToDo : check name age are not empty  show msg box if they are

        //2. Create an intent and out name & age into Extras
        Intent intent = new Intent(this, SayHelloActivity.class);
        intent.putExtra(EXTRA_NAME, name);
        intent.putExtra(EXTRA_AGE, age);
        //3. Tell OS to show new Activity with that intent

        startActivity(intent);





    }
}
