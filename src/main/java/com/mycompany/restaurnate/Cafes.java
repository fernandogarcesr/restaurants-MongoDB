package com.mycompany.restaurnate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.inc;
import static com.mongodb.client.model.Updates.set;
import java.util.Arrays;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 *
 * @author ferch
 */
public class Cafes {

    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "restauranteBD";
    private static final String COLLECTION_NAME = "cafes";

    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create(CONNECTION_STRING)) {
            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            MongoCollection<Document> cafesCollection = database.getCollection(COLLECTION_NAME);

            System.out.println("--- 1. INSERCIONES DE DOCUMENTOS ---");
            insertarDocumentos(cafesCollection);

            System.out.println("\n--- 2. FILTROS DE BUSQUEDA ---");
            ejecutarFiltros(cafesCollection);

            System.out.println("\n--- 3. UPDATES (ACTUALIZACIONES) ---");
            ejecutarUpdates(cafesCollection);

            System.out.println("\n--- 4. DELETES (ELIMINACIONES) ---");
            ejecutarDeletes(cafesCollection);

        } catch (Exception e) {
            System.err.println("Error de conexion o durante la ejecucion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // insersiones
    private static void insertarDocumentos(MongoCollection<Document> collection) {
        // insertar un solo documento: "Cafe de la Plaza"
        Document cafeDeLaPlaza = new Document("name", "Cafe de la Plaza")
                .append("stars", 4.3)
                .append("categories", Arrays.asList("Cafe", "Postres", "Desayuno"));

        collection.insertOne(cafeDeLaPlaza);
        System.out.println("-> 'Cafe de la Plaza' insertado.");

        // 2. Insertar varios documentos adicionales
        List<Document> nuevosCafes = Arrays.asList(
                new Document("name", "Espresso Express")
                        .append("stars", 4.8)
                        .append("categories", Arrays.asList("Cafe", "Rapido", "Takeaway")),
                new Document("name", "The Tea House")
                        .append("stars", 3.9)
                        .append("categories", Arrays.asList("Te", "Infusiones", "Postres")),
                new Document("name", "Morning Brew")
                        .append("stars", 4.0)
                        .append("categories", Arrays.asList("Cafe", "Desayuno", "Bakery"))
        );

        collection.insertMany(nuevosCafes);
        System.out.println("-> Tres documentos adicionales insertados.");
    }

    // filtros
    private static void ejecutarFiltros(MongoCollection<Document> collection) {
        // Documentos con stars >= 4.5.
        Bson filtro1 = gte("stars", 4.5);
        System.out.println("\n--- Busqueda: Estrellas >= 4.5 ---");
        collection.find(filtro1).forEach(System.out::println);

        // Documentos cuyo nombre contiene "Cafe".
        Bson filtro2 = regex("name", "Cafe");
        System.out.println("\n--- Busqueda: Nombre contiene 'Cafe' ---");
        collection.find(filtro2).forEach(System.out::println);

        // Documentos con categories que incluyan "Postres".
        Bson filtro3 = eq("categories", "Postres");
        System.out.println("\n--- Busqueda: Categoria incluye 'Postres' ---");
        collection.find(filtro3).forEach(System.out::println);

        // Documentos con stars entre 3 y 4.3 
        Bson filtro4 = and(gte("stars", 3.0), lte("stars", 4.3));
        System.out.println("\n--- Busqueda: Estrellas entre 3.0 y 4.3 ---");
        collection.find(filtro4).forEach(System.out::println);

        // Documentos cuyo nombre empieza con "T".
        Bson filtro5 = regex("name", "^T");
        System.out.println("\n--- Busqueda: Nombre empieza con 'T' ---");
        collection.find(filtro5).forEach(System.out::println);
    }

    // updates
    private static void ejecutarUpdates(MongoCollection<Document> collection) {
        // Cambiar stars a 4.5 para "Morning Brew".
        Bson filtroUpdate1 = eq("name", "Morning Brew");
        Bson update1 = set("stars", 4.5);
        collection.updateOne(filtroUpdate1, update1);
        System.out.println("-> Update: 'Morning Brew' cambiado a 4.5 estrellas.");

        // incrementar stars +0.2 para documentos con categories que contengan "Bakery" o "Desayuno".
        Bson filtroUpdate2 = or(eq("categories", "Bakery"), eq("categories", "Desayuno"));
        Bson update2 = inc("stars", 0.2); // inc = incrementar
        long count2 = collection.updateMany(filtroUpdate2, update2).getModifiedCount();
        System.out.printf("-> Update: Se incrementaron 0.2 estrellas a %d documentos.\n", count2);

        // agregar campos phone = "555-111-2222" y open = true a "Cafe de la Plaza".
        Bson filtroUpdate3 = eq("name", "Cafe de la Plaza");
        Bson update3 = combine(set("phone", "555-111-2222"), set("open", true));
        collection.updateOne(filtroUpdate3, update3);
        System.out.println("-> Update: 'Cafe de la Plaza' recibio telefono y estado 'open'.");
    }

    // deletes
    private static void ejecutarDeletes(MongoCollection<Document> collection) {
        // eliminar documento con name = "Espresso Express".
        Bson filtroDelete1 = eq("name", "Espresso Express");
        long count1 = collection.deleteOne(filtroDelete1).getDeletedCount();
        System.out.printf("-> Delete: Se elimino %d documento con nombre 'Espresso Express'.\n", count1);

        // eliminar todos los documentos con stars < 4.
        Bson filtroDelete2 = lt("stars", 4.0);
        long count2 = collection.deleteMany(filtroDelete2).getDeletedCount();
        System.out.printf("-> Delete: Se eliminaron %d documentos con menos de 4.0 estrellas.\n", count2);

        // Eliminar documentos con categories que contengan "Takeaway" o "Infusiones".
        Bson filtroDelete3 = or(eq("categories", "Takeaway"), eq("categories", "Infusiones"));
        long count3 = collection.deleteMany(filtroDelete3).getDeletedCount();
        System.out.printf("-> Delete: Se eliminaron %d documentos con categorias 'Takeaway' o 'Infusiones'.\n", count3);

    }
}
