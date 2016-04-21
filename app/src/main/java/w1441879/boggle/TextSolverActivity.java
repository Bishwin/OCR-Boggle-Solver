package w1441879.boggle;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.HashSet;
import java.util.Set;

import w1441879.boggle.boggleSolver.Solver;
import w1441879.boggle.dictionary.Trie;

public class TextSolverActivity extends Activity implements View.OnClickListener {
    private EditText gameBoard;
    private ListView listView;
    private Button submitBoard, clearBoard;
    Set<String> results = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_solver);

        gameBoard = (EditText) findViewById(R.id.game_board);

        listView = (ListView) findViewById(R.id.result_view);

        submitBoard = (Button) findViewById(R.id.submit_board);
        submitBoard.setOnClickListener(this);
        clearBoard = (Button) findViewById(R.id.clear_board);
        clearBoard.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Fragment fragment;
        if(v == submitBoard){

            Trie dictionary = new Trie(TextSolverActivity.this);


            Solver solver = new Solver(dictionary);


            String str = gameBoard.getText().toString();
            System.out.println("str" + str);
            System.out.println("gameboard" + gameBoard.toString());
            solver.addBoard(str);
            results = solver.solvePuzzle();

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, results.toArray(new String[results.size()]));

            listView.setAdapter(adapter);

            //fragment = new ResultFragment();
            //replaceFragment(fragment);
        } else {
            gameBoard.setText("");
        }
    }

    public void replaceFragment(Fragment fragement){
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.fragment_container, fragement);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
