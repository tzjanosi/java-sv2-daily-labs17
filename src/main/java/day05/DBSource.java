package day05;

import org.mariadb.jdbc.MariaDbDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

public class DBSource {
    private MariaDbDataSource dataSource;

    public DBSource(String schema, String username, String password){
        String url="jdbc:mariadb://localhost:3306/"+schema+"?useUnicode=true";
        try{
            dataSource = new MariaDbDataSource();
            dataSource.setUrl(url);
            dataSource.setUser(username);
            dataSource.setPassword(password);
        }
        catch (SQLException sqle) {
            throw new IllegalStateException("Can not create data source", sqle);
        }
    }

    public MariaDbDataSource getDataSource() {
        return dataSource;
    }
}
