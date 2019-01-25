package spell;

import java.util.ArrayList;
import java.util.Collections;

public class Words implements ITrie {
    public WordNode root = new WordNode(null);

    private int getHashCode(WordNode node, int hashCode) {

        for (int i = 'a'; i <= 'z'; i++) {

            WordNode currNode = node.getSubNode((char) i);

            if (currNode != null) {
                hashCode += 7 * getHashCode(currNode, hashCode);
            }
        }

        return hashCode;
    }

    @Override
    public int hashCode() {
        return getHashCode(root, 1);
    }

    private boolean isEqual(WordNode node1, WordNode node2) {

        if (node1 == null && node2 == null) {
            return true;
        }

        if (node1 == null || node2 == null) {
            return false;
        }

        if (node1.getValue() != node2.getValue()) {
            return false;
        }

        for (int i = 'a'; i <= 'z'; i++) {

            boolean result = isEqual(node1.getSubNode((char)i), node2.getSubNode((char)i));

            if (!result) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Words)) {
            return false;
        }
        
        return isEqual(this.root, ((Words)obj).root);
    }

    private void returnString(WordNode node, StringBuilder sb) {

        for (int i = 'a'; i <= 'z'; i++) {

            WordNode currNode = node.getSubNode((char)i);

            if (currNode != null) {
                if (currNode.getWord() != null) {
                    sb.append(currNode.getWord()).append("\n");
                }
                returnString(currNode, sb);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        returnString(root, sb);
        return sb.toString();
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
        possMatches.clear(); // for some reason the TA test harness needs this to be cleared

        addDeletionPossibilities(word, possMatches);
        addTranspositionPossibilities(word, possMatches);
        addAlterationPossibilities(word, possMatches);
        addInsertionPossibilities(word, possMatches);

        ArrayList<WordNode> matches = new ArrayList<>();
        matches.clear(); // for some reason the TA test harness needs this to be cleared

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

    private int returnWordCount(WordNode node, int count) {

        for (int i = 'a'; i <= 'z'; i++) {

            WordNode currNode = node.getSubNode((char)i);

            if (currNode != null) {
                if (currNode.getValue() > 0) {
                    count++;
                }
                count = returnWordCount(currNode, count);
            }
        }

        return count;
    }

    private int returnNodeCount(WordNode node, int count) {

        for (int i = 'a'; i <= 'z'; i++) {

            WordNode currNode = node.getSubNode((char)i);

            if (currNode != null) {
                count++;
                count = returnNodeCount(currNode, count);
            }
        }

        return count;
    }

    @Override
    public int getWordCount() {

        return returnWordCount(root, 0);
    }

    @Override
    public int getNodeCount() {

        return returnNodeCount(root, 1);
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
