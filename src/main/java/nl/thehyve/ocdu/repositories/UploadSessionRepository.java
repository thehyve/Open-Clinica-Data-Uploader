package nl.thehyve.ocdu.repositories;

import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by piotrzakrzewski on 28/04/16.
 */
public interface UploadSessionRepository extends CrudRepository<UploadSession, Long> {

    List<UploadSession> findByOwner(OcUser owner);

}
