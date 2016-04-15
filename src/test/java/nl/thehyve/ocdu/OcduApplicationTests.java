package nl.thehyve.ocdu;

import nl.thehyve.ocdu.soap.ResponseHandlers.ListStudiesResponseHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omg.CORBA.portable.InputStream;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.File;
import java.io.FileInputStream;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OcduApplication.class)
@WebAppConfiguration
public class OcduApplicationTests {

	@Test
	public void contextLoads() {
	}


	@Test
	public void responseHandlerSimpleCase() throws Exception {
		MessageFactory messageFactory = MessageFactory.newInstance();

		FileInputStream is = new FileInputStream(new File("docs/responseExamples/listStudiesResponse.xml"));

		SOAPMessage mockedResponse = messageFactory.createMessage(null, is);
		ListStudiesResponseHandler.parseListStudiesResponse(mockedResponse);
		assert true;
	}
	//TODO: We should put our Unit Tests here. Later if we have time we can also add Hamcrest Integration tests.

}
