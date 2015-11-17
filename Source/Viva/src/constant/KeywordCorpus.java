/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package constant;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Truy Thu
 */
public class KeywordCorpus {
    private Set<String> place;
    private Set<String> time;
    private Set<String> person;
    private Set<String> relation;
    
    public KeywordCorpus() {
        FileInputStream is;
        place = new HashSet<String>();
        try {
            is = new FileInputStream(System.getProperty("user.dir/assets/Data")+"/places.txt");
            Scanner input = new Scanner(is, "UTF-8");//Ä‘á»�c theo báº£ng mÃ£ utf-8
            while (input.hasNext()) place.add(input.nextLine());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(KeywordCorpus.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        time = new HashSet<String>();
        try {
            is = new FileInputStream(System.getProperty("user.dir/assets/Data")+"/time.txt");
            Scanner input = new Scanner(is, "UTF-8");//Ä‘á»�c theo báº£ng mÃ£ utf-8
            while (input.hasNext()) time.add(input.nextLine());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(KeywordCorpus.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        person = new HashSet<String>();
        try {
            is = new FileInputStream(System.getProperty("user.dir/assets/Data")+"/person.txt");
            Scanner input = new Scanner(is, "UTF-8");//Ä‘á»�c theo báº£ng mÃ£ utf-8
            while (input.hasNext()) person.add(input.nextLine().toLowerCase());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(KeywordCorpus.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        relation = new HashSet<String>();
        try {
            is = new FileInputStream(System.getProperty("user.dir/assets/Data")+"/relation.txt");
            Scanner input = new Scanner(is, "UTF-8");//Ä‘á»�c theo báº£ng mÃ£ utf-8
            while (input.hasNext()) relation.add(input.nextLine());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(KeywordCorpus.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public boolean checkTimeKeyword(String key) {
        return time.contains(key);
    }
    
    public boolean checkPlace(String _place) {
        return place.contains(_place);
    }
    
    public void printTime() {
        Iterator it = time.iterator();
        
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }
    
    public boolean checkRelation(String key) {
        return relation.contains(key);
    }
    
    public int checkPerson(String _person) {
        Iterator it = person.iterator();
        String name;
        
        while (it.hasNext()) {
            name = (String) it.next();
            if (name.equals(_person)) return 1;
            if (name.indexOf(_person)!=-1) return 0;
        }
        return -1;
    }
    
}
