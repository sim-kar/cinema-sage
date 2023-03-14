package com.dt199g.project.presentation;

import com.dt199g.project.application.Translator;
import io.reactivex.rxjava3.core.Observable;
import java.util.Scanner;

/**
 * A chatbot that gives movie recommendations. Asks for user input in the form of genre, year and/or
 * the name of cast or crew member, and responds with a movie that meets the given criteria. If no
 * such movie exists, it responds to that effect instead.
 *
 * @author Simon Karlsson
 */
public class Chatbot {
    private final Translator translator;
    Scanner in = new Scanner(System.in);

    /**
     * Initialize a new Chatbot.
     *
     * @param translator the translator to use
     */
    public Chatbot(Translator translator) {
        this.translator = translator;
    }

    /**
     * Get a new observable that emits dialogue from the chatbot and takes user input.
     *
     * @return the observable
     */
    public Observable<String> get() {
        return Observable.create(emitter -> {
            String input;

            emitter.onNext(
                """
                Hello! I am a chat bot that can help you find movies to watch.
                Give me a genre, a year or the name of a member of the cast or crew, and I will do my best to give you a recommendation.
                I am also contractually obligated to say that "This product uses the TMDB API but is not endorsed or certified by TMDB."
                
                How can I help you?"""
            );

            while (true) {
                input = in.nextLine();

                if (input.equalsIgnoreCase("no")) {
                    System.out.println("Thanks for chatting!");
                    break;
                }

                // need to wait for response or next prompt will be printed before it
                emitter.onNext(
                        translator.makeRequest(input)
                                .blockingFirst()
                );

                emitter.onNext("Can I help you find anything else?");
            }

            emitter.onComplete();
        });

    }
}
