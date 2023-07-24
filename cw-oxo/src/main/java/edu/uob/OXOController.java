package edu.uob;

import javax.xml.crypto.dsig.keyinfo.KeyValue;

public class OXOController {
    OXOModel gameModel;

    public OXOController(OXOModel model) {
        gameModel = model;
    }


    public void handleIncomingCommand(String command) throws OXOMoveException {
        if(gameModel.getWinner() != null || gameModel.isGameDrawn()){
            return;
        }
        if(isValid(command)){
            int row = toAscii(command.substring(0,1).toLowerCase());
            int col = toAscii(command.substring(1,2).toLowerCase());
            gameModel.setCellOwner(row - 'a', col - '1' ,gameModel.getPlayerByNumber(gameModel.getCurrentPlayerNumber()));
            int nums = gameModel.getNumberOfPlayers();
            winnerTest();
            if(!gameModel.isWin()){
                if(gameModel.getCurrentPlayerNumber() < nums - 1){
                    gameModel.setCurrentPlayerNumber(gameModel.getCurrentPlayerNumber() + 1);
                } else if (gameModel.getCurrentPlayerNumber() == nums - 1) {
                    gameModel.setCurrentPlayerNumber(0);
                }
            }
//            if(gameModel.getCurrentPlayerNumber() == 0){
//                gameModel.setCurrentPlayerNumber(1);
//            }else {
//                gameModel.setCurrentPlayerNumber(0);
//            }
        }
        if(setGameModel()){
            System.out.println("Game Drawn!");
//            return;
        }
    }
    public void addRow() {
//        if (gameModel.getNumberOfRows() < 9 && !gameModel.isWin()) {
        if (gameModel.getNumberOfRows() < 9 ) {
            gameModel.addRow();
            gameModel.deleteGameDrawn();
        }else System.out.println("add false!");
    }
    public void removeRow() {
        boolean isNull = true;
        for (int i = 0; i < gameModel.getNumberOfColumns(); i++) {
            if (gameModel.getCellOwner( gameModel.getNumberOfRows()-1,i) != null) {
                isNull = false;
                System.out.println("remove false!");
            }
        }
//        if(gameModel.getNumberOfRows() > 1 && isNull==true && !gameModel.isWin()){
        if(gameModel.getNumberOfRows() > 1 && isNull){
            gameModel.removeRow();

            if(setGameModel()){
                gameModel.setGameDrawn();
                if(gameModel.isGameDrawn()){
                    addRow();
                }
            }
        }
    }

    public void addColumn() {
//        if (gameModel.getNumberOfColumns() < 9  && !gameModel.isWin()) {
        if (gameModel.getNumberOfColumns() < 9) {
            gameModel.addColumn();
            gameModel.deleteGameDrawn();
        }else System.out.println("add false!");
    }
    public void removeColumn() {
        boolean isNull = true;
//        System.out.println(isNull);
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            if(gameModel.getCellOwner(i, gameModel.getNumberOfColumns()-1) != null){
                System.out.println("remove false!");
                isNull = false;
            }
        }
//        if (gameModel.getNumberOfColumns() > 1 && isNull  && !gameModel.isWin()) {
        if (gameModel.getNumberOfColumns() > 1 && isNull) {
            gameModel.removeColumn();
            if(setGameModel()){
                gameModel.setGameDrawn();
                if(gameModel.isGameDrawn()){
                    addColumn();
                }
            }
        }
    }

    //test if game is drawn
    public boolean setGameModel(){
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            for (int j = 0; j < gameModel.getNumberOfColumns(); j++) {
                if(gameModel.getCellOwner(i,j) == null){
                    return false;
                }
            }
        }
        gameModel.setGameDrawn();
        return true;
    }
    public void increaseWinThreshold() {
        int threshold =  gameModel.getWinThreshold();
//        if(threshold < 9 && !gameModel.isWin()){
//        if(threshold < 9){
        threshold++;
        gameModel.setWinThreshold(threshold);
        System.out.println("threshold:" + threshold);
//        }else System.out.println("The maximum threshold is 9, you have already got it！");

    }
    public void decreaseWinThreshold() {
        int threshold =  gameModel.getWinThreshold();
//        if(threshold > 1 && !gameModel.isWin()){
//        System.out.println(!gameModel.isBegin());
        if(threshold > 3 && !gameModel.isBegin()){
            threshold--;
            gameModel.setWinThreshold(threshold);
            System.out.println("threshold:" + threshold);
        }else System.out.println("Decrease WT false!");
    }
    public void reset() {
        int rows = gameModel.getNumberOfRows();
        int cols = gameModel.getNumberOfColumns();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                gameModel.setCellOwner(i,j,null);
            }
        }
        gameModel.setWinner(null);
        gameModel.deleteGameDrawn();
        gameModel.setCurrentPlayerNumber(0);
    }
    public void winnerTest(){
        int row = gameModel.getNumberOfRows();
        int col = gameModel.getNumberOfColumns();
        int threshold =  gameModel.getWinThreshold();
        //Determine if the row is winner
        if(isRowWin(row,col,threshold)){
            return;
        }
        //Determine if the col is winner
        if(isColWin(row,col,threshold)){
            return;
        }
        //Determine if the right-diagonal is winner
        if(isDiagonalWin(row,col,threshold)){
            return;
        }
        //Determine if the left-rog is winner
        if(isCdiagonalWin(row,col,threshold)) {
            return;
        }
    }

    public boolean isRowWin(int row,int col,int threshold){
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col - threshold + 1; j++) {
                int cnt = 1;
                if (gameModel.getCellOwner(i,j) != null) {
                    for(int k = 1; k < threshold; k++) {
                        if(gameModel.getCellOwner(i, j + k) != null){
//                            char a = gameModel.getCellOwner(i, j).getPlayingLetter();
//                            char b = gameModel.getCellOwner(i, j + k).getPlayingLetter();
//                            if (a == b) {
                            if (gameModel.getCellOwner(i,j) == gameModel.getCellOwner(i,j+k)) {
                                cnt++;
                            }
                        }
                    }
                }
                if (cnt == gameModel.getWinThreshold()) {
                    gameModel.setWinner(gameModel.getCellOwner(i,j));
                    return true;
                }
            }
        }
        return false;
    }
    public boolean isColWin(int row,int col,int threshold){
        for (int i = 0; i < row - threshold + 1; i++) {
            for (int j = 0; j < col; j++) {
                int cnt = 1;
                if (gameModel.getCellOwner(i,j) != null) {
                    for(int k = 1;k < threshold; k++) {
                        if(gameModel.getCellOwner(i + k, j) != null){
//                            char a = gameModel.getCellOwner(i, j).getPlayingLetter();
//                            char b = gameModel.getCellOwner(i + k, j).getPlayingLetter();
//                            Character.compare(gameModel.getCellOwner(i, j).getPlayingLetter(),gameModel.getCellOwner(i + k, j).getPlayingLetter());
//                            if (a == b){
                            if (gameModel.getCellOwner(i,j) == gameModel.getCellOwner(i+k,j)){
                                cnt++;
                            }
                        }

                    }
                }
                if (cnt == gameModel.getWinThreshold()) {
                    gameModel.setWinner(gameModel.getCellOwner(i,j));
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isDiagonalWin(int row,int col,int threshold){
        for (int i = 0; i < row - threshold + 1; i++) {
            for (int j = 0; j < col - threshold + 1; j++) {
                int cnt = 1;
                if (gameModel.getCellOwner(i,j) != null) {
                    for(int k = 1;k < threshold; k++) {
                        if(gameModel.getCellOwner(i + k, j + k) != null){
//                            char a = gameModel.getCellOwner(i, j).getPlayingLetter();
//                            char b = gameModel.getCellOwner(i + k, j + k).getPlayingLetter();
//                            if (a == b) {
                            if (gameModel.getCellOwner(i,j) == gameModel.getCellOwner(i+k,j+k)) {
                                cnt++;
                            }
                        }

                    }
                }
                if (cnt == gameModel.getWinThreshold()) {
                    gameModel.setWinner(gameModel.getCellOwner(i,j));
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isCdiagonalWin(int row,int col,int threshold){
        for (int i = 0; i < row - threshold + 1; i++) {
            for (int j = threshold - 1; j < col; j++) {
                int cnt = 1;
                if (gameModel.getCellOwner(i,j) != null) {
                    for(int k = 1; k < threshold; k++) {
                        if(gameModel.getCellOwner(i + k, j - k) != null){
//                            char a = gameModel.getCellOwner(i, j).getPlayingLetter();
//                            char b = gameModel.getCellOwner(i + k, j - k).getPlayingLetter();
//                            if (a == b) {
                            if (gameModel.getCellOwner(i,j) == gameModel.getCellOwner(i+k,j-k)) {
                                cnt++;
                            }
                        }
                    }
                }
                if (cnt == gameModel.getWinThreshold()) {
                    gameModel.setWinner(gameModel.getCellOwner(i,j));
                    return true;
                }
            }
        }
        return false;
    }
    public boolean isValid(String command) throws OXOMoveException{
//        int row = toAscii(command.substring(0,1).toLowerCase());
//        int col = toAscii(command.substring(1,2).toLowerCase());
//        System.out.println("111111111");
        if(command.length() != 2){
            System.out.println("not equals 2!");
            throw new OXOMoveException.InvalidIdentifierLengthException(command.length());
        }
        int row  = command.toLowerCase().charAt(0);
        int col  = command.toLowerCase().charAt(1);
        char rowc = command.charAt(0);
        char colc = command.charAt(1);
//        System.out.println("length!!!!!!!");
//        System.out.println(command.length());
//        System.out.println(row);//97
//        System.out.println(rowc);//a
//        System.out.println(col);//49
//        System.out.println(colc);//1
        if (!isAlpha(row)) {
//            System.out.println("not invalid alpha!");
            throw new OXOMoveException.InvalidIdentifierCharacterException(OXOMoveException.RowOrColumn.ROW, rowc);
        } else if (!isNumber(col)) {
//            System.out.println("not number!");
            throw new OXOMoveException.InvalidIdentifierCharacterException(OXOMoveException.RowOrColumn.COLUMN, colc);
        } else if (row - 96 > gameModel.getNumberOfRows()) {
//            System.out.println("not row!");
            throw new OXOMoveException.OutsideCellRangeException(OXOMoveException.RowOrColumn.ROW,row - 96);
        } else if (col - 48 > gameModel.getNumberOfColumns() || col - 48 <= 0) {
//            System.out.println("not col!");
            throw new OXOMoveException.OutsideCellRangeException(OXOMoveException.RowOrColumn.COLUMN,col - 48);
        } else if (gameModel.getCellOwner(row - 'a',col - '1') != null) {
//            System.out.println("already taken!");
            throw new OXOMoveException.CellAlreadyTakenException(row - 'a',col - '1');
        }
        return true;
    }

    public static boolean isAlpha(int s){
//        System.out.println("s:"+ s);
        if(s >= 97 && s <= 122){//a - z
            return true;
        } else return s >= 65 && s <= 90;//A - Z
    }

    public static boolean isNumber(int s){
//        System.out.println(s-48);
        if(s - 48 >= 0 && s - 48 <= 9){
            return true;
        }else{
            return false;
        }
    }

    public static int toAscii(String s){
        StringBuilder sb = new StringBuilder();
        String ascString = null;
        int asciiInt;
        for (int i = 0; i < s.length(); i++){
            sb.append((int)s.charAt(i));
//            char c = s.charAt(i);
        }
        ascString = sb.toString();
        asciiInt = Integer.parseInt(ascString);
        return asciiInt;
    }
}
