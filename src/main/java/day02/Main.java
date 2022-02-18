package day02;

import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        String url="jdbc:mariadb://localhost:3306/movies-actors?useUnicode=true";
        String username="root";
        String password="MariaDB1984";
        MariaDbDataSource dataSource;
        try{
            dataSource = new MariaDbDataSource();
            dataSource.setUrl(url);
            dataSource.setUser(username);
            dataSource.setPassword(password);
        }
        catch (SQLException sqle) {
            throw new IllegalStateException("Can not create data source", sqle);
        }
        ActorsRepository actorsRepository=new ActorsRepository(dataSource);
        actorsRepository.saveActor(new Actor("Christian Slater"));
        System.out.println(actorsRepository.findActorById(2));
        MoviesRepository moviesRepository=new MoviesRepository(dataSource);
        moviesRepository.saveMovie(new Movie("Anyámék a havon", LocalDate.of(2022, 2, 15)));
        System.out.println(moviesRepository.findAllMovies());
    }
}
