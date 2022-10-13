package todo;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ResourceFinder {
	private static DataSource dataSource = null;
	public static Connection getConnection() throws SQLException,NamingException{
		if(dataSource==null) {
			InitialContext context = new InitialContext();
			dataSource = (DataSource)context.lookup("java:comp/env/linkpcksec");
		}
		return (dataSource.getConnection());
	}

}
