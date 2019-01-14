package uk.ac.ebi.ddi.pipeline.indexer.io;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.*;

/**
 * Persister for serializing or deserializing objects to file system
 *
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 29/09/15
 */
public class FileSystemPersisterFactory {
    public static final Logger LOGGER = LoggerFactory.getLogger(FileSystemPersisterFactory.class);

    /**
     * central location for all the persisted files
     */
    private Resource locationPrefix;


    public FileSystemPersisterFactory(Resource locationPrefix) {
        this.locationPrefix = locationPrefix;
        LOGGER.info("Created factory at " + locationPrefix);
    }

    public FileSystemPersister getInstance(String key) throws IOException {
        return new FileSystemPersister(locationPrefix, key);
    }

    public void setLocationPrefix(Resource locationPrefix) {
        this.locationPrefix = locationPrefix;
    }

    public static class FileSystemPersister {

        /**
         * central location for all the persisted files
         */
        private Resource locationPrefix;
        /**
         * unique key to be used as folder name
         */
        private String key;
        /**
         * location where the files are persisted
         */
        private File location;

        private FileSystemPersister(Resource locationPrefix, String key) throws IOException {
            this.locationPrefix = locationPrefix;
            this.key = key;

            initTempDirectory();
        }

        /**
         * Initialize the persistent location
         */
        private void initTempDirectory() throws IOException {
            // initialize the temporary location
            location = new File(locationPrefix.getFile().getAbsolutePath() + File.separator + key);
            if (!location.exists()) {
                location.mkdir();
            }
        }

        /**
         * Persist an object using a given property
         * Note: the property will be used as the persisted file name
         */
        public synchronized void persist(String property, Object obj) throws IOException {
            if (obj instanceof Serializable) {
                try (
                    FileOutputStream fos = new FileOutputStream(location.getAbsolutePath() + File.separator + property);
                    ObjectOutputStream out = new ObjectOutputStream(fos)) {
                    out.writeObject(obj);
                    out.flush();
                }
            } else {
                String msg = "Object is not serializable, cannot be persisted to the local file system storage";
                LOGGER.error(msg);
                throw new IllegalArgumentException(msg);
            }

        }

        /**
         * Load an serialized object using a given property
         */
        public synchronized Object load(String property) throws IOException, ClassNotFoundException {
            File serializedObject = new File(location.getAbsolutePath() + File.separator + property);
            if (serializedObject.exists()) {
                try (
                        FileInputStream fis = new FileInputStream(serializedObject);
                        ObjectInputStream ois = new ObjectInputStream(fis)) {
                    return ois.readObject();
                }
            } else {
                return null;
            }
        }

        /**
         * clear all the persisted files
         */
        public void clear() throws IOException {
            // remove the folder
            if (location.exists()) {
                //delete whole directory
                FileUtils.forceDelete(location);
            }
        }
    }

}
