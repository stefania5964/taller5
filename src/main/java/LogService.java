import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import static spark.Spark.*;
public class LogService {
    private static MongoClient mongo;

    public static void main(String[] args) {

        port(getPort());

        get("/", (req, res) -> {
            res.type("application/json");
            Gson gson = new Gson();
            return gson.toJson(getLogs());
        });

        post("/", (req, res) -> {
            res.type("application/json");
            insertLog(req.body());
            Gson gson = new Gson();
            return gson.toJson(getLogs());
        });

    }
    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 35001;
    }

    public static List<Document> getLogs() {
        mongo = new MongoClient("172.31.86.79", 27017);
        MongoDatabase db = mongo.getDatabase("admin");
        MongoCollection<Document> collection = db.getCollection("Logs");

        List<Document> documents = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().limit(10).sort(Sorts.descending("createdAt")).iterator()) {
            while (cursor.hasNext()) {
                documents.add(cursor.next());
            }
        }

        System.out.println(documents + "\n");
        mongo.close();
        return documents;
    }

    public static void insertLog(String body) {
        mongo = new MongoClient( "172.31.86.79" , 27017);
        MongoDatabase db = mongo.getDatabase("admin");

        MongoCollection<Document> collection = db.getCollection("Logs");

        System.out.println(body);
        Document document = Document.parse(body);
        System.out.println("POSTEANDO");
        document.append("createdAt", new Date());

        collection.insertOne(document);
    }



}
