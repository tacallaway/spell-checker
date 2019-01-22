package spell;

import java.util.ArrayList;

public class Words implements ITrie {
    WordNode root = new WordNode();

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public void add(String word) {
        WordNode currNode = root;

        for(int i = 0; i < word.length(); i++) {

            char letter = word.charAt(i);

            currNode = currNode.insertSubNode(letter, i == (word.length() - 1));
        }

    }

    private void addDeletionPossibilities(ArrayList<String> possMatches) {
        // add deletion options
    }

    private void addTranspositionPossibilities(ArrayList<String> possMatches) {

    }

    private void addAlterationPossibilities(ArrayList<String> possMatches) {

    }

    private void addAdditionPossibilities(ArrayList<String> possMatches) {

    }

    private WordNode findSimilarWord(String word) {

        ArrayList<String> matchPossibilities = new ArrayList<>();

        addDeletionPossibilities(matchPossibilities);
        addTranspositionPossibilities(matchPossibilities);
        addAlterationPossibilities(matchPossibilities);
        addAdditionPossibilities(matchPossibilities);

        // match logic

        return null;
    }

    private WordNode findExactWord(String word) {

        WordNode currNode = root;

        for(int i = 0; i < word.length() && currNode != null; i++) {

            char letter = word.charAt(i);

            currNode = currNode.getSubNode(letter);
        }

        return currNode;
    }

    @Override
    public INode find(String word) {

        WordNode currNode = findExactWord(word);

        if (currNode == null) {

            currNode = findSimilarWord(word);
        }

        return currNode;
    }

    @Override
    public int getWordCount() {
        return 0;
    }

    @Override
    public int getNodeCount() {
        return 0;
    }

    public class WordNode implements ITrie.INode {
        private int value = 0;
        private WordNode[] nodes = new WordNode[26];

        public WordNode insertSubNode(char letter, boolean endOfWord) {
            WordNode nextNode = getSubNode(letter);

            if (nextNode == null) {
                nextNode = new WordNode();
                nodes[letter - 'a'] = nextNode;
            }

            if (endOfWord) {
                nextNode.incrementValue();
            }

            return nextNode;
        }

        public WordNode insertSubNode(char letter) {
            return insertSubNode(letter, false);
        }

        public WordNode getSubNode(char letter) {
            if (letter < 97 || letter > 122) {
                return null;
            }

            return nodes[letter - 'a'];
        }

        @Override
        public int getValue() {
            return value;
        }

        public void incrementValue() {
            value++;
        }
    }
}
