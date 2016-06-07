package nl.thehyve.ocdu.repositories;

import nl.thehyve.ocdu.models.OCEntities.Event;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by piotrzakrzewski on 16/04/16.
 */
public interface EventRepository extends CrudRepository<Event, Long> {

    List<Event> findByOwner(OcUser owner);

    List<Event> findBySubmission(UploadSession submission);

}
