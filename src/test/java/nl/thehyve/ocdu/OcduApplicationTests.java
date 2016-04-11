package nl.thehyve.ocdu;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OcduApplication.class)
@WebAppConfiguration
public class OcduApplicationTests {

	@Test
	public void contextLoads() {
	}

	//TODO: We should put our Unit Tests here. Later if we have time we can also add Hamcrest Integration tests.

}
