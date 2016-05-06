package nl.thehyve.ocdu.validators.checks;

import nl.thehyve.ocdu.models.MetaData;
import nl.thehyve.ocdu.models.OCEntities.OcEntity;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;

/**
 * Created by piotrzakrzewski on 04/05/16.
 */
public interface OcEntityCheck {

    ValidationErrorMessage getCorrespondingError(OcEntity data, MetaData metaData);

}
