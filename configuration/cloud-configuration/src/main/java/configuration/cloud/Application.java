package configuration.cloud;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {

    @Value("${NAME:World}")
    private String name;

    // <1>
    @Bean
    DataSource postgres(
            @Value("${vcap.services.pivotal-ms-pgsql.credentials.uri}") String envUrl // <2>
    ) throws Exception { // <3>
        URI uri = URI.create(envUrl);
        String host = uri.getHost();
        int port = uri.getPort();
        String db = uri.getPath();
        String user = uri.getUserInfo().split(":")[0];
        String pw = uri.getUserInfo().split(":")[1];
        String url = String.format("jdbc:postgresql://%s:%s%s", host, port, db);
        return new SimpleDriverDataSource(org.postgresql.Driver.class.newInstance(), url, user, pw); // <4>
    }

    @Bean
    JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    CommandLineRunner logTheDatabase(JdbcTemplate jdbcTemplate) { // <5>
        return args -> {
            jdbcTemplate.query("select * from RESERVATION order by FIRST_NAME",
                    (RowCallbackHandler) (rs) -> System.out.println(rs.getString("FIRST_NAME") + ' ' + rs.getString("LAST_NAME")));
            System.out.println("Hello " + name + "!");  // <6>
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
