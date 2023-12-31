package com.sim_kar.cinema_sage.application;

import com.sim_kar.cinema_sage.data.Repository;
import io.reactivex.rxjava3.core.Observable;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Services requests to find movies.
 *
 * @author Simon Karlsson
 */
public class MovieService implements Service {
    private final Repository repository;

    /**
     * Initialize a new MovieService.
     *
     * @param repository a repository that van be used to get data on movies.
     */
    public MovieService(Repository repository) {
        this.repository = repository;
    }

    @Override
    public Observable<String> findMovie(String genre, String name, String year) {
        // use currying to get the filter string; order of parameters matter
        return Observable.just(makeFilter())
                .zipWith(
                        getGenreID(genre, repository.getGenres()),
                        Function::apply
                )
                .zipWith(
                        getPersonID(repository.getPerson(name)),
                        Function::apply
                )
                .map(f -> f.apply(year))
                // getMovie returns a Observable so use flatMap to flatten
                .flatMap(repository::getMovie);
    }

    /**
     * Get the ID of a genre. Returns an empty string if the given genre doesn't exist.
     *
     * @param genre the genre to get the ID of
     * @param genres an Observable of genres as stringified JSON
     * @return the genre's ID; or an empty string if the genre doesn't exist
     */
    private Observable<String> getGenreID(String genre, Observable<String> genres) {
        return getMatch(
                Pattern.compile("\\d+(?=,\"name\":\"" + genre.toLowerCase() + "\"})"),
                // match case; genres are capitalized in JSON returned by API
                genres.map(String::toLowerCase)
        );
    }

    /**
     * Get the ID of a person. Returns an empty string if the given person doesn't exist.
     *
     * @param person an Observable of the person's data as strigified JSON
     * @return the person's ID; or an empty string if the person doesn't exist
     */
    private Observable<String> getPersonID(Observable<String> person) {
        return getMatch(
                // positive lookahead to only include the "id" before "known_for", since that is a list
                // of movies that include their own ids which will also match the pattern otherwise
                Pattern.compile("(?<=\"id\":)\\d+(?=.+\"known_for\")"),
                person
        );
    }

    /**
     * Captures the first expression matching the pattern in the input's emissions, and returns
     * them. If an emission doesn't produce a match, an empty string is returned instead.
     *
     * @param pattern the pattern to look for
     * @param input an Observable of the strings to parse
     * @return an Observable with the matching patterns; or empty strings if no match was found
     */
    private Observable<String> getMatch(Pattern pattern, Observable<String> input) {
        return Observable.just(pattern)
                .zipWith(input, Pattern::matcher)
                .map(matcher -> matcher.find() ? matcher.group() : "");
    }

    /**
     * Higher-order function that returns curry function {@link MovieService#makeFilter}. Cannot use
     * the function directly in a stream since it will just use a new instance for each application;
     * using the function returned by this method will keep using the same instance returned by
     * currying.
     *
     * @return {@link MovieService#makeFilter} curry function
     */
    private Function<String,
                Function<String,
                    Function<String, String>>> makeFilter() {
        return makeFilter;
    }

    /**
     * Curry function to create a string of query parameters acting as a filter for finding a movie.
     * The order of parameters is important: genre, person, and year. Unused filter can be left
     * blank. Results from a filter generated by this function uses default sorting, which is by
     * descending popularity.
     */
    private final Function<String,
                    Function<String,
                        Function<String, String>>> makeFilter =
            genre -> person -> year -> String.format(
                    "?with_genres=%s&with_people=%s&primary_release_year=%s",
                    genre,
                    person,
                    year
            );

    /**
     * Curry function to create a string of query parameters acting as a filter for finding a movie.
     * The order of parameters is important: genre, person, year, and sorting. Unused filter can be
     * left blank. Results from a filter generated by this function uses default sorting, which is
     * by descending popularity.
     */
    private final Function<String,
                    Function<String,
                        Function<String,
                            Function<String, String>>>> makeSortedFilter =
            genre -> person -> year -> sorting -> String.format(
                    "?with_genres=%s&with_people=%s&primary_release_year=%s&sort_by=%s",
                    genre,
                    person,
                    year,
                    sorting
            );
}
