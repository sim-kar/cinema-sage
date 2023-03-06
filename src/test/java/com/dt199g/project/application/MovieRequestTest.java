package com.dt199g.project.application;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

class MovieRequestTest {
    static MovieRequest request;

    @BeforeAll
    static void setup() {
        Service service = mock(Service.class);
        MovieRequestTest.request = new MovieRequest(service);
    }

    /**
     * Names should be captured and non-names should not be captured. Should remove trailing "'s".
     */
    @Test
    void getName() {
        assertAll(
                () -> assertEquals(request.getName("Hello my name is Harry Elmo."), "Harry Elmo"),
                () -> assertEquals(request.getName("Jean-Claude Van Damme is a triple name"), "Jean-Claude Van Damme"),
                () -> assertEquals(request.getName("Some names have apostrophes, like Elsa O'Connolly"), "Elsa O'Connolly"),
                () -> assertEquals(request.getName("Names like Frances DuBois are also possible."), "Frances DuBois"),
                () -> assertEquals(request.getName("Single names like Bob are a no-go."), ""),
                () -> assertEquals(request.getName("Tom Cruise's best movie is Mission Impossible"), "Tom Cruise")
        );
    }

    /**
     * Should only capture years between 1900-2099; other dates should not be captured.
     */
    @Test
    void getYear() {
        assertAll(
                () -> assertEquals(request.getYear("Star Wars was released 1979."), "1979"),
                () -> assertEquals(request.getYear("There is no good music after the year 2000"), "2000"),
                () -> assertEquals(request.getYear("2010; the best year of my life"), "2010"),
                () -> assertEquals(request.getYear("I was born the year 1899."), ""),
                () -> assertEquals(request.getYear("What will the world of 2100 be like?"), "")
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
                () -> assertEquals(request.getGenre("I want to see a horror movie!"), "horror"),
                () -> assertEquals(request.getGenre("What was the best drama from this year?"), "drama"),
                () -> assertEquals(request.getGenre("Give me an action movie starring Tom Cruise?"), "action"),
                () -> assertEquals(request.getGenre("What was the best documentary released in 2001?"), "documentary"),
                () -> assertEquals(request.getGenre("I wanna see the science fiction movie that made the most money!"), "science fiction"),
                () -> assertEquals(request.getGenre("A comedy that doesn't suck please."), "comedy"),
                () -> assertEquals(request.getGenre("I wanna see a movie that doesn't suck for once."), ""),
                () -> assertEquals(request.getGenre("Can you recommend a good movie?"), "")
        );
    }
}