package sharks_umass.scanit.apis;

/* This class is used to hold the result of the Definer class, in Python I'd just return a 3-tuple
 * but forsooth, I have to use Java */
public class DefinerResult {
    private String word;
    private String definition;
    private String example;

    public DefinerResult(String word, String definition, String example){
        this.word = word;
        this.definition = definition;
        this.example = example;
    }

    public String getWord(){
        return word;
    }

    public String getDefinition(){
        return definition;
    }

    public String getExample(){
        return example;
    }
}
