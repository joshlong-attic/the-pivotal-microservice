package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.net.URI;
import java.util.Collection;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@RestController
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
    DataSource prod(@Value("${vcap.services.pivotal-ms-pgsql.credentials.uri}") String envUrl) throws Exception {
        URI uri = URI.create(envUrl);
        String host = uri.getHost();
        int port = uri.getPort();
        String db = uri.getPath();
        String userAndPw = uri.getUserInfo();
        String user = userAndPw.split(":")[0], pw = userAndPw.split(":")[1];
        String url = String.format("jdbc:postgresql://%s:%s%s", host, port, db);
        return new SimpleDriverDataSource(
                org.postgresql.Driver.class.newInstance(), url, user, pw);
    }

    @Bean
    JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

class Reservation {

    private final String firstName, lastName;

    @Override
    public String toString() {
        return "Reservation{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    public Reservation(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {

        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}

@RestController
class ReservationsRestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/reservations")
    Collection<Reservation> reservations() {
        return this.jdbcTemplate.query("select * from RESERVATION",
                (rs, i) -> new Reservation(rs.getString("FIRST_NAME"), rs.getString("LAST_NAME")));
    }

}