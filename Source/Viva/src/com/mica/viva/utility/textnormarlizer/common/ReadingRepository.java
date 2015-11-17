/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mica.viva.utility.textnormarlizer.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lelig_000
 */
public class ReadingRepository {

    private HashMap<String, String> dictionary;//dictionary for nsw

    public ReadingRepository(File inputFile) {
        try {
            dictionary = new HashMap<String, String>();
            File[] listFile = inputFile.listFiles();
            for (int i = 0; i < listFile.length; i++) {
                File file = listFile[i];
                //System.out.println(file.getName());
                BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
                String data = "";
                while ((data = bfr.readLine()) != null) {
                    //System.out.println(data);
                    String key = data.substring(0, data.indexOf("|"));
                    String value = "";
                    if (data.lastIndexOf("|") > data.indexOf("|")) {
                        value = data.substring(data.indexOf("|") + 1, data.lastIndexOf("|"));
                    } else {
                        value = data.substring(data.indexOf("|") + 1);
                    }
//                    System.out.println(key+ " " +value);
                    dictionary.put(key, value);
                }
                bfr.close();
            }
            //System.out.println(dictionary.size());
        } catch (IOException ex) {
            Logger.getLogger(ReadingRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @return dictionary
     */
    public HashMap<String, String> getDictionary() {
        return dictionary;
    }

    public static void main(String[] args) {
        ReadingRepository rr = new ReadingRepository(new File(Constant.GENERAL_DATABASE));
    }
}
