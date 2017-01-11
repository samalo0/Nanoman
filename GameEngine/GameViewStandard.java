package com.stephenmaloney.www.nanoman.GameEngine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.stephenmaloney.www.nanoman.GameObjects.GameObject;

import java.util.List;

public class GameViewStandard extends View implements GameView {
    private List<GameObject> mGameObjects;
    final private Matrix mMatrixViewScale = new Matrix();

    final private Paint mPaint = new Paint();

    final Bitmap mBitmapOffscreen;
    final Canvas mCanvasOffscreen;
    final Rect mRectSource = new Rect(0, 0, VIEW_WIDTH, VIEW_HEIGHT);

    public GameViewStandard(Context context) {
        super(context);

        mBitmapOffscreen = Bitmap.createBitmap(VIEW_WIDTH, VIEW_HEIGHT, Bitmap.Config.ARGB_8888);
        mCanvasOffscreen = new Canvas(mBitmapOffscreen);
    }

    public GameViewStandard(Context context, AttributeSet attrs) {
        super(context, attrs);

        mBitmapOffscreen = Bitmap.createBitmap(VIEW_WIDTH, VIEW_HEIGHT, Bitmap.Config.ARGB_8888);
        mCanvasOffscreen = new Canvas(mBitmapOffscreen);
    }

    public GameViewStandard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mBitmapOffscreen = Bitmap.createBitmap(VIEW_WIDTH, VIEW_HEIGHT, Bitmap.Config.ARGB_8888);
        mCanvasOffscreen = new Canvas(mBitmapOffscreen);
    }

    @Override
    public void draw() {
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        synchronized (mGameObjects) {
            final int numObjects = mGameObjects.size();
            for (int i = 0; i < numObjects; i++) {
                mGameObjects.get(i).onDraw(mCanvasOffscreen);
            }
        }

        canvas.setMatrix(mMatrixViewScale);
        canvas.drawBitmap(mBitmapOffscreen, mRectSource, mGameView, mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
        super.onSizeChanged(w, h, oldWidth, oldHeight);

        // calculate scale and location of the game view, and create a scale/translate matrix
        final float pixelScaleFactor = (float)h / VIEW_HEIGHT;
        final float viewLeftEdge = (w / 2.0f) - ((VIEW_WIDTH * pixelScaleFactor) / 2.0f);

        mMatrixViewScale.reset();
        mMatrixViewScale.postScale(pixelScaleFactor, pixelScaleFactor);
        mMatrixViewScale.postTranslate(viewLeftEdge, 0);
    }

    @Override
    public void setGameObjects(List<GameObject> gameObjects) {
        mGameObjects = gameObjects;
    }
}
