package com.yairHouse.snookerhall;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 *  Created by Lavy and Ronit .
 */
public class CustomView extends SurfaceView implements Runnable{
    private Context context;

    private SurfaceHolder holder;
    private Thread thread; // animation loop thread
    private Paint bgPaint;
    public static final int MAX_LEVEL = 2; // the last exists level


    private float wScreen, hScreen; // screen dimentions

    private Bitmap picRacket, picBall;
//    private Bitmap [] picGreenBricks;
    private Bitmap [][] picBricks; // all bricks pictures

    private Racket racket; // the player's racket
    private Ball ball; // the playing ball
    //private Brick  brick;
    private BricksCollection bc; // bricks collection

    private  Statistics data; // game data
    public static final int POINT_BRICK = 5; // points for eack brick collision
    public static final int POINT_RACKET = 1; // points for each racket bounce


    private int gameStatus;
   // preferences
    private AppPreference appPref;
    private boolean isSound;
    private int level = 1;



    private SoundManager sm;
    public static final int GAME_STOP = 1;
    public static final int GAME_RUN = 2;

    public float oldEx; // previous value of the touch

    private Handler finishHandler;


    public CustomView(Context context, float wScreen, float hScreen, Handler h) {
        super(context);


        this.context = context;
        sm = new SoundManager(context);
        holder = getHolder();
        this.finishHandler = h;

        // preferences
        appPref = new AppPreference(this.context);
        this.isSound = appPref.getSoundSw();

        bgPaint = new Paint();
        bgPaint.setColor(Color.BLACK);

        picRacket = BitmapFactory.decodeResource(getResources(), R.drawable.racket);
        picBall =  BitmapFactory.decodeResource(getResources(), R.drawable.ball);

        picBricks = new Bitmap[6][2];
        picBricks[0][0] = BitmapFactory.decodeResource(getResources(), R.drawable.green_brick);
        picBricks[0][1] = BitmapFactory.decodeResource(getResources(), R.drawable.green_brick_cracked);
        picBricks[1][0] = BitmapFactory.decodeResource(getResources(), R.drawable.orange_brick);
        picBricks[1][1] = BitmapFactory.decodeResource(getResources(), R.drawable.orange_brick_cracked);
        picBricks[2][0] = BitmapFactory.decodeResource(getResources(), R.drawable.blue_brick);
        picBricks[2][1] = BitmapFactory.decodeResource(getResources(), R.drawable.blue_brick_cracked);
        picBricks[3][0] = BitmapFactory.decodeResource(getResources(), R.drawable.yellow_brick);
        picBricks[3][1] = BitmapFactory.decodeResource(getResources(), R.drawable.yellow_brick_cracked);
        picBricks[4][0] = BitmapFactory.decodeResource(getResources(), R.drawable.violet_brick);
        picBricks[4][1] = BitmapFactory.decodeResource(getResources(), R.drawable.violet_brick_cracked);
        picBricks[5][0] = BitmapFactory.decodeResource(getResources(), R.drawable.red_brick);
        picBricks[5][1] = BitmapFactory.decodeResource(getResources(), R.drawable.red_brick_cracked);

        this.wScreen = wScreen;
        this.hScreen = hScreen;

        initNewGame();

        //------------ thread start----------------
        thread = new Thread(this);
        thread.start();

    }

    /**
     * Initializes all objects for new game
     */
    public void initNewGame()
    {
        racket = new Racket(wScreen, hScreen, picRacket);
        ball = new Ball(wScreen, hScreen, racket.getY(), picBall );

        bc = new BricksCollection(wScreen, hScreen, level, picBricks);
        data = new Statistics(wScreen, hScreen, picBall, level);

        gameStatus = GAME_RUN;
    }
    



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float ex = event.getX();
        float ey = event.getY();
        if ( event.getAction() == MotionEvent.ACTION_DOWN)
        {
            oldEx = ex;
        }
        if ( event.getAction() == MotionEvent.ACTION_MOVE)
        {
            racket.moveTo(ex);
            if( !ball.isInMove()) {
                ball.startsMoving(ex - oldEx);
                oldEx = ex;
            }
        }
        return true;
    }

    public void drawSurface()
    {
        if (holder.getSurface().isValid()) {
            Canvas canvas = holder.lockCanvas();
            canvas.drawPaint(bgPaint);
            racket.draw(canvas);
            ball.draw(canvas);
            bc.draw(canvas);
            data.draw(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void run() {
        while (gameStatus == GAME_RUN )
        {
            drawSurface();
            // wall bounce
            if (ball.moveIt())
                if (this.isSound)
                    sm.playBallBounce();
            // racket bounce
            if (ball.intersects(racket)) {
                if (this.isSound)
                    sm.playBallBounce();
                data.incScore(POINT_RACKET);
            }
            // collision between the ball and the bricks
            if(bc.intersects(ball)) {

                if (this.isSound)
                    sm.playBlip();
                data.incScore(POINT_BRICK);
            }

            // lost ball
            if ( ball.isLost())
            {
                racket.initLocation();
                ball.initLocation(racket.getY());
                ball.setInMove(false);
                data.decNumBalls();
                drawSurface();
                if ( data.isEmpty())
                {

                    gameStatus = GAME_STOP;
                    gameFinish(1); // doesn't finish level
                }

            }

            // check win
            if ( bc.isEmpty())
            {
                drawSurface();
               level++;
                if (level > MAX_LEVEL) {
                    gameStatus = GAME_STOP;
                    appPref.setPrefHighScore(data.getScore());
                    gameFinish(0); // success message
                }
                else // play the next level
                {
                    initNewGame();
                }

            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends message to main activity
     * @param res 0 - finished successfully, 1 - not finished
     */
    public void gameFinish(int res)
    {
        Message msg = finishHandler.obtainMessage();
        msg.arg1 = res;
        finishHandler.sendMessage(msg);
    }
    public void startNewGame()
    {
        if ( level > MAX_LEVEL)
            level = 1;
        initNewGame();
        thread = new Thread(this);
        thread.start();
    }
    public void pause()
    {
        gameStatus = GAME_STOP;
    }

    public void resume()
    {
        gameStatus = GAME_RUN;
    }


}
