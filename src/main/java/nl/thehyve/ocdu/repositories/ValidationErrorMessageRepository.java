package nl.thehyve.ocdu.repositories;

import nl.thehyve.ocdu.models.ValidationErrorMessage;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by piotrzakrzewski on 03/05/16.
 */
public interface ValidationErrorMessageRepository extends CrudRepository<ValidationErrorMessage, Long>{
}
