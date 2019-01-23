package spell;

import java.util.ArrayList;
import java.util.Collections;

public class Words implements ITrie {
    WordNode root = new WordNode(null);

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

            currNode = currNode.insertSubNode(letter, i == (word.length() - 1) ? word : null);
        }

    }

    private void addDeletionPossibilities(String word, ArrayList<String> possMatches) {
        if (word.length() < 2) {
            return;
        }

        for(int i = 0; i < word.length(); i++) {
            String possMatch;
            if (i == 0) {
                possMatch = word.substring(1);
            } else if (i == word.length() - 1) {
                possMatch = word.substring(0, i);
            } else {
                possMatch = word.substring(0, i) + word.substring(i+1);
            }

            possMatches.add(possMatch);
        }
    }

    private void addTranspositionPossibilities(String word, ArrayList<String> possMatches) {

        for (int i = 0; i < word.length() - 1; i++) {

            char[] ch = word.toCharArray();
            char temp = ch[i];
            ch[i] = ch[i + 1];
            ch[i + 1] = temp;

            possMatches.add(new String(ch));
        }
    }

    private void addAlterationPossibilities(String word, ArrayList<String> possMatches) {

        for (int i = 0; i < word.length(); i++) {

            for (int j = 'a'; j <= 'z'; j++) {

                StringBuilder possMatch = new StringBuilder(word);
                possMatch.setCharAt(i, (char)j);
                possMatches.add(possMatch.toString());
            }
        }
    }

    private void addInsertionPossibilities(String word, ArrayList<String> possMatches) {

        for (int i = 0; i <= word.length(); i++) {

            for (int j = 'a'; j <= 'z'; j++) {

                StringBuilder possMatch = new StringBuilder(word);
                possMatch.insert(i, (char)j);
                possMatches.add(possMatch.toString());
            }
        }
    }

    private WordNode findSimilarWord(String word) {

        ArrayList<String> possMatches = new ArrayList<>();

        addDeletionPossibilities(word, possMatches);
        addTranspositionPossibilities(word, possMatches);
        addAlterationPossibilities(word, possMatches);
        addInsertionPossibilities(word, possMatches);

        ArrayList<WordNode> matches = new ArrayList<>();

        // edit distance 1
        for (String possMatch : possMatches) {

            WordNode currNode = findExactWord(possMatch);

            if (currNode != null) {

                matches.add(currNode);
            }
        }

        // edit distance 2
        if (matches.size() == 0) {
            ArrayList<String> possMatches2 = new ArrayList<>();

            for (String possMatch : possMatches) {
                addDeletionPossibilities(possMatch, possMatches2);
                addTranspositionPossibilities(possMatch, possMatches2);
                addAlterationPossibilities(possMatch, possMatches2);
                addInsertionPossibilities(possMatch, possMatches2);
            }

            for (String possMatch : possMatches2) {

                WordNode currNode = findExactWord(possMatch);

                if (currNode != null) {

                    matches.add(currNode);
                }
            }
        }

        Collections.sort(matches, Collections.reverseOrder());

        return matches.size() > 0 ? matches.get(0) : null;
    }

    private WordNode findExactWord(String word) {

        WordNode currNode = root;

        for(int i = 0; i < word.length() && currNode != null; i++) {

            char letter = word.charAt(i);

            currNode = currNode.getSubNode(letter);
        }

        return currNode == null || currNode.getValue() == 0 ? null : currNode;
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

    public class WordNode implements ITrie.INode, Comparable<WordNode> {
        private int value = 0;
        private WordNode[] nodes = new WordNode[26];
        private String word;

        public WordNode(String word) {
            this.word = word;
        }

        public WordNode insertSubNode(char letter, String word) {
            WordNode nextNode = getSubNode(letter);

            if (nextNode == null) {
                nextNode = new WordNode(word);
                nodes[letter - 'a'] = nextNode;
            }

            if (word != null) {
                nextNode.incrementValue();
                nextNode.setWord(word);
            }

            return nextNode;
        }

        public WordNode getSubNode(char letter) {
            if (letter < 97 || letter > 122) {
                return null;
            }

            return nodes[letter - 'a'];
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        @Override
        public int getValue() {
            return value;
        }

        public void incrementValue() {
            value++;
        }

        @Override
        public int compareTo(WordNode o) {
            int result = this.getValue() - o.getValue();

            if (result == 0) {
                result = o.getWord().compareTo(this.getWord());
            }

            return result;
        }
    }
}
