package com.dt199g.project.application;

import io.reactivex.rxjava3.subjects.PublishSubject;

/**
 * Used to service requests. The method getMovies is used to receive responses.
 *
 * @author Simon Karlsson
 */
public interface Service {

    /**
     * Try to find a movie with the given parameters. Use {@link Service#getMovies()} to receive
     * result.
     *
     * @param genre the genre of the movie
     * @param name the name of a person in the movie
     * @param year the year the movie was released
     */
    void findMovie(String genre, String name, String year);

    /**
     * Returns a PublishSubject that emits results from
     * {@link Service#findMovie(String, String, String)}.
     *
     * @return a PublishSubject that emits results from requests
     */
    PublishSubject<String> getMovies();
}
