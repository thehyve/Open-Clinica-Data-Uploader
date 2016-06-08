package nl.thehyve.ocdu.validators.crossChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.CRFVersionMismatchError;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.openclinica.ws.beans.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks if the CRF-version as specified in the input, matches the CRF-version of existing data for each subject
 * registered in OpenClinica.
 *
 * Created by jacob on 6/2/16.
 */
public class CRFVersionMatchCrossCheck implements ClinicalDataCrossCheck {


    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> clinicalDataList, MetaData metaData, List<StudySubjectWithEventsType> subjectWithEventsTypeList) {

        if (clinicalDataList.isEmpty()) {
            return null;
        }
        // Assumption is that there is only 1 event and 1 CRF in a data file and that the clincalDataList only contains a single subjectID

        String studyIdentifier = metaData.getStudyIdentifier();

        List<String> offendingNames = new ArrayList<>();
        for (ClinicalData clinicalDataToUpload : clinicalDataList) {
            String subjectLabel = clinicalDataToUpload.getSsid();
            List<ClinicalData> clinicalDataPresentInStudy = convertToClinicalData(subjectWithEventsTypeList, subjectLabel, studyIdentifier);
            for (ClinicalData clinicalDataInStudy : clinicalDataPresentInStudy) {
                if (! clinicalDataInStudy.hasSameCRFVersion(clinicalDataToUpload)) {
                    String msg = "Subject " + subjectLabel + " has a mismatching CRF version (" +
                            clinicalDataToUpload.getCrfVersion()
                            + ") for CRF "
                            + clinicalDataInStudy.getCrfName()
                            + " in event " + clinicalDataInStudy.getEventName()
                            + ", repeat number " + clinicalDataToUpload.getEventRepeat();
                    if (!offendingNames.contains(msg)) {
                        offendingNames.add(msg);
                    }
                }
            }
        }

        if (offendingNames.isEmpty()) {
            return null;
        }
        CRFVersionMismatchError crfVersionMismatchError = new CRFVersionMismatchError();
        crfVersionMismatchError.addAllOffendingValues(offendingNames);
        return crfVersionMismatchError;

    }

    private List<ClinicalData> convertToClinicalData(List<StudySubjectWithEventsType> subjectWithEventsTypeList, String studySubjectLabel, String studyIdentifier) {
        // TODO convert to lambda expressions ????
        List<ClinicalData> ret = new ArrayList<>();
        for (StudySubjectWithEventsType subjectWithEventsType : subjectWithEventsTypeList) {
            if (studySubjectLabel.equals(subjectWithEventsType.getLabel())) {
                EventsType eventsType = subjectWithEventsType.getEvents();
                for (EventResponseType eventResponseType : eventsType.getEvent()) {
                    String eventOID = eventResponseType.getEventDefinitionOID();
                    Integer eventOrdinal = Integer.parseInt(eventResponseType.getOccurrence());
                    for (EventCrfInformationList eventCrfInformationList : eventResponseType.getEventCrfInformation()) {
                        List<EventCrfType>  eventCrfTypeList = eventCrfInformationList.getEventCrf();
                        for (EventCrfType eventCrfType : eventCrfTypeList) {
                            ClinicalData clinicalData = new ClinicalData(studyIdentifier,
                                    null,
                                    subjectWithEventsType.getLabel(),
                                    eventOID,
                                    eventOrdinal,
                                    eventCrfType.getName(),
                                    null,
                                    eventCrfType.getVersion(),
                                    null,
                                    null,
                                    null);
                            ret.add(clinicalData);
                        }
                    }
                }
            }
        }
        return ret;
    }
}
