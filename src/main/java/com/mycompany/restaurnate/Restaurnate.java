/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.restaurnate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.util.ArrayList;
import java.util.Arrays; // Usado para Array.asList
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

/**
 *
 * @author ferch
 */
public class Restaurnate {

    public static void main(String[] args) {
        MongoClient client = MongoClients.create("mongodb://localhost:27017/");
        MongoDatabase db = client.getDatabase("restauranteBD");
        MongoCollection<Document> col = db.getCollection("restaurnate");

        Document document = new Document();

        /**
         * docuemtno anidado, dentro de otro Document documentEmbbedded = new
         * Document();
         *
         * documentEmbbedded.appendChild("atribute",1);
         */
        document.append("name", "Tacos de la Allende");
        document.append("stars", 4.5);
        col.insertOne(document);
        System.out.println("Documento 1 insertado.");

        ArrayList<Document> lista = new ArrayList<>();

        lista.add(new Document("name", "Sushilito").append("stars", 5).append("categorias", Arrays.asList("sushi", "comida china", "fast food", "bebidas")));
        

        lista.add(new Document("name", "Deshuasadero").append("stars", 3.5).append("categorias", Arrays.asList
        ("sushi", "hamburguesas", "pizza", "bebidas")));

        col.insertMany(lista);
        System.out.println("Documentos multiples insertados.");
        
        System.out.println("\n--- Busquedas ---");
        //busquedas con filtros
        System.out.println("Busqueda por nombre: Sushilito");
        for (Document d : col.find(Filters.eq("name", "Sushilito"))) {
            System.out.println(d.toJson());;
        }

        System.out.println("\nBusqueda por calificacion (> 4)");
        for (Document d : col.find(Filters.gt("stars", 4.0))) {
            System.out.println(d.toJson());;
        }

        //filtros logicos
        Bson filtro = Filters.and(Filters.gte("star", 3), Filters.lte("stars", 4));

        System.out.println("\nBusqueda por calificacion (3.0 <= x <= 4.0)");
        for (Document d : col.find(filtro)) {
            System.out.println(d.toJson());;
        }
        // busqueda dentro de Array (Filters.in)
        System.out.println("\n Busqueda por categoría (pizza O hamburguesas)");
        for (Document d : col.find(Filters.in("categorias", Arrays.asList("pizza"
        ,"hamburguesas")))){
            System.out.println(d.toJson());
        }
        
        //busqueda con regex
        System.out.println("\nBusqueda con Regex (nombre que termine con o)");
        for (Document d : col.find(Filters.regex("name", "%o"))) {
            System.out.println(d.toJson());;
        }

        //update
        col.updateOne(Filters.eq("name", "Sushilito"), Updates.set("stars", 1));
        //agregar un atributo
        col.updateOne(Filters.eq("name", "Sushilito"), Updates.set("phone", "6441005643"));

        col.updateOne(Filters.eq("_id", new ObjectId("691cffaf7dc75f3c609996ed")), Updates.inc("stars", 1));//incrementar en 1 las estrellas con ese id

        col.deleteOne(Filters.eq("_id", new ObjectId("691cffaf7dc75f3c609996ed")));
    }
}
