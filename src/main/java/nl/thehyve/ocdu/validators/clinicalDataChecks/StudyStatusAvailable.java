package nl.thehyve.ocdu.validators.clinicalDataChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.StudyStatusError;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by piotrzakrzewski on 22/06/16.
 */
public class StudyStatusAvailable implements ClinicalDataCrossCheck {

    public static final String STUDY_STATUS_ALLOWING_UPLOAD = "available";

    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData, Map<ClinicalData, ItemDefinition> itemDefMap, List<StudySubjectWithEventsType> studySubjectWithEventsTypeList, Map<ClinicalData, Boolean> shownMap, Map<String, Set<CRFDefinition>> eventMap) {
        if (!metaData.getStatus().equals(STUDY_STATUS_ALLOWING_UPLOAD)) {
            ValidationErrorMessage error = new StudyStatusError();
            error.addOffendingValue("Study:" +metaData.getStudyName()+" has status: "+  metaData.getStatus());
            return error;
        }
        return null;
    }
}
