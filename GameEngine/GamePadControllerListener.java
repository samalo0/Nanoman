package com.stephenmaloney.www.nanoman.GameEngine;

import android.view.KeyEvent;
import android.view.MotionEvent;

public interface GamePadControllerListener {
    boolean dispatchGenericMotionEvent(MotionEvent event);
    boolean dispatchKeyEvent(KeyEvent event);
}
