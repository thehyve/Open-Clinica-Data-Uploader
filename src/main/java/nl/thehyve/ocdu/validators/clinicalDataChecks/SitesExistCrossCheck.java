package nl.thehyve.ocdu.validators.clinicalDataChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.SiteDoesNotExist;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Checks if the sites supplied in the data file actually exist in the OpenClinica metadata.
 * Created by Jacob Rousseau on 22-Jun-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public class SitesExistCrossCheck implements ClinicalDataCrossCheck {

    public static final String EMPTY_SITE_DENOTATION = "";

    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData, Map<ClinicalData, ItemDefinition> itemDefMap, List<StudySubjectWithEventsType> studySubjectWithEventsTypeList, Map<ClinicalData, Boolean> shownMap, Map<String, Set<CRFDefinition>> eventMap) {
        Set<String> siteNamesPresentInStudy = new HashSet<>();
        siteNamesPresentInStudy.add(EMPTY_SITE_DENOTATION);
        metaData.getSiteDefinitions().stream().forEach(siteDefinition -> siteNamesPresentInStudy.add(siteDefinition.getName()));
        List<ClinicalData> violators = data.stream()
                .filter(clinicalData -> !siteNamesPresentInStudy.contains(clinicalData.getSite()))
                .collect(Collectors.toList());
        if (violators.size() > 0) {
            ValidationErrorMessage error =
                    new SiteDoesNotExist();
            List<String> nonExistentSiteNames = new ArrayList<>();
            violators.stream().forEach(clinicalData ->
            { String siteName = clinicalData.getSite();
                if(!nonExistentSiteNames.contains(siteName)) nonExistentSiteNames.add(siteName);
            });
            error.addAllOffendingValues(nonExistentSiteNames);
            return error;
        } else return null;

    }
}
