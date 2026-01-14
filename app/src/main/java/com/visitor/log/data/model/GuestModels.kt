package com.visitor.log.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// 1. Network Response Models
data class GuestApiResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<GuestDto>?,
    @SerializedName("total_count") val totalCount: Int?
)

data class GuestDto(
    @SerializedName("guest_id") val guestId: String,
    @SerializedName("name") val name: String,
    @SerializedName("mobile") val mobile: String,
    @SerializedName("pass_category") val passCategory: String?, // VIP / Regular
    @SerializedName("booking_id") val bookingId: String?,
    @SerializedName("kyc_status") val kycStatus: String?, // Verified / Pending
    @SerializedName("entry_time") val entryTime: String?,
    @SerializedName("exit_time") val exitTime: String?
)

// 2. Room Entity
@Entity(tableName = "guests")
data class GuestEntity(
    @PrimaryKey val id: String, // Map to guest_id
    val name: String,
    val mobile: String,
    val passCategory: String,
    val bookingId: String,
    val kycStatus: String,
    val entryTime: String,
    val exitTime: String,
    val page: Int // Helper to track page
)

// 3. Remote Keys for Paging
@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val guestId: String,
    val prevKey: Int?,
    val nextKey: Int?
)

// Mapper Extension
fun GuestDto.toEntity(page: Int): GuestEntity {
    return GuestEntity(
        id = this.guestId,
        name = this.name,
        mobile = this.mobile,
        passCategory = this.passCategory ?: "Regular",
        bookingId = this.bookingId ?: "N/A",
        kycStatus = this.kycStatus ?: "Pending",
        entryTime = this.entryTime ?: "-",
        exitTime = this.exitTime ?: "-",
        page = page
    )
}