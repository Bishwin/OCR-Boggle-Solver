package w1441879.boggle;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

import w1441879.boggle.boggleSolver.Solver;
import w1441879.boggle.dictionary.Trie;

public class TextSolverFragment extends Fragment implements View.OnClickListener {
    private final int MAX_WORD_LENGTH = 16;
    private EditText gameBoard;
    private ListView listView;
    private Button submitBoard;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.activity_text_solver,container,false);

        gameBoard = (EditText) view.findViewById(R.id.game_board);
        listView = (ListView) view.findViewById(R.id.result_view);
        submitBoard = (Button) view.findViewById(R.id.submit_board);
        submitBoard.setOnClickListener(this);
        Button clearBoard = (Button) view.findViewById(R.id.clear_board);
        clearBoard.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v == submitBoard){

            //create dictionary and solver instance
            Trie dictionary = new Trie(getActivity());
            Solver solver = new Solver(dictionary);

            String str = gameBoard.getText().toString();

            if(gameBoard.length() == MAX_WORD_LENGTH){
                System.out.println("str" + str);
                System.out.println("gameboard" + gameBoard.toString());
                solver.addBoard(str);

                //start solver and display results
                Set<String> results = solver.solvePuzzle();
                displayResults(results);

            }else {
                Snackbar snackbar = Snackbar.make(view, "Please Input 16 Letters Exactly", Snackbar.LENGTH_LONG);
                snackbar.show();
            }

        } else {
            //clear textbox on "clear" button press
            gameBoard.setText("");
        }
    }

    /**
     * Displays results of the solving algorithm
     * to screen in a fragment using a listview
     * iterates through results checking word length
     * to assign points based on Boggle rules
     * @param results result set to be displayed
     */
    public void displayResults(Set<String> results){

        String[] resultsArray = results.toArray(new String[results.size()]);

        //orders array, longest words first
        Arrays.sort(resultsArray, new Comparator<String>() {
            @Override
            public int compare(String str1, String str2) {
                if(str1.length() < str2.length()){
                    return 1;
                } else if(str1.length() > str2.length()){
                    return -1;
                } else return 0;
            }
        });

        //concats Boggle points
        for(int i =0; i<resultsArray.length; i++){
            int length = resultsArray[i].length();
            if(length >=8 ){
                resultsArray[i] = resultsArray[i].concat(" (11pt)");
            }else if(length == 7){
                resultsArray[i] = resultsArray[i].concat(" (4pt)");
            }else if(length == 6){
                resultsArray[i] = resultsArray[i].concat(" (3pt)");
            }else if(length == 5){
                resultsArray[i] = resultsArray[i].concat(" (2pt)");
            }else if(length <= 4){
                resultsArray[i] = resultsArray[i].concat(" (1pt)");
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, resultsArray);
        listView.setAdapter(adapter);
    }

    public void setText(String word){
        gameBoard.setText("");
        gameBoard.setText(word);
    }
}
