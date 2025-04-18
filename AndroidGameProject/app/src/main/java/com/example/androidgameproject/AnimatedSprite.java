package com.example.androidgameproject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Log;

public class AnimatedSprite {
    private int x;
    private int y;
    private int xStep;
    private int yStep;
    private Bitmap myPic;
    private int width;
    private int height;
    private int imgH;
    private int imgW;
    private int baseX = 1200;

    AnimatedSprite(int xStart, int yStart, Bitmap pic, int scrW, int scrH){
        x=xStart;
        y=yStart;
        xStep=50;
        yStep=40;
        myPic=pic;
        width=scrW;
        height=scrH;
        imgH = pic.getHeight();
        imgW = pic.getWidth();
        Log.d("APV",x+" "+y+" "+scrW+" "+scrH);
    }

    public void draw(Canvas canvas){
        Log.d("APV","InDraw "+x+" "+y);
        canvas.drawBitmap(myPic,x,y,null);
    }

    public void drawCentered(Canvas canvas){
        Log.d("APV","InDraw "+x+" "+y);
        int xc = x - myPic.getWidth()/2;
        int yc = y - myPic.getHeight()/2;
        canvas.drawBitmap(myPic,xc,yc,null);
    }

    public void moveLeft() {
        x = x - xStep;
    }

    public void moveRight() {
        x = x + xStep;
    }

    public void moveUp() {
        y = y - yStep;
    }

    public void moveDown() {
        y = y + yStep;
    }

    public void setPic (Bitmap updPic){
        myPic = updPic;
    }

    public int getX() {
        return x;
    }

    public int getY() { return y; }

    public void setSlideHeight() {
        y = y + yStep + 20;
    }

    public void setRunningHeight() {
        y = y - yStep - 20;
    }

    public void setObsBack() {
        x = baseX;
    }

    //public void setObsSpeed(int newStep) { xStep = newStep; }

    //public void setJumpSpeed(int newStep) { yStep = newStep; }

    public Bitmap getBitmap() {
        return myPic;
    }

    public Rect getBounds() {
        return new Rect(x, y, x + myPic.getWidth(), y + myPic.getHeight());
    }
}
