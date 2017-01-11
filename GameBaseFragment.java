package com.stephenmaloney.www.nanoman;

import android.app.Fragment;

public abstract class GameBaseFragment extends Fragment {
    public boolean onBackPressed() {
        return false;
    }
}
