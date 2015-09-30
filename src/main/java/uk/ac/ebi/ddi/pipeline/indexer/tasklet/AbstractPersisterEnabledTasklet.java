package uk.ac.ebi.ddi.pipeline.indexer.tasklet;

import org.springframework.util.Assert;
import uk.ac.ebi.ddi.pipeline.indexer.io.FileSystemPersisterFactory;


import java.io.IOException;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 29/09/15
 */
public abstract class AbstractPersisterEnabledTasklet extends AbstractTasklet {

    protected FileSystemPersisterFactory.FileSystemPersister filePersister;
    protected String persisterKey;

    public void persist(Object objToPersist) throws IOException {
        filePersister.persist(persisterKey, objToPersist);
    }

    public void setFilePersister(FileSystemPersisterFactory.FileSystemPersister filePersister) {
        this.filePersister = filePersister;
    }

    public void setPersisterKey(String persisterKey) {
        this.persisterKey = persisterKey;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(persisterKey, "Persister key can not be null");
        Assert.notNull(filePersister, "File persister can not be null");
    }
}
