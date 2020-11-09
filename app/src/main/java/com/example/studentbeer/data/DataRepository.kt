package com.example.studentbeer.data

import android.util.Log
import com.example.studentbeer.data.models.BarModel
import com.google.firebase.firestore.FirebaseFirestore


class DataRepository {
    // Tags For Log
    private val successTag: String = "!!!"
    private val errorTag: String = "@@@"

    // FireStore
    private val db = FirebaseFirestore.getInstance();

    private fun convertBarModelToHashMap(bar: BarModel): HashMap<String, Any> {
        return hashMapOf<String, Any>(
            "barName" to bar.barName,
            "latitude" to bar.latitude,
            "longitude" to bar.longitude,
            "streetName" to bar.streetName,
            "openHours" to bar.openHours,
            "rating" to bar.rating,
            "beerPrice" to bar.beerPrice,
            "id" to bar.id
        )
    }

    // Add all the data in TempSingleTon to FireStore, DO NOT CALL THIS FUNCTION.
    fun addTempData() {
        try {
            for (bar in TempSingleTon.bars) {
                db.collection("bars").add(convertBarModelToHashMap(bar)).addOnSuccessListener {
                    Log.d(successTag, "Document Successfully added")
                    addIdToTempData(it.id)
                }.addOnFailureListener {
                    Log.w(errorTag, "Error adding document")
                }
            }
        } catch (event: Exception) {
            Log.d(errorTag, "Something went wrong! func {addTempData} $event")
        }
    }

    // Add document id to bars
    private fun addIdToTempData(id: String) {
        try {
            db.collection("bars").document(id).update("id", id).addOnSuccessListener {
                Log.d(successTag, "Document Successfully changed")
            }.addOnFailureListener {
                Log.w(errorTag, "Error changing document", it)
            }
        } catch (event: Exception) {
            Log.d(errorTag, "Something went wrong! func {addIdToTempData} $event")
        }
    }

    // Read a bar
    fun getDocumentById(id: String): BarModel? {
        var bar: BarModel? = null
        try {
            db.collection("bars").document(id).get().addOnSuccessListener {
                bar = it.toObject(BarModel::class.java)
                Log.d(successTag, "Document Successfully fetched")
            }.addOnFailureListener {
                Log.w(errorTag, "Error fetching document", it)
            }
        } catch (event: Exception) {
            Log.d(errorTag, "Something went wrong! func {getDocumentById} $event")
        }
        return bar
    }

    // read all data from FireStore, collection "bars"

    fun getAllBars(): MutableList<BarModel> {
        val bars: MutableList<BarModel> = mutableListOf()
        try {
            db.collection("bars").get().addOnSuccessListener {
                it.forEach { queryDocument ->
                    bars.add(queryDocument.toObject(BarModel::class.java))
                }
                Log.d(successTag, "Documents Successfully fetched")
            }.addOnFailureListener {
                Log.w(errorTag, "Error fetching documents", it)
            }
        } catch (event: Exception) {
            Log.d(errorTag, "Something went wrong! func {getAllBars} $event")
        }
        return bars
    }
}