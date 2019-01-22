package spell;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SpellCorrector implements ISpellCorrector {
    Words words = new Words();

    @Override
    public void useDictionary(String dictionaryFileName) throws IOException {

        try(BufferedReader dictionary = new BufferedReader(new FileReader(dictionaryFileName))) {

            String line;
            while ((line = dictionary.readLine()) != null) {

                String word = line.trim().toLowerCase();

                if (word.matches("^[a-z]+$")) {

                    words.add(word);
                }
            }
        }
    }

    @Override
    public String suggestSimilarWord(String inputWord) {

        Words.INode foundWord = words.find(inputWord);

        if (foundWord != null && foundWord.getValue() > 0) {

            return inputWord;
        }

        return null;
    }
}
