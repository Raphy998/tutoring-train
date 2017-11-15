/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.error;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.HashMap;

/**
 *
 * @author Elias
 */
public class ErrorMessageProvider {
    private static ErrorMessageProvider instance = null;
    
    private HashMap<Language, HashMap<Integer, String>> errorMsgs;
    
    private ErrorMessageProvider() {
        errorMsgs = new HashMap<>();
        loadMsgsFromFile(Language.getDefault());
    }
    
    public static ErrorMessageProvider getInstance() {
        if (instance == null) {
            instance = new ErrorMessageProvider();
        }
        return instance;
    }
    
    private void loadMsgsFromFile(Language lang) {
        ObjectMapper mapper = new ObjectMapper();
        //Get file from resources folder
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource(getFileName(lang)).getFile());

            JsonNode jsonTree = mapper.readTree(file);
            HashMap<Integer, String> localizedMsgs = new HashMap<>();
            
            for (int i=1; i<= Error.getMaxErrorCode(); i++) {
                localizedMsgs.put(i, jsonTree.get(String.valueOf(i)).asText());
            }
            errorMsgs.remove(lang);
            errorMsgs.put(lang, localizedMsgs);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private String getFileName(Language lang) {
        return "ErrorMessages/err_" + lang.name().toLowerCase() + ".json";
    }
    
    public String get(int errorCode, Language lang) {
        String errorMsg = null;
        
        HashMap<Integer, String> localizedErrorMsgs = errorMsgs.get(lang);
        if (localizedErrorMsgs == null) {   //if msgs not found
            loadMsgsFromFile(lang);         //try to load them from file
            localizedErrorMsgs = errorMsgs.get(lang);
            if (localizedErrorMsgs == null) {           //if still not found
                localizedErrorMsgs = errorMsgs.get(Language.getDefault());   //use default
            }
        }
        
        if (localizedErrorMsgs != null) {
            errorMsg = localizedErrorMsgs.get(errorCode);
        }
        
        return errorMsg;
    }
}
