package com.stephenmaloney.www.nanoman;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class WelcomeFragment extends GameBaseFragment {
    public WelcomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Button buttonNewGame = (Button) view.findViewById(R.id.buttonNewGame);
        buttonNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final GameFragment gameFragment = new GameFragment();
                final Bundle bundle = new Bundle();
                bundle.putBoolean("continue", false);
                gameFragment.setArguments(bundle);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, gameFragment, MainActivity.TAG_FRAGMENT)
                        .commit();
            }
        });

        final Button buttonContinueGame = (Button) view.findViewById(R.id.buttonContinueGame);
        buttonContinueGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final GameFragment gameFragment = new GameFragment();
                final Bundle bundle = new Bundle();
                bundle.putBoolean("continue", true);
                gameFragment.setArguments(bundle);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, gameFragment, MainActivity.TAG_FRAGMENT)
                        .commit();
            }
        });

        final Button buttonSetup = (Button) view.findViewById(R.id.buttonSetup);
        buttonSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new SetupFragment(), MainActivity.TAG_FRAGMENT)
                        .commit();
            }
        });
    }
}
