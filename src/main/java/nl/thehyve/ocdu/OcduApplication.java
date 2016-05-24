package nl.thehyve.ocdu;

import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.repositories.UploadSessionRepository;
import nl.thehyve.ocdu.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Date;

@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
public class OcduApplication {

    public static void main(String[] args) {
        SpringApplication.run(OcduApplication.class, args);
    }

    private static final Logger log = LoggerFactory.getLogger(OcduApplication.class);

    @Bean
    public CommandLineRunner testData(UploadSessionRepository repository, UserRepository usrRepository) {
        return (args) -> {
            log.info("Loading autonomous module...");
            ApplicationContext context =
                    new ClassPathXmlApplicationContext(new String[] {"autonomous.xml"});
            log.info("Generating test data ...");
            OcUser bogusUser= new OcUser();
            bogusUser.setUsername("Example user");
            bogusUser.setOcEnvironment("http://ocdu-openclinica-dev.thehyve.net/OpenClinica-ws");
            usrRepository.save(bogusUser);
            repository.save(new UploadSession("session1", UploadSession.Step.MAPPING, new Date(),
                    bogusUser));
        };
    }

}
