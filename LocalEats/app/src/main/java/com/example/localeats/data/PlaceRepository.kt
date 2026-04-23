package com.example.localeats.data

import android.util.Log
import com.example.localeats.model.PlaceDetails
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class PlaceRepository {
    private val client = OkHttpClient()

    fun fetchPlaceDetails(
        apiKey: String,
        placeId: String,
        callback: (PlaceDetails?) -> Unit
    ) {

        val url = "https://places.googleapis.com/v1/places/$placeId"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("X-Goog-Api-Key", apiKey)
            .addHeader(
                "X-Goog-FieldMask",
                "displayName,formattedAddress,location"
            )
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                // Log.e("PLACE DETAILS", "Response: ${response.code} - ${response.body}")
                val json = JSONObject(response.body!!.string())
                Log.d("API_BODY", json.toString())
                val name = json.optJSONObject("displayName")
                    ?.optString("text") ?: ""

                val address = json.optString("formattedAddress")

                val location = json.optJSONObject("location")

                val latLng = location?.let {
                    LatLng(
                        it.getDouble("latitude"),
                        it.getDouble("longitude")
                    )
                }

                callback(
                    PlaceDetails(
                        id = placeId,
                        name = name,
                        address = address,
                        latLng = latLng
                    )
                )
            }
        })
    }

    fun getNearbyPlaces(
        apiKey: String,
        lat: Double,
        lng: Double,
        radius: Int = 1500,
        callback: (List<Place>) -> Unit
    ) {
        val url = "https://places.googleapis.com/v1/places:searchNearby"

        val bodyJson = JSONObject().apply {
            put("includedTypes", "restaurant")
            put("maxResultCount", 20)

            put("locationRestriction", JSONObject().apply {
                put("circle", JSONObject().apply {
                    put("center", JSONObject().apply {
                        put("latitude", lat)
                        put("longitude", lng)
                    })
                    put("radius", radius.toDouble())
                })
            })
        }

        val request = Request.Builder()
            .url(url)
            .post(bodyJson.toString().toRequestBody("application/json".toMediaType()))
            .addHeader("X-Goog-Api-Key", apiKey)
            .addHeader("Content-Type", "application/json")
            .addHeader(
                "X-Goog-FieldMask",
                "places.id,places.displayName,places.location,places.formattedAddress"
            )
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                // Log.e("PlaceRepo", "Nearby failed", e)
                callback(emptyList())
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val body = response.body?.string() ?: run {
                        callback(emptyList()); return
                    }

                    // Log.e("PlaceRepo", "code: ${response.code} - body: $body")
                    val json = JSONObject(body)
                    val placesArray = json.optJSONArray("places") ?: run {
                        callback(emptyList()); return
                    }

                    val results = mutableListOf<Place>()

                    for (i in 0 until placesArray.length()) {
                        val obj = placesArray.getJSONObject(i)

                        val id = obj.optString("id")

                        val name = obj.optJSONObject("displayName")
                            ?.optString("text") ?: ""

                        val loc = obj.optJSONObject("location")

                        val latLng = loc?.let {
                            LatLng(
                                it.getDouble("latitude"),
                                it.getDouble("longitude")
                            )
                        }

                        val address = obj.optString("formattedAddress")

                        val place = Place.builder()
                            .setId(id)
                            .setName(name)
                            .setLatLng(latLng)
                            .setAddress(address)
                            .build()

                        results.add(place)
                    }

                    callback(results)

                } catch (e: Exception) {
                    // Log.e("PlaceRepo", "Parse error", e)
                    callback(emptyList())
                }
            }
        })
    }

    // -------------------------
    // AUTOCOMPLETE SEARCH
    // -------------------------
    fun searchPlaces(
        apiKey: String,
        query: String,
        callback: (List<Place>) -> Unit
    ) {
        val url = "https://places.googleapis.com/v1/places:autocomplete"

        val json = JSONObject().apply {
            put("input", query)
        }

        val request = Request.Builder()
            .url(url)
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .addHeader("X-Goog-Api-Key", apiKey)
            .addHeader(
                "X-Goog-FieldMask",
                "suggestions.placePrediction.placeId,suggestions.placePrediction.text.text" )
            .build()
// ,suggestions.placePrediction.formattedAddress"

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                callback(emptyList())
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyStr = response.body?.string() ?: return
                val jsonResponse = JSONObject(bodyStr)
                val suggestions = jsonResponse.optJSONArray("suggestions") ?: return

                val results = mutableListOf<Place>()

                for (i in 0 until suggestions.length()) {
                    val item = suggestions.getJSONObject(i)
                    val prediction = item.getJSONObject("placePrediction")

                    val id = prediction.optString("placeId")
                    val name = prediction
                        .getJSONObject("text")
                        .optString("text")

                    // val address = prediction.optString("formattedAddress")

                    val place = Place.builder()
                        .setId(id)
                        .setName(name)
                        // .setAddress(address)
                        .build()

                    results.add(place)
                }

                callback(results)
            }
        })
    }
}