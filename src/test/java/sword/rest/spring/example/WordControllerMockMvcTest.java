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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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
    private WordController superHeroController;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .standaloneSetup(superHeroController)
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
}