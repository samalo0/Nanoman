package com.stephenmaloney.www.nanoman;

import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.stephenmaloney.www.nanoman.GameEngine.GamePadControllerListener;

public class MainActivity extends AppCompatActivity {
    public final static String TAG_FRAGMENT = "GAME_FRAGMENT";

    private GamePadControllerListener mGamepadControllerListener;

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        if(mGamepadControllerListener != null) {
            if(mGamepadControllerListener.dispatchGenericMotionEvent(event)) return true;
        }
        return super.dispatchGenericMotionEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(mGamepadControllerListener != null) {
            if(mGamepadControllerListener.dispatchKeyEvent(event)) return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        final GameBaseFragment fragment = (GameBaseFragment) getFragmentManager().findFragmentByTag(TAG_FRAGMENT);
        if(fragment == null) super.onBackPressed();
        else if(!fragment.onBackPressed()) super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new WelcomeFragment(), TAG_FRAGMENT)
                .commit();
    }

    public void setGamepadControllerListener(GamePadControllerListener listener) {
        mGamepadControllerListener = listener;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if(hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
