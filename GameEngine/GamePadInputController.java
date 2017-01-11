package com.stephenmaloney.www.nanoman.GameEngine;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.stephenmaloney.www.nanoman.MainActivity;

public class GamePadInputController extends InputController implements GamePadControllerListener {
    private MainActivity mActivity;
    private boolean mMovingX = false;
    private boolean mMovingY = false;

    private final int mStartButton;
    private final int mBButton;
    private final int mAButton;

    public GamePadInputController(View view, MainActivity activity, boolean onScreenControls) {
        super(view, onScreenControls);
        mActivity = activity;

        // read joy pad buttons
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        mBButton = sharedPreferences.getInt("ButtonB", KeyEvent.KEYCODE_BUTTON_1);
        mAButton = sharedPreferences.getInt("ButtonA", KeyEvent.KEYCODE_BUTTON_2);
        mStartButton = sharedPreferences.getInt("ButtonStart", KeyEvent.KEYCODE_BUTTON_10);
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_MOVE) {
            if (event.getAxisValue(MotionEvent.AXIS_X) < -.8) {
                // left
                mDirectionX = -1;
                mMovingX = true;
            } else if (event.getAxisValue(MotionEvent.AXIS_X) > .8) {
                // right
                mDirectionX = 1;
                mMovingX = true;
            } else if (mMovingX) {
                mDirectionX = 0;
                mMovingX = false;
            }

            if (event.getAxisValue(MotionEvent.AXIS_Y) < -.8) {
                // up
                mMovingY = true;
                mDirectionY = -1;
            } else if (event.getAxisValue(MotionEvent.AXIS_Y) > .8) {
                // down
                mMovingY = true;
                mDirectionY = 1;
            } else if (mMovingY) {
                mMovingY = false;
                mDirectionY = 0;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN) {
            final int key = event.getKeyCode();
            if(key == mBButton) mButtonBPressed = true;
            else if(key == mAButton) mButtonAPressed = true;
            else if(key == mStartButton) mButtonStartPressed = true;
        }
        else if(event.getAction() == KeyEvent.ACTION_UP) {
            final int key = event.getKeyCode();
            if(key == mBButton) mButtonBPressed = false;
            else if(key == mAButton) mButtonAPressed = false;
            else if(key == mStartButton) mButtonStartPressed = false;
        }

        return false;
    }

    @Override
    public void onStart() {
        mActivity.setGamepadControllerListener(this);
    }

    @Override
    public void onStop() {
        mActivity.setGamepadControllerListener(null);
    }
}
