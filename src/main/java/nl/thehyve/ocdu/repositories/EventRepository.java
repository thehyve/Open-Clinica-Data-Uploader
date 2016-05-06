package nl.thehyve.ocdu.repositories;

import nl.thehyve.ocdu.models.OCEntities.Event;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by piotrzakrzewski on 16/04/16.
 */
public interface EventRepository extends CrudRepository<Event, Long> {

    List<Event> findByOwner(String owner);

    List<Event> findBySubmission(String submission);

}
