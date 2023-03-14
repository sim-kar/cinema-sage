package com.dt199g.project.application;

import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Takes a user request in string form and parses it for parameters to pass to the supplied service
 * to handle; will then take the response and translate it to natural text.
 *
 * @author Simon Karlsson
 */
public class MovieTranslator implements Translator {
    private final Service service;

    // must start with uppercase, but allows for names like Wolcott-Scott, DeBeer, O'Connor
    // must be at least two names, but can be more like Jean-Claude Van Damme
    private static final Pattern NAME =
            Pattern.compile("([A-Z][A-Za-z'-]+ [A-Z][A-Za-z'-]+( [A-Z][A-Za-z'-]+)*)");

    // must be a year from the 19th or 20th century
    private static final Pattern YEAR =
            Pattern.compile("(19|20)\\d{2}");

    private static final Pattern GENRE =
            Pattern.compile(
                    // ignore some common adjectives/adverbs that would match otherwise
                    "(?!good|popular|most|best|highest)"
                    // science fiction is the only two-word genre; adding an optional extra word
                    // group to the expression generates too many false positives and makes the
                    // expression even longer and more convoluted, so using literal pattern instead
                    + "(science fiction|"
                    // look for something beginning with "a", "an", "the" that isn't "movie" or "film"
                    + "(?<=\\ba |\\ban |\\bthe )(?!movie|film)[a-z-]+"
                    // or look for something followed by "from", "released", "starring", etc. that
                    // isn't "movie" or "film"
                    + "|[a-z-]+(?<!movie|film)(?= from| released| starring| featuring| with| that))"
            );

    /**
     * Initialize a new MovieTranslator.
     *
     * @param service the service to use
     */
    public MovieTranslator(Service service) {
        this.service = service;
    }

    @Override
    public void send(String request) {
        service.findMovie(
                getGenre(request),
                getName(request),
                getYear(request)
        );
    }

    /**
     * Find and return a name from a request. Names must be capitalized, and contain at least a
     * first and last name, but longer names will also be found as long as they are all capitalized.
     * Double names with hyphens (Mary-Jane), named with apostrophes (O'Brien), and names with more
     * than one capitalized letter (DuBois) all work.
     *
     * @param request the request to parse a name from
     * @return a name; or an empty string if no name was found
     */
    String getName(String request) {
        return getMatch(request, NAME)
                // remove trailing "'s", otherwise you get "John Woo's" instead of "John Woo"
                .replaceAll("'s$", "");
    }

    /**
     * Find and return a year from a request. The year must be 4 digits and between 1900 and 2099.
     *
     * @param request the request to parse a year from
     * @return a year; or an empty string if no name was found
     */
    String getYear(String request) {
        return getMatch(request, YEAR);
    }

    /**
     * Find and return a genre from a request. The genre must be all lowercase letters, or hyphens,
     * such as "action" or "science-fiction". Can produce false positives, particularly adjectives.
     * For example, the request "Recommend an awesome movie!" will interpret "awesome" as the genre.
     *
     * @param request the request to parse a genre from
     * @return a genre; or an empty string if no genre was found
     */
    String getGenre(String request) {
        return getMatch(request, GENRE);
    }

    /**
     * Looks for the pattern in the request and returns the first match. Returns an empty string
     * if no match is found.
     *
     * @param request the string to parse
     * @param pattern the pattern to look for
     * @return the matching pattern; or an empty string if no match was found
     */
    private String getMatch(String request, Pattern pattern) {
        return Stream.of(request)
                .map(pattern::matcher)
                .map(matcher -> (matcher.find()) ? matcher.group() : "")
                .findFirst()
                .orElse("");
    }
}
