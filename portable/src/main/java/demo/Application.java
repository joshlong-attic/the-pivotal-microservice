package demo;

import org.h2.Driver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.sql.ResultSet;

/**
 * TODO move this to Cloud Foundry! (no wifi onplane..)
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {

    @Bean
    @Profile({"dev", "default"})
    DataSource dev() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }

    // export SPRING_PROFILES_ACTIVE=prod then run this
    @Bean
    @Profile("prod")
    DataSource prod(@Value("${h2.prod.uri}") String url,
                    @Value("${h2.prod.user}") String user,
                    @Value("${h2.prod.password}") String pw) {
        SimpleDriverDataSource simpleDriverDataSource = new SimpleDriverDataSource();
        simpleDriverDataSource.setDriverClass(Driver.class);
        simpleDriverDataSource.setUrl(url);
        simpleDriverDataSource.setUsername(user);
        simpleDriverDataSource.setPassword(pw);
        return simpleDriverDataSource;
    }

    @Bean
    JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    CommandLineRunner test(JdbcTemplate jdbcTemplate) {
        return args ->
                jdbcTemplate.query("select * from RESERVATION",
                        (RowCallbackHandler) (ResultSet rs) ->
                                System.out.println(String.format("found reservation for %s, %s", rs.getString("LAST_NAME"), rs.getString("FIRST_NAME"))));

    }


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
