package sharks_umass.scanit.apis;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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
        String str = "https://www.dictionaryapi.com/api/v1/references/collegiate/xml/"
                + word +"?key=" + API_KEY;

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

            // Checks for quotes in example
            Element aq = (Element) tempDf.getElementsByTagName("vi").item(0);
            String quote = null;
            if(aq.getElementsByTagName("aq").item(0) != null){
                quote = aq.getElementsByTagName("aq").item(0).getTextContent();
            }

            String sentence = (quote != null) ? rawSentence.replace(quote, "-"+quote) : rawSentence;

            return new DefinerResult(word.toLowerCase(), tempDf.getTextContent(), sentence);


            /*
            Element def = (Element) doc.getElementsByTagName("def").item(0);
            int defIndex = 0;
            NodeList defList = doc.getElementsByTagName("def");
            // Look through <dt> elements to find one with an example
            NodeList dtList = def.getElementsByTagName("dt");
            int dtIndex = 0;
            int viIndex = 0;
            Element temp = (Element) dtList.item(0);
            System.out.println("temppppp " +temp.getElementsByTagName("vi").item(0));
            System.out.println(dtList.getLength());
            while(viIndex < dtList.getLength() && (temp == null || temp.getElementsByTagName("vi").item(0) == null)) {
                viIndex++;
                temp = (Element) dtList.item(viIndex);
                System.out.println("inloop");
                System.out.println(temp.getTextContent());
            }
            System.out.println("end loop");
            String sentence = "<sorry, no example sentence available>";
            String rawSentence = null; // used for diff
            System.out.println("temp="+temp);
            if (viIndex < dtList.getLength() && temp != null && temp.getElementsByTagName("vi").item(0) != null) {
                // Set index for <dt>
                dtIndex = viIndex;
                Element vi = (Element) temp.getElementsByTagName("vi").item(0);
                System.out.println(vi);
                sentence = vi.getTextContent();
                rawSentence = vi.getTextContent(); //was scared of copy, I forget how java works a little
                // check if there is a "quoted person"
                NodeList aq = vi.getElementsByTagName("aq"); // only exists if definition is a quote
                String quote = null;
                if(aq.getLength() > 0){
                    quote = aq.item(0).getTextContent();
                }
                sentence = (quote != null) ? sentence.replace(quote, "-"+quote) : sentence;
            }
            Element dt = (Element) def.getElementsByTagName("dt").item(dtIndex);
            String definition = dt.getTextContent();
            System.out.println(word.toLowerCase());
            System.out.println((rawSentence != null) ? definition.replace(sentence, ""): definition);
            System.out.println(sentence);
            */

        } catch (MalformedURLException e) {
            System.out.println("MALFORMED URL");
            e.printStackTrace();
        } catch (IOException e){
            System.out.println("IO EXCEPTION");
            e.printStackTrace();
        } catch (ParserConfigurationException e){
            System.out.println("PARSER CONFIG EXCEPTION");
            e.printStackTrace();
        } catch (SAXException e){
            System.out.println("SAX EXCEPTION");
            e.printStackTrace();
        }

        return new DefinerResult(word.toLowerCase(),"Definition not available","");
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