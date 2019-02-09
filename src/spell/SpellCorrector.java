package spell;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SpellCorrector implements ISpellCorrector {
    Words words = new Words();

    @Override
    public void useDictionary(String dictionaryFileName) throws IOException {

        Scanner scanner = new Scanner(new File(dictionaryFilename));

        while (scanner.hasNextLine()) {
            
            String word = scanner.nextLine().trim().toLowerCase();

            if (word.matches("^[a-z]+$")) {

                words.add(word);
            }
        }
    }

    @Override
    public String suggestSimilarWord(String inputWord) {

        if (inputWord == null) {
            return null;
        }

        inputWord = inputWord.toLowerCase();

        Words.WordNode foundWord = words.findSimilarWord(inputWord);

        if (foundWord != null && foundWord.getValue() > 0) {

            return foundWord.getWord();
        }

        return null;
    }
}
