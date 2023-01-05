package net.swordcraft.server.database

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import org.bson.Document

class DatabaseStorage(
    databaseName: String = "data",
    settings: MongoClientSettings
) {
    constructor(databaseName: String = "data", connectionUrl: String = "mongodb://localhost:27017") : this(
        databaseName,
        MongoClientSettings.builder().applyConnectionString(ConnectionString(connectionUrl)).build()
    )

    val client: MongoClient = MongoClients.create(settings)
    val database = client.getDatabase(databaseName)

    fun getCollection(collectionName: String): MongoCollection<Document> = database.getCollection(collectionName)
    fun updateCollection(collectionName: String, filter: Document, data: Document) {
        getCollection(collectionName).updateOne(filter, data)
    }

    fun close() {
        client.close()
    }

    fun dropAll() {
        database.drop()
    }

    fun dropCollection(collectionName: String) {
        database.getCollection(collectionName).drop()
    }

}