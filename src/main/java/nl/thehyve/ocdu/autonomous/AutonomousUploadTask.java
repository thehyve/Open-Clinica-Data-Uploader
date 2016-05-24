package nl.thehyve.ocdu.autonomous;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

/**
 * Controller to coordinate the autonomous upload process
 *
 * Created by jacob on 5/11/16.
 */
@Component
public class AutonomousUploadTask {

    private static final Logger log = LoggerFactory.getLogger(AutonomousUploadTask.class);

    private FileCopyService fileCopyService;

    public void run() {
        log.info("Running autonomousUploadTask...");
        try {
            fileCopyService.start();
            List<Path> filePathList = fileCopyService.obtainWorkPathList();
            for (Path filePath : filePathList) {
                // how do you obtain the associated mapping file from the input files?
                fileCopyService.successfulFile(filePath);
            }
            fileCopyService.stop();
        }
        catch (Exception e) {
            log.error("Failed to complete run: " + e.getMessage());
            return;
        }
        log.info("Finished running autonomousUploadTask");
    }

    public void setFileCopyService(FileCopyService fileCopyService) {
        this.fileCopyService = fileCopyService;
    }
}
