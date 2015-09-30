package uk.ac.ebi.ddi.pipeline.indexer.cli;

import org.apache.commons.cli.*;
import org.apache.commons.configuration.XMLConfiguration;
import uk.ac.ebi.ddi.pipeline.indexer.utils.CommandOptions;


/**
 * This class allow the user to interact with the library:
 *  - Validate files, download files, read, enrich them, etc.
 *
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 19/08/2015
 */
public class indexerCLI {


    @SuppressWarnings("static-access")
    public static void main(String[] args) throws Exception {

        XMLConfiguration config = ConfigurationFileBootstrap.getBootstrapSettings();

        // Definite command line
        CommandLineParser parser = new PosixParser();
        Options options = new Options();

        //Help page
        String helpOpt = "help";
        options.addOption("h", helpOpt, false, "print help message");

        String reportFileOpt = "reportFile";
        options.addOption(reportFileOpt, true, "Record errors/warn messages into outfile. If not set, print message on the screen.");

        String source  = "source";
        options.addOption(source, true, "input the source from the config file, the current sources are: " + ConfigurationFileBootstrap.getSourceNames(config).toString());

        String levelWarn = "warn";
        options.addOption(levelWarn, true, "Choose validation level (default level is Warn): \n" +
                "\t Warn: This category do a complete Schema and semantic validation of the file \n" +
                "\t Error: This category do a validation at level of XML Schema");

        String commandLine = "command";
        options.addOption(commandLine, true, "This command specify the process that will take in the backend from the possible command: " + CommandOptions.getValuesName().toString());

        // Parse command line
        CommandLine line = parser.parse(options, args);

        if (line.hasOption(helpOpt) || !line.hasOption(source)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("validatorCLI", options);
        }else {

            String reportName = line.getOptionValue(reportFileOpt);
        }
    }
}
