# Cinema Sage
A chatbot that provides movie recommendations using the [TMDB API](https://developer.themoviedb.org/docs).


## Table of Contents
- [General Info](#general-information)
- [Built with](#built-with)
- [Key Features](#key-features)
- [Setup](#setup)
- [Usage](#usage)
- [Acknowledgements](#acknowledgements)
- [Contact](#contact)


## General Information

- Cinema Sage is a chatbot that leverages a REST API to provide movie recommendations to users. 
  It processes natural language requests and responds with a relevant movie suggestion from the 
  API. 
- REGEX (regular expressions) are used to parse input, as well as the data returned from the API.
  Specifically, regex features such as alternation, quantifiers, character classes, lookarounds, 
  and grouping are used. 
- The project is built with functional and reactive programming in the form of the Java Streams 
  API and RxJava. It incorporates functional programming concepts, including pure functions, 
  lambda expressions, higher-order functions, currying, and composition. In addition, reactive 
  programming is used to handle requests asynchronously in a parallel and non-blocking manner 
  that is both reliable and scalable. The implementation prioritizes statelessness and minimizes
  side effects.
- The project uses a 3-layered architecture. The topmost presentation layer consists of a CLI 
  (command line interface)—in other words the chatbot that the end-user interacts with. The 
  second layer handles the application logic, which parses and formats requests from the user as 
  well as responses from the API. Finally, the data layer handles the communication with the API.
  The architecture is designed to ensure unidirectional interaction between each layer and the 
  layer directly beneath it. Moreover, dependency injection is used to keep components decoupled, 
  promote separation of concerns, and improve testability.
- The main purpose of the project was to practice and improve my understanding of functional and 
  reactive programming concepts.


## Built with
- Java Stream API (JDK8+)
- Maven - version 3.6.3
- RxJava - version 3.1.6
- JUnit5 - version 5.7.0
- Mockito - version 5.1.1


## Key Features
- Parses natural language requests and responds in natural language.
- Provides movie recommendations based on provided genre, year or person. Or any combination 
  thereof.
- Retrieves up-to-date movie data from a REST API.
- Can handle multiple requests in parallel without blocking.


## Setup
Requires Maven (the earliest tested version is 3.6.3) and JDK17+. This project also uses the
[TMDB API](https://developer.themoviedb.org/docs) which requires an API key. This key must be 
placed in a file named `apiKey` inside the project's `src/main/resources` directory.

- Clone the project from GitHub. 
- Navigate to the directory containing the project. 
- Run the command `mvn package` from the command line and wait for it to finish. 
- Navigate to the target directory: `cd target`. 
- Run the command `java -jar CinemaSage-1.0-SNAPSHOT.jar` to run the application.

## Usage

### Making queries:
You can ask the chatbot for movie recommendations of a certain genre, from a certain year of 
release, or featuring a certain person (e.g. actor or director), or any combination of
the three.

Some things to keep in mind:

- Names must be capitalized. Names with hyphens or apostrophes, such as "Gordon-Levitt" or
  "O'Brien", will work. As will names that are longer than two, such as "Jean-Claude Van Damme."
- Any year between 1900 and 2099 is acceptable.
- Only genres that are listed in the API will work. These are action, adventure, animation,
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


## Acknowledgements
This product uses the TMDB API but is not endorsed or certified by TMDB.

<img src="./src/main/resources/tmdb_logo.svg" alt="TMDB logo" height="180" width="180">


## Contact
Created by [Simon Karlsson](mailto:a.simon.karlsson@gmail.com) - feel free to contact me!
