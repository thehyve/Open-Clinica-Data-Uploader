package nl.thehyve.ocdu;

import nl.thehyve.ocdu.models.MetaDataTree;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcTreePath;
import nl.thehyve.ocdu.soap.ResponseHandlers.GetStudyMetadataResponseHandler;
import org.junit.Before;
import org.junit.Test;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import static nl.thehyve.ocdu.services.DataService.buildTree;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by piotrzakrzewski on 21/06/16.
 */
public class TreeTests {

    private MetaData metaData;

    @Before
    public void setUp() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        File testFile = new File("docs/responseExamples/getStudyMetadata2.xml"); //TODO: Replace File with Path
        FileInputStream in = new FileInputStream(testFile);

        SOAPMessage mockedResponseGetMetadata = messageFactory.createMessage(null, in);
        this.metaData = GetStudyMetadataResponseHandler.parseGetStudyMetadataResponse(mockedResponseGetMetadata);
    }

    @Test
    public void treeFiltering() throws Exception {
        MetaDataTree metaDataTree = buildTree(metaData);
        OcTreePath selection = new OcTreePath();
        selection.setEvent("Non-repeating event");
        selection.setCrf("SuperSimpleCRF");
        selection.setVersion("0.1");
        MetaDataTree result = OcTreePath.filter(metaDataTree, selection);
        assertThat(result, is(notNullValue()));
        assertThat(result, hasProperty("children"));
        assertThat(result, hasProperty("name", is("0.1")));
        List<MetaDataTree> children = result.getChildren();
        assertThat(children, hasSize(7));
    }
}
