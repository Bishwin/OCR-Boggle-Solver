package w1441879.boggle.dictionary;

public class TrieNode {
    char c;
    boolean isWord;
    TrieNode[] children;
    int childNum;

    /**
     * Constructor for root node
     */
    public TrieNode() {
        isWord = false;
        children = new TrieNode[26];
        childNum = 0;
    }

    /**
     * Constructor for children nodes
     * @param c , a character of a word
     */
    public TrieNode(char c) {
        this.c = c;
        isWord = false;
        children = new TrieNode[26];
        childNum = 0;
    }

    /**
     * Adds a word to the trie recursively
     * @param word , the word to be added
     */
    public void addWord(String word) {

        if (word.isEmpty()) {
            isWord = true;
            return;
        }

        int index = word.charAt(0) - 'a';

        if (children[index] == null) {
            children[index] = new TrieNode(word.charAt(0));
            childNum++;
        }

        children[index].addWord(word.substring(1));
    }

    /**
     * Checks a node branch until for the desired word
     * returns null if the word doesn't exist
     * or a node for the words last letter
     * @param word , the word to be checked
     */
    public TrieNode checkNode(String word){
        TrieNode node = this;
        for (int i = 0; i < word.length(); i++) {
            int index = word.charAt(i) - 'a';
            TrieNode child = node.children[index];
            if (child == null) {
                // There is no such word
                return null;
            }
            node = child;
        }
        return node;
    }

    /**
     * Calls checkNode to check the given
     * string is a valid word
     * @param word , the word to be checked
     * @return boolean
     */
    public boolean isWord(String word){
        TrieNode node = checkNode(word);
        return node !=null && node.isWord;

    }

    /**
     * Calls checkNode to check the given
     * string is a valid prefix
     * @param prefix , the prefix to be checked
     * @return boolean
     */
    public boolean isPrefix(String prefix){
        TrieNode node = checkNode(prefix);
        return node !=null && node.childNum > 0;
    }
}

