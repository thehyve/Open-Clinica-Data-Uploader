package nl.thehyve.ocdu.soap.SOAPRequestFactories;

import nl.thehyve.ocdu.models.OCEntities.Study;
import nl.thehyve.ocdu.models.OcDefinitions.SiteDefinition;
import org.apache.commons.lang3.StringUtils;
import org.openclinica.ws.beans.SiteRefType;
import org.openclinica.ws.beans.StudyRefType;

/**
 * Created by piotrzakrzewski on 17/06/16.
 */
public class StudyRefFactory {
    //TODO: make sure all siteRefs and siteRefs are created with methods from this class
    public static StudyRefType createStudyRef(Study study) {
        StudyRefType studyRefType = new StudyRefType();
        studyRefType.setIdentifier(study.getIdentifier());
        return studyRefType;
    }

    public static StudyRefType createStudyRef(Study study, SiteDefinition siteDefinition) {
        StudyRefType studyRefType = new StudyRefType();
        studyRefType.setIdentifier(study.getIdentifier());
        if (siteDefinition != null && !siteDefinition.equals("")) {
            SiteRefType siteRef = createSiteRef(siteDefinition);
            studyRefType.setSiteRef(siteRef);
        }
        return studyRefType;
    }

    private static SiteRefType createSiteRef(SiteDefinition siteDefinition) {
        SiteRefType siteRef = new SiteRefType();
        siteRef.setIdentifier(siteDefinition.getSiteOID());
        return siteRef;
    }

}
