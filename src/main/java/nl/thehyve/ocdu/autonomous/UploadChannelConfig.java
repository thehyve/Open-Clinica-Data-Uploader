package nl.thehyve.ocdu.autonomous;

import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.List;

/**
 * Defines channels for specific studies with which uploads are performed.
 * Created by Jacob Rousseau on 20-Jul-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public class UploadChannelConfig {

    private List<UploadChannel> uploadChannelList;

    public UploadChannelConfig() {
    }

    public UploadChannel findUploadChannel(Path filePath) {
        String fileName = filePath.toFile().getName();
        String filePrefix = StringUtils.substringBefore(fileName, "_");
        if (StringUtils.isEmpty(filePrefix) || (filePrefix.length() != 5)) {
            return null;
        }
        boolean isPresent =
            uploadChannelList.stream().filter(uploadChannel -> filePrefix.equals(uploadChannel.getStudyPrefix())).findAny().isPresent();
        if (! isPresent) {
            return null;
        }
        return uploadChannelList.stream().filter(uploadChannel -> filePrefix.equals(uploadChannel.getStudyPrefix())).findAny().get();
    }

    public List<UploadChannel> getUploadChannelList() {
        return uploadChannelList;
    }

    public void setUploadChannelList(List<UploadChannel> uploadChannelList) {
        this.uploadChannelList = uploadChannelList;
    }

    public static class UploadChannel {

        /**
         * The name of the channel.
         */
        private String name;

        /**
         * The upload session name
         */
        private String uploadSession;

        /**
         * the sha1 hash of the user password
         */
        private String ocPassword;

        /**
         * an email address to which the results of the autonomous upload must be sent
         */
        private String resultNotificationEmail;

        /**
         * A 5 character prefix to associate filenames with a study and upload session.
         */
        private String studyPrefix;


        public UploadChannel() {
        }

        public UploadChannel(String name, String uploadSession, String studyPrefix, String ocPassword, String ocEnvironmentName, String resultNotificationEmail) {
            this.name = name;
            this.uploadSession = uploadSession;
            // TODO move the ocPassword into a keystore and use the uploadSession as the key to retrieve it
            this.ocPassword = ocPassword;
            this.resultNotificationEmail = resultNotificationEmail;
            this.studyPrefix = studyPrefix;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUploadSession() {
            return uploadSession;
        }

        public void setUploadSession(String uploadSession) {
            this.uploadSession = uploadSession;
        }

        public String getOcPassword() {
            return ocPassword;
        }

        public void setOcPassword(String ocPassword) {
            this.ocPassword = ocPassword;
        }

        public String getResultNotificationEmail() {
            return resultNotificationEmail;
        }

        public void setResultNotificationEmail(String resultNotificationEmail) {
            this.resultNotificationEmail = resultNotificationEmail;
        }

        public String getStudyPrefix() {
            return studyPrefix;
        }

        public void setStudyPrefix(String studyPrefix) {
            this.studyPrefix = studyPrefix;
        }
    }
}
