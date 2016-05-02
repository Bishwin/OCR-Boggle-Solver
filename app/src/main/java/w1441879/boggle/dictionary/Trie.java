package w1441879.boggle.dictionary;

import android.content.Context;
import android.content.res.AssetManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Trie {
    private TrieNode root;
    private Context mContext;

    /**
     * Constructor for trie
     */
    public Trie(){
        root = new TrieNode();
        readDictionary();
    }

    /**
     * Constructor for trie
     * @param context, requires context to access assets
     */
    public Trie(Context context){
        mContext = context;
        root = new TrieNode();
        readDictionary();
    }


    /**
     * Reads from dictionary file "enable1.txt" located in resources
     * populates trie with words longer than 3 shorter than 17, per boggle rules
     * though the board is a 4x4 grid, allowing maximum 16 letter word
     * "q" appears as "qu" in the game, thus explains 17 maximum word length.
     */
    private void readDictionary(){
        //String file = "\\dictionary\\enable1.txt";

        AssetManager assetManager = mContext.getAssets();

        try {
            //BufferedReader reader = new BufferedReader( new FileReader(file));
            InputStream inputStream = assetManager.open("enable1.txt");

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String str;
            try {
                while ((str = reader.readLine()) != null) {
                    int length = str.length();
                    if (length >= 3 && length <= 17) {
                        addWord(str);
                    }
                }
            }catch(IOException e){
                System.out.println("#1 file not found");
            }

        }catch (IOException e){
            System.out.println("#2 file not found");
        }
    }

    /**
     * Adds a word to the trie recursively
     * @param word , the word to be added
     */
    public void addWord(String word){
        root.addWord(word.toLowerCase());
    }


    /**
     * Testing method before Solver existed
     * @param word , the word to be checked exists
     */
    public void checkWord(String word){
        isPrefix(word.toLowerCase());
        isWord(word.toLowerCase());

    }


    /**
     * Calls respective method in the rootnode
     * checks if a word exists
     * @param word , the word to be checked
     * @return boolean
     */
    public boolean isWord(String word){
        return root.isWord(word);
    }

    /**
     * Calls respective method in the rootnode
     * checks if a String combination is a prefix to a word
     * @param prefix , the prefix to be checked
     * @return boolean
     */
    public boolean isPrefix(String prefix){
        boolean result = root.isPrefix(prefix);
        if(result){
            System.out.println(prefix + " is prefix");
        }
        return result;
    }
}

