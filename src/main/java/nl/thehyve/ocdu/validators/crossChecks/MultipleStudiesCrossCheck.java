package nl.thehyve.ocdu.validators.crossChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.IncorrectNumberOfStudies;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by piotrzakrzewski on 11/05/16.
 */
public class MultipleStudiesCrossCheck implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData, List<StudySubjectWithEventsType> subjectWithEventsTypeList) {
        Stream<ClinicalData> stream = data.stream();
        Set<String> studiesUsed = stream.map(clinicalData -> clinicalData.getStudy()).collect(Collectors.toSet());
        if (studiesUsed.size() != 1) {
            IncorrectNumberOfStudies error = new IncorrectNumberOfStudies();
            if (studiesUsed.size() == 0)
                error.addOffendingValue("No studies referenced in the data");
            else {
                studiesUsed.forEach(studyName ->
                        error.addOffendingValue(studyName));
            }
            return error;
        } else return null;

    }
}
