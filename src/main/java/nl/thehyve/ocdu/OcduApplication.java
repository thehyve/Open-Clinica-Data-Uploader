package nl.thehyve.ocdu;

import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.repositories.UploadSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
public class OcduApplication {

    public static void main(String[] args) {
        SpringApplication.run(OcduApplication.class, args);
    }

    private static final Logger log = LoggerFactory.getLogger(OcduApplication.class);

    @Bean
    public CommandLineRunner testData(UploadSessionRepository repository) {
        return (args) -> {
            log.info("Generating test data ...");
            repository.save(new UploadSession("Jack", UploadSession.Step.MAPPING, new Date()));

        };
    }

}
