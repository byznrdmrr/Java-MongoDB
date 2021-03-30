package com.sau.tasarım;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.sf.json.JSONSerializer;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

//import net.sf.json.JSONArray;

public class ConnectionMongoDB {

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONArray readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONArray json = new JSONArray(jsonText);
            return json;
        } finally {
            is.close();
        }

    }

    public static void main(String[] args) throws IOException,JSONException  {
        MongoClient client = new MongoClient("localhost",27017); //mongodb'ye bağlanmak için client oluşturuldu.
        MongoDatabase cveDB = client.getDatabase("CVE");
        //cveDB.createCollection("Vulnerabilities");
        MongoCollection<Document> vulnerabilitiesCollection = cveDB.getCollection("Vulnerabilities");

        JSONArray json = readJsonFromUrl("https://access.redhat.com/hydra/rest/securitydata/cve.json?after=1999-01-01&per_page=50000");

        List<Document> jsonList = new ArrayList<Document>();
        net.sf.json.JSONArray array = net.sf.json.JSONArray.fromObject(json.toString());

        for (Object object : array) {
            net.sf.json.JSONObject jsonStr = (net.sf.json.JSONObject) JSONSerializer.toJSON(object);
            Document jsnObject = Document.parse(jsonStr.toString());
            jsonList.add(jsnObject);
        }
        vulnerabilitiesCollection.insertMany(jsonList);

    }
}
