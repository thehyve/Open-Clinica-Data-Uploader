package nl.thehyve.ocdu.validators.crossChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.CRFDoesNotExist;
import nl.thehyve.ocdu.models.errors.CrfCouldNotBeVerified;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 10/05/16.
 */
public class CrfCouldNotBeVerifiedCrossCheck implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData, List<StudySubjectWithEventsType> subjectWithEventsTypeList) {
        Map<String, Set<CRFDefinition>> eventMap = buildEventMap(metaData);
        List<ClinicalData> crfCouldNotBeVerifiedOffenders = getcrfCouldNotBeVerifiedOffenders(data, eventMap);
        if (crfCouldNotBeVerifiedOffenders.size() > 0) {
            CrfCouldNotBeVerified error = new CrfCouldNotBeVerified();
            List<String> offendingNames = new ArrayList<>();
            crfCouldNotBeVerifiedOffenders.stream().forEach(clinicalData -> {
                String crf = clinicalData.getCrfName();
                if (!offendingNames.contains(crf)) offendingNames.add(crf);
            });
            error.addAllOffendingValues(offendingNames);
            return error;
        } else return null;
    }

    private List<ClinicalData> getcrfCouldNotBeVerifiedOffenders(List<ClinicalData> data, Map<String, Set<CRFDefinition>> eventMap) {
        return data.stream().filter(clinicalData -> {
            Set<CRFDefinition> valid = eventMap.get(clinicalData.getEventName());
            if (valid == null) return true;
            else return false;
        }).collect(Collectors.toList());
    }

}
