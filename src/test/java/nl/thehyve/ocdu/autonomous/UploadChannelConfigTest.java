package nl.thehyve.ocdu.autonomous;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Jacob Rousseau on 21-Jul-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public class UploadChannelConfigTest {

    private UploadChannelConfig uploadChannelConfig;

    @Test
    public void testFindUploadChannel() throws Exception {
        Path filePath = Paths.get("/home/tomcat/upload/12345_Data.txt");
        UploadChannelConfig.UploadChannel uploadChannel = uploadChannelConfig.findUploadChannel(filePath);
        Assert.assertEquals(null, uploadChannel);

        filePath = Paths.get("/home/tomcat/upload/123_Data.txt");
        uploadChannel = uploadChannelConfig.findUploadChannel(filePath);
        Assert.assertEquals(null, uploadChannel);

        filePath = Paths.get("/home/tomcat/upload/_Data.txt");
        uploadChannel = uploadChannelConfig.findUploadChannel(filePath);
        Assert.assertEquals(null, uploadChannel);

        filePath = Paths.get("/home/tomcat/upload/Data.txt");
        uploadChannel = uploadChannelConfig.findUploadChannel(filePath);
        Assert.assertEquals(null, uploadChannel);

        filePath = Paths.get("/home/tomcat/upload/STABC_Data.txt");
        uploadChannel = uploadChannelConfig.findUploadChannel(filePath);
        Assert.assertEquals("SessionABC", uploadChannel.getUploadSession());
    }

    @Before
    public void setUp() throws Exception {
        uploadChannelConfig = UploadChannelConfigLoader.load("docs/exampleFiles/autonomous-upload-channels-test.yml");
    }
}
