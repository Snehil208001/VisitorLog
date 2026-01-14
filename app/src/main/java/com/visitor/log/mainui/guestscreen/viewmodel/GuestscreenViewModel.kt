package com.visitor.log.mainui.guestscreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.visitor.log.data.db.GuestDatabase
import com.visitor.log.data.model.GuestEntity
import com.visitor.log.data.network.GuestApiService
import com.visitor.log.data.repository.GuestRemoteMediator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class GuestscreenViewModel @Inject constructor(
    private val apiService: GuestApiService,
    private val database: GuestDatabase
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    private val _totalCount = MutableStateFlow(0)
    val totalCount: StateFlow<Int> = _totalCount.asStateFlow()

    @OptIn(ExperimentalPagingApi::class)
    val guestPagingFlow: Flow<PagingData<GuestEntity>> = searchQuery.flatMapLatest { query ->
        Pager(
            config = PagingConfig(pageSize = 5, prefetchDistance = 1),
            remoteMediator = GuestRemoteMediator(
                query = query,
                apiService = apiService,
                database = database,
                onTotalCountUpdated = { count ->
                    _totalCount.value = count
                }
            ),
            pagingSourceFactory = { database.guestDao().getGuests(query) }
        ).flow
    }.cachedIn(viewModelScope)

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }
}