package nl.thehyve.ocdu.repositories;

import nl.thehyve.ocdu.models.OcDefinitions.CodeListItemDefinition;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by piotrzakrzewski on 03/05/16.
 */
public interface CodeListItemDefinitionRepository extends CrudRepository<CodeListItemDefinition, Long> {
}
