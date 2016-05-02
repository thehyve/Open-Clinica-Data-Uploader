package nl.thehyve.ocdu.repositories;

import nl.thehyve.ocdu.models.ClinicalData;
import nl.thehyve.ocdu.models.OcUser;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by piotrzakrzewski on 28/04/16.
 */
public interface UserRepository extends CrudRepository<OcUser, Long> {


    List<OcUser> findByOcEnvironment(String ocEnvironment);

    List<OcUser> findByUsername(String username);

}
