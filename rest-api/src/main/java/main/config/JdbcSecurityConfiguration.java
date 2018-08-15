package main.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableGlobalAuthentication
public class JdbcSecurityConfiguration extends org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter{

	private static final String USERS_QUERY = "SELECT username, password, enabled FROM table(STORED_PROC_PACKAGE.get_user(?))";

	private static final String AUTHORITIES_QUERY = "SELECT username, role_name from table(STORED_PROC_PACKAGE.get_user_roles(?))";

	Logger log = LoggerFactory.getLogger(JdbcSecurityConfiguration.class);

	@Autowired
	DataSource dataSource;
	
	@Autowired
	NamedParameterJdbcTemplate jdbcTemplate;
		
    @Bean
    public UserDetailsService userDetailsService(){
        JdbcDaoImpl jdbcDao = new JdbcDaoImpl();
        jdbcDao.setDataSource(dataSource);
        jdbcDao.setUsersByUsernameQuery(USERS_QUERY);
        jdbcDao.setAuthoritiesByUsernameQuery(AUTHORITIES_QUERY);
        return jdbcDao;
    }
	

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
	    DaoAuthenticationProvider authProvider
	      = new DaoAuthenticationProvider();
	    authProvider.setUserDetailsService(userDetailsService());
	    authProvider.setPasswordEncoder(passwordEncoder());
	    return authProvider;
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		return bCryptPasswordEncoder;
	}
	
}
