package day02;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MoviesRepository {
    private DataSource dataSource;

    public MoviesRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveMovie(Movie movieToSave){
        try(
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO movies (title,release_date) VALUES(?,?);");
        ){
            String title= movieToSave.getTitle();
            LocalDate releaseDate= movieToSave.getReleaseDate();
            Date releaseDateAsDate=Date.valueOf(releaseDate);
            stmt.setString(1, title);
            stmt.setDate(2, releaseDateAsDate);
            stmt.executeUpdate();
        }
        catch (SQLException sqle) {
            throw new IllegalStateException("Can not insert into Movies", sqle);
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
