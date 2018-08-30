package uk.ac.ebi.ddi.pipeline.indexer.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Utility class for file access
 *
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 29/09/15
 */
public final class FileUtil {

    public static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    static public String DOT = ".";
    private FileUtil() {
    }
    /**
     * Get file extension for a given file
     *
     * @param file given file
     * @return String  file extension
     */
    public static String getFileExtension(File file) {
        String fileName = file.getName();
        return getFileExtension(fileName);
    }

    public static String getFileExtension(String fileName) {
        int mid = fileName.lastIndexOf(DOT);
        String fileNameExt = null;
        if (mid > 0) {
            fileNameExt = fileName.substring(mid + 1, fileName.length()).toLowerCase();
        }

        return fileNameExt;
    }

    /**
     * Check whether a given file is a binary file
     *
     * @param file given file object
     * @return boolean true means binary file
     * @throws java.io.IOException exception while reading the given file
     */
    public static boolean isBinaryFile(File file) throws IOException {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            int size = file.length() > 500 ? 500 : new Long(file.length()).intValue();
            byte[] bytes = new byte[size];

            in.read(bytes, 0, bytes.length);
            short bin = 0;
            for (byte thisByte : bytes) {
                char it = (char) thisByte;
                if (!Character.isWhitespace(it) && !Character.isISOControl(it)) {
                    bin++;
                }

                if (bin >= 5) {
                    return true;
                }
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }

        return false;
    }

    public static String tail(File file, int numberOfChars) throws IOException {
        RandomAccessFile fileHandler = null;
        try {
            fileHandler = new RandomAccessFile(file, "r");
            long fileLength = file.length() - 1;
            StringBuilder sb = new StringBuilder();

            for (long filePointer = fileLength; filePointer > (fileLength - numberOfChars); filePointer--) {
                fileHandler.seek(filePointer);
                int readByte = fileHandler.readByte();
                sb.append((char) readByte);
            }

            return sb.reverse().toString();
        } finally {
            if (fileHandler != null)
                fileHandler.close();
        }
    }

    public static String getDecompressedFileName(File file) throws IOException {
        String fileName = file.getName();
        ZipFile zipFile = null;


        try {
            // get file as input stream
            if (isGzipped(file)) {
                String compressedFileName = file.getName();
                fileName = compressedFileName.substring(0, compressedFileName.length() - 3);
            } else if (isZipped(file) && file.exists() && file.canRead()) {
                zipFile = new ZipFile(file);
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                // read only the first entry from zip
                if (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    fileName = entry.getName();
                }
            }
        } finally {
            if (zipFile != null) {
                zipFile.close();
            }
        }

        return getRealFileName(fileName);
    }

    public static String getRealFileName(String fileName) {
        String name = fileName;

        if (name.contains("/") || name.contains("\\")) {
            String[] parts = name.split("/");
            name = parts[parts.length - 1];
            parts = name.split("\\\\");
            name = parts[parts.length - 1];
        }

        return name;
    }

    public static InputStream getFileInputStream(File file) throws IOException {
        InputStream fileInputStream = null;

        if (isGzipped(file)) {
            fileInputStream = new GZIPInputStream(new FileInputStream(file));
        } else if (isZipped(file)) {
            ZipFile zipFile = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            // read only the first entry from zip
            if (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                fileInputStream = zipFile.getInputStream(entry);
            }
        } else {
            fileInputStream = new FileInputStream(file);
        }

        return fileInputStream;
    }

    public static boolean isZipped(File file) {
        String fileExtension = FileUtil.getFileExtension(file);
        return "zip".equals(fileExtension);
    }

    public static boolean isGzipped(File file) {
        String fileExtension = FileUtil.getFileExtension(file);
        return "gz".equals(fileExtension);
    }

    public static boolean isFileEmpty(File file) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            return reader.readLine() == null;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public static boolean downloadFileFromURL(String urlStr, String localFile) throws IOException {
        // Create a new trust manager that trust all certificates
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }

        URL url = new URL(urlStr);
        URLConnection connection = url.openConnection();
        InputStream is = connection.getInputStream();

        FileOutputStream fileOutput = new FileOutputStream(new File(localFile));
        byte[] buffer = new byte[2048];
        int bufferLength; //used to store a temporary size of the buffer

        while ( (bufferLength = is.read(buffer)) > 0 )
            fileOutput.write(buffer, 0, bufferLength);

        fileOutput.close();

        return true;
    }

    /**
     * Write object to file for future recover
     * @param file
     * @param object
     */
    public static void writeObjectToFile(File file, Serializable object) throws IOException {
        try (FileOutputStream cacheFile = new FileOutputStream(file)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(cacheFile);
            objectOutputStream.writeObject(object);
        }
    }

    /**
     * Load object from file
     * @param file
     * @return
     */
    public static <T> T loadObjectFromFile(File file) throws IOException, ClassNotFoundException {
        try (FileInputStream fileIn = new FileInputStream(file)) {
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            Object obj = objectIn.readObject();
            return (T) obj;
        }
    }
}
