package day04;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

public class ActorsRepository {
    private DataSource dataSource;

    public ActorsRepository(DataSource dataSource) {
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
    public Actor saveActor(Actor actorToSave){
        Optional<Actor> foundActor=findActorByName(actorToSave.getName());
        if(foundActor.isPresent()){
            return foundActor.get();
        }
        try(
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO actors (actor_name) VALUES(?);",Statement.RETURN_GENERATED_KEYS);
        ){
            String name= actorToSave.getName();
            stmt.setString(1, name);
            stmt.executeUpdate();
            Actor actorWithID=new Actor((int)executeAndGetGeneratedKey(stmt),name);
            return(actorWithID);
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
            }
        } catch (SQLException sqle) {
            throw new IllegalArgumentException("Error by select", sqle);
        }
    }
    public Optional<Actor> findActorByName(String name){
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM actors WHERE actor_name = ?");
        ) {
            stmt.setString(1, name);
            try (
                    ResultSet rs = stmt.executeQuery();
            ) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    Actor actor=new Actor(id,name);
                    return Optional.of(actor);
                }
                return Optional.empty();
            }
        } catch (SQLException sqle) {
            throw new IllegalArgumentException("Error by select", sqle);
        }
    }

}
