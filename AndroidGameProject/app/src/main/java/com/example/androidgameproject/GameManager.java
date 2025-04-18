package com.example.androidgameproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class GameManager  extends SurfaceView implements Runnable {
    private int scrHeight;
    private int scrWidth;
    private Canvas myCanvas;
    private int SLEEP_TIME;
    private int lastScore;
    private SurfaceHolder holder;
    private Bitmap aPic1;
    private Bitmap aPic2;
    private Bitmap aPic3;
    private Bitmap aPic4;
    private Bitmap aPic5;
    private Bitmap aPic6;
    private Bitmap aPic7;
    private Bitmap aPic8;
    private Bitmap aPic9;
    private Bitmap aPic10;
    private Bitmap aPic11;
    private Bitmap bPic1;
    private Bitmap bPic2;
    private Bitmap cPic1;
    private Bitmap cPic2;
    private Bitmap dPic1;
    private Bitmap ePic1;
    private Bitmap fPic1;
    private Bitmap gPic1;
    private AnimatedSprite as1; // runner
    private AnimatedSprite as2; // speaker
    private AnimatedSprite as3; // rolling ball
    private AnimatedSprite as4; // spike
    private AnimatedSprite as5; // 4 spikes
    private AnimatedSprite as6; // drop obstacle 1
    private AnimatedSprite as7; // drop obstacle 2
    private AnimatedSprite obsAs; // obstacle each time
    private Thread thread;
    private int obsCounter;
    private int keepRunning;
    private int maxKeepRunning = 16;
    private double upPart = 0.625;
    // private int currObsSpeed = 50;
    private int baseY = 1550;
    private int baseX = 1150;
    private int characterBaseX = 250;
    private int bgIndex = 0;
    private Context context;
    private boolean endedGame;
    private MusicManager musicManager;
    private Paint scorePaint;
    private static int score = 0;
    private Random random;
    private boolean isStanding;
    private boolean isJumping;
    private boolean isSliding;
    private Matrix ballMatrix;

    GameManager(Context context, int width, int height) {
        super(context);
        this.context = context;
        musicManager = new MusicManager();
        random = new Random();
        scrWidth = width;
        scrHeight = height;
        holder = getHolder();
        myCanvas = new Canvas();
        ballMatrix = new Matrix();
        ballMatrix.postRotate(2);
        aPic1 = BitmapFactory.decodeResource(getResources(), R.drawable.run1);
        as1 = new AnimatedSprite(characterBaseX, baseY, aPic1,scrWidth,scrHeight);
        aPic2 = BitmapFactory.decodeResource(getResources(), R.drawable.run2);
        aPic3 = BitmapFactory.decodeResource(getResources(), R.drawable.run3);
        aPic4 = BitmapFactory.decodeResource(getResources(), R.drawable.run4);
        aPic5 = BitmapFactory.decodeResource(getResources(), R.drawable.run5);
        aPic6 = BitmapFactory.decodeResource(getResources(), R.drawable.run6);
        aPic7 = BitmapFactory.decodeResource(getResources(), R.drawable.jump1);
        aPic8 = BitmapFactory.decodeResource(getResources(), R.drawable.jump2);
        aPic9 = BitmapFactory.decodeResource(getResources(), R.drawable.jump3);
        aPic10 = BitmapFactory.decodeResource(getResources(), R.drawable.stand1);
        aPic11 = BitmapFactory.decodeResource(getResources(), R.drawable.slideresized);
        aPic11 = Bitmap.createScaledBitmap(aPic11, (int)(aPic11.getWidth() * 0.75), (int)(aPic11.getHeight() * 0.75), true);

        keepRunning = 0;
        SLEEP_TIME = 55;
        lastScore = 0;
        endedGame = false;
        isStanding = false;

        scorePaint = new Paint();
        scorePaint.setColor(Color.BLACK);        // Set the color of the text
        scorePaint.setTextSize(100);             // Set the size of the text
        scorePaint.setAntiAlias(true);           // Optional: Smooth the text
        scorePaint.setTypeface(Typeface.DEFAULT_BOLD);  // Optional: Set a bold font style

        // volume
        bPic1 = BitmapFactory.decodeResource(getResources(), R.drawable.speaker);
        bPic2 = BitmapFactory.decodeResource(getResources(), R.drawable.mute);
        as2 = new AnimatedSprite(60,320, bPic1,scrWidth,scrHeight);

        obsCounter = 5;

        // rolling ball
        cPic1 = BitmapFactory.decodeResource(getResources(), R.drawable.downobs31);
        as3 = new AnimatedSprite(baseX + 100, baseY + 200, cPic1 ,scrWidth,scrHeight);
        cPic2 = Bitmap.createScaledBitmap(cPic1, cPic1.getWidth(), cPic1.getHeight(), false);

        // one spike
        dPic1 = BitmapFactory.decodeResource(getResources(), R.drawable.downobs1);
        as4 = new AnimatedSprite(baseX, baseY + 90,dPic1,scrWidth,scrHeight);

        // four spikes
        ePic1 = BitmapFactory.decodeResource(getResources(), R.drawable.downobs2);
        as5 = new AnimatedSprite(baseX, baseY + 180,ePic1,scrWidth,scrHeight);

        // drop obstacle 1
        fPic1 = BitmapFactory.decodeResource(getResources(), R.drawable.dropobs1);
        as6 = new AnimatedSprite(baseX, baseY - 360, fPic1,scrWidth,scrHeight);

        // drop obstacle 2
        gPic1 = BitmapFactory.decodeResource(getResources(), R.drawable.dropobs2);
        as7 = new AnimatedSprite(baseX, baseY - 360, gPic1,scrWidth,scrHeight);

        thread = new Thread(this);
        thread.start();
    }

    private void drawSurface() {
        if (holder.getSurface().isValid()) {
            myCanvas = holder.lockCanvas();
            bgFlow();
            as1.draw(myCanvas);
            as2.draw(myCanvas);
            as3.drawCentered(myCanvas); // spikes ball
            as4.draw(myCanvas);
            as5.draw(myCanvas);
            as6.draw(myCanvas);
            as7.draw(myCanvas);
            // Draw the score at the top left corner
            myCanvas.drawText("Score: " + score, 340, 420, scorePaint);
            // להוסיף best score שתוצג תמיד ליד התוצאה הנוכחית
            // add pause button
            holder.unlockCanvasAndPost(myCanvas);
        }
    }

    private void bgFlow() {
        int bgWidth = BitmapFactory.decodeResource(getResources(), R.drawable.midbg).getWidth();
        myCanvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.leftbg), bgIndex, 250, null);
        myCanvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.midbg), bgIndex + bgWidth, 250, null);
        myCanvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.rightbg), bgIndex + (2 * bgWidth), 250, null);

        bgIndex -= 10;
        if (bgIndex <= (-2 * bgWidth)) {
            bgIndex = 0;
        }

        // try to ensure that there's always two parts ahead,
        // and once one part moves out of view, set it back
    }

    public void changeToSpeakerImage() {
        as2.setPic(bPic1);
    }

    public void changeToMuteImage() {
        as2.setPic(bPic2);
    }

    public int rollObs(){
        return random.nextInt(obsCounter) + 1;
    }

    public boolean isBoundingBoxCollision(AnimatedSprite sprite1, AnimatedSprite sprite2) {
        return sprite1.getBounds().intersect(sprite2.getBounds());
    }

    private Rect getOverlapBounds(Rect rect1, Rect rect2) {
        return new Rect(
            Math.max(rect1.left, rect2.left),
            Math.max(rect1.top, rect2.top),
            Math.min(rect1.right, rect2.right),
            Math.min(rect1.bottom, rect2.bottom)
        );
    }

    private boolean isPixelNonTransparent(Bitmap bitmap, int x, int y) {
        if (x < 0 || y < 0 || x >= bitmap.getWidth() || y >= bitmap.getHeight()) {
            return false;
        }
        int pixel = bitmap.getPixel(x, y);
        return (pixel & 0xFF000000) != 0; // Check if alpha is not 0
    }

    public boolean isPixelPerfectCollision(AnimatedSprite sprite1, AnimatedSprite sprite2) {
        if (!isBoundingBoxCollision(sprite1, sprite2)) {
            return false;
        }

        // Get overlapping rectangle
        Rect overlap = getOverlapBounds(sprite1.getBounds(), sprite2.getBounds());

        Bitmap bitmap1 = sprite1.getBitmap();
        Bitmap bitmap2 = sprite2.getBitmap();

        for (int x = overlap.left; x < overlap.right; x++) {
            for (int y = overlap.top; y < overlap.bottom; y++) {
                // Translate coordinates relative to each sprite
                int sprite1X = x - sprite1.getX();
                int sprite1Y = y - sprite1.getY();
                int sprite2X = x - sprite2.getX();
                int sprite2Y = y - sprite2.getY();

                // Check pixel transparency
                if (isPixelNonTransparent(bitmap1, sprite1X, sprite1Y) &&
                        isPixelNonTransparent(bitmap2, sprite2X, sprite2Y)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void run() {
        int i = 0;
        int sentObsCounter = -40;
        int obsRandNumber = rollObs();
        while (!endedGame) {
            if(!isStanding){
                if (keepRunning>0) {
                    if(isJumping){
                        if(keepRunning>(upPart*maxKeepRunning)) {
                            as1.setPic(aPic7);
                            as1.moveUp();
                        }
                        else if(keepRunning>((1-upPart)*maxKeepRunning)) {
                            as1.setPic(aPic9);
                        }
                        else{
                            as1.setPic(aPic8);
                            as1.moveDown();
                        }
                    }
                    else if(isSliding){
                        as1.setPic(aPic11);
                    }
                    keepRunning--;

                    if(keepRunning == 0){
                        if(isSliding){
                            as1.setRunningHeight();
                        }
                        isJumping = false;
                        isSliding = false;
                    }
                }
                else {
                    switch (i % 6) {
                        case 0:
                            as1.setPic(aPic1);
                            break;
                        case 1:
                            as1.setPic(aPic2);
                            break;
                        case 2:
                            as1.setPic(aPic3);
                            break;
                        case 3:
                            as1.setPic(aPic4);
                            break;
                        case 4:
                            as1.setPic(aPic5);
                            break;
                        case 5:
                            as1.setPic(aPic6);
                            break;
                    }
                    i++;
                }
                if(sentObsCounter > 0){

                    switch (obsRandNumber) {
                        case 1:
                            obsAs = as3;
                            ballMatrix.postRotate(-15);
                            cPic2 = Bitmap.createBitmap(cPic1, 0, 0, cPic1.getWidth(), cPic1.getHeight(), ballMatrix, true);
                            obsAs.setPic(cPic2);
                            break;
                        case 2:
                            obsAs = as4;
                            break;
                        case 3:
                            obsAs = as5;
                            break;
                        case 4:
                            obsAs = as6;
                            break;
                        case 5:
                            obsAs = as7;
                            break;
                    }
                    obsAs.moveLeft();

                    if(isPixelPerfectCollision(as1, obsAs)){
                        endedGame = true;
                        musicManager.stopMusic();
                        Intent intent = new Intent(context, EndScreenActivity.class);
                        intent.putExtra("final_score", score);
                        context.startActivity(intent);
                        resetScore();
                        // make sure to start backround music again after the user comes back
                    }

                    if(obsAs.getX() < (characterBaseX - obsAs.getBitmap().getWidth())){
                        score += 10;
                        // passed obstacle
                    }
                    if(obsAs.getX() < -150){
                        sentObsCounter = -30;
                        obsRandNumber = rollObs();
                        obsAs.setObsBack();
                        as3.setPic(cPic1);
                    }
                }

                if(lastScore < (score - 250)){
                    // SLEEP_TIME = (int)(SLEEP_TIME * 0.75);
                    // make steps - x steps - larger every time and make the small at the start
                    lastScore = score;
                }

                if(!endedGame){
                    score++;
                    drawSurface();
                    sentObsCounter++;
                }
            }
            try {
                // Thread.sleep(SLEEP_TIME);
                Thread.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void jump() {
        keepRunning = maxKeepRunning;
        isJumping = true;
    }

    public void slide()
    {
        keepRunning = (int) (maxKeepRunning * 0.8);
        as1.setSlideHeight();
        isSliding = true;
    }

    public boolean isNotJumping() {
        return keepRunning == 0;
    }

    public void stand() {
        as1.setPic(aPic10);
        isStanding = true;
    }

    public void stopStanding() {
        isStanding = false;
    }

    public void resetScore() {
        score = 0;
    }
}

