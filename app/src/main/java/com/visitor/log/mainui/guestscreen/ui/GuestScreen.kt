package com.visitor.log.mainui.guestscreen.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.visitor.log.data.model.GuestEntity
import com.visitor.log.mainui.guestscreen.viewmodel.GuestscreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestScreen(
    viewModel: GuestscreenViewModel = hiltViewModel()
) {
    val guests = viewModel.guestPagingFlow.collectAsLazyPagingItems()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val context = LocalContext.current

    // Error Handling Toast
    LaunchedEffect(guests.loadState) {
        if (guests.loadState.refresh is LoadState.Error) {
            Toast.makeText(context, "Error loading data", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(bottom = 8.dp)
            ) {
                CenterAlignedTopAppBar(
                    title = { Text("Visitor List", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = {}) { Icon(Icons.Filled.FilterList, contentDescription = null) }
                        IconButton(onClick = {}) { Icon(Icons.Filled.Notifications, contentDescription = null) }
                    }
                )
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = { Text("Search name, phone...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedIndicatorColor = Color.LightGray,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = Color(0xFF135bec), // Primary Blue from HTML
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Guest")
            }
        }
    ) { innerPadding ->

        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (guests.loadState.refresh is LoadState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (guests.itemCount == 0 && guests.loadState.refresh !is LoadState.Loading) {
                // Empty State
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Outlined.Block, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                    Text("No guest data available", color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF6F6F8)), // Light bg from HTML
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        count = guests.itemCount,
                        key = guests.itemKey { it.id },
                        contentType = guests.itemContentType { "guest" }
                    ) { index ->
                        val guest = guests[index]
                        if (guest != null) {
                            GuestCard(guest)
                        }
                    }

                    if (guests.loadState.append is LoadState.Loading) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GuestCard(guest: GuestEntity) {
    val isVip = guest.passCategory.equals("VIP", ignoreCase = true)
    val isVerified = guest.kycStatus.equals("Verified", ignoreCase = true)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = guest.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111318)
                    )
                    Text(
                        text = "Global Tech Summit 2024",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // VIP/Category Chip
                Surface(
                    color = if (isVip) Color(0xFFFFF8E1) else Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(100),
                    border = if (isVip) BorderStroke(1.dp, Color(0xFFFFE082)) else null
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isVip) Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFF8F00))
                        Text(
                            text = guest.passCategory.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isVip) Color(0xFFFF8F00) else Color.Gray
                        )
                    }
                }
            }

            // Body
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                // Phone
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE3F2FD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Call, contentDescription = null, tint = Color(0xFF135bec), modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(guest.mobile, fontWeight = FontWeight.Medium, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Booking ID and Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.ConfirmationNumber, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ID: ", fontSize = 12.sp, color = Color.Gray)
                        Text(guest.bookingId, fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = Color.Black)
                    }

                    // KYC Status
                    Surface(
                        color = if (isVerified) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isVerified) Icons.Filled.VerifiedUser else Icons.Outlined.Pending,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (isVerified) Color(0xFF2E7D32) else Color(0xFFEF6C00)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = guest.kycStatus,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isVerified) Color(0xFF2E7D32) else Color(0xFFEF6C00)
                            )
                        }
                    }
                }
            }

            // Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .background(Color(0xFFF9FAFB))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Bed, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Text(" ${guest.entryTime}", fontSize = 11.sp, color = Color.Gray)

                    if (guest.exitTime != "-" && guest.exitTime.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.width(1.dp).height(12.dp).background(Color.LightGray))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Exit: ${guest.exitTime}", fontSize = 11.sp, color = Color.Gray)
                    }
                }

                Text(
                    text = "DETAILS >",
                    fontSize = 11.sp,
                    color = Color(0xFF135bec),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}