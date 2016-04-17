package nl.thehyve.ocdu;

import nl.thehyve.ocdu.factories.ClinicalDataFactory;
import nl.thehyve.ocdu.models.ClinicalData;
import nl.thehyve.ocdu.models.Study;
import nl.thehyve.ocdu.services.FileService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Every.everyItem;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by piotrzakrzewski on 16/04/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OcduApplication.class)
@WebAppConfiguration
public class ClinicalDataFactoryTests {
    private static final Logger log = LoggerFactory.getLogger(ClinicalDataFactoryTests.class);


    private ClinicalDataFactory factory;
    private Path testFile;

    @Autowired
    FileService fileService;

    @Test
    public void depositionDataFileTest() throws Exception{
        List<String> errorMessages = fileService.depositDataFile(testFile, "user", "submission");
        assertEquals(0, errorMessages.size());
    }

    @Test
    public void testFilePathCorrect() throws Exception {
        assertEquals(true, Files.exists(testFile));
    }

    @Test
    public void clinicalDataFactoryTest1() {
        List<ClinicalData> clinicalData = factory.createClinicalData(testFile);
        assertThat(
                clinicalData,
                everyItem(is(allOf(notNullValue(), instanceOf(ClinicalData.class)))));
        assertEquals(6, clinicalData.size());
    }

    @Before
    public void setUp() throws Exception {
        this.factory = new ClinicalDataFactory("TEST_USER","TEST_SUBMISSION");
        this.testFile = Paths.get("docs/exampleFiles/data.txt");
    }
}
