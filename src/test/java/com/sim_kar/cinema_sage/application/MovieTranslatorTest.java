package com.sim_kar.cinema_sage.application;

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
                () -> assertEquals(translator.getNameFromRequest("Hello my name is Harry Elmo."), "Harry Elmo"),
                () -> assertEquals(translator.getNameFromRequest("Jean-Claude Van Damme is a triple name"), "Jean-Claude Van Damme"),
                () -> assertEquals(translator.getNameFromRequest("Some names have apostrophes, like Elsa O'Connolly"), "Elsa O'Connolly"),
                () -> assertEquals(translator.getNameFromRequest("Names like Frances DuBois are also possible."), "Frances DuBois"),
                () -> assertEquals(translator.getNameFromRequest("Single names like Bob are a no-go."), ""),
                () -> assertEquals(translator.getNameFromRequest("Tom Cruise's best movie is Mission Impossible"), "Tom Cruise")
        );
    }

    /**
     * Should only capture years between 1900-2099; other dates should not be captured.
     */
    @Test
    void getYear() {
        assertAll(
                () -> assertEquals(translator.getYearFromRequest("Star Wars was released 1979."), "1979"),
                () -> assertEquals(translator.getYearFromRequest("There is no good music after the year 2000"), "2000"),
                () -> assertEquals(translator.getYearFromRequest("2010; the best year of my life"), "2010"),
                () -> assertEquals(translator.getYearFromRequest("I was born the year 1899."), ""),
                () -> assertEquals(translator.getYearFromRequest("What will the world of 2100 be like?"), "")
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
                () -> assertEquals(translator.getGenreFromRequest("I want to see a horror movie!"), "horror"),
                () -> assertEquals(translator.getGenreFromRequest("What was the best drama from this year?"), "drama"),
                () -> assertEquals(translator.getGenreFromRequest("Give me an action movie starring Tom Cruise?"), "action"),
                () -> assertEquals(translator.getGenreFromRequest("What was the best documentary released in 2001?"), "documentary"),
                () -> assertEquals(translator.getGenreFromRequest("I wanna see the science fiction movie that made the most money!"), "science fiction"),
                () -> assertEquals(translator.getGenreFromRequest("A comedy that doesn't suck please."), "comedy"),
                () -> assertEquals(translator.getGenreFromRequest("I wanna see a movie that doesn't suck for once."), ""),
                () -> assertEquals(translator.getGenreFromRequest("Can you recommend a good movie?"), "")
        );
    }

    /**
     * Should capture the title string in the stringified movie JSON response.
     */
    @Test
    void getTitle() {
        String input = """
                       {"page":1,"results":[{"adult":false,"backdrop_path":
                       "/uT5G4fA7jKxlJNfwYPMm353f5AI.jpg","genre_ids":[28,12,16,35,10751],
                       "id":140300,"original_language":"en","original_title":
                       "Kung Fu Panda 3","overview":"Continuing his \\"legendary adventures of 
                       awesomeness\\", Po must face two hugely epic, but different threats: one 
                       supernatural and the other a little closer to his home.",
                       "popularity":76.498,"poster_path":"/oajNi4Su39WAByHI6EONu8G8HYn.jpg",
                       "release_date":"2016-01-23","title":"Kung Fu Panda 3","video":false,
                       "vote_average":6.9,"vote_count":5070}],"total_pages":1,"total_results":1}
                       """;

        assertEquals(translator.getTitleFromResponse(input), "Kung Fu Panda 3");
    }
}