package day01;

import GeneraltAzonositoLekerdezese.activitytracker.Activity;
import GeneraltAzonositoLekerdezese.activitytracker.ActivityType;
import org.mariadb.jdbc.MariaDbDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;

public class ActorsRepository {
    private DataSource dataSource;

    public ActorsRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveActor(){
        try(
                Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement()){
            stmt.executeUpdate("INSERT INTO actors (actor_name) VALUES('Fehér Virág');");
        }
        catch (SQLException sqle) {
            throw new IllegalStateException("Can not insert into actors", sqle);
        }
    }
    public Actor findActorById(long id){
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT actor_name FROM actors WHERE id = ?");
        ) {
            stmt.setLong(1, id);
            try (
                    ResultSet rs = stmt.executeQuery();
            ) {
                if (rs.next()) {
                    String name = rs.getString("actor_name");
                    Actor actor=new Actor((int)id,name);
                    return actor;
                }
                throw new IllegalArgumentException("No result");
            } catch (SQLException sqle) {
                throw new IllegalArgumentException("Error by select", sqle);
            }
        } catch (SQLException sqle) {
            throw new IllegalArgumentException("Error by select", sqle);
        }
    }

}
