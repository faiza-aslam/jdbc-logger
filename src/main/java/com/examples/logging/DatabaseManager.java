package com.examples.logging;

import java.beans.PropertyVetoException;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public final class DatabaseManager {

	private DataSource dataSource = null;
	
	public DatabaseManager(String driverClassName, String jdbcUrl, String username, String password) throws PropertyVetoException {
		loadDataSource(driverClassName, jdbcUrl, username, password);
	}

	private void loadDataSource(String driverClassName, String jdbcUrl, String username, String password) throws PropertyVetoException {
		
		ComboPooledDataSource cpds = new ComboPooledDataSource();
		cpds.setDriverClass(driverClassName);
		cpds.setJdbcUrl(jdbcUrl);
		cpds.setUser(username);
		cpds.setPassword(password);

		// Optional Settings
		cpds.setInitialPoolSize(20);
		cpds.setMinPoolSize(10);
		cpds.setMaxPoolSize(50);
		/*cpds.setAcquireIncrement(5);
		cpds.setMaxConnectionAge(100);*/
		cpds.setCheckoutTimeout(1000);
		cpds.setMaxStatements(500);
		
		/**
		 * https://itellity.wordpress.com/2013/07/18/mysql-reconnect-issues-or-the-last-packet-successfully-received-from-the-server-xx-milliseconds-ago-errors/
		 */
		cpds.setTestConnectionOnCheckin(false);
		cpds.setTestConnectionOnCheckout(true);
		cpds.setMaxIdleTime(1800);
		cpds.setIdleConnectionTestPeriod(1000);
		
		this.dataSource = cpds;
	}
	
	public DataSource getDataSource() {
		return this.dataSource;
	}
	
}
