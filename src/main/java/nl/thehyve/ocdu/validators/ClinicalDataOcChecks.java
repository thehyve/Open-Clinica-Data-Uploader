package nl.thehyve.ocdu.validators;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.MetaData;
import nl.thehyve.ocdu.models.ValidationErrorMessage;
import nl.thehyve.ocdu.validators.checks.DataFieldWidthCheck;
import nl.thehyve.ocdu.validators.checks.OcEntityCheck;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotrzakrzewski on 04/05/16.
 */
public class ClinicalDataOcChecks {




    private final List<ClinicalData> clinicalData;
    private final MetaData metadata;
    private  List<OcEntityCheck> recordChecks;

    public ClinicalDataOcChecks(MetaData metadata, List<ClinicalData> clinicalData) {
        this.metadata = metadata;
        this.clinicalData = clinicalData;
        recordChecks.add(new DataFieldWidthCheck());
    }

    public List<ValidationErrorMessage> getErrors() {
        List<ValidationErrorMessage> errors = new ArrayList<>();
        clinicalData.stream().forEach(cData ->
        {
            List<ValidationErrorMessage> recordErrors = getRecordErrors(cData);
            errors.addAll(recordErrors);
        });
        List<ValidationErrorMessage> interRecordInconsitencies = getInterRecordInconsitencies();
        errors.addAll(interRecordInconsitencies);
        return errors;
    }

    private List<ValidationErrorMessage> getRecordErrors(ClinicalData data) {
        List<ValidationErrorMessage> errors = new ArrayList<>();
        recordChecks.stream().forEach(
                check -> {
                    boolean passed = check.check(data,metadata);
                    if (!passed) errors.add(check.getCorrespondingError());
                }
        );
        return errors;
    }

    private List<ValidationErrorMessage> getInterRecordInconsitencies() {
        return new ArrayList<>();
    }

}
