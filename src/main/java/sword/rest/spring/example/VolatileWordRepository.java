package sword.rest.spring.example;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
final class VolatileWordRepository implements WordRepository {

    private final Map<String, String> data = new HashMap<>();
    private int lastId;

    @Override
    public Map<String, String> getAll() {
        return Collections.unmodifiableMap(data);
    }

    @Override
    public void remove(String id) throws WordNotFoundException {
        if (data.remove(id) == null) {
            throw new WordNotFoundException(id);
        }
    }

    @Override
    public String get(String id) throws WordNotFoundException {
        final String text = data.get(id);
        if (text == null) {
            throw new WordNotFoundException(id);
        }

        return text;
    }

    @Override
    public void replace(String id, String text) throws InvalidTextException, WordNotFoundException, TextAlreadyPresentException {
        if (text == null) {
            throw new InvalidTextException(null);
        }

        final String currentText = data.get(id);
        if (currentText == null) {
            throw new WordNotFoundException(id);
        }
        else if (!text.equals(currentText) && data.containsValue(text)) {
            throw new TextAlreadyPresentException(text);
        }
        else {
            data.put(id, text);
        }
    }

    @Override
    public String create(String text) throws InvalidTextException, TextAlreadyPresentException {
        if (text == null) {
            throw new InvalidTextException(null);
        }

        if (data.containsValue(text)) {
            throw new TextAlreadyPresentException(text);
        }
        else {
            final String newId = Integer.toString(++lastId);
            data.put(newId, text);
            return newId;
        }
    }
}
