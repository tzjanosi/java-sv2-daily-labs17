package EgyszeruJDBCLekerdezes.activitytracker;

import org.flywaydb.core.Flyway;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ActivityTrackerMain {
    public MariaDbDataSource createDBSource(String schema, String username, String password){
        String url="jdbc:mariadb://localhost:3306/"+schema+"?useUnicode=true";

        try{
            MariaDbDataSource dataSource = new MariaDbDataSource();
            dataSource.setUrl(url);
            dataSource.setUser(username);
            dataSource.setPassword(password);
            return dataSource;
        }
        catch (SQLException sqle) {
            throw new IllegalStateException("Can not create data source", sqle);
        }
    }
    public static void saveActivity(MariaDbDataSource dataSource, Activity activity) {
        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO activities (start_time,activity_desc,activity_type) VALUES(?,?,?);");
        ){
            LocalDateTime dateTime=activity.getStartTime();
            Timestamp dateTimeAsTimestamp=Timestamp.valueOf(dateTime);
            String description= activity.getDesc();
            ActivityType activityType=activity.getType();
            String activityTypeAsString=activityType.name();

            stmt.setTimestamp(1, dateTimeAsTimestamp);
            stmt.setString(2, description);
            stmt.setString(3, activityTypeAsString);
            stmt.execute();
        }
        catch (SQLException sqle) {
            throw new IllegalStateException("Can not save data ", sqle);
        }
    }
    public static Activity selectSingleActivity(MariaDbDataSource dataSource,int id) {
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
                    Activity activity=new Activity(id,startTime,desc,type);
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
    public static List<Activity> selectAllActivities(MariaDbDataSource dataSource) {
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
    public static void main(String[] args) {
        ActivityTrackerMain activityTracker=new ActivityTrackerMain();
        MariaDbDataSource dataSource=activityTracker.createDBSource("activitytracker","activitytracker","activitytracker");
//        Flyway flyway = Flyway.configure().dataSource(dataSource).load();
        Flyway flyway = Flyway.configure().locations("db/migrations/activitytracker").dataSource(dataSource).load();

        flyway.clean();
        flyway.migrate();

        Activity avtivity1=new Activity(LocalDateTime.of(2022, 2, 13, 15, 56),"Jó kis testmozgás",ActivityType.HIKING);
        Activity avtivity2=new Activity(LocalDateTime.of(2022, 2, 14, 16, 57),"Tüskésrét",ActivityType.RUNNING);
        Activity avtivity3=new Activity(LocalDateTime.of(2022, 2, 15, 17, 5),"Környéken",ActivityType.BIKING);
        Activity avtivity4=new Activity(LocalDateTime.of(2022, 2, 16, 14, 0),"Zsolnay-negyed",ActivityType.BASKETBALL);
        saveActivity(dataSource,avtivity1);
        saveActivity(dataSource,avtivity2);
        saveActivity(dataSource,avtivity3);
        saveActivity(dataSource,avtivity4);

        Activity selectedActivity=selectSingleActivity(dataSource,2);
        System.out.println(selectedActivity);

        List<Activity> selectedActivities=selectAllActivities(dataSource);
        System.out.println(selectedActivities);
    }
}
