package w1441879.boggle;

import android.support.v4.app.Fragment;
import android.os.Bundle;
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
    private EditText gameBoard;
    private ListView listView;
    private Button submitBoard, clearBoard;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.activity_text_solver,container,false);

        gameBoard = (EditText) view.findViewById(R.id.game_board);
        listView = (ListView) view.findViewById(R.id.result_view);
        submitBoard = (Button) view.findViewById(R.id.submit_board);
        submitBoard.setOnClickListener(this);
        clearBoard = (Button) view.findViewById(R.id.clear_board);
        clearBoard.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v == submitBoard){

            Trie dictionary = new Trie(getActivity());

            Solver solver = new Solver(dictionary);

            String str = gameBoard.getText().toString();
            System.out.println("str" + str);
            System.out.println("gameboard" + gameBoard.toString());
            solver.addBoard(str);

            //Set<String> results = new HashSet<>();
            Set<String> results = solver.solvePuzzle();
            displayResults(results);


        } else {
            gameBoard.setText("");
        }
    }

    private void displayResults(Set<String> results){

        String[] resultsArray = results.toArray(new String[results.size()]);

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
}
