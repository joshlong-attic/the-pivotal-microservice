package configuration.external;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {

    @Value("#{ systemEnvironment['PATH'].split( systemProperties['path.separator'] )}")
    private String[] pathFromSpeL; // <1>

    @Value("${path}")
    private String pathFromPropertyPlaceholders; // <2>

    @Value("${name:World}")
    private String name; // <3>

    @Autowired
    private Environment environment;

    @Bean
    CommandLineRunner begin() throws Throwable {
        return args -> {
            System.out.println(this.pathFromPropertyPlaceholders);
            System.out.println(StringUtils.arrayToDelimitedString(this.pathFromSpeL, " "));

            System.out.println(this.environment.getProperty("path")); // <4>

            System.out.println("Hello, " + this.name + '!');

        };
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
