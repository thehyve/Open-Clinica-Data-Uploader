package nl.thehyve.ocdu.validators.crossChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;

import java.util.List;

/**
 * Created by piotrzakrzewski on 12/05/16.
 */
public class DataTypeCrossCheck implements ClinicalDataCrossCheck  {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData) {
        //
        return null;
    }
}
