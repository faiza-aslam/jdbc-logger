package com.examples.logging;

import java.beans.PropertyVetoException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class EventLoggingHandler extends Handler {

	private String driverClassName;
	private String jdbcUrl;
	private String username;
	private String password;
	private String insertStatement;

	private static boolean ready = false;
	private DatabaseManager dbManager = null;
	
    private void configure() {

    	LogManager manager = LogManager.getLogManager();
        String cname = getClass().getName();
        
        setDriverClassName(manager.getProperty(cname+".driverClassName"));
        setJdbcUrl(manager.getProperty(cname+".jdbcUrl"));
        setUsername(manager.getProperty(cname+".username"));
        setPassword(manager.getProperty(cname+".password"));
        setInsertStatement(manager.getProperty(cname+".insertStatement"));
    }
	
	public EventLoggingHandler() {
		configure();
	}
	
	@Override
	public void publish(LogRecord record) {

		if (!isReady()) {
			return;
		}

		try {
			insertEvent(record);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isReady() {
		if (ready) {
			return true;
		}

		try {
			setupConnectionPool();
			ready = true;
		} catch (Exception e) {
			e.printStackTrace();
			ready = false;
		}
		
		return ready;
	}
	
	private void setupConnectionPool() throws PropertyVetoException {
		dbManager = new DatabaseManager(driverClassName, jdbcUrl, username, password);		
	}

	private void insertEvent(LogRecord logRecord) throws SQLException {

		QueryRunner qRunner = new QueryRunner(dbManager.getDataSource());
		qRunner.insert(insertStatement, 
						new ResultSetHandler<Object>() {
							public Object handle(ResultSet rs) throws SQLException {
								return null;
							}
						}, 
						getParams(logRecord));
	}

	private Object[] getParams(LogRecord logRecord) {
		return new Object[] {
				null, /* For primary key to auto generate */
				new Date(logRecord.getMillis()), /* created_timestamp */
				logRecord.getLevel().toString(), /* log_level */
				logRecord.getParameters()[0], 	/* event_type */
				logRecord.getParameters()[1], 	/* principal_name */
				logRecord.getMessage(), 		/* event_detail */
				1 								/* is_active */
		};
	}

	@Override
	public void flush() {
	
	}

	@Override
	public void close() throws SecurityException {
		if(dbManager.getDataSource()!=null && dbManager.getDataSource() instanceof ComboPooledDataSource) {
			ComboPooledDataSource ds = (ComboPooledDataSource)dbManager.getDataSource();
			ds.close();
		}		
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public void setInsertStatement(String insertStatement) {
		this.insertStatement = insertStatement;
	}
}
