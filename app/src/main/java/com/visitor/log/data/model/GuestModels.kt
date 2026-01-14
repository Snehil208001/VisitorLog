package com.visitor.log.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class GuestApiResponse(
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
    @SerializedName("e_pass_code") val guestId: String?,

    @SerializedName("guest_name") val name: String?,

    @SerializedName("contact_no") val mobile: String?,

    @SerializedName("pass_category") val passCategory: String?,
    @SerializedName("booking_id") val bookingId: String?,
    @SerializedName("kyc_status") val kycStatus: String?,

    @SerializedName("time") val entryTime: String?,

    @SerializedName("exit_time") val exitTime: String?
)

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

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val guestId: String,
    val prevKey: Int?,
    val nextKey: Int?
)

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