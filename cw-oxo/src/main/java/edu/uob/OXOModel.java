package edu.uob;

import java.util.ArrayList;

public class OXOModel {

    //    private OXOPlayer[][] cells;
    private ArrayList<ArrayList<OXOPlayer>> cells;
    //    private OXOPlayer[] players;
    private  ArrayList<OXOPlayer> players = new ArrayList<OXOPlayer>();
    //    private  ArrayList<OXOPlayer> players;
    private int currentPlayerNumber;
    private OXOPlayer winner;
    private boolean gameDrawn;
    private int winThreshold;

    public OXOModel(int numberOfRows, int numberOfColumns, int winThresh) {
        winThreshold = winThresh;
//        cells = new OXOPlayer[numberOfRows][numberOfColumns];
        cells = new ArrayList<ArrayList<OXOPlayer>>(numberOfRows);
        ArrayList<OXOPlayer> tmp = new ArrayList<>(numberOfColumns);
        for (int i = 0; i < numberOfColumns; i++) {
            tmp.add(null);
        }
        for (int i = 0; i < numberOfRows; i++) {
            cells.add(new ArrayList<OXOPlayer>(tmp));
        }
        players = new ArrayList<OXOPlayer>();
    }

    public int getNumberOfPlayers() {
        return players.size();
    }

    public void addPlayer(OXOPlayer player) {
//        for (int i = 0; i < players.size(); i++) {
//            if (players[i] == null) {
//                players[i] = player;
//                return;
//            }
//        }
//        if(!isWin()){
//            System.out.println("addPlayer!!!!");
//            System.out.println(player.getPlayingLetter());
        players.add(player);
//        }

    }

    public OXOPlayer getPlayerByNumber(int number) {
        return players.get(number);
    }

    public OXOPlayer getWinner() {
        return winner;
    }

    public void setWinner(OXOPlayer player) {
        winner = player;
    }

    public boolean isWin(){
        if(winner == null){
            return false;
        }
        return true;
    }

    public boolean isBegin(){
        for (int i = 0; i < getNumberOfRows() - 1; i++) {
            for (int j = 0; j < getNumberOfColumns() - 1; j++) {
                if (getCellOwner(i,j) != null && !isWin()){
                    return true;
                }
            }
        }
        return false;
    }

    public int getCurrentPlayerNumber() {
        return currentPlayerNumber;
    }

    public void setCurrentPlayerNumber(int playerNumber) {
        currentPlayerNumber = playerNumber;
    }

    public int getNumberOfRows() {
        return cells.size();
    }

    public int getNumberOfColumns() {
        return cells.get(0).size();
    }

    public OXOPlayer getCellOwner(int rowNumber, int colNumber) {
        return cells.get(rowNumber).get(colNumber);
    }

    public void setCellOwner(int rowNumber, int colNumber, OXOPlayer player) {
//        OXOPlayer tmp = player;
        cells.get(rowNumber).set(colNumber,player);
//        cells[rowNumber][colNumber] = player;
    }

    public void setWinThreshold(int winThresh) {
        winThreshold = winThresh;
    }

    public int getWinThreshold() {
        return winThreshold;
    }

    public void setGameDrawn() { gameDrawn = true; }

    public void deleteGameDrawn() {
        System.out.println("get in delete gamedrawn!");
        gameDrawn = false;
    }

    public boolean isGameDrawn() {
        return gameDrawn;
    }

    public void addRow(){
        int col = getNumberOfColumns();
        ArrayList<OXOPlayer> tmp = new ArrayList<>();
        for (int i = 0; i < col; i++) {
            tmp.add(null);
        }
        cells.add(tmp);
    }
    public void addColumn(){
        int row = getNumberOfRows();
        for (int i = 0; i < row; i++) {
            cells.get(i).add(null);
        }
    }

    public void removeRow(){
        int row = getNumberOfRows();
        cells.remove(row-1);
    }

    public void removeColumn(){
        int col = getNumberOfColumns();
        int row = getNumberOfRows();
        for (int i = 0; i < row; i++) {
            cells.get(i).remove(col-1);
        }
    }

}
