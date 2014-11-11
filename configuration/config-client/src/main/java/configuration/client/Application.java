package configuration.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@RestController
public class Application {

    @Bean
    @RefreshScope
    NameHolder nameHolder(@Value("${name:World}") String name) {
        return new NameHolder(name);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

class NameHolder {

    private String name;

    public NameHolder(String name) {
        this.name = name;
        System.out.println("creating a new Name Holder for the value '" + this.name + "'");
    }

    public String getName() {
        return name;
    }

}
@RestController
class NameRestController {

    @Autowired
    private NameHolder nameHolder;

    @RequestMapping("/hi")
    String hi() {
        return "Hello, " + this.nameHolder.getName() + "!";
    }
}
