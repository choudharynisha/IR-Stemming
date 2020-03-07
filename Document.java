/*
 * File = Document.java
 * Date = Friday, February 14, 2020
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Document {
    private ArrayList<String> rank = new ArrayList<>(); // the unique tokens ranked in descending order by frequency
    private HashMap<String, ArrayList<Integer>> appearances = new HashMap<>(); // the document ids each token appears in
    private HashMap<String, Integer> frequencies = new HashMap<>(); // the freqencies of each token
    private HashMap<String, Double> tfidf = new HashMap<>(); // the TF-IDF value of each token
    private int id; // the document id
    private String author; // the document / article author
    private String title; // the document / article title
    
    Document(int id, ArrayList<String> rank, HashMap<String, ArrayList<Integer>> appearances, HashMap<String, Double> tfidf,
             HashMap<String, Integer> frequencies) {
        this.frequencies = frequencies;
        this.id = id;
        this.rank = rank;
        this.tfidf = tfidf;
        
        author = title = "";
    } // Document
    
    public HashMap<String, ArrayList<Integer>> allAppearances() {
        return appearances;
    } // allAppearances()
    
    public ArrayList<String> rankedList() {
        return rank;
    } // rankedList()
    
    public HashMap<String, Integer> frequencies() {
        return frequencies;
    } // frequencies()
    
    public HashMap<String, Double> tfidf() {
        return tfidf;
    } // tfidf()
    
    public String getAuthor() {
        return author;
    } // getAuthor()
    
    public String getTitle() {
        return title;
    } // getTitle()
    
    public void setAuthor(String author) {
        this.author = author;
    } // setAuthor()
    
    public void setTitle(String title) {
        this.title = title;
    } // setTitle()
    
    @Override
    public String toString() {
        return title + " by " + author;
    } // toString()
} // Document
