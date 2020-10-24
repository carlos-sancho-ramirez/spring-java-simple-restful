package sword.rest.spring.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static sword.rest.spring.example.WordController.WORD_PATH;

/**
 * Basic unit tests for the WordController.
 *
 * This test has been creating following the information located in the following place:
 * https://thepracticaldeveloper.com/guide-spring-boot-controller-tests/
 */
@ExtendWith(MockitoExtension.class)
final class WordControllerMockMvcTest {

    private MockMvc mvc;

    @InjectMocks
    private WordController wordController;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .standaloneSetup(wordController)
                .build();
    }

    @Test
    void emptyWordsOnStart() throws Exception {
        final MockHttpServletResponse response = mvc
                .perform(get(WordController.WORD_COLLECTION_PATH).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("{}");
    }

    private MockHttpServletResponse insertWord(String word) throws Exception {
        return mvc
                .perform(post(WordController.WORD_COLLECTION_PATH).accept(MediaType.APPLICATION_JSON).content(word))
                .andReturn().getResponse();
    }

    private MockHttpServletResponse getWord(String id) throws Exception {
        final String path = WORD_PATH.replace("{id}", id);
        return mvc
                .perform(get(path).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
    }

    private String extractWordId(String location) {
        return location.substring(location.lastIndexOf('/') + 1);
    }

    @Test
    void insertWord() throws Exception {
        final MockHttpServletResponse response = insertWord("myWord");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        final String location = response.getHeader("Location");
        assertNotNull(location);
        assertThat(response.getContentAsString()).isEqualTo("{\"" + WordController.COLUMN_NAME + "\":\"myWord\"}");
    }

    @Test
    void getInsertedWord() throws Exception {
        final String location = extractWordId(insertWord("myWord").getHeader("Location"));
        final MockHttpServletResponse response = getWord(location);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("{\"" + WordController.COLUMN_NAME + "\":\"myWord\"}");
    }
}