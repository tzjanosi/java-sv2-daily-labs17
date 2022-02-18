package day05;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class RatingRepository {
    DataSource dataSource;

    public RatingRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertRating(Movie movie, int rating){
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO ratings (movie_id,rating) VALUES(?,?);");
        ) {
            stmt.setInt(1, movie.getId());
            stmt.setInt(2, rating);
            stmt.execute();
        } catch (SQLException sqle) {
            throw new IllegalStateException("Can not insert data into ratings!", sqle);
        }
    }
    public void insertRatings(Movie movie, List<Integer> ratings){
        try (Connection conn = dataSource.getConnection();) {
            conn.setAutoCommit(false);
            try (
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO ratings (movie_id,rating) VALUES(?,?);");
            ) {
                for (Integer rating:ratings){
                    if(rating<1||rating>5){
                        conn.rollback();
                        return;
                    }
                    stmt.setInt(1, movie.getId());
                    stmt.setInt(2, rating);
                    stmt.execute();
                }
                conn.commit();
            }
        }catch (SQLException sqle) {
            throw new IllegalStateException("Can not insert data into ratings!", sqle);
        }
    }
}
