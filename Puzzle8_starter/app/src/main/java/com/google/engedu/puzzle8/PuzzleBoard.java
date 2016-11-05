package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;


public class PuzzleBoard {

    private static final int NUM_TILES = 3;
    int steps;
    private static final int[][] NEIGHBOUR_COORDS = {
            { -1, 0 },
            { 1, 0 },
            { 0, -1 },
            { 0, 1 }
    };
    private ArrayList<PuzzleTile> tiles;
    PuzzleBoard previousBoard;

    PuzzleBoard(Bitmap bitmap, int parentWidth) {
        tiles=new ArrayList<>();
        int width, height,k=0;
        Bitmap[][] bitmaps = new Bitmap[NUM_TILES][NUM_TILES];

        // Divide the original bitmap width by the desired vertical column count
        width = bitmap.getWidth() / NUM_TILES;
        // Divide the original bitmap height by the desired horizontal row count
        height = bitmap.getHeight() / NUM_TILES;
        // Loop the array and create bitmaps for each coordinate
        for(int x = 0; x < NUM_TILES; ++x) {
            for(int y = 0; y < NUM_TILES; ++y) {
                // Create the sliced bitmap
                bitmaps[x][y] = Bitmap.createBitmap(bitmap, y* width, x* height, width, height);
                Bitmap b=Bitmap.createScaledBitmap(bitmaps[x][y],200,200,true);

                if(x!=NUM_TILES-1 || y!=NUM_TILES-1) {
                    PuzzleTile p=new PuzzleTile(b,k++);
                    tiles.add(p);
                }
                else{

                    tiles.add(null);
                }
            }
        }

    }
    PuzzleBoard(PuzzleBoard otherBoard) {
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
        steps=otherBoard.steps+1;
        previousBoard=otherBoard;
    }

    public PuzzleBoard getPreviousBoard(){
        return previousBoard;
    }



    public void reset() {
        // Nothing for now but you may have things to reset once you implement the solver.
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    public ArrayList<PuzzleBoard> neighbours() {
        int i,j;
        ArrayList<PuzzleBoard> PuzzleBoardConfig=new ArrayList<>();
        for(i=0;i<NUM_TILES*NUM_TILES;i++){
            if(tiles.get(i)==null)
                break;
        }

        int x=i/NUM_TILES,y=i%NUM_TILES;
        for(j=0;j<4;j++){
            if(NEIGHBOUR_COORDS[j][0]+x>=0 &&NEIGHBOUR_COORDS[j][0]+x<NUM_TILES && NEIGHBOUR_COORDS[j][1]+y>=0 && NEIGHBOUR_COORDS[j][1]+y<NUM_TILES) {
                PuzzleBoard copy=new PuzzleBoard(this);
                copy.swapTiles(i, XYtoIndex(NEIGHBOUR_COORDS[j][0] + x, NEIGHBOUR_COORDS[j][1] + y));
                copy.priority();
                PuzzleBoardConfig.add(copy);
            }
        }


        return PuzzleBoardConfig;


    }

    public int priority() {
        int i,x,y,dx,dy, manhattanDistanceSum = 0;
        for(i=0;i<NUM_TILES*NUM_TILES;i++){
            if(tiles.get(i)==null) {

                continue;
            }
            int value=tiles.get(i).getNumber();

            if(i-value==0)
                continue;
            x=(value)/NUM_TILES;
            y=(value)%NUM_TILES;
            dx=i/NUM_TILES;
            dy=i%NUM_TILES;
            manhattanDistanceSum+=Math.abs(x-dx)+Math.abs(y-dy);


        }

        return manhattanDistanceSum+steps;
    }

}



