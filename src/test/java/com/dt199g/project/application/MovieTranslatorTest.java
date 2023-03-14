package com.dt199g.project.application;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

class MovieTranslatorTest {
    static MovieTranslator translator;

    @BeforeAll
    static void setup() {
        Service service = mock(Service.class);
        MovieTranslatorTest.translator = new MovieTranslator(service);
    }

    /**
     * Names should be captured and non-names should not be captured. Should remove trailing "'s".
     */
    @Test
    void getName() {
        assertAll(
                () -> assertEquals(translator.getName("Hello my name is Harry Elmo."), "Harry Elmo"),
                () -> assertEquals(translator.getName("Jean-Claude Van Damme is a triple name"), "Jean-Claude Van Damme"),
                () -> assertEquals(translator.getName("Some names have apostrophes, like Elsa O'Connolly"), "Elsa O'Connolly"),
                () -> assertEquals(translator.getName("Names like Frances DuBois are also possible."), "Frances DuBois"),
                () -> assertEquals(translator.getName("Single names like Bob are a no-go."), ""),
                () -> assertEquals(translator.getName("Tom Cruise's best movie is Mission Impossible"), "Tom Cruise")
        );
    }

    /**
     * Should only capture years between 1900-2099; other dates should not be captured.
     */
    @Test
    void getYear() {
        assertAll(
                () -> assertEquals(translator.getYear("Star Wars was released 1979."), "1979"),
                () -> assertEquals(translator.getYear("There is no good music after the year 2000"), "2000"),
                () -> assertEquals(translator.getYear("2010; the best year of my life"), "2010"),
                () -> assertEquals(translator.getYear("I was born the year 1899."), ""),
                () -> assertEquals(translator.getYear("What will the world of 2100 be like?"), "")
        );
    }

    /**
     * Should capture genres proceeded by "a", "an", "the", or followed by "from", "released",
     * "starring", "featuring", "with", "that". Should also ignore the false positives "movie" and
     * "film", and the adjectives and adverbs "good", "popular", "most", "best", "highest".
     */
    @Test
    void getGenre() {
        assertAll(
                () -> assertEquals(translator.getGenre("I want to see a horror movie!"), "horror"),
                () -> assertEquals(translator.getGenre("What was the best drama from this year?"), "drama"),
                () -> assertEquals(translator.getGenre("Give me an action movie starring Tom Cruise?"), "action"),
                () -> assertEquals(translator.getGenre("What was the best documentary released in 2001?"), "documentary"),
                () -> assertEquals(translator.getGenre("I wanna see the science fiction movie that made the most money!"), "science fiction"),
                () -> assertEquals(translator.getGenre("A comedy that doesn't suck please."), "comedy"),
                () -> assertEquals(translator.getGenre("I wanna see a movie that doesn't suck for once."), ""),
                () -> assertEquals(translator.getGenre("Can you recommend a good movie?"), "")
        );
    }
}