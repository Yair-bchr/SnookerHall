package com.yairHouse.snookerhall;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnNewGame,btnExit, btnRules, btnDefinitions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_start);

        btnNewGame =(Button)findViewById(R.id.btnNewGame) ;
        btnExit =(Button)findViewById(R.id.btnDefinitions) ;
        btnRules =(Button)findViewById(R.id.btnRules) ;
        btnDefinitions =(Button)findViewById(R.id.btnDefinitions) ;

        btnNewGame.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnRules.setOnClickListener(this);
        btnDefinitions.setOnClickListener(this);

    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
    @Override
    public void onClick(View view) {

        Intent intent = null;
        switch (view.getId()){
            case R.id.btnNewGame:
                intent = new Intent(StartActivity.this, Users.class);
                break;
            case R.id.btnRules:
                intent = new Intent(StartActivity.this, Rules.class);
                break;
            case R.id.btnDefinitions:
                intent = new Intent(StartActivity.this, DefinitionsActivity.class);
                break;
        }
        startActivity(intent);

    }
}