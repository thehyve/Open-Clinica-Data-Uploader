package nl.thehyve.ocdu.autonomous;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
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


    public FileCopyService() throws Exception {
        log.info("Starting FileCopyService");
        log.info("");
        log.info("Source directory: " + sourceDirectory);
        log.info("Failed files directory: " + failedFilesDirectory);
        log.info("Completed files directory: " + completedFilesDirectory);
        log.info("");
    }

    public void start() throws Exception {
        checkReadWriteAccess();
    }


    public List<Path> obtainWorkList() {
        // explicit naming of variables for clarity of the call to FileUtils.listFiles.
        boolean recursive = false;
        String[] extensionList = null;
        Collection<File> fileCollection = FileUtils.listFiles(new File(sourceDirectory), extensionList, recursive);
        ArrayList<Path> ret = new ArrayList<Path>();
        for (File file : fileCollection) {
            ret.add(file.toPath());
        }
        return ret;
    }

    public void stop() throws Exception {
    }

    public void failedFile(Path path) throws Exception {
        String newFileName = addTimeStamp(path, failedFilesDirectory);
        FileUtils.moveFile(path.toFile(), new File(newFileName));
        log.info("Failed file '" + path + "' moved to failed files directory");
    }

    public void successfulFile(Path path) throws Exception {
        String newFileName = addTimeStamp(path, completedFilesDirectory);
        FileUtils.moveFile(path.toFile(), new File(newFileName));
        log.info("Successfully completed file '" + path + "' moved to completed files directory");
    }

    private static String addTimeStamp(Path aPath, String aDestinationDirectory) {
        Path fileName = aPath.getFileName();
        String newFileName = DateFormatUtils.format(GregorianCalendar.getInstance(), "yyyyMMddHHmmssSSS") + "-" + fileName.toString();
        return aDestinationDirectory + File.separator + newFileName;
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
    }
}
