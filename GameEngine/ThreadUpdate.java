package com.stephenmaloney.www.nanoman.GameEngine;

class ThreadUpdate extends Thread {
    private boolean mGameIsRunning = false;
    private boolean mPauseGame = false;
    private final Object mLock = new Object();
    private GameEngine mGameEngine;

    ThreadUpdate(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    boolean isGameRunning() {
        return mGameIsRunning;
    }

    void pauseGame() {
        mPauseGame = true;
    }

    void resumeGame() {
        if(mPauseGame) {
            mPauseGame = false;
            synchronized (mLock) {
                mLock.notify();
            }
        }
    }

    @Override
    public void run() {
        super.run();

        long previousTimeMillis;
        long currentTimeMillis;
        long elapsedMillis;

        previousTimeMillis = System.currentTimeMillis();

        while(mGameIsRunning) {
            while(mPauseGame) {
                try {
                    synchronized(mLock) {
                        mLock.wait();
                    }
                } catch (InterruptedException e) {
                    // do nothing
                }
            }

            currentTimeMillis = System.currentTimeMillis();
            elapsedMillis = currentTimeMillis - previousTimeMillis;

            if(elapsedMillis < 10) {
                try {
                    sleep(10 - elapsedMillis);
                }
                catch (InterruptedException e) {
                    // nothing
                }

                currentTimeMillis = System.currentTimeMillis();
                elapsedMillis = currentTimeMillis - previousTimeMillis;
            }

            mGameEngine.onUpdate(elapsedMillis);
            previousTimeMillis = currentTimeMillis;
        }
    }

    void startGame() {
        mGameIsRunning = true;
        mPauseGame = false;
        start();
    }

    void stopGame() {
        mGameIsRunning = false;
        resumeGame();
    }
}
