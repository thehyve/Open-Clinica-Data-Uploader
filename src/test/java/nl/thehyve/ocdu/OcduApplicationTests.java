package nl.thehyve.ocdu;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OcduApplication.class)
@WebAppConfiguration
public class OcduApplicationTests {


    @Test
    public void contextLoads() {
    }

    @Autowired
    OCEnvironmentsConfig ocEnvironmentsConfig;

    @Test
    public void envPropsTest() throws Exception {
        List<OCEnvironmentsConfig.OCEnvironment> ocEnvironments = ocEnvironmentsConfig.getOcEnvironments();
        assertEquals(true, ocEnvironments.size() > 0);
        OCEnvironmentsConfig.OCEnvironment ocEnvironment = ocEnvironments.get(0);
        assertEquals(false, ocEnvironment.getName().isEmpty());
    }
}
