package thales.spring.angular.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
public class WebApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
   
    @Bean
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }
    
    /**
     * User detail methode generate a list of user enable to connect
     * @return UserDetailsService
     */
    @Bean
    public UserDetailsService userDetailService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("admin").password("superman").roles("ADMIN").build());
        return manager;
    }
    
    /**
     * Digest entry point for authantication
     * @return DigestAuthenticationEntryPoint
     */
    public DigestAuthenticationEntryPoint digestEntryPoint() {
        DigestAuthenticationEntryPoint digestAuthenticationEntryPoint = new DigestAuthenticationEntryPoint();
        digestAuthenticationEntryPoint.setKey("Admin panel authentication");
        digestAuthenticationEntryPoint.setRealmName("Digest ForJo");
        return digestAuthenticationEntryPoint;
    }
    
    /**
     * Filter use to make link beetween entry point and userDetailService
     * @param d Digest entry point
     * @return DigestAuthenticationFilter
     */
    public DigestAuthenticationFilter digestAuthentificationFilter(DigestAuthenticationEntryPoint d) {
        DigestAuthenticationFilter filter = new DigestAuthenticationFilter();
        filter.setAuthenticationEntryPoint(d);
        filter.setUserDetailsService(userDetailService());
        return filter;
    }
    
    /**
     * Methode to add digest authentication for admin path
     * @param http
     * @throws Exception 
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        DigestAuthenticationEntryPoint d = digestEntryPoint();
        http.antMatcher("/api/admin").authorizeRequests().anyRequest().authenticated().and().exceptionHandling().authenticationEntryPoint(d).and().addFilter(digestAuthentificationFilter(d));
    }
    
    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        web.httpFirewall(allowUrlEncodedSemicolonHttpFirewall());
    }
    
    @Bean
    public HttpFirewall allowUrlEncodedSemicolonHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setUnsafeAllowAnyHttpMethod(true);
        firewall.setAllowUrlEncodedSlash(true);
        firewall.setAllowUrlEncodedPercent(true);
        firewall.setAllowSemicolon(true);
        return firewall;
    }
}
