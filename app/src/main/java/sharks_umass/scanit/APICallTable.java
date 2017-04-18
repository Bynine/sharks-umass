package sharks_umass.scanit;

/**
 * Created by Tyler on 4/12/2017.
 */

public abstract class APICallTable {

    long apiID;
    Object inputData;
    Object outputData;

    abstract void callApi();
}
