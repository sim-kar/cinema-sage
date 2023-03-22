# Project
## How to use
### API key:
This project uses the TMDB API which requires an API key. This key must be placed in a file 
named `apiKey` inside the project's `src/main/resources` directory.

### Making queries:
It is possible to ask the chatbot for movie recommendations of a certain genre, from a certain 
year of release, or featuring a certain person (e.g. actor or director), or any combination of 
the three. There are some limitations associated with each argument.

- Names must be capitalized. Names with hyphens or apostrophes, such as "Gordon-Levitt" or 
  "O'Brien", will work. As will names that are longer than two, such as "Jean-Claude Van Damme."
- Any year between 1900 and 2099 is acceptable.
- Only genres that are listed in the API will work. These are: action, adventure, animation, 
  comedy, crime, documentary, drama, family, fantasy, history, horror, music, mystery, romance, 
  science fiction, thriller, war, and western. Asking for a "war film" or "horror movie" will 
  work, of course.
- Avoid using adjectives and adverbs in requests (e.g. "Give me an awesome action movie"), as they 
  might be interpreted as a genre by the bot. The request will still work, but the genre might be 
  ignored.

### Example queries:

- **Genre:** Find me a horror movie to watch
- **Person:** I wanna see a Jean-Claude Van Damme movie
- **Year:** Can you recommend a movie from 1984?
- **Genre and year:** Give me a comedy from 2001
- **Genre and person:** Recommend a thriller by Steven Spielberg
- **Person and year:** Is there anything with Tom Cruise from 2022?
- **Genre, person and year:** Help me find a science fiction movie from 2017 with Harrison Ford

## Environment & Tools
Windows 10, IntelliJ IDEA 2021.3.3 (Ultimate Edition), git version 2.29.2.windows.1, OpenJDK 17, 
RxJava3 3.1.6

## Purpose
The goal is to build a chatbot that leverages a REST API to provide movie recommendations to 
users. The chatbot will accept natural language input from the user in the form of queries 
containing genre, release year, and/or individuals associated with the movie (e.g., actors, 
directors, etc.), and will respond with a relevant movie suggestion from the API.

To achieve this, the chatbot should utilize regex (regular expressions) to parse the user's 
input, as well as the data returned from the API. Specifically, the chatbot will incorporate 
regex features such as alternation, quantifiers, character classes, lookarounds, and grouping.

The chatbot should also use functional programming in the form of the Java Streams API, and 
incorporate functional programming concepts, including pure functions, lambda expressions, 
higher-order functions, currying, and composition. The code should avoid side effects and states 
as much as possible, and keep imperative code to a minimum.

Moreover, the chatbot should also use reactive programming in the form of RxJava, including 
basic operations such as throttle and take, and advanced operations such as flatMap and merge.

The code should well-organized and documented, and make considerations for readability and 
maintainability. Additionally, it should handle errors gracefully, and provide an intuitive and 
user-friendly interface.

## Procedures
The chatbot was organized in a three-layered architecture: a bottom layer that communicates with 
the API, a middle layer contains the application logic, and a top layer provides the user 
interface for receiving input and displaying output. It was implemented from the bottom-up, 
starting with the client that uses an HTTP client to sends requests to the API.

The client holds an observable of an HTTP client, which is subscribed on the `io.()` scheduler, 
since sending HTTP requests to the API is a blocking operation. This makes the chatbot scalable, 
since the Client can handle multiple requests in parallel, meaning one client can service many 
users at the same time. The client also uses the `.filter()` operation to filter the responses 
from unsuccessful requests, and `retry()` to recover from any errors and retry a request three 
times. Other than immutable fields that are initialized when it is created, the Client is 
stateless. The public `sendRequest()` method doesn't have any side effects, but since it 
sends an HTTP GET request the output cannot be guaranteed to be identical every time. For 
example, the API might be offline for some reason, resulting in a failed request.
Instead of making requests directly from the Client, a repository was implemented, which
provides an interface of the requests that are possible to make, and abstracts away the need for
constructing the URL strings for those requests.

Next to be implemented was the application layer, which contains a service and a translator that 
handle the business logic. The translator takes natural language user requests from the chatbot 
and parses them for parameters. The service uses these parameters to make requests to the 
repository. The service also abstracts away some intricacies of making requests from the 
translator. When using the API to search for movies, it is not possible to use a genre or person 
directly, but rather their ID must be retrieved from the API first. The service handles this 
when making requests. To get the ID number associated with a genre, a regex with positive 
lookahead is used:

```java
public class MovieService implements Service {
    
    // ...

    private Observable<String> getGenreID(String genre, Observable<String> genres) {
      return getMatch(
              Pattern.compile("\\d+(?=,\"name\":\"" + genre.toLowerCase() + "\"})"),
              // match case; genres are capitalized in JSON returned by API
              genres.map(String::toLowerCase)
      );
    }
}
```

The regex pattern to get the ID of a person uses both positive lookahead and positive lookbehind 
to only get the ID of the person, and ignore other IDs that are also included in the response 
from the API.

```java
public class MovieService implements Service {
    
    // ...

    private Observable<String> getPersonID(Observable<String> person) {
      return getMatch(
              // positive lookahead to only include the "id" before "known_for", since that is a list
              // of movies that include their own ids which will also match the pattern otherwise
              Pattern.compile("(?<=\"id\":)\\d+(?=.+\"known_for\")"),
              person
      );
    }
}
```

The following method is used by the service to get a movie:

```java
public class MovieService implements Service {
    
    // ...
  
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
}
```

`makeFilter()` is a higher-order function that returns the function `makeFilter` - a curry 
function that builds a URL query parameter string with all the parameters to be used in an API 
request.

```java
public class MovieService implements Service {
    
    // ...
    
    private final Function<String,
                    Function<String,
                        Function<String, String>>> makeFilter =
            genre -> person -> year -> String.format(
                    "?with_genres=%s&with_people=%s&primary_release_year=%s",
                    genre,
                    person,
                    year
            );
}
```

A higher-order function that returns `makeFilter` was necessary since referencing `makeFilter` 
directly in the stream would return a new instance every time, which prevented the currying from 
being applied. Since `getGenreID()` and `getPersonID()` need to make requests to the API, they 
both return Observables. Therefore, `zipWith()` is used to apply their emissions to the curry 
function. The year however doesn't need an API call, so it is just a String. A lambda function 
is used to apply it to the curry function, which returns the final string of query parameters. 
Lastly, since `Repository#getMovie()` returns an Observable, `flatMap()` was used to flatten the 
stream.

The translator was implemented next. It uses regex to extract the genre, year and/or person from 
the user's request, and the title of the movie in the response from the API. The pattern to find 
a year uses alternation to only look for years beginning with 19 or 20, and a quantifier to look 
for two more digits: `(19|20)\\d{2}`. This will find any year between 1900-2099. The pattern to 
find names uses character classes to match capitalized names that can contain hyphens and 
apostrophes, and grouping to match optional extra names allowing for complex names such as 
*Jean-Claude Van Damme* or *Frances O'Connor*: `([A-Z][A-Za-z'-]+ [A-Z][A-Za-z'-]+( [A-Z]
[A-Za-z'-]+)*)`. The regex to find a movie's title in a response uses positive lookbehind and 
lookahead, and a negative character class to get the entire title string enclosed by double 
quotes: `(?<="title":")[^"]+(?=")`. The task to find the genre was more complex, which is 
reflected in the regex:

```java
public class MovieTranslator implements Translator {
    
    //...

    String getGenreFromRequest(String request) {
      return getMatch(
              Pattern.compile(
                      // ignore some common adjectives/adverbs that would match otherwise
                      "(?!good|popular|most|best|highest)"
                      // science fiction is the only two-word genre; adding an optional extra word
                      // group to the expression generates too many false positives and makes the
                      // expression even longer and more convoluted, so using literal pattern instead
                      + "(science fiction"
                      // or look for something beginning with "a", "an", "the" that isn't "movie" or "film"
                      + "|(?<=\\ba |\\bA |\\ban |\\bAn |\\bthe |\\bThe )(?!movie|film)[a-z-]+"
                      // or look for something followed by "from", "released", "starring", etc. that
                      // isn't "movie" or "film"
                      + "|[a-z-]+(?<!movie|film)(?= from| released| starring| featuring| with| that| by))"
              ),
              request
      );
    }
}
```
The grammar and form of genres vary. For example, some act as adjectivally, such as "a horror 
movie", and some act as nouns, such as "a drama". Moreover, some genres are two words instead of 
one, e.g. "science fiction". Because of this, any regex that captures genres will also 
inadvertently capture adjectives or adverbs as well. Negative lookahead was used to ignore some 
of these false matches. In addition, the full regex uses grouping, alternation, character 
classes, positive lookbehind, negative lookbehind, and positive lookahead.

To avoid repetition in the regex methods, a helper method was implemented as a pure function, 
i.e. it doesn't rely on any outside states or produce any side effects, and it always returns 
the same output given the same input.

```java
public class MovieTranslator implements Translator {

  //...

  private String getMatch(String input, Pattern pattern) {
      return Stream.of(input)
              .map(pattern::matcher)
              .map(matcher -> (matcher.find()) ? matcher.group() : "")
              .findFirst()
              .orElse("");
    }
}
```

To translate the response from the API to natural language, functional composition was used to 
compose several single-responsibility functions:

```java
public class MovieTranslator implements  Translator {
    
    // ...
  
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
}
```

The `translateResponse()` method first uses a method with a regex (described above) to map the 
response to the movie's title, and then uses `generateResponse()` to map that to a generated 
natural language response containing the title, or - if the request failed to find a movie - a 
natural language response to that effect. To make the responses seem a bit less artificial, 
several possible responses were added, and one would be selected randomly. The method has no 
side effects, but the randomization of responses precludes it from being a pure function.

```java
public class MovieTranslator implements  Translator {
    
    // ...
  
    private Observable<String> translateResponse(Observable<String> response) {
      return response
              .map(this::getTitleFromResponse)
              .map(this::generateResponse);
    }

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
}
```

The class `Chatbot` in the presentation layer provides the interface for the user to interact with.
It takes the form of a chat conversation, where the bot queries the user for input, and uses 
that input to generate a response. It uses `Observable.fromCallable(() -> new Scanner(System.in).
nextLine())` with `.repeat()` to continually read user input. To protect against users spamming
requests to the client, it uses `.throttleFirst()` to only accept the first request every second.
This is also where the reactive streams in the application are subscribed to, so errors are 
handled here. The stream will first try to recover with `.retry(3)` before letting the user know 
that something has gone wrong.

At the root of the project, in Project, all the classes are initialized and dependencies are 
injected.

## Discussion
The chatbot successfully responds to user requests with movie recommendations. It will recommend 
movies based on combinations of genre, year of release, and/or a person, and will respond 
accordingly if it is unable to find anything that matches the given criteria. The chatbot is 
intuitive to use, and will provide the desired results as long as requests follow a reasonable 
format. But as mentioned in *Procedures*, the form and format of genres varies and the regex can 
match adjectives or adverbs that follow the same format. Using regex to parse natural language 
is not perfect. The simplest solution would be to keep a list of all possible genres to match 
against, but I believed this was against the spirit of the assignment.

As stated in the purpose, this project had many supplemental goals to fulfill. How they were 
achieved was detailed in the *Procedures*. The project includes regex using alternation, 
quantifiers, character classes, grouping and several different lookarounds. It includes uses of 
the Stream API with basic operations such as `map()`, `skip()`, `findFirst()`, `orElse()`. Many 
basic operations in the Stream API and RxJava work identically. For example, `filter()`, which 
is used `MovieClient#sendRequest()` to filter unsuccessful requests.

The project as a whole tries to adhere to Functional Programming principles. As demonstrated in 
*Procedures*, it uses functional composition, lambda expressions, higher-order functions and 
currying. States, assignments and imperative code has been avoided as much as possible. States 
and assignment are only used in fields for dependencies that are passed as parameters upon class 
initialization. Methods are implemented as pure functions to as great an extent as possible. The 
exceptions are when methods need to use a dependency, such as when `MovieService#findMovie()` 
uses the repository passed as a dependency to get a movie, or when the consistency of the output 
cannot be guaranteed. This happens when a call is made to the API, since outside factors can 
influence the response, or in `MovieTranslator#generateResponse()` that uses randomness to 
generate responses. But many methods, such as `MovieService#getGenreID()`, `MovieService#getMatch
()`, `MovieTranslator#getNameFromRequest`, and so on, are pure functions.

Some considerations that have been made for readability is to only have one operation per line; 
using short, easy to parse lambda functions; and using short, single-purpose methods with 
descriptive naming. Long and complex regex have also been broken up into smaller sections with 
explanations, since they can be difficult to parse.

The chatbot includes both basic and advanced RxJava operations, such as `throttleFirst()`, 
`repeat()`, `retry()`, `subscribeOn()`, `zipWith()` and `flatMap()`. Both `throttleFirst()` and 
`subscribeOn()` are used to improve the scalability of the project. `subscribeOn()` subscribes 
the client to the `io()` scheduler, so that each request is handled by its own thread. This way, 
several requests can be handled in parallel without blocking - meaning that the client can 
handle requests from many users at once. `throttleFirst()` is used in `Chatbot` to only accept 
the first request from a user every 1000 ms, in an effort to stop users from spamming the client 
making API requests. That way a single client can service more users at the same time.

Considerations have been made for maintainability as well. Components are isolated, both in 
terms of separation of concerns, but also in terms of being independent. Dependency injection is 
used to make it easier to make changes in one component without affecting another. This also 
improves testability, since dependencies can be mocked. Observables are also isolated from one 
another. The client for example can try to recover from a failure without affecting the rest of the 
system with `retry()`, leading to improved reliability.

The `Chatbot` also attempts to recover from errors with `retry()`, but if it is unable to do so, 
errors are handled in the `subscribe()` here, at the end of the Observable chain. Since the nature 
of the error is not relevant to the end user, the chatbot instead tells the user that something 
went wrong. If the system was put into practice, it would be a good idea to log errors here, or 
in the individual Observers they might appear, such as in the client. For example, the HTTP 
error code of failed API requests could be logged there.

---

This product uses the TMDB API but is not endorsed or certified by TMDB.
![TMDB logo](./src/main/resources/tmdb_logo.svg){height=180 width=180}
