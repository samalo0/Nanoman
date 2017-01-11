package com.stephenmaloney.www.nanoman;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

public class VirtualDPad extends Button {
    private final Path mPathLeft = new Path();
    private Region mRegionLeft;

    private final Path mPathUp = new Path();
    private Region mRegionUp;

    private final Path mPathDown = new Path();
    private Region mRegionDown;

    private final Path mPathRight = new Path();
    private Region mRegionRight;

    private final Paint mPaint = new Paint();

    public int mDirectionX;
    public int mDirectionY;

    public VirtualDPad(Context context) {
        super(context);
    }

    public VirtualDPad(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VirtualDPad(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*
    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(ResourcesCompat.getColor(getResources(), R.color.colorKeypad, null));
        canvas.drawPath(mPathLeft, mPaint);
        canvas.drawPath(mPathRight, mPaint);
        super.onDraw(canvas);
    }
    */

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mPathLeft.reset();
        mPathLeft.moveTo(0, 0);
        mPathLeft.lineTo(0, h);
        mPathLeft.lineTo(w / 2.0f, h / 2.0f);
        mPathLeft.lineTo(0, 0);
        mPathLeft.close();

        final RectF rectFLeft = new RectF();
        mPathLeft.computeBounds(rectFLeft, true);
        final Rect rectLeft = new Rect((int)rectFLeft.left, (int)rectFLeft.top, (int)rectFLeft.right, (int)rectFLeft.bottom);
        mRegionLeft = new Region();
        mRegionLeft.setPath(mPathLeft, new Region(rectLeft));

        mPathUp.reset();
        mPathUp.moveTo(0, 0);
        mPathUp.lineTo(w, 0);
        mPathUp.lineTo(w / 2.0f, h / 2.0f);
        mPathUp.lineTo(0, 0);
        mPathUp.close();

        final RectF rectFUp = new RectF();
        mPathUp.computeBounds(rectFUp, true);
        final Rect rectUp = new Rect((int)rectFUp.left, (int)rectFUp.top, (int)rectFUp.right, (int)rectFUp.bottom);
        mRegionUp = new Region();
        mRegionUp.setPath(mPathUp, new Region(rectUp));

        mPathRight.reset();
        mPathRight.moveTo(w, 0);
        mPathRight.lineTo(w, h);
        mPathRight.lineTo(w / 2.0f, h / 2.0f);
        mPathRight.lineTo(w, 0);
        mPathRight.close();

        final RectF rectFRight = new RectF();
        mPathRight.computeBounds(rectFRight, true);
        final Rect rectRight = new Rect((int)rectFRight.left, (int)rectFRight.top, (int)rectFRight.right, (int)rectFRight.bottom);
        mRegionRight = new Region();
        mRegionRight.setPath(mPathRight, new Region(rectRight));

        mPathDown.reset();
        mPathDown.moveTo(0, h);
        mPathDown.lineTo(w, h);
        mPathDown.lineTo(w / 2.0f, h / 2.0f);
        mPathDown.lineTo(0, h);
        mPathDown.close();

        final RectF rectFDown = new RectF();
        mPathDown.computeBounds(rectFDown, true);
        final Rect rectDown = new Rect((int)rectFDown.left, (int)rectFDown.top, (int)rectFDown.right, (int)rectFDown.bottom);
        mRegionDown = new Region();
        mRegionDown.setPath(mPathDown, new Region(rectDown));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mRegionLeft.contains((int) event.getX(), (int) event.getY())) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    mDirectionX = -1;
                    mDirectionY = 0;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mDirectionX = 0;
                    mDirectionY = 0;
                    break;
            }
        }
        else if(mRegionUp.contains((int) event.getX(), (int) event.getY())) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    mDirectionY = -1;
                    mDirectionX = 0;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mDirectionY = 0;
                    mDirectionX = 0;
                    break;
            }
        }
        else if(mRegionRight.contains((int) event.getX(), (int) event.getY())) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    mDirectionX = 1;
                    mDirectionY = 0;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mDirectionX = 0;
                    mDirectionY = 0;
                    break;
            }
        }
        else if(mRegionDown.contains((int) event.getX(), (int) event.getY())) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    mDirectionY = 1;
                    mDirectionX = 0;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mDirectionY = 0;
                    mDirectionX = 0;
                    break;
            }
        }
        else {
            mDirectionX = 0;
            mDirectionY = 0;
        }

        return true;
    }
}
