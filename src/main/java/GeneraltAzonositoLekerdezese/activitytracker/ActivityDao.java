package GeneraltAzonositoLekerdezese.activitytracker;

import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ActivityDao {
    private MariaDbDataSource dataSource;

    public ActivityDao(MariaDbDataSource dataSource) {
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
    public Activity saveActivity(Activity activity){
        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO activities (start_time,activity_desc,activity_type) VALUES(?,?,?);",Statement.RETURN_GENERATED_KEYS);
        ){
            LocalDateTime localDateTime=activity.getStartTime();
            Timestamp dateTimeAsTimestamp=Timestamp.valueOf(localDateTime);
            String description= activity.getDesc();
            ActivityType activityType=activity.getType();
            String activityTypeAsString=activityType.name();

            stmt.setTimestamp(1, dateTimeAsTimestamp);
            stmt.setString(2, description);
            stmt.setString(3, activityTypeAsString);
            stmt.executeUpdate();
//            stmt.execute();
            Activity activityWithID=new Activity(activity);
            activityWithID.setId((int)executeAndGetGeneratedKey(stmt));
            return(activityWithID);
        }
        catch (SQLException sqle) {
            throw new IllegalStateException("Can not save data ", sqle);
        }
    }

    public Activity findActivityById(long id){
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT start_time, activity_desc, activity_type FROM activities WHERE id = ?");
        ) {
            stmt.setLong(1, id);
            try (
                    ResultSet rs = stmt.executeQuery();
            ) {
                if (rs.next()) {
                    LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();

                    String desc = rs.getString("activity_desc");
                    ActivityType type = ActivityType.valueOf(rs.getString("activity_type"));
                    Activity activity=new Activity((int)id,startTime,desc,type);
                    return activity;
                }
                throw new IllegalArgumentException("No result");
            } catch (SQLException sqle) {
                throw new IllegalArgumentException("Error by select", sqle);
            }
        } catch (SQLException sqle) {
            throw new IllegalArgumentException("Error by select", sqle);
        }
    }

    public List<Activity> listActivities(){
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM activities ORDER BY id DESC");
        ) {
            try (
                    ResultSet rs = stmt.executeQuery();
            ) {
                List<Activity> activities=new ArrayList<>();
                Activity activity;
                while (rs.next()) {
                    int id=rs.getInt("id");
                    LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
                    String desc = rs.getString("activity_desc");
                    ActivityType type = ActivityType.valueOf(rs.getString("activity_type"));
                    activity=new Activity(id,startTime,desc,type);
                    activities.add(activity);
                }
                if(activities.size()>0){
                    return activities;
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
