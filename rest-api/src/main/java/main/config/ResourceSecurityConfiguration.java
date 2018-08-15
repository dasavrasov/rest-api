package main.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * Базовая аутентификация
 * @author savrasov
 *
 */
@Configuration
//@EnableAuthorizationServer
//@EnableResourceServer
public class ResourceSecurityConfiguration extends WebSecurityConfigurerAdapter{
	
	@Autowired
	RESTAuthenticationEntryPoint authenticationEntryPoint;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests().antMatchers("/test").permitAll()
			.antMatchers("/","/swagger-resources").permitAll()
//			.access("hasRole('USER')")
			.antMatchers("/testDATABASE").access("hasRole('ADMIN') or hasRole('USER')")
			.antMatchers("/example/**").access("hasRole('USER')")
			.antMatchers("/monitor/**").access("hasRole('ADMIN')")
//			.and().formLogin()
		   .and()
		   .httpBasic()
		   .and()		   
		   .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
		   .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);//We don't need sessions to be created.;	
					
	}
}
