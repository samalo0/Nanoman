package com.stephenmaloney.www.nanoman.GameEngine;

import android.view.KeyEvent;
import android.view.MotionEvent;

import com.stephenmaloney.www.nanoman.MainActivity;

public class GamePadInputControllerSetup implements GamePadControllerListener {
    private MainActivity mActivity;

    public int mLastButtonPress = 0;

    public GamePadInputControllerSetup(MainActivity activity) {
        mActivity = activity;
    }
    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN) {
            mLastButtonPress = event.getKeyCode();
        }
        return false;
    }

    public void onStart() {
        mActivity.setGamepadControllerListener(this);
    }

    public void onStop() {
        mActivity.setGamepadControllerListener(null);
    }
}
