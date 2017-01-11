package com.stephenmaloney.www.nanoman.GameEngine;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.preference.PreferenceManager;

import com.stephenmaloney.www.nanoman.GameObjects.CollisionGameObject;
import com.stephenmaloney.www.nanoman.GameObjects.FPSCounter;
import com.stephenmaloney.www.nanoman.GameObjects.GameObject;
import com.stephenmaloney.www.nanoman.GameObjects.Gate;
import com.stephenmaloney.www.nanoman.GameObjects.Player.Player;
import com.stephenmaloney.www.nanoman.GameObjects.PowerUps.AmmoLarge;
import com.stephenmaloney.www.nanoman.GameObjects.PowerUps.AmmoSmall;
import com.stephenmaloney.www.nanoman.GameObjects.PowerUps.HealthLarge;
import com.stephenmaloney.www.nanoman.GameObjects.PowerUps.HealthSmall;
import com.stephenmaloney.www.nanoman.GameObjects.PowerUps.OneUp;
import com.stephenmaloney.www.nanoman.GameObjects.StageSelect;
import com.stephenmaloney.www.nanoman.MainActivity;
import com.stephenmaloney.www.nanoman.R;
import com.stephenmaloney.www.nanoman.GameObjects.Stage;
import com.stephenmaloney.www.nanoman.SoundManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameEngine {
    public enum GameSound {
        BIG_EYE_JUMP,
        BLOCK_PUZZLE,
        ENEMY_DAMAGE,
        ENEMY_DINK,
        ENEMY_SHOOT,
        EXPLOSION,
        GAME_START,
        GATE,
        MENU_PAUSE,
        MENU_SELECTION,
        METER_ADD,
        ONE_UP,
        PLAYER_DAMAGE,
        PLAYER_DEATH,
        PLAYER_LAND,
        PLAYER_WARP,
        QUAKE,
        SCISSORS,
        TRAM,
        WATER,
        WATER_RUSH,
        WEAPON_CUTTER,
        WEAPON_ELECTRICITY,
        WEAPON_FIRE,
        WEAPON_GUTS,
        WEAPON_ICE,
        WEAPON_MAGNET,
        WEAPON_PSHOT,
    }

    public final static int STATE_NORMAL = 0;
    public final static int STATE_RESTART_STAGE = 1;
    private final static int STATE_GATE_SCROLLING_X = 2;
    public final static int STATE_SCROLLING_Y = 3;
    private final static int STATE_WEAPON_SELECT = 4;
    public final static int STATE_TRANSITION_TO_STAGE_SELECT = 5;
    private final static int STATE_STAGE_SELECT = 6;
    private final static int STATE_TRANSITION_TO_STAGE = 7;
    private int mState = STATE_STAGE_SELECT;
    private boolean mStartHeldDown = true;

    public final static int RANDOM_SPAWN_TIMEOUT = 10000;
    public final static float GRAVITY_ACCELERATION = .0011f;
    public final static float TERMINAL_VELOCITY = .6f;

    private final List<GameObject> mGameObjects = new ArrayList<>();
    private final List<CollisionGameObject> mCollisionGameObjects = new ArrayList<>();
    private final List<GameObject> mObjectsToAdd = new ArrayList<>();
    private final List<GameObject> mObjectsToRemove = new ArrayList<>();

    private final MainActivity mActivity;
    private final GameView mGameView;

    public Stage mStage;
    private int mCurrentStage;

    public final Player mPlayer;
    private final StageSelect mStageSelect;

    private ThreadDraw mDrawThread;
    private ThreadUpdate mUpdateThread;

    public InputController mInputController;

    private SoundManager mSoundManager;

    private Gate mGate = null;

    private Random mRandom = new Random();

    public GameEngine(MainActivity mainActivity, GameView gameView, boolean continueGame) {
        // save main activity (to access UI thread)
        mActivity = mainActivity;

        // create the sound manager
        mSoundManager = new SoundManager(mainActivity);

        // create stage select
        mStageSelect = new StageSelect(getResources());
        addGameObject(mStageSelect);

        // create a player
        mPlayer = new Player(getResources());

        if(continueGame) progressLoad();

        // save the game view for drawing
        mGameView = gameView;
        mGameView.setGameObjects(mGameObjects);

        musicPlayLooped(R.raw.music_stage_select);
    }

    public void addGameObject(final GameObject gameObject) {
        if(isRunning()) mObjectsToAdd.add(gameObject);
        else {
            mGameObjects.add(gameObject);
            if(gameObject instanceof CollisionGameObject) mCollisionGameObjects.add((CollisionGameObject) gameObject);
        }
    }

    public Context getContext() {
        return mActivity.getApplicationContext();
    }

    public Resources getResources() { return mActivity.getResources(); }

    public int getTile(int tileX, int tileY) {
        return mStage.mTileMap[tileY][tileX];
    }

    public int getTileSolid() {
        return mStage.mSolidTileNumber;
    }

    public static boolean isObjectVisible(Rect boundingBox) {
        return Rect.intersects(boundingBox, GameView.mViewPort);
    }

    private boolean isRunning() {
        return mUpdateThread != null && mDrawThread != null && mUpdateThread.isGameRunning() && mDrawThread.isGameRunning();
    }

    public void musicPause() {
        mSoundManager.pauseBgMusic();
    }

    public void musicPlayLooped(int resourceId) {
        mSoundManager.unloadMusic();
        mSoundManager.playMusic(resourceId, true);
    }

    public void musicPlayOnce(int resourceId) {
        mSoundManager.unloadMusic();
        mSoundManager.playMusic(resourceId, false);
    }

    private void musicResume() {
        mSoundManager.resumeBgMusic();
    }

    public void onDraw() {
        mGameView.draw();
    }

    public void onPause() {
        mUpdateThread.pauseGame();
        mDrawThread.pauseGame();
        mSoundManager.pauseBgMusic();
    }

    public void onResume() {
        mUpdateThread.resumeGame();
        mDrawThread.resumeGame();
        mSoundManager.resumeBgMusic();
    }

    public void onStop() {
        mSoundManager.unloadMusic();
    }

    public void onUpdate(long elapsedMillis) {
        switch(mState) {
            case STATE_NORMAL:
                synchronized(mGameObjects) {
                    // update objects
                    final int numGameObjects = mGameObjects.size();
                    for(int i = 0; i < numGameObjects; i++) {
                        mGameObjects.get(i).onUpdate(elapsedMillis, this);
                    }

                    // add/remove objects extra objects to prevent collision checks on them
                    while(!mObjectsToRemove.isEmpty()) {
                        if(mObjectsToRemove.get(0) instanceof CollisionGameObject) mCollisionGameObjects.remove(mObjectsToRemove.get(0));
                        mGameObjects.remove(mObjectsToRemove.remove(0));
                    }
                }

                // check collisions
                final int numCollisionGameObjects = mCollisionGameObjects.size();
                for(int i = 0; i < numCollisionGameObjects; i++) {
                    CollisionGameObject objectA = mCollisionGameObjects.get(i);
                    for(int j = i + 1; j < numCollisionGameObjects; j++) {
                        CollisionGameObject objectB = mCollisionGameObjects.get(j);
                        if(objectA.checkCollision(objectB)) {
                            objectA.onCollision(this, objectB);
                            objectB.onCollision(this, objectA);
                        }
                    }
                }

                // add/remove objects
                synchronized(mGameObjects) {
                    while(!mObjectsToRemove.isEmpty()) {
                        if(mObjectsToRemove.get(0) instanceof CollisionGameObject) mCollisionGameObjects.remove(mObjectsToRemove.get(0));
                        mGameObjects.remove(mObjectsToRemove.remove(0));
                    }
                    while(!mObjectsToAdd.isEmpty()) {
                        if(mObjectsToAdd.get(0) instanceof CollisionGameObject) mCollisionGameObjects.add((CollisionGameObject) mObjectsToAdd.get(0));
                        mGameObjects.add(mObjectsToAdd.remove(0));
                    }
                }

                // check for start or select being pressed
                if(!mInputController.mButtonStartPressed) mStartHeldDown = false;

                if(mInputController.mButtonStartPressed && !mStartHeldDown) {
                    mState = STATE_WEAPON_SELECT;
                    mStartHeldDown = true;
                    synchronized (mGameObjects) {
                        mPlayer.mWeaponSelect.redraw();
                        mGameObjects.add(mPlayer.mWeaponSelect);
                    }
                    soundPlay(GameSound.MENU_PAUSE);
                }
                break;
            case STATE_RESTART_STAGE:
                startStage(mCurrentStage, true);
                break;
            case STATE_GATE_SCROLLING_X:
                synchronized(mGameObjects) {
                    mGate.onUpdate(elapsedMillis, this);
                    mPlayer.onUpdateForceTravelX(this, elapsedMillis, mGate.mPassDirection);
                    mStage.onUpdateGateScrollX(elapsedMillis);
                }
                break;
            case STATE_SCROLLING_Y:
                synchronized(mGameObjects) {
                    mStage.onUpdateScrollY(this, elapsedMillis, mPlayer);
                }
                break;
            case STATE_TRANSITION_TO_STAGE_SELECT:
                stopGame();

                // free game objects
                synchronized(mGameObjects) {
                    mGameObjects.clear();
                    mCollisionGameObjects.clear();
                    mObjectsToAdd.clear();
                    mObjectsToRemove.clear();

                    // add the stage select
                    addGameObject(mStageSelect);
                }

                mState = STATE_STAGE_SELECT;
                musicPlayLooped(R.raw.music_stage_select);

                startGame();
                break;
            case STATE_STAGE_SELECT:
                synchronized(mGameObjects) {
                    mStageSelect.onUpdate(elapsedMillis, this);
                }
                break;
            case STATE_TRANSITION_TO_STAGE:
                startStage(mCurrentStage, false);
                break;
            case STATE_WEAPON_SELECT:
                synchronized(mGameObjects) {
                    mPlayer.mWeaponSelect.onUpdate(elapsedMillis, this);

                    if(!mInputController.mButtonStartPressed) mStartHeldDown = false;

                    if(mInputController.mButtonStartPressed && !mStartHeldDown) {
                        mState = STATE_NORMAL;
                        mStartHeldDown = true;
                        synchronized (mGameObjects) {
                            mGameObjects.remove(mPlayer.mWeaponSelect);
                        }
                        soundPlay(GameSound.MENU_PAUSE);
                    }
                }
                break;
        }
    }

    private void progressLoad() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        final int pattern = sharedPreferences.getInt("WeaponsPresent", -1);

        if (pattern == -1) return;

        for (int i = 0; i < mPlayer.mWeaponSelect.mSelectionPresent.length; i++) {
            mPlayer.mWeaponSelect.mSelectionPresent[i] = (((pattern >> i) & 1) == 1);
        }
    }

    public void progressSave() {
        int pattern = 0;
        for(int i = 0; i < mPlayer.mWeaponSelect.mSelectionPresent.length; i++) {
            pattern |= ((mPlayer.mWeaponSelect.mSelectionPresent[i] ? 1 : 0) << i);
        }

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        sharedPreferences.edit().putInt("WeaponsPresent", pattern).apply();
    }

    public void randomSpawn(int centerX, int centerY) {
        final int value = mRandom.nextInt(100);

        if(value < 10) {
            HealthLarge healthLarge = new HealthLarge(getResources(), centerX, centerY);
            addGameObject(healthLarge);
        }
        else if(value < 25) {
            HealthSmall healthSmall = new HealthSmall(getResources(), centerX, centerY);
            addGameObject(healthSmall);
        }
        else if(value < 40) {
            AmmoSmall ammoSmall = new AmmoSmall(getResources(), centerX, centerY);
            addGameObject(ammoSmall);
        }
        else if(value < 50) {
            AmmoLarge ammoLarge = new AmmoLarge(getResources(), centerX, centerY);
            addGameObject(ammoLarge);
        }
        else if(value < 53) {
            OneUp oneUp = new OneUp(getResources(), centerX, centerY);
            addGameObject(oneUp);
        }
    }

    public void removeGameObject(final GameObject gameObject) {
        mObjectsToRemove.add(gameObject);
    }

    public void setInputController(InputController inputController) {
        mInputController = inputController;
    }

    public void setPlayerStartPosition(int absolutePositionX, int absolutePositionY, int direction) {
        if(!mPlayer.mCheckPointSet) {
            mPlayer.mStartPositionX = absolutePositionX;
            mPlayer.mStartPositionY = absolutePositionY;
            mPlayer.mStartDirection = direction;
        }
        else {
            absolutePositionX = mPlayer.mStartPositionX;
            absolutePositionY = mPlayer.mStartPositionY;
        }

        // set the stage viewport to that location
        GameView.mViewPort.offsetTo(absolutePositionX - (absolutePositionX % GameView.VIEW_WIDTH), absolutePositionY - (absolutePositionY % GameView.VIEW_HEIGHT));
    }

    public void setScrollLockX(int x, int direction) {
        mStage.setScrollLockX(x, direction);
    }

    public void setState(int state) {
        // change game engine state
        mState = state;
    }

    public void setStateGateScrollingX(Gate gate) {
        mState = STATE_GATE_SCROLLING_X;
        mStage.setGateTransition(gate);
        mGate = gate;
    }

    public void setStateTransitionToStage(int stage) {
        mState = STATE_TRANSITION_TO_STAGE;
        mCurrentStage = stage;
    }

    public void setTile(int tileX, int tileY, int tileNumber) {
        mStage.mTileMap[tileY][tileX] = tileNumber;
    }

    public void soundPlay(GameSound gameSound) {
        mSoundManager.playSoundForGameEvent(gameSound);
    }

    public void startGame() {
        stopGame();

        final int numGameObjects = mGameObjects.size();
        for(int i=0; i < numGameObjects; i++) {
            mGameObjects.get(i).startGame(this);
        }

        mUpdateThread = new ThreadUpdate(this);
        mUpdateThread.startGame();

        mDrawThread = new ThreadDraw(this);
        mDrawThread.startGame();

        mInputController.onStart();
    }

    public void startStage(int stage, boolean restart) {
        mCurrentStage = stage;

        // stop the game
        stopGame();

        synchronized (mGameObjects) {

            // free all game objects
            mGameObjects.clear();
            mCollisionGameObjects.clear();
            mObjectsToAdd.clear();
            mObjectsToRemove.clear();

            // clear checkpoints if not restarting a stage
            if(!restart) mPlayer.mCheckPointSet = false;

            switch (stage) {
                case StageSelect.STAGE_BOMBMAN:
                    if (restart) mStage.xmlLoadStageFromResource(this, R.xml.stage_bomb);
                    else mStage = new Stage(this, R.xml.stage_bomb);
                    musicPlayLooped(R.raw.music_bombman_stage);
                    break;
                case StageSelect.STAGE_ELECMAN:
                    if (restart) mStage.xmlLoadStageFromResource(this, R.xml.stage_elec);
                    else mStage = new Stage(this, R.xml.stage_elec);
                    musicPlayLooped(R.raw.music_elecman_stage);
                    break;
                case StageSelect.STAGE_CUTMAN:
                    if (restart) mStage.xmlLoadStageFromResource(this, R.xml.stage_cut);
                    else mStage = new Stage(this, R.xml.stage_cut);
                    musicPlayLooped(R.raw.music_cutman_stage);
                    break;
                case StageSelect.STAGE_GUTSMAN:
                    if (restart) mStage.xmlLoadStageFromResource(this, R.xml.stage_gut);
                    else mStage = new Stage(this, R.xml.stage_gut);
                    musicPlayLooped(R.raw.music_gutsman_stage);
                    break;
                case StageSelect.STAGE_ICEMAN:
                    if (restart) mStage.xmlLoadStageFromResource(this, R.xml.stage_ice);
                    else mStage = new Stage(this, R.xml.stage_ice);
                    musicPlayLooped(R.raw.music_iceman_stage);
                    break;
                case StageSelect.STAGE_FIREMAN:
                    if (restart) mStage.xmlLoadStageFromResource(this, R.xml.stage_fire);
                    else mStage = new Stage(this, R.xml.stage_fire);
                    musicPlayLooped(R.raw.music_fireman_stage);
                    break;
                case StageSelect.STAGE_WILY:
                    if (restart) mStage.xmlLoadStageFromResource(this, R.xml.stage_wily1);
                    else mStage = new Stage(this, R.xml.stage_wily1);
                    musicPlayLooped(R.raw.music_wily_stage1);
                    break;
                case StageSelect.STAGE_WILY2:
                    if (restart) mStage.xmlLoadStageFromResource(this, R.xml.stage_wily2);
                    else mStage = new Stage(this, R.xml.stage_wily2);
                    musicPlayLooped(R.raw.music_wily_stage2);
                    break;
                case StageSelect.STAGE_WILY3:
                    if (restart) mStage.xmlLoadStageFromResource(this, R.xml.stage_wily3);
                    else mStage = new Stage(this, R.xml.stage_wily3);
                    musicPlayLooped(R.raw.music_wily_stage2);
                    break;
                case StageSelect.STAGE_WILY4:
                    if (restart) mStage.xmlLoadStageFromResource(this, R.xml.stage_wily4);
                    else mStage = new Stage(this, R.xml.stage_wily4);
                    musicPlayLooped(R.raw.music_wily_stage2);
                    break;
            }

            // add stage and player back
            mGameObjects.add(0, mStage);

            mGameObjects.add(1, mPlayer);
            mCollisionGameObjects.add(0, mPlayer);
        }

        startGame();

        mState = STATE_NORMAL;
    }

    private void stopGame() {
        mInputController.onStop();

        if (mUpdateThread != null) {
            mUpdateThread.stopGame();
        }

        if(mDrawThread != null) {
            mDrawThread.stopGame();
        }
    }
}
