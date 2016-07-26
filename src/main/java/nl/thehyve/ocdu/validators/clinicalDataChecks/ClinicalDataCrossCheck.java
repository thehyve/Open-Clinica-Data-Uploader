package nl.thehyve.ocdu.validators.clinicalDataChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * All ClinicalData validation logic which relies on OpenClinica metadata should be implemented in classes
 * implementing this interface. Each validation rule should have a separate class.
 * These checks are meant to be run by ClinicalDataChecksRunner, which makes sure are inputs are provided as
 * necessary.
 *
 * Created by piotrzakrzewski on 04/05/16.
 */
public interface ClinicalDataCrossCheck {

    ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData,
                                                 Map<ClinicalData, ItemDefinition> itemDefMap, List<StudySubjectWithEventsType>                                                         studySubjectWithEventsTypeList, Map<ClinicalData,
                                                        Boolean> shownMap,
                                                 Map<String, Set<CRFDefinition>> eventMap);
    // All the maps are present here for performance reasons - precomputing all the maps before running checks is much
    // more efficient.

}
