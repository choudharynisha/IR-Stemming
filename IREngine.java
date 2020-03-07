/*
 * File = IREngine.java
 * Date = Tuesday, February 11, 2020
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.Exception;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class IREngine {
    public static void main(String[] args) {
        // ensuring that four and only four command line arguments are provided
        if(args.length < 1) {
            System.out.println("Unspecified path, number of articles, and file of queries");
            return;
        }
        
        if(args.length < 3) {
            System.out.println("Missing path, missing number of articles, missing file of queires");
            return;
        }
        
        if(args.length > 3) {
            System.out.println("Too many arguments");
            return;
        }
        
        Document[] documents = new Document[Integer.valueOf(args[1])]; // the array containing all document information
        
        for(int i = 1; i < (Integer.valueOf(args[1]) + 1); i++) {
            if((i > 261) && (i < 266)) {
                // skips missing files
                continue;
            }
            
            HashMap<String, Double> tfidf = new HashMap<>();
            
            // obtain tokens and their corresponding frequency count for the ith file, rank them, and find their appearance
            // count in the other documents
            HashMap<String, Integer> file = readFile(args[0], i); // stores unique tokens and their corresponding frequencies
            ArrayList<String> rank = rank(file); // all unique tokens in file ranked by frequency (most common = beginning)
            HashMap<String, ArrayList<Integer>> appearances = appearance(args[0], rank, Integer.valueOf(args[1]));
            int tmfc = file.get(rank.get(0)); // term frequency of the most commonly found token
            
            for(String token : rank) {
                int frequency = file.get(token); // the token's frequency in the document
                tfidf.put(token, tfidfweight(tfweight(frequency, tmfc),
                                             idfweight(Integer.valueOf(args[1]), appearances.get(token).size())));
            }
            
            Document document = new Document(i, rank, appearances, tfidf, file);
            documents[i - 1] = findTitle(args[0], i, document); // so that the printed results provide more descriptive
        }
        
        printQueryResults(findResults(getQueries(args[2]), documents));
    } // main()
    
    /**
     *  Finds the documents each specified token appears in
     *  @param path   The path of the files relative to the current directory
     *  @param list   The ArrayList with the specified tokens
     *  @param number The number of files in the directory
     *  @return The HashMap containing the token and its corresponding ArrayList of document IDs it appears in
     */
    public static HashMap<String, ArrayList<Integer>> appearance(String path, ArrayList<String> list, int number) {
        HashMap<String, ArrayList<Integer>> documents = new HashMap<>();
        
        for(String token : list) {
            ArrayList<Integer> ids = new ArrayList<>();
            
            for(int i = 1; i < (number + 1); i++) {
                // goes through every file to check if token is in the file
                HashMap<String, Integer> tokens = readFile(path, i);
                
                if(tokens.containsKey(token)) {
                    // this document contains this token
                    ids.add(i);
                }
            }
            
            documents.put(token, ids);
        }
        
        return documents;
    } // appearance()
    
    /**
     *  Finds how similar the query and a document are by computing the cosine of the angle between the query vector and the
     *  document vector to determine how relevant a document may be to the query
     *  @param query The query
     *  @param document The potentially relevant document
     *  @param total number of documents being looked at
     *  @return The QueryResult containing the cosine of the angle between the two vectors, where a cosine of 0.0 means the
     *          the document is irrelevant to the given query
     */
    public static QueryResult cosineSimilarity(String query, Document document, int total) {
        ArrayList<Double> documentVector = new ArrayList<>();
        ArrayList<Double> queryVector = new ArrayList<>();
        HashMap<String, Integer> frequencies = new HashMap<>();
        int tfmc = 0;
        String[] tokens = query.split(" ");
        
        for(int i = 0; i < tokens.length; i++) {
            // standardize each token in the query and check if they are in the provided document
            tokens[i] = standardize(tokens[i]);
            
            // computing the term frequency in the query
            if(frequencies.containsKey(tokens[i])) {
                // if the frequencies is already in the frequencies HashMap
                int fij = frequencies.get(tokens[i]);
                frequencies.put(tokens[i], ++fij);
                continue;
            }
            
            frequencies.put(tokens[i], 1); // the token was not already in the frequencies HashMap
        }
        
        // find the term frequency of the most common token for the TF Weight calculation
        for(String token : frequencies.keySet()) {
            if(frequencies.get(token) > tfmc) {
                tfmc = frequencies.get(token);
            }
        }
        
        for(String token : tokens) {
            queryVector.add(tfidfweight(tfweight(frequencies.get(token), tfmc), idfweight(total, 1)));
            
            if(document.tfidf().get(token) == null) {
                // if the token does not appear in the document
                documentVector.add(0.0);
            } else {
                documentVector.add(document.tfidf().get(token));
            }
        }
        
        double dotProduct = dotProduct(queryVector, documentVector); // stored in order to save some time per document
        
        if(dotProduct > 0.0) {
            // the document has a non-zero cosine similarity, indicating that it has some relevance to the query
            return new QueryResult(document, (dotProduct / denominator(queryVector, documentVector)));
        }
        
        return new QueryResult(document, 0.0);
    } // cosineSimilarity()
    
    /**
     *  Computes the denominator for the cosine similarity
     *  @param query    The query vector
     *  @param document The document vector
     *  @return The value of the denominator
     */
    public static double denominator(ArrayList<Double> query, ArrayList<Double> document) {
        return Math.sqrt(squaredSum(query) * squaredSum(document));
    } // denominator
    
    /**
     *  Calculates the dot product of two vectors
     *  @param query    The query vector
     *  @param document The document vector
     *  @return The dot product of one and two
     */
    public static double dotProduct(ArrayList<Double> query, ArrayList<Double> document) {
        double dotProduct = 0.0;
        
        for(int i = 0; i < query.size(); i++) {
            if(document.get(i) == null) {
                // accounts for query potentially being a bigger ArrayList than document and avoids potential
                // NullPointerExceptions
                continue;
            }
            
            dotProduct += (query.get(i) * document.get(i));
        }
        
        return dotProduct;
    } // dotProduct()
    
    /**
     *  Finds the corresponding results for all queries
     *  @param queries   The array of queries
     *  @param documents The ArrayList of documents to query
     *  @return The HashMap of each query mapped to its array of QueryResult objects as results
     */
    public static HashMap<String, ArrayList<QueryResult>> findResults(String[] queries, Document[] documents) {
        HashMap<String, ArrayList<QueryResult>> correspondingResults = new HashMap<>();
        
        for(String query : queries) {
            correspondingResults.put(query, query(query, documents));
        }
        
        return correspondingResults;
    } // findResults()
    
    /**
     *  Finds the title of the article and updates the document
     *  @param path     The path the file can be found in, relative to the current directory
     *  @param id       The id of the document
     *  @param document The document to be updated
     *  @return The updated document
     */
    public static Document findTitle(String path, int id, Document document) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path + id + ".txt"));
            
            if(id < 84) {
                // the NY Times articles
                document.setTitle(reader.readLine());
                reader.close();
                return document;
            }
            
            if(id < 176) {
                // the astrology books
                reader.readLine();
                reader.readLine();
                document.setTitle(reader.readLine());
                reader.close();
                return document;
            }
            
            for(int i = 0; i < 8; i++) {
                // skips the first 8 lines to get the title line of the horror books
                reader.readLine();
            }
            
            document.setTitle(reader.readLine().substring(7));
            reader.close();
            return document;
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return document;
    } // findTitle()
    
    /**
     *  Processes the file with each query to identify individual queries and assumes that each query is separated by a
     *  newline character
     *  @param file The file name (with the path preceding the file name)
     *  @return An array with each individual query
     */
    public static String[] getQueries(String file) {
        ArrayList<String> allQueries = new ArrayList<>();
        
        try {
            // read in each line of the file to store them
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String query = reader.readLine();
            
            while(query != null) {
                allQueries.add(query);
                query = reader.readLine();
            }
            
            reader.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        String[] queries = new String[allQueries.size()];
        
        // storing all lines that have at least one token, potentially empty elements at the end of the array
        for(int i = 0; i < queries.length; i++) {
            queries[i] = allQueries.get(i);
        }
        
        return queries;
    } // getQueries()
    
    /**
     *  For each line, a new, unique word gets added to the HashMap or the number of occurrences is incremented.
     *  @param lines An ArrayList of strings (where each string is a line)
     *  @return A linked hashmap with all tokens and their corresponding number of occurrences
     */
    public static HashMap<String, Integer> getTokenCount(ArrayList<String> lines) {
        HashMap<String, Integer> occurrences = new HashMap<>();
        
        for(String line : lines) {
            String[] tokens = line.split(" ");
            
            for(String token : tokens) {
                token = standardize(token);
                
                if(token == null) {
                    // avoids a potential NullPointerException
                    continue;
                }
                
                if(occurrences.containsKey(token)) {
                    int value = occurrences.get(token);
                    occurrences.replace(token, value, (value + 1));
                    continue;
                }
                
                occurrences.put(token, 1);
            }
        }
        
        return occurrences;
    } // getTokenCount()
    
    /**
     *  Computes the inverted document frequency (IDF) weight of a specified token, where more frequent tokens are given a
     *  smaller IDF weight and less frequent tokens are given higher IDF weight
     *  @param total      The total number of documents in the collection
     *  @param containing The total number of documents the specified token is in
     *  @return The IDF weight
     */
    public static double idfweight(int total, int containing) {
        return Math.log((double) total / containing) / Math.log(2);
    } // idfweight()
    
    /**
     *  Prints queries and their corresponding results
     *  @param results The corresponding results for each query, stored in an ArrayList of QueryResult objects
     */
    public static void printQueryResults(HashMap<String, ArrayList<QueryResult>> results) {
        for(String query : results.keySet()) {
            System.out.println(query);
            
            ArrayList<QueryResult> documents = sort(results.get(query));
            
            for(QueryResult result : documents) {
                System.out.println(result.document().toString() + " " + result.cosineSimilarity());
            }
            
            System.out.println();
        }
    } // printQueryResults()
    
    /**
     *  Finds articles / results based on queries
     *  @param query The query
     *  @param documents The documents to query
     *  @return The QueryResult ArrayList of files that are relevant to the query
     */
    public static ArrayList<QueryResult> query(String query, Document[] documents) {
        ArrayList<QueryResult> files = new ArrayList<>();
        
        for(int i = 0; i < documents.length; i++) {
            // check if the document has any relevance to the query
            QueryResult result = cosineSimilarity(query, documents[i], documents.length);
            
            if(result.cosineSimilarity() > 0.0) {
                // the document has some relevance to the query
                files.add(result);
            }
        }
        
        return files;
    } // query()
    
    /**
     *  Sorts an array of QueryResult objects using the quicksort algorithm in ascending order by cosine similarity
     *  @param results The array of QueryResult objects
     *  @param left The index of the left-bound of the subarray to perform quicksort on
     *  @param right The index of the right-bound of the subarray to perform quicksort on
     */
    public static void quicksort(QueryResult[] results, int left, int right) {
        if(left >= right) {
            return;
        }
        
        // pivot is a random element between results[left] (inclusive) and results[right]
        int random = (int)(Math.random() * (right - left)) + left;
        swap(results, left, random);
        int middle = left;
        
        for(int i = (left + 1); i <= right; i++) {
            // take all of the articles with cosine similarities less than that of the article at the pivot and put them to
            // the left of the pivot
            if((results[i].cosineSimilarity() < results[left].cosineSimilarity()) && (middle > -1)) {
                swap(results, ++middle, i);
            }
        }
        
        swap(results, left, middle); // results is now partitioned at results[middle]
        quicksort(results, left, (middle - 1)); // recurse on the left half of the array
        quicksort(results, (middle + 1), right); // recurse on the right half of the array
    } // quicksort()
    
    /**
     *  Rank the words by frequency
     *  @param map The HashMap, currently unordered
     *  @return The ArrayList with tokens ordered by most frequent to least frequent
     */
    public static ArrayList<String> rank(HashMap<String, Integer> map) {
        ArrayList<String> tokens = new ArrayList<>();
        ArrayList<String> reversed = new ArrayList<>();
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(map.entrySet());
        
        // sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() { 
            public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) { 
                return (entry1.getValue()).compareTo(entry2.getValue()); 
            }
        });
        
        // put data from sorted list to the hashmap
        for(Map.Entry<String, Integer> entry : list) {
            tokens.add(entry.getKey());
        }
        
        // reverse the words by storing them into the reversed ArrayList by iterating starting from the back
        for(int i = (tokens.size() - 1); i > -1; i--) {
            reversed.add(tokens.get(i));
        }
        
        return reversed;
    } // rank()
    
    /**
     *  Reads in the lines of a specific file
     *  @param path   The path of the files relative to the current directory
     *  @param number The number of files to be read in (from 1 to number, inclusive)
     *  @return A linked hashmap with the pairs of keys (unique words) and values (the number of times each unique word
     *          appears in the specified file provided)
     */
    public static HashMap<String, Integer> readFile(String path, int number) {
        ArrayList<String> lines = new ArrayList<>();
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path + number + ".txt"));
            String line = reader.readLine();
            
            while(line != null) {
                lines.add(line);
                line = reader.readLine();
            }
            
            reader.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return getTokenCount(lines);
    } // readFile()
    
    /**
     *  Sorts an ArrayList of QueryResult objects by their cosine similarity in descending order
     *  @param list The ArrayList of QueryResults
     *  @return The ArrayList of QueryResult objects sorted in descending order by their cosine similarity
     */
    public static ArrayList<QueryResult> sort(ArrayList<QueryResult> list) {
        QueryResult[] results = new QueryResult[list.size()];
        
        for(int i = 0; i < list.size(); i++) {
            results[i] = list.get(i);
        }
        
        quicksort(results, 0, (results.length - 1));
        
        list.clear();
        
        for(int i = (results.length - 1); i > -1; i--) {
            // iterate from the end of the array to the beginning so that results can be printed with the highest cosine
            // similarity value first
            list.add(results[i]);
        }
        
        return list;
    } // sort()
    
    /**
     *  Computes the sum of all squared values of a vector
     *  @param vector The ArrayList of TF-IDF weights
     *  @return The computed sum of all squared values in vector
     */
    public static double squaredSum(ArrayList<Double> vector) {
        double sum = 0.0;
        
        for(double tfidf : vector) {
            sum += (tfidf * tfidf);
        }
        
        return sum;
    } // squaredSum()
    
    /**
     *  Makes every character in each token lowercase and removes any trailing punctuation that may be present
     *  @param token The original token
     *  @return The standardized token
     */
    public static String standardize(String token) {
        if((token.length() > 0) && ((token.charAt(0) == '“') || (token.charAt(0) == '('))) {
            token = token.substring(1);
        }

        if((token.length() < 1) || (token.equals("—"))) {
            return null;
        }
        
        token = token.toLowerCase();
        
        // removing apostrophes in contractions and ellipsis mark
        if((token.length() > 2) && (token.substring(token.length() - 3).equals("..."))) {
            token = token.substring(0, token.length() - 3);
        } else if((token.length() > 2) && (token.substring(token.length() - 3).equals("’ve"))) {
            token = token.substring(0, token.length() - 3) + "ve";
        } else if((token.length() > 2) && (token.substring(token.length() - 3).equals("’re"))) {
            token = token.substring(0, token.length() - 3) + "re";
        } else if((token.length() > 1) && (token.substring(token.length() - 2).equals("’s"))) {
            token = token.substring(0, token.length() - 2) + "s";
        } else if((token.length() > 1) && (token.substring(token.length() - 2).equals("’d"))) {
            token = token.substring(0, token.length() - 2) + "d";
        } else if((token.length() > 1) && (token.substring(token.length() - 2).equals("’t"))) {
            token = token.substring(0, token.length() - 2) + "t";
        } else if((token.length() > 1) && (token.substring(token.length() - 2).equals("’m"))) {
            token = token.substring(0, token.length() - 2) + "m";
        }
        
        // removes all trailing punctuation
        if(token.endsWith(".")) {
            token = token.substring(0, token.length() - 1);
        } else if(token.endsWith(",")) {
            token = token.substring(0, token.length() - 1);
        } else if(token.endsWith("?")) {
            token = token.substring(0, token.length() - 1);
        } else if(token.endsWith("!")) {
            token = token.substring(0, token.length() - 1);
        } else if(token.endsWith(";")) {
            token = token.substring(0, token.length() - 1);
        } else if(token.endsWith("'")) {
            token = token.substring(0, token.length() - 1);
        } else if(token.endsWith(":")) {
            token = token.substring(0, token.length() - 1);
        } else if(token.endsWith("”")) {
            token = token.substring(0, token.length() - 1);
        } else if(token.endsWith(".)")) {
            token = token.substring(0, token.length() - 2);
        }
        
        return token;
    } // standardize()
    
    /**
     *  Swaps two elements in a QueryResult array at specified indices
     *  @param array The QueryResult array
     *  @param one The index of one of the elements to swap
     *  @param two The index of the other element to swap
     */
    public static void swap(QueryResult[] array, int left, int right) {
        QueryResult result = array[left];
        array[left] = array[right];
        array[right] = result;
    } // swap()
    
    /**
     *  Computes the term frequency-inverted document frequency (TF-IDF) weight of a specified token
     *  @param tf  The TF weight of a specified token
     *  @param idf The IDF weight of a specified token
     *  @return The TF-IDF weight
     */
    public static double tfidfweight(double tf, double idf) {
        return tf * idf;
    } // tfidfweight()
    
    /**
     *  Computes the term frequency (TF) weight of a specified token, which represents how often a particular token appears,
     *  relative to other tokens in the same document
     *  @param tf   How frequent a particular token
     *  @param tfmc The token frequency of the most commonly found token in the document
     *  @return The TF weight
     */
    public static double tfweight(int tf, int tfmc) {
        return ((double) tf) / tfmc;
    } // tfweight()
} // IREngine
