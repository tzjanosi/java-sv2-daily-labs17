package EgyszeruJDBCAdatmodositas.activitytracker;

import org.flywaydb.core.Flyway;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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
    public void saveActivity(MariaDbDataSource dataSource, Activity activity) {
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
        activityTracker.saveActivity(dataSource,avtivity1);
        activityTracker.saveActivity(dataSource,avtivity2);
        activityTracker.saveActivity(dataSource,avtivity3);
        activityTracker.saveActivity(dataSource,avtivity4);
    }
}
