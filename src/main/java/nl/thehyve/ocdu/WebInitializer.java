package nl.thehyve.ocdu;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

/**
 * Necessary for the application to run out of the box on default Tomcat 7 configuration.
 *
 * Created by piotrzakrzewski on 12/04/16.
 */
public class WebInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(OcduApplication.class);
    }
}
