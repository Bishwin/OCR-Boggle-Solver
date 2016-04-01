package w1441879.boggle.boggleSolver;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import w1441879.boggle.dictionary.Trie;

/**
 * TODO: boggle solver algorithm
 */
public class Solver {

    Trie dictionary;
    String[][] board;
    boolean[][] tracker;
    final static int GRID_SIZE = 4;
    Set<String> results = new HashSet<>();

    public Solver(){
        dictionary = new Trie();
        board = new String[GRID_SIZE][GRID_SIZE];
    }

    public Solver(Trie dictionary){
        this.dictionary = dictionary;
        board = new String[GRID_SIZE][GRID_SIZE];
    }

    public void addBoard(String str){
        int counter = 0;
        if(str.length() == GRID_SIZE*GRID_SIZE){

            for(int col = 0; col < GRID_SIZE; col++){

                for(int row = 0; row < GRID_SIZE; row++){

                    char letter = str.charAt(counter);
                    if(letter == 'q'){
                        board[col][row] = "qu";
                        counter++;
                    }else {
                        board[col][row] = "" + letter;
                        counter++;
                    }
                }
            }
        }
        printBoard();
    }


    public void printBoard(){

        for(int col = 0; col < GRID_SIZE; col++){

            for(int row = 0; row < GRID_SIZE; row++){
                System.out.print(board[col][row]+ " ");
            }
            System.out.println("");
        }
    }

    public Set<String> solvePuzzle(){
        for(int col = 0; col < GRID_SIZE; col++){
            //int col = 0;
            //int row = 0;
            for(int row = 0; row < GRID_SIZE; row++){

                String currentWord = board[col][row];
                tracker = new boolean[GRID_SIZE][GRID_SIZE];
                solvePuzzle(tracker, currentWord,col, row);
            }
        }
        printResults();
        return results;

    }

    private void printResults(){
        Iterator<String> it = results.iterator();
        while(it.hasNext()){
            System.out.println(it.next());
        }
    }

    private boolean[][] deepCopy(boolean[][] A){
        boolean[][] B = new boolean[GRID_SIZE][GRID_SIZE];
        for(int x = 0; x < GRID_SIZE; x++){
            for(int y = 0; y < GRID_SIZE; y++){
                if(A[x][y]){
                    B[x][y] = A[x][y];
                }
            }
        }
        return B;
    }


    private void solvePuzzle( boolean[][] tracker,String word, int col, int row){

        if(dictionary.isWord(word)){
            results.add(word);
        }

        if(!dictionary.isPrefix(word)){
            return;
        }
        boolean[][] tmp = deepCopy(tracker);
        tmp[col][row] = true;

        //upper left
        if (0 <= col - 1 && 0 <= row - 1 && !tmp[col - 1][row - 1]){
            solvePuzzle(tmp, word+board[col-1][row-1], col-1, row-1 );
        }

        //up
        if (row - 1 >= 0 && !tmp[col][row - 1]){
            solvePuzzle( tmp, word + board[col][row-1], col, row-1 );
        }

        //upper right
        if (col + 1 < GRID_SIZE && row-1 >= 0 && !tmp[col + 1][row - 1]){
            solvePuzzle(tmp, word+board[col+1][row-1], col+1, row-1 );
        }

        //right
        if (col + 1 < GRID_SIZE && !tmp[col + 1][row]){
            solvePuzzle( tmp, word + board[col+1][row], col+1, row );
        }
        //lower right
        if (col+1 < GRID_SIZE && row+1 < GRID_SIZE && !tmp[col+1][row+1]){
            solvePuzzle(tmp, word+board[col+1][row+1], col+1, row+1 );
        }

        //down
        if (row + 1 < GRID_SIZE && !tmp[col][row + 1]){
            solvePuzzle( tmp,  word + board[col][row+1], col, row+1 );
        }

        //lower left
        if (col -1 >= 0 && row + 1 < GRID_SIZE && !tmp[col - 1][row + 1]){
            solvePuzzle(tmp, word+board[col-1][row+1], col-1, row+1 );
        }

        //left
        if (col -1 >= 0&& !tmp[col - 1][row]){
            solvePuzzle(tmp, word + board[col-1][row], col-1, row );
        }

        /**UP
         if(col-1 >= 0 && !tmp[col-1][row]){
         solvePuzzle(col-1, row, currentWord + board[col-1][row], tmp);
         }

         //UP RIGHT
         if(col-1 >= 0 && row+1 < GRID_SIZE && !tmp[col-1][row+1]){
         currentWord += board[col-1][row+1];
         solvePuzzle(col-1, row+1, currentWord, tmp);
         }

         //RIGHT
         if( row+1 < GRID_SIZE && !tmp[col][row+1]){
         currentWord += board[col][row+1];
         solvePuzzle(col, row+1, currentWord, tmp);
         }

         //DOWN RIGHT
         if(col+1 < GRID_SIZE && row+1 < GRID_SIZE && !tmp[col+1][row+1]){
         currentWord += board[col+1][row+1];
         solvePuzzle(col+1,row+1, currentWord, tmp);
         }

         //DOWN
         if(col+1 < GRID_SIZE && !tmp[col+1][row]){
         currentWord += board[col][row];
         solvePuzzle(col+1, row, currentWord, tmp);
         }

         //DOWN LEFT
         if(col+1 < GRID_SIZE && row-1 >= 0 && !tmp[col+1][row-1]){
         currentWord += board[col+1][row-1];
         solvePuzzle(col+1, row-1, currentWord, tmp);
         }

         //LEFT
         if(row-1 >= 0 && !tmp[col][row-1]){
         currentWord += board[col][row-1];
         solvePuzzle(col, row-1, currentWord, tmp);
         }

         //UP LEFT
         if(col-1 >=0 && row-1 >=0 && !tmp[col-1][row-1]){
         currentWord += board[col-1][row-1];
         solvePuzzle(col-1, row-1, currentWord, tmp);
         }
         /**
         * board[x][y]
         * right
         * if (x < 4)  x+1
         * bottom right
         * if (x<4 && y<4) x+1 && y+1
         * bottom
         * if (y<4) y+1
         * bottom left
         * if (x>0 && y>0) x-1 && y+1
         * left
         * if(x>0) x-1
         * top left
         * if (x>0 && y>0) x-1 && y-1
         * top
         * if(y>0) y-1
         * */
    }


}