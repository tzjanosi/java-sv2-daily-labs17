package MoviesActors;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MoviesRepository {
    private DataSource dataSource;

    public MoviesRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private long executeAndGetGeneratedKey(PreparedStatement stmt) {
        try (
                ResultSet rs = stmt.getGeneratedKeys();
        ) {
            if (rs.next()) {
                return rs.getLong(1);
            } else {
                throw new SQLException("No key has generated");
            }
        } catch (SQLException sqle) {
            throw new IllegalArgumentException("Error by insert", sqle);
        }
    }
    public Movie saveMovie(Movie movieToSave){
        Optional<Movie> foundMovie=findMovieByNameAndReleaseDate(movieToSave);
        if(foundMovie.isPresent()){
            return foundMovie.get();
        }
        try(
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO movies (title,release_date) VALUES(?,?);",Statement.RETURN_GENERATED_KEYS);
        ){
            String title= movieToSave.getTitle();
            LocalDate releaseDate= movieToSave.getReleaseDate();
            Date releaseDateAsDate=Date.valueOf(releaseDate);
            stmt.setString(1, title);
            stmt.setDate(2, releaseDateAsDate);
            stmt.executeUpdate();
            Movie movieWithID=new Movie((int)executeAndGetGeneratedKey(stmt),title,releaseDate);
            return movieWithID;
        }
        catch (SQLException sqle) {
            throw new IllegalStateException("Can not insert into Movies", sqle);
        }
    }
    public Optional<Movie> findMovieByNameAndReleaseDate(Movie movieToSearch){
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT id FROM movies WHERE (title=? AND release_date=?)");
        ) {
            String title = movieToSearch.getTitle();
            LocalDate releaseDate = movieToSearch.getReleaseDate();
            Date releaseDateAsDate = Date.valueOf(releaseDate);
            stmt.setString(1, title);
            stmt.setDate(2, releaseDateAsDate);

            try (
                    ResultSet rs = stmt.executeQuery();
            ){
                if (rs.next()) {
                    int id=rs.getInt("id");
                    Movie movieWithID=new Movie(id,title,releaseDate);
                    return Optional.of(movieWithID);
                }
                return Optional.empty();
            }
        }
        catch (SQLException sqle) {
            throw new IllegalArgumentException("Error by select MovieByNameAndReleaseDate", sqle);
        }
    }

    public List<Movie> findAllMovies(){
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM movies ORDER BY id DESC");
        ) {
            try (
                    ResultSet rs = stmt.executeQuery();
            ) {
                List<Movie> movies=new ArrayList<>();
                Movie movie;
                while (rs.next()) {
                    int id=rs.getInt("id");
                    String title = rs.getString("title");
                    LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
                    movie=new Movie(id,title,releaseDate);
                    movies.add(movie);
                }
                if(movies.size()>0){
                    return movies;
                }
                throw new IllegalArgumentException("No result");
            }
        } catch (SQLException sqle) {
            throw new IllegalArgumentException("Error by select", sqle);
        }
    }
}
