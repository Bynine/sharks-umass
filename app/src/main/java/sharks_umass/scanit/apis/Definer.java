package sharks_umass.scanit.apis;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class Definer {
    //TODO: Read about best practices for api key management
    private final String API_KEY = "a6237379-dcb3-4ba9-b2b9-0eba3e330076";

    // should this throw something like a WordUnDefinedException?
    // NOTE: THIS GETS 1 DEFINITION OF THE WORD AND 1 USAGE OF A WORD IN A SENTENCE
    public DefinerResult define(String word){
        if(word.equals("No text detected")) return new DefinerResult("", ":Nothing detected", "No example possible.");
        String str = null;
            str = "https://www.dictionaryapi.com/api/v1/references/collegiate/xml/"
                    + word +"?key=" + API_KEY;
            Log.d("URL", str);

        try {
            // Create URL
            URL url = new URL(str);

            // Get the HTTPResponse text and parse XML
            // Parsing XML from tutorial
            // https://www.tutorialspoint.com/java_xml/java_dom_parse_document.htm
            InputStream inStream = new ByteArrayInputStream(downloadUrl(url).getBytes(StandardCharsets.UTF_8));
            DocumentBuilderFactory dbFactory
                    = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inStream);
            doc.getDocumentElement().normalize();

            // Get the required elements -- NOTE: MW is terrible, they have poorly formatted XML
            // so there is some hackish stuff to deal with it; namely "diffing" strings
            // <def> holds definition "blocks"
            // inside <def>, <dt> has the definition text
            // IN SOME CASES in <dt>, <vi> has the definition used in an example
            // IN SOME CASES in <vi>, <aq> has the name of the person attributed to the example

            NodeList defList = doc.getElementsByTagName("def");
            if(defList == null) return new DefinerResult(word, ":Definition not available", "No example possible");

            Element tempDt;
            NodeList tempDtList;
            Element tempDf = null;
            String rawSentence = null;
            boolean firstFound = false; // once you get the first instance of a word, return it
            for(int defIndex = 0; defIndex < defList.getLength() &!firstFound; defIndex++){
                tempDt = (Element) defList.item(defIndex);
                tempDtList = tempDt.getElementsByTagName("dt");

                for(int dtIndex = 0; dtIndex < tempDtList.getLength() && !firstFound; dtIndex++){
                    tempDf = (Element) tempDtList.item(dtIndex);
                    if(tempDf.getElementsByTagName("vi").item(0) != null){
                        rawSentence = tempDf.getElementsByTagName("vi").item(0).getTextContent();
                        firstFound = true; //example is found
                    }
                }
            }

            if(!firstFound) {
                if(defList.item(0) != null)
                return new DefinerResult(word.toLowerCase(), defList.item(0).getTextContent(), "No example available");
                else return new DefinerResult(word.toLowerCase(), ":Unable to find definition", "No example available");
            }
            // Checks for quotes in example
            Element aq = (Element) tempDf.getElementsByTagName("vi").item(0);
            String quote = null;
            if(aq.getElementsByTagName("aq").item(0) != null){
                quote = aq.getElementsByTagName("aq").item(0).getTextContent();
            }

            String sentence = (quote != null) ? rawSentence.replace(quote, "-"+quote) : rawSentence;

            return new DefinerResult(word.toLowerCase(), tempDf.getTextContent().replace(sentence, ""), sentence);

        }
        catch (SAXException | IOException | ParserConfigurationException e) {}
        return new DefinerResult(word, ":Exception Occurred", "No example possible");
    }

    // Move to Utils Class? API class?
    // From https://developer.android.com/training/basics/network-ops/connecting.html
    private String downloadUrl(URL url) throws IOException {
        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(3000);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(3000);
            // For this use case, set HTTP method to GET.
            connection.setRequestMethod("GET");
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.setDoInput(true);
            // Open communications link (network traffic occurs here).
            connection.connect();
            //publishProgress(DownloadCallback.Progress.CONNECT_SUCCESS);
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();
            //publishProgress(DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS, 0);
            if (stream != null) {
                // Converts Stream to String with max length of 500.
                result = readStream(stream);
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }


}