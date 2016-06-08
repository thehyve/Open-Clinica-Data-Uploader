package nl.thehyve.ocdu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OcduApplication {

    public static void main(String[] args) {
        SpringApplication.run(OcduApplication.class, args);
    }

    private static final Logger log = LoggerFactory.getLogger(OcduApplication.class);
    /*@Bean
    public CommandLineRunner testData(UploadSessionRepository repository, OCUserRepository usrRepository) {
        return (args) -> {
            log.info("Loading autonomous module...");
            ApplicationContext context =
                    new ClassPathXmlApplicationContext(new String[] {"autonomous.xml"});
            log.info("Generating test data ...");
            OcUser bogusUser= new OcUser();
            bogusUser.setUsername("bogao");
            bogusUser.setOcEnvironment("http://ocdu-openclinica-dev.thehyve.net/OpenClinica-ws");
            usrRepository.save(bogusUser);
            repository.save(new UploadSession("session1", UploadSession.Step.MAPPING, new Date(),
                    bogusUser));
            repository.save(new UploadSession("session2", UploadSession.Step.EVENTS, new Date(),
                    bogusUser));
            repository.save(new UploadSession("session3", UploadSession.Step.MAPPING, new Date(),
                    bogusUser));
            repository.save(new UploadSession("session4", UploadSession.Step.SUBJECTS, new Date(),
                    bogusUser));
            repository.save(new UploadSession("session5", UploadSession.Step.OVERVIEW, new Date(),
                    bogusUser));
            repository.save(new UploadSession("session6", UploadSession.Step.MAPPING, new Date(),
                    bogusUser));

        };
    }*/

}
