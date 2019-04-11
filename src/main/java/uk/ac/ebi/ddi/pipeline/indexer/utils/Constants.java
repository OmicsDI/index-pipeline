package uk.ac.ebi.ddi.pipeline.indexer.utils;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 30/09/15
 */
public class Constants {


    public static final String RANGE_SEPARATOR = "-";
    public static final String COMMA_SEPARATOR = ",";
    public static final String TAB_SEPARATOR = "\t";
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final String OUTPUT_DIVIDER =
            "===================================================================================";

    public static final String PROJECT_DOI = "Project DOI";
    public static final String DOI = "DOI";


    public static final String GZIP_FILE_EXTENSION = "gz";
    public static final String ZIP_FILE_EXTENSION = "zip";
    public static final String MZTAB_FILE_EXTENSION = "mztab";
    public static final String PRIDE_MZTAB_FILE_EXTENSION = "pride.mztab";
    public static final String MGF_FILE_EXTENSION = "mgf";
    public static final String PRIDE_MGF_FILE_EXTENSION = "pride.mgf";
    public static final String README_FILE_NAME = "README.txt";


    public static final String NEWT = "NEWT";

    public static final double MZ_OUTLIER = 4;

    public static final String PROJECT_PARENT_WEBSITE_URL = "http://www.ebi.ac.uk/pride/archive/projects/";

    public static final String PSI_MOD = "MOD";
    public static final String MS = "MS";
    public static final String UNIMOD = "UNIMOD";

    public static final String MS_INSTRUMENT_MODEL_AC = "MS:1000031";
    public static final String MS_SOFTWARE_AC = "MS:1000531";
    public static final String MS_CONTACT_EMAIL_AC = "MS:1000589";

    public static final String MS_SOFTWARE_NAME = "software";
    public static final String MS_INSTRUMENT_MODEL_NAME = "instrument model";

    public static final String PEPTIDEATLAS_SRM = "SRM";
    public static final String BIOMODELS_DATABASE = "BioModels";
    public static final String SUBMITTER_KEYWORDS = "submitter_keywords";


}
