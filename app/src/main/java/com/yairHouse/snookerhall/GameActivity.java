package com.yairHouse.snookerhall;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 *  Created by Lavy and Ronit .
 */

public class GameActivity extends AppCompatActivity {


    FrameLayout frmView;
    CustomView cv;
    private boolean on_create_flag;


    private Handler handlerGameFinish = new Handler()
    {
        public void handleMessage(Message msg) {
            int res = msg.arg1; // 0 - finished level, 1- not finished level
            String msgStr = "You finished level successfully! One more game?" ;
            if ( res != 0)
                msgStr = "You doesn't finish level! One more attempt? ";

            AlertDialog alertDialog = new AlertDialog.Builder(GameActivity.this).create();
            alertDialog.setTitle("Arkanoid Game");
            alertDialog.setMessage(msgStr);
            alertDialog.setCancelable(false);

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    cv.startNewGame();
                }
                });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(GameActivity.this, StartActivity.class);
                    startActivity(intent);
                }
            });

            alertDialog.show();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        frmView = findViewById(R.id.frmView);
        on_create_flag = true;

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if ( cv == null && hasFocus) {
            cv = new CustomView(this, frmView.getWidth(), frmView.getHeight(),handlerGameFinish);
            frmView.addView(cv);
        }
    }

    @Override
    protected void onResume() {
        if ( ! on_create_flag)
        {
            cv.pause();
            on_create_flag = false;
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        cv.pause();
        super.onPause();
    }


    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        MenuItem playItem = menu.findItem(R.id.music);
        if (MusicService.isRunning)
            playItem.setIcon(R.drawable.pause);
         else
            playItem.setIcon(R.drawable.play);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //when selcting option in the menu
        // main --> go to menu
        //music --> stop/play music
        //instraction --> go to instraction

        int id = item.getItemId();
        Intent intent = null;

        switch (id) {
            case R.id.music:
                if (MusicService.isRunning) {
                    stopService(new Intent(this, MusicService.class));
                    item.setTitle("UnMute");
                    item.setIcon(R.drawable.play);
                } else {
                    startService(new Intent(this, MusicService.class));
                    item.setTitle("Mute");
                    item.setIcon(R.drawable.pause);
                }
                MusicService.isRunning = !MusicService.isRunning;
                break;
            case R.id.menu_home:
                intent = new Intent(this, StartActivity.class);
                startActivity(intent);
                break;
            case R.id.settMenu:
                intent = new Intent(this, SettingActivity.class);
                finish();
                startActivity(intent);
                break;

        }
        return true;
    }

}