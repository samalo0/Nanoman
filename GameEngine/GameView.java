package com.stephenmaloney.www.nanoman.GameEngine;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import com.stephenmaloney.www.nanoman.GameObjects.GameObject;
import com.stephenmaloney.www.nanoman.GameObjects.Tile;

import java.util.List;

public interface GameView {
    int VIEW_WIDTH = 256;
    int VIEW_HEIGHT = 240;
    int VIEW_WIDTH_DIV_2 = VIEW_WIDTH >> 1;
    int VIEW_HEIGHT_DIV_2 = VIEW_HEIGHT >> 1;
    int VIEW_WIDTH_IN_TILES = 256 >> Tile.SIZE_POW_2;

    Rect mGameView = new Rect(0, 0, VIEW_WIDTH, VIEW_HEIGHT);
    Rect mViewPort = new Rect(0, 0, VIEW_WIDTH, VIEW_HEIGHT);

    Paint mPaint = new Paint();
    Matrix mMatrix = new Matrix();

    void draw();

    void setGameObjects(List<GameObject> gameObjects);
}
