package com.dt199g.project.application;

import com.dt199g.project.data.Repository;
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

    // positive lookahead to only include the "id" before "known_for", since that is a list
    // of movies that include their own ids which will also match the pattern otherwise
    private final static Pattern ID = Pattern.compile("(?<=\"id\":)\\d+(?=.+\"known_for\")");

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
                .zipWith(getGenreID(genre), Function::apply)
                .zipWith(getPersonID(name), Function::apply)
                .map(f -> f.apply(year))
                // getMovie returns a Observable so use flatMap to flatten
                .flatMap(repository::getMovie);
    }

    /**
     * Get a list of genres as stringified JSON.
     *
     * @return a list of genres as stringified JSON
     */
    Observable<String> getGenres() {
        return repository.getGenres();
    }

    /**
     * Get the ID of a genre. Returns an empty string if the given genre doesn't exist.
     *
     * @param genre the genre to get the ID of
     * @return the genre's ID; or an empty string if the genre doesn't exist
     */
    Observable<String> getGenreID(String genre) {
        // since it's a new pattern for each request we have to compile it every time
        Pattern pattern = Pattern.compile("\\d+(?=,\"name\":\"" + genre.toLowerCase() + "\"})");

        return getGenres()
                .map(String::toLowerCase) // match case; genres are capitalized in JSON
                .map(pattern::matcher)
                .map(matcher -> matcher.find() ? matcher.group() : "");
    }

    /**
     * Get the ID of a person. Returns an empty string if the given person doesn't exist.
     *
     * @param name the name of the person
     * @return the person's ID; or an empty string if the person doesn't exist
     */
    Observable<String> getPersonID(String name) {
        return repository.getPerson(name)
                .map(ID::matcher)
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
