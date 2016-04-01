package w1441879.boggle.dictionary;

/**
 * TODO: add method isWord() - checks words
 * TODO: add method isPrefix() - checks start of word
 * TODO: add method getWords() - get all words for testing
 */

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
     *
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
     *
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

    public TrieNode lookUp(String word){
        TrieNode node = this;
        if(word != null){
        for (int i = 0; i < word.length(); i++) {
            int index = word.charAt(i) - 'a';
            TrieNode child = node.children[index];
            if (child == null) {
                // There is no such word
                return null;
            }
            node = child;
        }
        }
        return node;
    }

    public boolean isWord(String word){
        TrieNode node = lookUp(word);
        return node !=null && node.isWord;

    }

    public boolean isPrefix(String word){
        TrieNode node = lookUp(word);
        return node !=null && node.childNum > 0;
    }
}

