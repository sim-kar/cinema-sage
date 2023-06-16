package com.sim_kar.cinema_sage.presentation;

import com.sim_kar.cinema_sage.application.Translator;
import io.reactivex.rxjava3.core.Observable;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * A chatbot that gives movie recommendations. Asks for user input in the form of genre, year and/or
 * the name of cast or crew member, and responds with a movie that meets the given criteria. If no
 * such movie exists, it responds to that effect instead.
 *
 * @author Simon Karlsson
 */
public class Chatbot {
    private final Translator translator;

    /**
     * Initialize a new Chatbot.
     *
     * @param translator the translator to use
     */
    public Chatbot(Translator translator) {
        this.translator = translator;
    }

    /**
     * Start the chatbot; lets the user make requests and receive responses.
     */
    public void start() {
        System.out.println("""
                Hello! I am a chat bot that can help you find movies to watch.
                Give me a genre, a year or the name of a member of the cast or crew, and I will do my best to give you a recommendation.
                I am also contractually obligated to say that "This product uses the TMDB API but is not endorsed or certified by TMDB."

                How can I help you?""");

        Observable.fromCallable(() -> new Scanner(System.in).nextLine())
                // keep asking for user input; keeps the program alive
                .repeat()
                // only accept one request every second to avoid spamming the client
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .flatMap(translator::makeRequest)
                .retry(3)
                .subscribe(response -> {
                    System.out.println(response);
                    System.out.println("Can I help you find anything else?");
                }, error -> {
                    System.out.println("Whoops, it seems like something has gone wrong.");
                    System.out.println("Please try again later.");
                });
        // }
    }
}
