package com.stephenmaloney.www.nanoman;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.stephenmaloney.www.nanoman.GameEngine.GamePadInputControllerSetup;

import java.util.Locale;

import static com.stephenmaloney.www.nanoman.MainActivity.TAG_FRAGMENT;

public class SetupFragment extends GameBaseFragment {
    GamePadInputControllerSetup mGamepadInputControllerSetup;

    public SetupFragment() {
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
        return inflater.inflate(R.layout.fragment_setup, container, false);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mGamepadInputControllerSetup != null) mGamepadInputControllerSetup.onStop();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mGamepadInputControllerSetup = new GamePadInputControllerSetup((MainActivity)getActivity());
        mGamepadInputControllerSetup.onStart();

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final int intButtonB = sharedPreferences.getInt("ButtonB", -1);
        final int intButtonA = sharedPreferences.getInt("ButtonA", -1);
        final int intButtonStart = sharedPreferences.getInt("ButtonStart", -1);
        final boolean boolOnScreenControls = sharedPreferences.getBoolean("OnScreenControls", true);
        final boolean boolMusic = sharedPreferences.getBoolean("Music", true);
        final boolean boolSound = sharedPreferences.getBoolean("Sound", true);

        final CheckBox checkBoxOnScreenControls = (CheckBox) view.findViewById(R.id.checkBoxOnScreenControls);
        checkBoxOnScreenControls.setChecked(boolOnScreenControls);
        checkBoxOnScreenControls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sharedPreferences.edit().putBoolean("OnScreenControls", b).apply();
            }
        });

        final Button buttonSetBButton = (Button)view.findViewById(R.id.buttonSetBButton);
        if(intButtonB == -1) buttonSetBButton.setText("Set B Button");
        else buttonSetBButton.setText(String.format(Locale.getDefault(), "Set B Button - %d", intButtonB));

        buttonSetBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonSetBButton.setText(String.format(Locale.getDefault(), "Set B Button - %d", mGamepadInputControllerSetup.mLastButtonPress));
                sharedPreferences.edit().putInt("ButtonB", mGamepadInputControllerSetup.mLastButtonPress).apply();
            }
        });

        final Button buttonSetAButton = (Button)view.findViewById(R.id.buttonSetAButton);
        if(intButtonA == -1) buttonSetAButton.setText("Set A Button");
        else buttonSetAButton.setText(String.format(Locale.getDefault(), "Set A Button - %d", intButtonA));

        buttonSetAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonSetAButton.setText(String.format(Locale.getDefault(), "Set A Button - %d", mGamepadInputControllerSetup.mLastButtonPress));
                sharedPreferences.edit().putInt("ButtonA", mGamepadInputControllerSetup.mLastButtonPress).apply();
            }
        });

        final Button buttonSetStartButton = (Button)view.findViewById(R.id.buttonSetStartButton);
        if(intButtonStart == -1) buttonSetStartButton.setText("Set START Button");
        else buttonSetStartButton.setText(String.format(Locale.getDefault(), "Set START Button - %d", intButtonStart));

        buttonSetStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonSetStartButton.setText(String.format(Locale.getDefault(), "Set START Button - %d", mGamepadInputControllerSetup.mLastButtonPress));
                sharedPreferences.edit().putInt("ButtonStart", mGamepadInputControllerSetup.mLastButtonPress).apply();
            }
        });

        final CheckBox checkBoxMusic = (CheckBox) view.findViewById(R.id.checkBoxMusic);
        checkBoxMusic.setChecked(boolMusic);
        checkBoxMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sharedPreferences.edit().putBoolean("Music", b).apply();
            }
        });

        final CheckBox checkBoxSound = (CheckBox) view.findViewById(R.id.checkBoxSound);
        checkBoxSound.setChecked(boolSound);
        checkBoxSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sharedPreferences.edit().putBoolean("Sound", b).apply();
            }
        });

        final Button buttonSetupBack = (Button) view.findViewById(R.id.buttonSetupBack);
        buttonSetupBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
