package sword.rest.spring.example;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@RestController
public final class WordController {

    private static final String WORD_COLLECTION_PATH = "/words";
    private static final String WORD_PATH = "/word/{id}";
    private static final String COLUMN_NAME = "text";

    private final Map<String, String> data = new HashMap<>();
    private int lastId;

    @GetMapping(WORD_COLLECTION_PATH)
    public Map<String, String> words() {
        return data;
    }

    @GetMapping("/word/{id}")
    public ResponseEntity word(@PathVariable String id) {
        if (data.containsKey(id)) {
            final Map<String, String> wordDictionary = new HashMap<>();
            wordDictionary.put(COLUMN_NAME, data.get(id));
            return ResponseEntity.ok(wordDictionary);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(WORD_COLLECTION_PATH)
    public ResponseEntity createWord(HttpServletRequest request, @RequestBody String text) {
        if (data.containsValue(text)) {
            return ResponseEntity.badRequest().body("Word '" + text + "' already exists");
        }
        else {
            final String requestUrl = request.getRequestURL().toString();
            if (!requestUrl.endsWith(WORD_COLLECTION_PATH)) {
                return ResponseEntity.status(500).build();
            }

            final String newId = Integer.toString(++lastId);
            data.put(newId, text);

            final String uriRoot = requestUrl.substring(0, requestUrl.length() - WORD_COLLECTION_PATH.length());
            try {
                final Map<String, String> wordDictionary = new HashMap<>();
                wordDictionary.put(COLUMN_NAME, text);
                return ResponseEntity.created(new URI(uriRoot + "/word/" + newId)).body(wordDictionary);
            }
            catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return ResponseEntity.status(500).build();
    }

    @DeleteMapping(WORD_PATH)
    public ResponseEntity deleteWord(@PathVariable String id) {
        if (data.remove(id) != null) {
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.badRequest().build();
        }
    }
}
