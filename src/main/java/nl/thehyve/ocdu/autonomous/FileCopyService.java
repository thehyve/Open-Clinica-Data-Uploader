package nl.thehyve.ocdu.autonomous;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class which is responsible for file copying for the autonomous upload
 * Created by jacob on 5/24/16.
 */
@Service
public class FileCopyService {

    private static final Logger log = LoggerFactory.getLogger(FileCopyService.class);

    private String sourceDirectory;

    private String failedFilesDirectory;

    private String completedFilesDirectory;

    private String workDirectory;

    public FileCopyService() throws Exception {
    }

    public void start() throws Exception {
        log.info("Starting FileCopyService");
        log.info("");
        log.info("Source directory: " + sourceDirectory);
        log.info("Failed files directory: " + failedFilesDirectory);
        log.info("Completed files directory: " + completedFilesDirectory);
        log.info("Work directory: " + workDirectory);
        log.info("");
        checkReadWriteAccess();
        FileUtils.copyDirectory(new File(sourceDirectory), new File(workDirectory));
        log.info("Copied files to work directory");
    }


    public List<Path> obtainWorkPathList() {
        // explicit naming of variables for clarity of the call to FileUtils.listFiles.
        boolean recursive = false;
        String[] extensionList = null;
        Collection<File> fileCollection = FileUtils.listFiles(new File(workDirectory), extensionList, recursive);
        ArrayList<Path> ret = new ArrayList<Path>();
        for (File file : fileCollection) {
            ret.add(file.toPath());
        }
        return ret;
    }

    public void stop() throws Exception {
        FileUtils.copyDirectory(new File(workDirectory), new File(failedFilesDirectory));
        log.info("Aborted and copyied all files to the failed files direcotry");
    }

    public void failedFile(Path path) throws Exception {
        Files.copy(path, new File(failedFilesDirectory).toPath(), StandardCopyOption.ATOMIC_MOVE);
        log.info("Failed file '" + path + "' moved to failed files directory");
    }

    public void successfulFile(Path path) throws Exception {
        Files.copy(path, new File(completedFilesDirectory).toPath(), StandardCopyOption.ATOMIC_MOVE);
        log.info("Sucessfully completed file '" + path + "' moved to completed files directory");
    }

    public void setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public void setFailedFilesDirectory(String failedFilesDirectory) {
        this.failedFilesDirectory = failedFilesDirectory;
    }

    public void setCompletedFilesDirectory(String completedFilesDirectory) {
        this.completedFilesDirectory = completedFilesDirectory;
    }

    public void setWorkDirectory(String workDirectory) {
        this.workDirectory = workDirectory;
    }


    private void checkReadWriteAccess() throws Exception {
        if (! Files.isReadable(new File(sourceDirectory).toPath())) {
            throw new IllegalStateException("Source directory '" + sourceDirectory + "' is not readable for application");
        }
        if (! Files.isWritable(new File(failedFilesDirectory).toPath())) {
            throw new IllegalStateException("Failed files directory '" + failedFilesDirectory + "' is not writable for application");
        }
        if (! Files.isWritable(new File(completedFilesDirectory).toPath())) {
            throw new IllegalStateException("Completed files directory '" + completedFilesDirectory + "' is not writable for application");
        }

        if (! Files.isReadable(new File(workDirectory).toPath())) {
            throw new IllegalStateException("Work directory '" + workDirectory + "' is not readable for application");
        }
        if (! Files.isWritable(new File(workDirectory).toPath())) {
            throw new IllegalStateException("Work directory '" + workDirectory + "' is not writable for application");
        }
    }
}
