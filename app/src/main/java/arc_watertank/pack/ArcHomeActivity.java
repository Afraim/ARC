package arc_watertank.pack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Struct;


public class ArcHomeActivity extends AppCompatActivity {

    Button autoButton1, manualButton1, OnButton1, OffButton1;
    TextView settings;
    LinearLayout AutoL, ManuL;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference WaterRef = FirebaseDatabase.getInstance().getReference("/WaterLevel");
    boolean Autochk = false;
    boolean Manualchk = false;
    boolean Onchk = false;
    boolean Offchk = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arc_home);
        getSupportActionBar().hide();

        autoButton1 = findViewById(R.id.autoButton);
        manualButton1 = findViewById(R.id.manualButton);
        OnButton1 = findViewById(R.id.ONButton);
        OffButton1 = findViewById(R.id.OFFButton);
        settings = findViewById(R.id.settings_button);
        ProgressBar progressBar = findViewById(R.id.progressbar);
        AutoL = findViewById(R.id.autoLayout);
        ManuL = findViewById(R.id.manualLayout);


        WaterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int level = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
                TextView pText = findViewById(R.id.percentageTxt);
                progressBar.setProgress(level);
                if(level<0 || level >100 ){
                    pText.setText("..." +" %");
                }
                else{
                    pText.setText(level+" %");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        autoButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Autochk) {
                    dbRef.child("MotoStatus").setValue("Auto");
                    autoButton1.setTextColor(getResources().getColor(R.color.white));
                    autoButton1.setBackgroundColor(getResources().getColor(R.color.ARC_blue));
                    Autochk = true;
                    Manualchk = false;
                    Onchk = true;
                    Offchk = true;
                    manualButton1.setTextColor(getResources().getColor(R.color.black));
                    manualButton1.setBackgroundColor(getResources().getColor(R.color.gray));
                    OffButton1.setTextColor(getResources().getColor(R.color.black));
                    OffButton1.setBackgroundColor(getResources().getColor(R.color.gray));
                    OnButton1.setTextColor(getResources().getColor(R.color.black));
                    OnButton1.setBackgroundColor(getResources().getColor(R.color.gray));
                    ManuL.setVisibility(View.GONE);

                }
                else{
                    autoButton1.setTextColor(getResources().getColor(R.color.black));
                    autoButton1.setBackgroundColor(getResources().getColor(R.color.gray));
                    Autochk = false;
                }
            }
        });
        manualButton1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!Manualchk) {
                    dbRef.child("MotoStatus").setValue("Manual");
                    manualButton1.setTextColor(getResources().getColor(R.color.white));
                    manualButton1.setBackgroundColor(getResources().getColor(R.color.ARC_blue));
                    Manualchk = true;
                    Autochk = false;
                    autoButton1.setTextColor(getResources().getColor(R.color.black));
                    autoButton1.setBackgroundColor(getResources().getColor(R.color.gray));
                    ManuL.setVisibility(View.VISIBLE);
                }
                else{
                    manualButton1.setTextColor(getResources().getColor(R.color.black));
                    manualButton1.setBackgroundColor(getResources().getColor(R.color.gray));
                    Manualchk = false;
                }
            }
        });
        OnButton1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!Onchk) {
                    dbRef.child("MotoStatus").setValue("ON");
                    OnButton1.setTextColor(getResources().getColor(R.color.white));
                    OnButton1.setBackgroundColor(getResources().getColor(R.color.ARC_blue));
                    Onchk = true;
                    Offchk = false;
                    Autochk = false;
                    autoButton1.setTextColor(getResources().getColor(R.color.black));
                    autoButton1.setBackgroundColor(getResources().getColor(R.color.gray));

                    OffButton1.setTextColor(getResources().getColor(R.color.black));
                    OffButton1.setBackgroundColor(getResources().getColor(R.color.gray));

                }
                else{
                    OnButton1.setTextColor(getResources().getColor(R.color.black));
                    OnButton1.setBackgroundColor(getResources().getColor(R.color.gray));
                    Onchk = false;

                }
            }
        });
        OffButton1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dbRef.child("MotoStatus").setValue("OFF");
                if (!Offchk) {
                    OffButton1.setTextColor(getResources().getColor(R.color.white));
                    OffButton1.setBackgroundColor(getResources().getColor(R.color.ARC_blue));
                    Offchk = true;
                    Onchk = false;
                    autoButton1.setTextColor(getResources().getColor(R.color.black));
                    autoButton1.setBackgroundColor(getResources().getColor(R.color.gray));

                    OnButton1.setTextColor(getResources().getColor(R.color.black));
                    OnButton1.setBackgroundColor(getResources().getColor(R.color.gray));

                }
                else{
                    OffButton1.setTextColor(getResources().getColor(R.color.black));
                    OffButton1.setBackgroundColor(getResources().getColor(R.color.gray));
                    Offchk = false;
                }
            }
        });


        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArcHomeActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }


}