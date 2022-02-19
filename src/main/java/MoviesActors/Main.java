package MoviesActors;

import GeneraltAzonositoLekerdezese.activitytracker.DBSource;
import org.flywaydb.core.Flyway;
import org.mariadb.jdbc.MariaDbDataSource;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {


        GeneraltAzonositoLekerdezese.activitytracker.DBSource dbSource= new DBSource("movies-actors","root","MariaDB1984");
        MariaDbDataSource dataSource= dbSource.getDataSource();


        Flyway flyway = Flyway.configure().locations("db/migrations/movies_actors").dataSource(dataSource).load();
        flyway.clean();
        flyway.migrate();

        ActorsRepository actorsRepository=new ActorsRepository(dataSource);
        actorsRepository.saveActor(new Actor("Kis Virág"));
        actorsRepository.saveActor(new Actor("Christian Slater"));
        actorsRepository.saveActor(new Actor("Gary Oldman"));
        actorsRepository.saveActor(new Actor("Andorai Péter"));
//        System.out.println(actorsRepository.findActorById(2));
        MoviesRepository moviesRepository=new MoviesRepository(dataSource);
        moviesRepository.saveMovie(new Movie("Anyámék a havon", LocalDate.of(2022, 2, 15)));
        moviesRepository.saveMovie(new Movie("Pad a parkban", LocalDate.of(2001, 1, 3)));
//        System.out.println(moviesRepository.findAllMovies());
        ActorsMoviesService actorsMoviesService=new ActorsMoviesService(actorsRepository, moviesRepository, new ActorsMoviesRepository(dataSource));
        actorsMoviesService.insertMovieWithActors("Pad a parkban", LocalDate.of(2001, 1, 3), List.of("Christian Slater", "Andorai Péter", "Cserhalmi György"));
        actorsMoviesService.insertMovieWithActors("Anyámék a havon", LocalDate.of(2022, 2, 15), List.of("Kis Virág", "Cserhalmi György", "Gary Oldman"));
        actorsMoviesService.insertMovieWithActors("Mielőtt befejezi röptét a denevér", LocalDate.of(1989, 9, 1), List.of("Csontos Róbert", "Bodnár Erika", "Máté Gábor"));
        actorsMoviesService.insertMovieWithActors("Anyámék a havon", LocalDate.of(2022, 2, 15), List.of("Christian Slater", "Cserhalmi György", "Gary Oldman"));

        MoviesRatingService moviesRatingService=new MoviesRatingService(moviesRepository,new RatingRepository(dataSource));
        moviesRatingService.addRating(new Movie("Pad a parkban", LocalDate.of(2001, 1, 3)), 1,2,3,2,5);
        moviesRatingService.addRating(new Movie("Mielőtt befejezi röptét a denevér", LocalDate.of(1989, 9, 1)), 5,5);
        moviesRatingService.addRating(new Movie("Anyámék a havon", LocalDate.of(2022, 2, 15)), 1,1,5);
        moviesRatingService.addRating(new Movie("Mielőtt befejezi röptét a denevér", LocalDate.of(1989, 9, 1)), 4);
    }
}
