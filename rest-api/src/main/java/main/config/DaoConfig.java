package main.config;

import java.util.Locale;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
public class DaoConfig {

	@Value("${jdbc.oracle.url}")
	private String url;
	
	@Value("${jdbc.oracle.driver}")
	private String driver;
	
	@Value("${jdbc.oracle.username}")
	private String user;
	
	@Value("${jdbc.oracle.password}")
	private String pass;	
	
	@Bean
	public DataSource dataSource() {
		Locale.setDefault(Locale.ENGLISH);
		BasicDataSource ds = new CustomBasicDataSource();
		ds.setDriverClassName(driver);
		ds.setUrl(url);
		ds.setUsername(user);
		ds.setPassword(pass);
		ds.setInitialSize(1);
		ds.setMaxIdle(1);		
		return ds;
	}

	@Bean
	public NamedParameterJdbcTemplate getJdbcTemplate() {
		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource());
		return jdbcTemplate;
	}
	
} 