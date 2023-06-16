package com.sim_kar.cinema_sage.application;

import io.reactivex.rxjava3.core.Observable;

/**
 * Used to service requests.
 *
 * @author Simon Karlsson
 */
public interface Service {

    /**
     * Try to find a movie with the given parameters.
     *
     * @param genre the genre of the movie
     * @param name the name of a person in the movie
     * @param year the year the movie was released
     */
    Observable<String> findMovie(String genre, String name, String year);
}
