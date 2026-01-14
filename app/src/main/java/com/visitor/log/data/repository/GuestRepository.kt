package com.visitor.log.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.visitor.log.data.db.GuestDatabase
import com.visitor.log.data.model.GuestEntity
import com.visitor.log.data.model.RemoteKeys
import com.visitor.log.data.model.toEntity
import com.visitor.log.data.network.GuestApiService
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class GuestRemoteMediator(
    private val query: String,
    private val apiService: GuestApiService,
    private val database: GuestDatabase
) : RemoteMediator<Int, GuestEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, GuestEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            // API Call
            val response = apiService.getGuests(page, state.config.pageSize)
            val guests = response.data ?: emptyList()
            val endOfPaginationReached = guests.isEmpty()

            database.withTransaction {
                // Clear DB if refreshing
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().clearRemoteKeys()
                    database.guestDao().clearGuests()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val keys = guests.map {
                    RemoteKeys(guestId = it.guestId, prevKey = prevKey, nextKey = nextKey)
                }
                val entities = guests.map { it.toEntity(page) }

                database.remoteKeysDao().insertAll(keys)
                database.guestDao().insertAll(entities)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, GuestEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { guest -> database.remoteKeysDao().remoteKeysGuestId(guest.id) }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, GuestEntity>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { guest -> database.remoteKeysDao().remoteKeysGuestId(guest.id) }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, GuestEntity>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { guestId ->
                database.remoteKeysDao().remoteKeysGuestId(guestId)
            }
        }
    }
}