package sharks_umass.scanit.apis;

import com.wolfram.alpha.WAEngine;
import com.wolfram.alpha.WAException;
import com.wolfram.alpha.WAPlainText;
import com.wolfram.alpha.WAPod;
import com.wolfram.alpha.WAQuery;
import com.wolfram.alpha.WAQueryResult;
import com.wolfram.alpha.WASubpod;

/**
 * Created by Tyler on 4/12/2017.
 */

public class Solver {
    private static String appid = "88JQEG-6TYRX8W68V";
    String Answer ="";



    public String solve(String input){

        // The WAEngine is a factory for creating WAQuery objects,
        // and it also used to perform those queries. You can set properties of
        // the WAEngine (such as the desired API output format types) that will
        // be inherited by all WAQuery objects created from it. Most applications
        // will only need to crete one WAEngine object, which is used throughout
        // the life of the application.
        WAEngine engine = new WAEngine();

        // These properties will be set in all the WAQuery objects created from this WAEngine.
        engine.setAppID(appid);
        engine.addFormat("plaintext");

        // Create the query.
        WAQuery query = engine.createQuery();

        // Set properties of the query.
        query.setInput(input);

        try {
            // This sends the URL to the Wolfram|Alpha server, gets the XML result
            // and parses it into an object hierarchy held by the WAQueryResult object.
            WAQueryResult queryResult = engine.performQuery(query);

            if (queryResult.isError()) {
                Answer = "Query error\n";
                Answer = Answer.concat("  error code: " + Integer.toString(queryResult.getErrorCode()) + "\n");
                Answer = Answer.concat("  error message: " + queryResult.getErrorMessage() + "\n");
            } else if (!queryResult.isSuccess()) {
                Answer = "Query was not understood; no results available.";
            } else {
                // Got a result.
                for (WAPod pod : queryResult.getPods()) {
                    if (!pod.isError()) {
                        if(!pod.getTitle().equals("Manipulatives illustration")) {
                            for (WASubpod subpod : pod.getSubpods()) {
                                for (Object element : subpod.getContents()) {
                                    if (element instanceof WAPlainText) {
                                        if(!((WAPlainText) element).getText().equals("")) {
                                            Answer = Answer.concat(pod.getTitle());
                                            Answer = Answer.concat("\n------------\n");
                                            Answer = Answer.concat(((WAPlainText) element).getText() + "\n");
                                        }
                                    }
                                }
                            }
                            Answer = Answer.concat("\n");
                        }
                    }
                }
            }
        }
        catch (WAException e) {
            e.printStackTrace();
        }

        Answer = Answer.replace('', '=');
        return Answer;
    }
}