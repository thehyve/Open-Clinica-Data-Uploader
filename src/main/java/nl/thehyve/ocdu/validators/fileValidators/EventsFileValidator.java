package nl.thehyve.ocdu.validators.fileValidators;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotrzakrzewski on 11/04/16.
 */
public class EventsFileValidator extends GenericFileValidator{


    public EventsFileValidator() {
        super(new String[]{}, new String[]{}); //TODO: put references from EventsFactory here for mandatory and integer columns
    }
}
