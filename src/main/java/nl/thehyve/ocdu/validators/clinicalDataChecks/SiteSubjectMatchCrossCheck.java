package nl.thehyve.ocdu.validators.clinicalDataChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.SubjectSiteMismatch;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Checks if a subject's study/site combination as specified in a data file matches the one
 * present in OpenClinica. Only active for existing subjects.
 * Created by Jacob Rousseau on 22-Jun-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public class SiteSubjectMatchCrossCheck implements ClinicalDataCrossCheck {

    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData, Map<ClinicalData, ItemDefinition> itemDefMap, List<StudySubjectWithEventsType> studySubjectWithEventsTypeList, Map<ClinicalData, Boolean> shownMap, Map<String, Set<CRFDefinition>> eventMap) {
        Map<String, String> subjectSiteCombinationsPresentInStudy = new HashMap<>();
        studySubjectWithEventsTypeList.
                stream().forEach(studySubjectWithEventsType -> subjectSiteCombinationsPresentInStudy.put(studySubjectWithEventsType.getLabel(), studySubjectWithEventsType.getStudyRef().getSiteRef() != null ? studySubjectWithEventsType.getStudyRef().getSiteRef().getIdentifier() : ""));

        List<ClinicalData> violators = data.stream()
                .filter(clinicalData -> (( SitesExistCrossCheck.EMPTY_SITE_DENOTATION.equals(clinicalData.getSite())) && ( subjectSiteCombinationsPresentInStudy.containsKey(clinicalData.getSsid()) &&
                                           (! subjectSiteCombinationsPresentInStudy.get(clinicalData.getSsid()).equals(clinicalData.getSite())))))
                .collect(Collectors.toList());
        if (violators.size() > 0) {
            ValidationErrorMessage error =
                    new SubjectSiteMismatch();
            List<String> mismatchingSubjectSiteCombinationList = new ArrayList<>();
            violators.stream().forEach(clinicalData ->
            { String subjectID = clinicalData.getSsid();
                if(!mismatchingSubjectSiteCombinationList.contains(subjectID)) mismatchingSubjectSiteCombinationList.add(subjectID);
            });
            error.addAllOffendingValues(mismatchingSubjectSiteCombinationList);
            return error;
        } else return null;

    }
}
