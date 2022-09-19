package arc_watertank.pack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsActivity extends AppCompatActivity {

    Button submit, cancel;
    TextView  push;
    EditText height;
    DatabaseReference FBDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_settings);
        submit = findViewById(R.id.Submit);
        cancel = findViewById(R.id.Cancel);
        height = findViewById(R.id.editTextHeight);
        push = findViewById(R.id.wifi_push);
        height.setTextColor(getResources().getColor(R.color.black));
        FBDB = FirebaseDatabase.getInstance().getReference();


        submit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                    // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                            (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        // Perform action on key press
                        SetHeightText();
                        return true;
                    }
                    return false;
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SetHeightText();
            }

        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ArcHomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                FBDB.child("/Wifi/Reset").setValue("high");
                handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            FBDB.child("/Wifi/Reset").setValue("low");
                                        }
                                    }
                        ,1000);

                Intent intent = new Intent(SettingsActivity.this, ArcHomeActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }

    public void SetHeightText(){
        try {
            int H = Integer.parseInt(String.valueOf(height.getText()));

            if (H < 10) {
                height.setTextColor(getResources().getColor(R.color.ARC_red));
                Toast.makeText(getApplicationContext(),"Height is not set properly",Toast.LENGTH_SHORT).show();
            }
            else{
                height.setTextColor(getResources().getColor(R.color.black));
                int upper_Limit, lower_Limit;

                upper_Limit = (H*90)/100;
                lower_Limit = (H*10)/100;

                FBDB.child("/Limit/upper").setValue(upper_Limit);
                FBDB.child("/Limit/lower").setValue(lower_Limit);

                height.setText("");

                Intent intent = new Intent(SettingsActivity.this, ArcHomeActivity.class);
                startActivity(intent);
                finish();
            }
        }
        catch (Exception e){
            height.setText("");
            height.setTextColor(getResources().getColor(R.color.black));
            Toast.makeText(getApplicationContext(),"Problem: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingsActivity.this, ArcHomeActivity.class);
        startActivity(intent);
        finish();
    }
}