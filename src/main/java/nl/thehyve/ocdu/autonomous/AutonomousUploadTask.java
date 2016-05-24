package nl.thehyve.ocdu.autonomous;

import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

/**
 * Controller to coordinate the autonomous upload process
 *
 * Created by jacob on 5/11/16.
 */
@Component
public class AutonomousUploadTask {

    private static final Logger log = LoggerFactory.getLogger(AutonomousUploadTask.class);


    public void run() {
        log.info("Running autonomousUploadTask.");

        log.info("Found XXX files");
    }
}
