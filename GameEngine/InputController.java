package com.stephenmaloney.www.nanoman.GameEngine;

import android.view.MotionEvent;
import android.view.View;

import com.stephenmaloney.www.nanoman.R;
import com.stephenmaloney.www.nanoman.VirtualDPad;

public class InputController implements View.OnTouchListener {
    public int mDirectionX = 0;
    public int mDirectionY = 0;
    public boolean mButtonBPressed = false;
    public boolean mButtonAPressed = false;
    public boolean mButtonStartPressed = false;

    private VirtualDPad mVirtualDpad;

    InputController(View view, boolean onScreenControls) {
        if(onScreenControls) {
            view.findViewById(R.id.buttonStart).setOnTouchListener(this);
            view.findViewById(R.id.buttonB).setOnTouchListener(this);
            view.findViewById(R.id.buttonA).setOnTouchListener(this);

            mVirtualDpad = (VirtualDPad) view.findViewById(R.id.buttonVirtualDPad);
            mVirtualDpad.setOnTouchListener(this);
        }
    }

    public void onStart() {
    }

    public void onStop() {
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getActionMasked();
        int id = view.getId();

        if(id == R.id.buttonVirtualDPad) {
            view.onTouchEvent(motionEvent);
            mDirectionX = mVirtualDpad.mDirectionX;
            mDirectionY = mVirtualDpad.mDirectionY;
            return true;
        }

        if(action == MotionEvent.ACTION_DOWN) {
            switch(id) {
                case R.id.buttonStart:
                    mButtonStartPressed = true;
                    break;
                case R.id.buttonB:
                    mButtonBPressed = true;
                    break;
                case R.id.buttonA:
                    mButtonAPressed = true;
                    break;
            }
        }
        else if(action == MotionEvent.ACTION_UP) {
            switch(id) {
                case R.id.buttonStart:
                    mButtonStartPressed = false;
                    break;
                case R.id.buttonB:
                    mButtonBPressed = false;
                    break;
                case R.id.buttonA:
                    mButtonAPressed = false;
                    break;
            }
        }
        return true;
    }
}
