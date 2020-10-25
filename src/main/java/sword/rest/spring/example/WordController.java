package sword.rest.spring.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@RestController
public final class WordController {

    static final String WORD_COLLECTION_PATH = "/words";
    static final String WORD_PATH = "/word/{id}";
    static final String COLUMN_NAME = "text";

    private final WordRepository wordRepository;

    @Autowired
    public WordController(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    @GetMapping(WORD_COLLECTION_PATH)
    public Map<String, String> words() {
        return wordRepository.getAll();
    }

    @GetMapping(WORD_PATH)
    public ResponseEntity word(@PathVariable String id) {
        try {
            final Map<String, String> wordDictionary = new HashMap<>();
            wordDictionary.put(COLUMN_NAME, wordRepository.get(id));
            return ResponseEntity.ok(wordDictionary);
        }
        catch (WordRepository.WordNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(WORD_COLLECTION_PATH)
    public ResponseEntity createWord(HttpServletRequest request, @RequestBody String text) {
        final String requestUrl = request.getRequestURL().toString();
        if (!requestUrl.endsWith(WORD_COLLECTION_PATH)) {
            return ResponseEntity.status(500).build();
        }

        try {
            final String newId = wordRepository.create(text);
            final String uriRoot = requestUrl.substring(0, requestUrl.length() - WORD_COLLECTION_PATH.length());

            final Map<String, String> wordDictionary = new HashMap<>();
            wordDictionary.put(COLUMN_NAME, text);
            return ResponseEntity.created(new URI(uriRoot + "/word/" + newId)).body(wordDictionary);
        }
        catch (WordRepository.InvalidTextException | WordRepository.TextAlreadyPresentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping(WORD_PATH)
    public ResponseEntity deleteWord(@PathVariable String id) {
        try {
            wordRepository.remove(id);
            return ResponseEntity.noContent().build();
        }
        catch (WordRepository.WordNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping(WORD_PATH)
    public ResponseEntity replaceWord(@PathVariable String id, @RequestBody String text) {
        try {
            wordRepository.replace(id, text);

            final Map<String, String> wordDictionary = new HashMap<>();
            wordDictionary.put(COLUMN_NAME, text);
            return ResponseEntity.ok(wordDictionary);
        }
        catch (WordRepository.TextAlreadyPresentException |
                WordRepository.WordNotFoundException |
                WordRepository.InvalidTextException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
