package sword.rest.spring.example;

import java.util.Map;

interface WordRepository {

    Map<String, String> getAll();

    String get(String id) throws WordNotFoundException;

    void remove(String id) throws WordNotFoundException;

    void replace(String id, String text) throws InvalidTextException, WordNotFoundException, TextAlreadyPresentException;

    String create(String text) throws InvalidTextException, TextAlreadyPresentException;

    class WordNotFoundException extends Exception {
        public WordNotFoundException(String id) {
            super("Word with id " + id + " does not exists");
        }
    }

    class TextAlreadyPresentException extends Exception {
        public TextAlreadyPresentException(String text) {
            super("Word '" + text + "' already exists");
        }
    }

    class InvalidTextException extends Exception {
        public InvalidTextException(String text) {
            super("Text '" + text + "' is not valid for a word");
        }
    }
}
