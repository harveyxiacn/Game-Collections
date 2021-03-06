package com.itgarage.harvey.gamecollections.amazon_product_advertising_api;

import android.util.Log;

import com.itgarage.harvey.gamecollections.models.Game;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * This class is used for parse the results back from Amazon Product Advertising API.
 */
public class Parser {
    /**
     * ---------------------  Search TAG ---------------------
     */
    private static final String KEY_ROOT = "Items";
    private static final String KEY_REQUEST_ROOT = "Request";
    private static final String KEY_REQUEST_CONTAINER = "IsValid";
    private static final String KEY_ITEM = "Item";
    private static final String KEY_SAMLL_IMAGE = "SmallImage";
    private static final String KEY_MEDIUM_IMAGE = "MediumImage";
    private static final String KEY_LARGE_IMAGE = "LargeImage";
    private static final String KEY_IMAGE_CONTAINER = "URL";
    private static final String KEY_ITEM_ATTRIBUTES = "ItemAttributes";
    private static final String KEY_EDITION = "Edition";
    private static final String KEY_GENRE = "Genre";
    private static final String KEY_MANUFACTURER = "Manufacturer";
    private static final String KEY_PLATFORM = "Platform";
    private static final String KEY_HARDWARE_PLATFORM = "HardwarePlatform";
    private static final String KEY_PUBLICATION_DATE = "PublicationDate";
    private static final String KEY_RELEASE_DATE = "ReleaseDate";
    private static final String KEY_TITLE = "Title";
    private static final String KEY_UPC_CODE = "UPC";
    private static final String KEY_ERROR = "Error";
    private static final String KEY_CODE = "Code";

    private static final String VALUE_VALID_RESPONCE = "True";


    /**
     * call in the app to get response list from Amazon
     * Pass a String url as a param which is returned by SignedRequestsHelper's Sign function
     */
    public NodeList getResponseNodeList(String service_url) {
        String searchResponse = this.getUrlContents(service_url);
        Log.i("url", "" + service_url);
        Log.i("response", "" + searchResponse);
        Document doc;
        NodeList items = null;
        if (searchResponse != null) {
            try {
                doc = this.getDomElement(searchResponse);
                items = doc.getElementsByTagName(KEY_ROOT);//<Items>
                Element element = (Element) items.item(0);// <Request>
                if (isResponseValid(element)) {
                    //if(doc.getElementsByTagName(KEY_ITEM)!=null)
                    //Log.i("before get item","");
                    items = doc.getElementsByTagName(KEY_ITEM);//<Item>
                    //Log.i("after get item", "");
                }else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return items;
    }

    /**
     * Create a game object and set the attributes.
     * Call in the app to get a game object to show.
     * Pass a NodeList object as a param, which is returned by function getResponseNodeList
     */
    public Game getSearchObject(NodeList list, int position) {
        Game game = new Game();
        Element e = (Element) list.item(position);

        game.setTitle(this.getValue((Element) (e.getElementsByTagName(KEY_ITEM_ATTRIBUTES).item(0))
                , KEY_TITLE));
        game.setGenre(this.getValue((Element) (e.getElementsByTagName(KEY_ITEM_ATTRIBUTES).item(0)), KEY_GENRE));
        game.setPlatform(this.getValue((Element) (e.getElementsByTagName(KEY_ITEM_ATTRIBUTES).item(0)), KEY_PLATFORM));
        game.setHardwarePlatform(this.getValue((Element) (e.getElementsByTagName(KEY_ITEM_ATTRIBUTES).item(0)), KEY_HARDWARE_PLATFORM));
        if(game.getPlatform().equals("")){
            game.setPlatform(game.getHardwarePlatform());
        }
        game.setManufacturer(this.getValue((Element) (e.getElementsByTagName(KEY_ITEM_ATTRIBUTES).item(0)), KEY_MANUFACTURER));
        game.setEdition(this.getValue((Element) (e.getElementsByTagName(KEY_ITEM_ATTRIBUTES).item(0)), KEY_EDITION));
        game.setPublicationDate(this.getValue((Element) (e.getElementsByTagName(KEY_ITEM_ATTRIBUTES).item(0)), KEY_PUBLICATION_DATE));
        game.setReleaseDate(this.getValue((Element) (e.getElementsByTagName(KEY_ITEM_ATTRIBUTES).item(0)), KEY_RELEASE_DATE));
        game.setUpcCode(this.getValue((Element) (e.getElementsByTagName(KEY_ITEM_ATTRIBUTES).item(0)), KEY_UPC_CODE));
        //Log.i("UPC", ""+game.getUpcCode());
        game.setSmallImage(this.getValue((Element) (e.getElementsByTagName(KEY_SAMLL_IMAGE).item(0))
                , KEY_IMAGE_CONTAINER));
        game.setMediumImage(this.getValue((Element) (e.getElementsByTagName(KEY_MEDIUM_IMAGE).item(0))
                , KEY_IMAGE_CONTAINER));
        game.setLargeImage(this.getValue((Element) (e.getElementsByTagName(KEY_LARGE_IMAGE).item(0))
                , KEY_IMAGE_CONTAINER));
        return game;
    }

    /**
     * Check if the response is valid.
     * @param element Element contains request.
     * @return true - response is valid, false - response is invalid.
     */
    public boolean isResponseValid(Element element) {
        NodeList nList = element.getElementsByTagName(KEY_REQUEST_ROOT);
        Element isValid = (Element) nList.item(0);//<IsValid>
        //Log.i("IsValid", ""+getValue(isValid, KEY_REQUEST_CONTAINER));
        boolean hasErrorCode = false;
        try{
            NodeList errorList = element.getElementsByTagName(KEY_ERROR);
            Element code = (Element) errorList.item(0);//<Code>
            if(code!=null) {
                //Log.i("Error code", "" + getValue(code, KEY_CODE));
                hasErrorCode = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if ((getValue(isValid, KEY_REQUEST_CONTAINER).equals(VALUE_VALID_RESPONCE)) && (!hasErrorCode)) {
            return true;
        }
        return false;
    }

    /**
     * Get contents from url.
     * @param theUrl The url contains contents.
     * @return Stringify contents.
     */
    private String getUrlContents(String theUrl) {
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL(theUrl);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()), 8);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    /**
     * Get xml document element.
     * @param xml Stringify response.
     * @return Document format response.
     */
    public Document getDomElement(String xml) {
        Document doc;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);

        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }

        return doc;
    }

    /**
     * Get element value from node list.
     * @param elem Node list target element.
     * @return Stringify element.
     */
    public final String getElementValue(Node elem) {
        Node child;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (child = elem.getFirstChild(); child != null; child = child
                        .getNextSibling()) {
                    if (child.getNodeType() == Node.TEXT_NODE
                            || (child.getNodeType() == Node.CDATA_SECTION_NODE)) {
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    /**
     * Get value by element and tag name.
     * @param item xml element.
     * @param str Tag name.
     * @return Value of element.
     */
    public String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }
}