package nl.thehyve.ocdu;

import nl.thehyve.ocdu.factories.ClinicalDataFactory;
import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.models.errors.FileFormatError;
import nl.thehyve.ocdu.repositories.ClinicalDataRepository;
import nl.thehyve.ocdu.repositories.UploadSessionRepository;
import nl.thehyve.ocdu.repositories.OCUserRepository;
import nl.thehyve.ocdu.services.FileService;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

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

    @Autowired
    OCUserRepository OCUserRepository;

    @Autowired
    UploadSessionRepository uploadSessionRepository;

    @Autowired
    ClinicalDataRepository clinicalDataRepository;

    private OcUser testUser;
    private UploadSession testSubmission;

    @Test
    @Ignore("Requires OC connection ")
    public void depositionDataFileTest() throws Exception{
        //List<FileFormatError> errorMessages = fileService.depositDataFile(testFile, testUser, testSubmission);
        //assertEquals(0, errorMessages.size());
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
        assertEquals(14, clinicalData.size());
    }

    @Before
    public void setUp() throws Exception {
        this.testUser = new OcUser();
        this.testUser.setUsername("tester");
        OCUserRepository.save(testUser);
        this.testSubmission = new UploadSession("submission1", UploadSession.Step.MAPPING, new Date(), this.testUser);
        uploadSessionRepository.save(testSubmission);
        this.factory = new ClinicalDataFactory(testUser, testSubmission);
        this.testFile = Paths.get("docs/exampleFiles/data.txt");
    }

    @After
    public void tearDown() throws Exception {
        clinicalDataRepository.deleteAll();
        uploadSessionRepository.deleteAll();
        OCUserRepository.deleteAll();
    }
}
