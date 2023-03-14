package com.dt199g.project.application;

import io.reactivex.rxjava3.subjects.PublishSubject;

/**
 * Used to respond to requests.
 *
 * @author Simon Karlsson
 */
public interface Response {
    /**
     * Returns a PublishSubject that emits responses to requests.
     *
     * @return a PublishSubject that emits responses to requests
     */
    PublishSubject<String> getResponses();
}
