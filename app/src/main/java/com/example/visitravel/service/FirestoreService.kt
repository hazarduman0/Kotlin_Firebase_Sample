package com.example.visitravel.service

import com.example.visitravel.config.AppConfig
import com.example.visitravel.models.Location
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.util.*

class FirestoreService {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun addPlace(location: Location): Boolean {
        val collectionRef = db.collection("places")

        val newLocation = hashMapOf(
            "userID" to AppConfig.uid,
            "location" to location.locationName,
            "description" to location.locationDescription,
            "date" to location.date
        )

        return try {
            val documentRef = collectionRef.add(newLocation).await()
            val locationId = documentRef.id
            location.locationId = locationId
            getPlacesByUserID()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getPlacesByUserID(): MutableList<Location> {
        val collectionRef = db.collection("places")
        val locations = mutableListOf<Location>()

        try {
            val querySnapshot = collectionRef.whereEqualTo("userID", AppConfig.uid).get().await()

            for (document in querySnapshot.documents) {
                val locationId = document.id
                val locationName = document.getString("location") ?: ""
                val locationDescription = document.getString("description") ?: ""
                val date = document.getDate("date") ?: Date()

                val location = Location(locationId, locationName, locationDescription, date)
                locations.add(location)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return locations
    }

    suspend fun updatePlace(location: Location): Boolean {
        val documentRef = db.collection("places").document(location.locationId ?: "")

        val updates = hashMapOf<String, Any>(
            "location" to location.locationName,
            "description" to location.locationDescription
        )

        return try {
            documentRef.set(updates, SetOptions.merge()).await()
            getPlacesByUserID()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteLocation(locationId: String): Boolean {
        val documentRef = db.collection("places").document(locationId)

        return try {
            documentRef.delete().await()
            getPlacesByUserID()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun searchPlacesByQuery(query: String): MutableList<Location> {
        val collectionRef = db.collection("places")
        val locations = mutableListOf<Location>()

        try {
            val querySnapshot = collectionRef
                .whereGreaterThanOrEqualTo("locationName", query)
                .whereLessThanOrEqualTo("locationName", query + "\uf8ff")
                .get()
                .await()

            for (document in querySnapshot.documents) {
                val locationId = document.id
                val locationName = document.getString("locationName") ?: ""
                val locationDescription = document.getString("description") ?: ""
                val date = document.getDate("date") ?: Date()

                val location = Location(locationId, locationName, locationDescription, date)
                locations.add(location)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return locations
    }
}