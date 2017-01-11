package com.stephenmaloney.www.nanoman.GameEngine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.stephenmaloney.www.nanoman.GameObjects.GameObject;
import com.stephenmaloney.www.nanoman.GameObjects.Stage;

import java.util.List;

public class GameViewSurface extends SurfaceView implements SurfaceHolder.Callback, GameView{
    private List<GameObject> mGameObjects;
    private boolean mReady;
    final private Matrix mMatrix = new Matrix();

    private final Object LOCK = new Object();

    final Bitmap mBitmapOffscreen;
    final Canvas mCanvasOffscreen;
    final Rect mRectSource = new Rect(0, 0, VIEW_WIDTH, VIEW_HEIGHT);

    public GameViewSurface(Context context) {
        super(context);
        getHolder().addCallback(this);

        mBitmapOffscreen = Bitmap.createBitmap(VIEW_WIDTH, VIEW_HEIGHT, Bitmap.Config.ARGB_8888);
        mCanvasOffscreen = new Canvas(mBitmapOffscreen);

    }

    public GameViewSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);

        mBitmapOffscreen = Bitmap.createBitmap(VIEW_WIDTH, VIEW_HEIGHT, Bitmap.Config.ARGB_8888);
        mCanvasOffscreen = new Canvas(mBitmapOffscreen);

    }

    public GameViewSurface(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);

        mBitmapOffscreen = Bitmap.createBitmap(VIEW_WIDTH, VIEW_HEIGHT, Bitmap.Config.ARGB_8888);
        mCanvasOffscreen = new Canvas(mBitmapOffscreen);
    }

    @Override
    public void draw() {
        if(!mReady) return;

        Canvas canvas = getHolder().lockCanvas();
        if(canvas == null) return;

        canvas.setMatrix(mMatrix);

        synchronized (mGameObjects) {
            int numObjects = mGameObjects.size();
            for (int i = 0; i < numObjects; i++) mGameObjects.get(i).onDraw(mCanvasOffscreen);
        }

        canvas.drawBitmap(mBitmapOffscreen, mRectSource, mGameView, mPaint);

        getHolder().unlockCanvasAndPost(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
        super.onSizeChanged(w, h, oldWidth, oldHeight);

        // calculate scale and location of the game view, and create a scale/translate matrix
        final float pixelScaleFactor = (float)h / VIEW_HEIGHT;
        final float viewLeftEdge = (w / 2.0f) - ((VIEW_WIDTH * pixelScaleFactor) / 2.0f);

        mMatrix.reset();
        mMatrix.postScale(pixelScaleFactor, pixelScaleFactor);
        mMatrix.postTranslate(viewLeftEdge, 0);
    }

    @Override
    public void setGameObjects(List<GameObject> gameObjects) {
        mGameObjects = gameObjects;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mReady = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mReady = false;
    }
}
