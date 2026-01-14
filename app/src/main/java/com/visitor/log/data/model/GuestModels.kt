package com.visitor.log.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// 1. Network Response Models
data class GuestApiResponse(
    // API returns "error_code" (Int), not "status" (Boolean)
    @SerializedName("error_code") val errorCode: Int?,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: List<GuestDto>?,
    @SerializedName("pagination") val pagination: PaginationDto?
)

data class PaginationDto(
    @SerializedName("page") val page: Int,
    @SerializedName("limit") val limit: Int,
    @SerializedName("total_records") val totalRecords: Int,
    @SerializedName("total_pages") val totalPages: Int
)

data class GuestDto(
    // Map "e_pass_code" from API to our "guestId"
    @SerializedName("e_pass_code") val guestId: String?,

    // Map "guest_name" from API to our "name"
    @SerializedName("guest_name") val name: String?,

    // Map "contact_no" from API to our "mobile"
    @SerializedName("contact_no") val mobile: String?,

    @SerializedName("pass_category") val passCategory: String?,
    @SerializedName("booking_id") val bookingId: String?,
    @SerializedName("kyc_status") val kycStatus: String?,

    // Map "time" from API to our "entryTime"
    @SerializedName("time") val entryTime: String?,

    // The API does not provide "exit_time" in the main object,
    // it's inside "entered_exit_guest_list". We can leave this null for now.
    @SerializedName("exit_time") val exitTime: String?
)

// 2. Room Entity
@Entity(tableName = "guests")
data class GuestEntity(
    @PrimaryKey val id: String,
    val name: String,
    val mobile: String,
    val passCategory: String,
    val bookingId: String,
    val kycStatus: String,
    val entryTime: String,
    val exitTime: String,
    val page: Int
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
        id = this.guestId ?: "UNKNOWN_ID_${System.currentTimeMillis()}", // Fallback ID if null
        name = this.name ?: "Unknown Name",
        mobile = this.mobile ?: "No Mobile",
        passCategory = this.passCategory ?: "Regular",
        bookingId = this.bookingId ?: "N/A",
        kycStatus = this.kycStatus ?: "Pending",
        entryTime = this.entryTime ?: "-",
        exitTime = this.exitTime ?: "-",
        page = page
    )
}