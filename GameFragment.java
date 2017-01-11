package com.stephenmaloney.www.nanoman;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.stephenmaloney.www.nanoman.GameEngine.GameEngine;
import com.stephenmaloney.www.nanoman.GameEngine.GameView;
import com.stephenmaloney.www.nanoman.GameEngine.GamePadInputController;

import static com.stephenmaloney.www.nanoman.MainActivity.TAG_FRAGMENT;

public class GameFragment extends GameBaseFragment {
    GameEngine mGameEngine;
    boolean mOnScreenControls = true;

    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public boolean onBackPressed() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new WelcomeFragment(), TAG_FRAGMENT)
                .commit();

        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_game, container, false);

        // check if on screen controls
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mOnScreenControls = sharedPreferences.getBoolean("OnScreenControls", true);
        if(mOnScreenControls) {
            final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.frameLayoutGame);
            final View viewKeypad = inflater.inflate(R.layout.view_keypad, frameLayout, false);
            frameLayout.addView(viewKeypad);
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mGameEngine.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mGameEngine != null) mGameEngine.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGameEngine.onStop();
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // check if continuing game
        final Bundle bundle = getArguments();
        final boolean continueGame = bundle.getBoolean("continue", false);

        mGameEngine = new GameEngine((MainActivity)getActivity(), (GameView) view.findViewById(R.id.fragmentGameGameView), continueGame);
        mGameEngine.setInputController(new GamePadInputController(view, (MainActivity)getActivity(), mOnScreenControls));
        mGameEngine.startGame();
    }
}
