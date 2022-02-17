package AlkalmazasArchitektura.activitytracker;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActivityDaoTest {
    ActivityDao activityDao;
    @BeforeEach
    void init(){
        DBSource dbSource= new DBSource("activitytracker","activitytracker","activitytracker");
        MariaDbDataSource dataSource= dbSource.getDataSource();
        activityDao=new ActivityDao(dataSource);

        Flyway flyway = Flyway.configure().locations("db/migrations/activitytracker").dataSource(dataSource).load();
        flyway.clean();
        flyway.migrate();

        Activity avtivity1=new Activity(LocalDateTime.of(2022, 2, 13, 15, 56),"Jó kis testmozgás",ActivityType.HIKING);
        Activity avtivity2=new Activity(LocalDateTime.of(2022, 2, 14, 16, 57),"Tüskésrét",ActivityType.RUNNING);
        Activity avtivity3=new Activity(LocalDateTime.of(2022, 2, 15, 17, 5),"Környéken",ActivityType.BIKING);
        Activity avtivity4=new Activity(LocalDateTime.of(2022, 2, 16, 14, 0),"Zsolnay-negyed",ActivityType.BASKETBALL);

        activityDao.saveActivity(avtivity1);
        activityDao.saveActivity(avtivity2);
        activityDao.saveActivity(avtivity3);
        activityDao.saveActivity(avtivity4);
    }

   @Test
    void findActivityByIdTest(){
        Activity selectedActivity=activityDao.findActivityById(2);
        assertEquals(LocalDateTime.of(2022, 2, 14, 16, 57), selectedActivity.getStartTime());
        assertEquals("Tüskésrét", selectedActivity.getDesc());
        assertEquals(ActivityType.RUNNING, selectedActivity.getType());
    }

    @Test
    void listActivitiesTest(){
        List<Activity> selectedActivities=activityDao.listActivities();
        assertEquals(4, selectedActivities.size());

        Activity selectedActivity=selectedActivities.get(1);
        assertEquals(LocalDateTime.of(2022, 2, 15, 17, 5), selectedActivity.getStartTime());
        assertEquals("Környéken", selectedActivity.getDesc());
        assertEquals(ActivityType.BIKING, selectedActivity.getType());
    }
}