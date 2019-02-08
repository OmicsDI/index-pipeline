package uk.ac.ebi.ddi.pipeline.indexer.utils;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 19/08/2015
 */
public class Utils {

    private Utils() {
    }

    public static String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;
        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }
            titleCase.append(c);
        }
        return titleCase.toString();
    }
}
