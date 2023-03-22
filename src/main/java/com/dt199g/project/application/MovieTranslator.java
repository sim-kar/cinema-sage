package com.dt199g.project.application;

import io.reactivex.rxjava3.core.Observable;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Takes a user request for a movie in string form and parses it for parameters to pass to the
 * supplied service to handle; will then take the response and translate it to natural text.
 *
 * @author Simon Karlsson
 */
public class MovieTranslator implements Translator {
    private final Service service;

    /**
     * Initialize a new MovieTranslator.
     *
     * @param service the service to use
     */
    public MovieTranslator(Service service) {
        this.service = service;
    }

    @Override
    public Observable<String> makeRequest(String request) {
        return translateResponse(
                service.findMovie(
                        getGenreFromRequest(request),
                        getNameFromRequest(request),
                        getYearFromRequest(request)
                )
        );
    }

    /**
     * Takes a stringified JSON response containing movie data and translates it to a natural
     * language response.
     *
     * @param response movie data as Stringified JSON
     * @return a natural language response
     */
    private Observable<String> translateResponse(Observable<String> response) {
        return response
                .map(this::getTitleFromResponse)
                .map(this::generateResponse);
    }

    /**
     * Generate a natural language response with the given title. If the title is empty, it will
     * return a random response stating that it couldn't find anything; otherwise it will return
     * a random response with the given title as a recommendation.
     *
     * @param title the title of a movie
     * @return a natural language response recommending the given title;
     *         or a response that it couldn't find anything if the given title is empty
     */
    private String generateResponse(String title) {
        return title.isEmpty()
                ? Stream.of(List.of(
                                "Sorry, I wasn't able to find a movie like that.",
                                "I don't think such a movie exists, I'm afraid.",
                                "I couldn't find anything matching that description. Sorry!",
                                "I wasn't able to find the movie you are looking for."
                ))
                .map(l -> l.get(new Random().nextInt(l.size())))
                .findFirst()
                .orElse("")
                : Stream.of(List.of(
                                "%s is the movie you're looking for!",
                                "How about %s?",
                                "In that case, I would recommend %s.",
                                "I suggest %s."
                ))
                .map(l -> l.get(new Random().nextInt(l.size())))
                .map(response -> String.format(response, title))
                .findFirst()
                .orElse("");
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
    String getNameFromRequest(String request) {
        return getMatch(
                Pattern.compile(
                        // must start with uppercase and be at least two names,
                        // but allows for names like Wolcott-Scott, DeBeer, O'Connor
                        "([A-Z][A-Za-z'-]+ [A-Z][A-Za-z'-]+"
                        // optional extra group(s) to allow names longer than two,
                        // such as Jean-Claude Van Damme
                        + "( [A-Z][A-Za-z'-]+)*)"),
                request
        )
                // remove trailing "'s", otherwise you get "John Woo's" instead of "John Woo"
                .replaceAll("'s$", "");
    }

    /**
     * Find and return a year from a request. The year must be 4 digits and between 1900 and 2099.
     *
     * @param request the request to parse a year from
     * @return a year; or an empty string if no name was found
     */
    String getYearFromRequest(String request) {
        return getMatch(
                // must be a year from the 19th or 20th century
                Pattern.compile("(19|20)\\d{2}"),
                request
        );
    }

    /**
     * Find and return a genre from a request. The genre must be all lowercase letters, or hyphens,
     * such as "action" or "science-fiction". Can produce false positives, particularly adjectives.
     * For example, the request "Recommend an awesome movie!" will interpret "awesome" as the genre.
     *
     * @param request the request to parse a genre from
     * @return a genre; or an empty string if no genre was found
     */
    String getGenreFromRequest(String request) {
        return getMatch(
                Pattern.compile(
                        // ignore some common adjectives/adverbs that would match otherwise
                        "(?!good|popular|most|best|highest)"
                        // science fiction is the only two-word genre; adding an optional extra word
                        // group to the expression generates too many false positives and makes the
                        // expression even longer and more convoluted, so using literal pattern instead
                        + "(science fiction|"
                        // look for something beginning with "a", "an", "the" that isn't "movie" or "film"
                        + "(?<=\\ba |\\bA |\\ban |\\bAn |\\bthe |\\bThe )(?!movie|film)[a-z-]+"
                        // or look for something followed by "from", "released", "starring", etc. that
                        // isn't "movie" or "film"
                        + "|[a-z-]+(?<!movie|film)(?= from| released| starring| featuring| with| that| by))"
                ),
                request
        );
    }

    /**
     * Find and return the title from a response. Returns the first title if there are several
     * movies in the response.
     *
     * @param movie the response containing movie data as stringified JSON
     * @return the movie's title
     */
    String getTitleFromResponse(String movie) {
        return getMatch(
                // captures the string (word characters and whitespaces) between '"title:"' and '"'
                Pattern.compile("(?<=\"title\":\")[^\"]+(?=\")"),
                movie
        );
    }

    /**
     * Looks for the pattern in the input and returns the first match. Returns an empty string
     * if no match is found.
     *
     * @param input the string to parse
     * @param pattern the pattern to look for
     * @return the matching pattern; or an empty string if no match was found
     */
    private String getMatch(Pattern pattern, String input) {
        return Stream.of(input)
                .map(pattern::matcher)
                .map(matcher -> (matcher.find()) ? matcher.group() : "")
                .findFirst()
                .orElse("");
    }
}
