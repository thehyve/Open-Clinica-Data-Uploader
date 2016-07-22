package nl.thehyve.ocdu.autonomous;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Unit test for {@link UploadChannelConfigLoader}
 * Created by Jacob Rousseau on 21-Jul-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public class UploadChannelConfigLoaderTest {

    @Test
    public void testLoadCorrectPath() throws Exception {
        UploadChannelConfig uploadChannelConfig =
                UploadChannelConfigLoader.load("docs/exampleFiles/autonomous-upload-channels-test.yml");
        Assert.assertEquals("STXYZ", uploadChannelConfig.getUploadChannelList().get(0).getStudyPrefix());
    }

    @Test(expected = IOException.class)
    public void testLoadInvalidPath() throws Exception {
        UploadChannelConfig uploadChannelConfig =
                UploadChannelConfigLoader.load("docs/exampleFiles/NON-EXISTENT.yml");
    }
}
