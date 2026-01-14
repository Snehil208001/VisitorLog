package com.visitor.log.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import com.visitor.log.data.model.GuestEntity
import com.visitor.log.data.model.RemoteKeys

@Dao
interface GuestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(guests: List<GuestEntity>)

    @Query("SELECT * FROM guests WHERE name LIKE '%' || :query || '%' OR mobile LIKE '%' || :query || '%' ORDER BY page ASC")
    fun getGuests(query: String): PagingSource<Int, GuestEntity>

    @Query("DELETE FROM guests")
    suspend fun clearGuests()
}

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeys>)

    @Query("SELECT * FROM remote_keys WHERE guestId = :guestId")
    suspend fun remoteKeysGuestId(guestId: String): RemoteKeys?

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()
}

@Database(entities = [GuestEntity::class, RemoteKeys::class], version = 1, exportSchema = false)
abstract class GuestDatabase : RoomDatabase() {
    abstract fun guestDao(): GuestDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}