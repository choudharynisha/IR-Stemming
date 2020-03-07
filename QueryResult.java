/*
 * File = QueryResult.java
 * Date = Tuesday, February 18, 2020
 */

public class QueryResult {
    private Document document; // the document being searched on
    private double cosineSimilarity; // the corresponding cosine similarity of the document for the particular query
    
    public QueryResult(Document document, double cosineSimilarity) {
        this.document = document;
        this.cosineSimilarity = cosineSimilarity;
    } // QueryResult()
    
    public Document document() {
        return document;
    } // document()
    
    public double cosineSimilarity() {
        return cosineSimilarity;
    } // cosineSimilarity
} // QueryResult
