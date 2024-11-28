package my.android.webrtc.model

data class LatLong(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

data class MUser (
    val id:String = "",
    val name:String = "",
    val phoneNumber:String = "",
    val email:String = "",
    val createdAt:String = "",
    val address: String = "",
    val location: LatLong = LatLong()
)

fun mUserToMap(user: MUser): Map<String, Any> {
    return mapOf(
        "id" to user.id,
        "name" to user.name,
        "phoneNumber" to user.phoneNumber,
        "email" to user.email,
        "createdAt" to user.createdAt,
        "location" to user.location.let {
            mapOf(
                "latitude" to it.latitude,
                "longitude" to it.longitude
            )
        }
    )
}