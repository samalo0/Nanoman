package com.stephenmaloney.www.nanoman;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.preference.PreferenceManager;

import com.stephenmaloney.www.nanoman.GameEngine.GameEngine;

import java.io.IOException;
import java.util.HashMap;

public class SoundManager {
    private final static int MAX_STREAMS = 3;

    private final Context mContext;
    private SoundPool mSoundPool;
    private HashMap<GameEngine.GameSound, Integer> mSoundsMap;
    private MediaPlayer mBgPlayer;

    private int mResourceLastPlayedMusic = 0;
    private boolean mLastPlayedMusicLooping = false;

    private final boolean mEnableMusic;
    private final boolean mEnableSound;

    public SoundManager(Activity activity) {
        // retrieve music/sound settings
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        mEnableMusic = sharedPreferences.getBoolean("Music", true);
        mEnableSound = sharedPreferences.getBoolean("Sound", true);

        mContext = activity.getApplicationContext();
        if(mEnableSound) loadSounds();
    }

    private void loadEventSound(Context context, GameEngine.GameSound event, int resourceId) {
        AssetFileDescriptor descriptor = context.getResources().openRawResourceFd(resourceId);
        final int soundId = mSoundPool.load(descriptor, 1);
        mSoundsMap.put(event, soundId);
    }

    public void playMusic(int resourceId, boolean looping) {
        if(!mEnableMusic) return;

        try {
            mBgPlayer = new MediaPlayer();
            final AssetFileDescriptor afd = mContext.getResources().openRawResourceFd(resourceId);
            mBgPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mBgPlayer.setLooping(looping);
            mBgPlayer.prepare();
            mBgPlayer.setVolume(.5f, .5f);
            mBgPlayer.start();

            mResourceLastPlayedMusic = resourceId;
            mLastPlayedMusicLooping = looping;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSounds() {
        final AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();
        mSoundPool = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(MAX_STREAMS).build();

        mSoundsMap = new HashMap<>();

        loadEventSound(mContext, GameEngine.GameSound.BIG_EYE_JUMP, R.raw.sound_big_eye);
        loadEventSound(mContext, GameEngine.GameSound.BLOCK_PUZZLE, R.raw.sound_block_puzzle);
        loadEventSound(mContext, GameEngine.GameSound.ENEMY_DAMAGE, R.raw.sound_enemy_damage);
        loadEventSound(mContext, GameEngine.GameSound.ENEMY_DINK, R.raw.sound_enemy_dink);
        loadEventSound(mContext, GameEngine.GameSound.ENEMY_SHOOT, R.raw.sound_enemy_shoot);
        loadEventSound(mContext, GameEngine.GameSound.EXPLOSION, R.raw.sound_explosion);
        loadEventSound(mContext, GameEngine.GameSound.GAME_START, R.raw.sound_game_start);
        loadEventSound(mContext, GameEngine.GameSound.GATE, R.raw.sound_gate);
        loadEventSound(mContext, GameEngine.GameSound.MENU_PAUSE, R.raw.sound_menu_pause);
        loadEventSound(mContext, GameEngine.GameSound.MENU_SELECTION, R.raw.sound_menu_select);
        loadEventSound(mContext, GameEngine.GameSound.METER_ADD, R.raw.sound_meter_add);
        loadEventSound(mContext, GameEngine.GameSound.ONE_UP, R.raw.sound_one_up);
        loadEventSound(mContext, GameEngine.GameSound.PLAYER_DAMAGE, R.raw.sound_player_damage);
        loadEventSound(mContext, GameEngine.GameSound.PLAYER_DEATH, R.raw.sound_player_death);
        loadEventSound(mContext, GameEngine.GameSound.PLAYER_LAND, R.raw.sound_player_land);
        loadEventSound(mContext, GameEngine.GameSound.PLAYER_WARP, R.raw.sound_player_warp);
        loadEventSound(mContext, GameEngine.GameSound.QUAKE, R.raw.sound_quake);
        loadEventSound(mContext, GameEngine.GameSound.SCISSORS, R.raw.sound_cut);
        loadEventSound(mContext, GameEngine.GameSound.TRAM, R.raw.sound_tram);
        loadEventSound(mContext, GameEngine.GameSound.WATER, R.raw.sound_water);
        loadEventSound(mContext, GameEngine.GameSound.WATER_RUSH, R.raw.sound_water_rush);
        loadEventSound(mContext, GameEngine.GameSound.WEAPON_CUTTER, R.raw.sound_rolling_cutter);
        loadEventSound(mContext, GameEngine.GameSound.WEAPON_ELECTRICITY, R.raw.sound_elec_beam);
        loadEventSound(mContext, GameEngine.GameSound.WEAPON_FIRE, R.raw.sound_fire1);
        loadEventSound(mContext, GameEngine.GameSound.WEAPON_GUTS, R.raw.sound_guts);
        loadEventSound(mContext, GameEngine.GameSound.WEAPON_ICE, R.raw.sound_ice);
        loadEventSound(mContext, GameEngine.GameSound.WEAPON_MAGNET, R.raw.sound_mag_beam);
        loadEventSound(mContext, GameEngine.GameSound.WEAPON_PSHOT, R.raw.sound_pshot);
    }

    public void pauseBgMusic() {
        if(mBgPlayer != null) mBgPlayer.pause();
    }

    public void playSoundForGameEvent(GameEngine.GameSound event) {
        if(!mEnableSound) return;

        final Integer soundId = mSoundsMap.get(event);
        if(soundId != null) mSoundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    public void resumeBgMusic() {
        if(mBgPlayer != null) mBgPlayer.start();
        else if(mResourceLastPlayedMusic != 0)  playMusic(mResourceLastPlayedMusic, mLastPlayedMusicLooping);
    }

    public void unloadMusic() {
        if(mBgPlayer != null) {
            mBgPlayer.stop();
            mBgPlayer.release();
            mBgPlayer = null;
        }
    }
}
