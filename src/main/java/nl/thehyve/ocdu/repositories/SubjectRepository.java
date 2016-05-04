package nl.thehyve.ocdu.repositories;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OCEntities.Subject;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by piotrzakrzewski on 16/04/16.
 */
public interface SubjectRepository extends CrudRepository<Subject, Long> {

    List<ClinicalData> findByOwner(String owner);

    List<ClinicalData> findBySubmission(String submission);

}
