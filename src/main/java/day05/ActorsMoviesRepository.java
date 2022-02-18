package day05;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ActorsMoviesRepository {
    private DataSource dataSource;

    public ActorsMoviesRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private boolean alreadyInTheTable(Actor actor,Movie movie){
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT id FROM actors_movies WHERE actor_id = ? AND movie_id=?");
        ) {
            stmt.setInt(1, actor.getId());
            stmt.setInt(2, movie.getId());
            try (
                    ResultSet rs = stmt.executeQuery();
            ) {
                if (rs.next()) {
                    return true;
                }
                return false;
            }
        } catch (SQLException sqle) {
            throw new IllegalArgumentException("Error by select", sqle);
        }
    }

    public void insertActorAndMovieID(Actor actor,Movie movie){
        if(!alreadyInTheTable(actor,movie)) {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO actors_movies (actor_id,movie_id) VALUES(?,?);");
            ) {
                stmt.setInt(1, actor.getId());
                stmt.setInt(2, movie.getId());
                stmt.execute();
            } catch (SQLException sqle) {
                throw new IllegalStateException("Can not insert data into actors_movies!", sqle);
            }
        }
    }
}
