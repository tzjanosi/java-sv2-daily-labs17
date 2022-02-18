package day04;

import java.time.LocalDate;
import java.util.List;

public class ActorsMoviesService {
    private ActorsRepository actorsRepository;
    private MoviesRepository moviesRepository;
    private ActorsMoviesRepository actorsMoviesRepository;

    public ActorsMoviesService(ActorsRepository actorsRepository, MoviesRepository moviesRepository, ActorsMoviesRepository actorsMoviesRepository) {
        this.actorsRepository = actorsRepository;
        this.moviesRepository = moviesRepository;
        this.actorsMoviesRepository = actorsMoviesRepository;
    }

    public void insertMovieWithActors(String title, LocalDate releaseDate, List<String> actorNames){
        Movie insertedMovie=moviesRepository.saveMovie(new Movie(title,releaseDate));
        Actor insertedActor;
        for(String actorName:actorNames){
            insertedActor=actorsRepository.saveActor(new Actor(actorName));
            actorsMoviesRepository.insertActorAndMovieID(insertedActor,insertedMovie);
        }
    }
}
