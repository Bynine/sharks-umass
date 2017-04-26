package sharks_umass.scanit.apis;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

import sharks_umass.scanit.CameraViewActivity;
import sharks_umass.scanit.CropViewActivity;


public class Definer {
    //TODO: Read about best practices for api key management
    private final String API_KEY = "a6237379-dcb3-4ba9-b2b9-0eba3e330076";
    private String word;
    private DefinerResult definerResult;

    public Definer(String word){
        this.word = word;
    }

    private class AsyncCaller extends AsyncTask<Void, Void, Void>
    {
        private String xmlResponse;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected Void doInBackground(Void... params) {
            String str = "https://www.dictionaryapi.com/api/v1/references/collegiate/xml/"
                    + word +"?key=" + API_KEY;

            try {
                // Create URL
                URL url = new URL(str);
                xmlResponse = downloadUrl(url);

            } catch (MalformedURLException e) {
                System.out.println("MALFORMED URL");
                e.printStackTrace();
            } catch (IOException e){
                System.out.println("IO EXCEPTION");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Get the HTTPResponse text and parse XML
            // Parsing XML from tutorial
            // https://www.tutorialspoint.com/java_xml/java_dom_parse_document.htm
            InputStream inStream = new ByteArrayInputStream(xmlResponse.getBytes(StandardCharsets.UTF_8));
            DocumentBuilderFactory dbFactory
                    = DocumentBuilderFactory.newInstance();
            try {


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
                for (int defIndex = 0; defIndex < defList.getLength() & !firstFound; defIndex++) {
                    tempDt = (Element) defList.item(defIndex);
                    tempDtList = tempDt.getElementsByTagName("dt");

                    for (int dtIndex = 0; dtIndex < tempDtList.getLength() && !firstFound; dtIndex++) {
                        tempDf = (Element) tempDtList.item(dtIndex);
                        if (tempDf.getElementsByTagName("vi").item(0) != null) {
                            rawSentence = tempDf.getElementsByTagName("vi").item(0).getTextContent();
                            firstFound = true; //example is found
                        }
                    }
                }

                // Checks for quotes in example
                Element aq = (Element) tempDf.getElementsByTagName("vi").item(0);
                String quote = null;
                if (aq.getElementsByTagName("aq").item(0) != null) {
                    quote = aq.getElementsByTagName("aq").item(0).getTextContent();
                }

                String sentence = (quote != null) ? rawSentence.replace(quote, "-" + quote) : rawSentence;

                definerResult = new DefinerResult(word.toLowerCase(), tempDf.getTextContent(), sentence);
            } catch (IOException e){
                Log.d("IO EXCEPTION", e.getMessage());
            } catch (ParserConfigurationException e){
                Log.d("PARSER CONFIG EXCEPTION", e.getMessage());
            } catch (SAXException e){
                Log.d("SAX EXCEPTION", e.getMessage());
            }
            definerResult = new DefinerResult(word.toLowerCase(),"Definition not available","");
        }

    }

    // should this throw something like a WordUnDefinedException?
    // NOTE: THIS GETS 1 DEFINITION OF THE WORD AND 1 USAGE OF A WORD IN A SENTENCE
    public DefinerResult define(){
        AsyncCaller task = new AsyncCaller();
        task.execute();
        System.out.println("CHECK: " + definerResult == null ? "null" : "not null");
        //Log.d("CHECK: ", definerResult == null ? "null" : "not null");
        return definerResult;
        //return new DefinerResult(word.toLowerCase(),"Definition not available","");
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
