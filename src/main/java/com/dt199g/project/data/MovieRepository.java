package com.dt199g.project.data;

import io.reactivex.rxjava3.core.Flowable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Used to get movie data from The Movie Database API. Needs an HTTP client that is set up to
 * communicate with the API.
 *
 * @author Simon Karlsson
 */
public class MovieRepository implements Repository {
    Client client;

    /**
     * Initializes a new MovieRepository. Needs an HTTP client that is set up to communicate with
     * The Movie Database API.
     *
     * @param client an HTTP client that communicates with The Movie Database API.
     */
    public MovieRepository(Client client) {
        this.client = client;
    }

    /**
     * {@inheritDoc}
     *
     * @param name the name of the person
     * @return the person's data as stringified JSON; empty if request was unsuccessful
     */
    public Flowable<String> getPerson(String name) {
        // query value should be URL encoded
        return client.sendRequest(
                "/search/person?query=" + URLEncoder.encode(name, StandardCharsets.UTF_8)
        );
    }

    /**
     * {@inheritDoc}
     *
     * @return a list of genres as stringified JSON; empty if request was unsuccessful
     */
    public Flowable<String> getGenres() {
        return client.sendRequest("/genre/movie/list");
    }

    /**
     * {@inheritDoc}
     * The filter must start with '?'.
     *
     * @param filter the filter to use to find a movie
     * @return the movie's data as stringified JSON; empty if request was unsuccessful
     */
    public Flowable<String> getMovie(String filter) {
        return client.sendRequest("/discover/movie" + filter);
    }

    /**
     * {@inheritDoc}
     * The filter must start with '?', and sortBy with '&'.
     *
     * @param filter the filter to use to find a movie
     * @param sortBy the criteria to sort movies by
     * @return the top ranking movie according to the sorting method that the filter applies to as
     *         stringified JSON; empty if request was unsuccessful
     */
    public Flowable<String> getMovie(String filter, String sortBy) {
        // for example '&sort_by=release_date.desc'
        return client.sendRequest("/discover/movie" + filter + sortBy);
    }
}
