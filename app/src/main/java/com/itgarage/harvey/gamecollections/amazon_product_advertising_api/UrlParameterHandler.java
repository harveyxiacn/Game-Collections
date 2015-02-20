package com.itgarage.harvey.gamecollections.amazon_product_advertising_api;

import java.util.HashMap;
import java.util.Map;

public class UrlParameterHandler {

    public static UrlParameterHandler paramHandler;
    private UrlParameterHandler() {}


    public static synchronized UrlParameterHandler getInstance(){
        if(paramHandler==null){
            paramHandler=new UrlParameterHandler();
            return paramHandler;
        }
        return paramHandler;
    }

    /* build the params to pass into signed requests helper's sign function as a param*/
    public Map<String,String> buildMapForItemSearch(){
        Map<String, String> myparams = new HashMap<String, String>();
        myparams.put("Service", "AWSECommerceService");
        myparams.put("Operation", "ItemSearch");
        myparams.put("Version", "2011-08-01");
        //myparams.put("ContentType", "text/xml");
        myparams.put("SearchIndex", ItemSearchArgs.SEARCH_INDEX);//for searching video games
        myparams.put("Keywords", ItemSearchArgs.KEYWORDS);
        myparams.put("AssociateTag", Keys_Tag.ASSOCIATE_TAG);// change in Keys_Tag class
        myparams.put("ResponseGroup", "Images,ItemAttributes");
        return myparams;
    }

    public Map<String,String> buildMapForItemLookUp(){
        Map<String, String> myparams = new HashMap<String, String>();
        myparams.put("Service", "AWSECommerceService");
        myparams.put("Operation", "ItemLookup");
        myparams.put("Version", "2011-08-01");
        //myparams.put("ContentType", "text/xml");
        myparams.put("SearchIndex", ItemLookupArgs.SEARCH_INDEX);//for searching video games
        myparams.put("ItemId", ItemLookupArgs.ITEM_ID);
        myparams.put("IdType", ItemLookupArgs.ID_TYPE);
        myparams.put("AssociateTag", Keys_Tag.ASSOCIATE_TAG);// change in Keys_Tag class
        myparams.put("ResponseGroup", "Images,ItemAttributes");
        return myparams;
    }

}
