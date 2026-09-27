package com.aethersync.app.ui

import android.graphics.Bitmap
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aethersync.app.model.SessionStatus
import com.aethersync.app.model.TransferSession
import com.aethersync.app.qr.QRManager

// Xender Theme Colors
val XenderGreen = Color(0xFF00C853)
val XenderDarkGreen = Color(0xFF009624)
val DarkBackground = Color(0xFF121212)
val CardBackground = Color(0xFF1E1E1E)
val LightText = Color(0xFFEEEEEE)
val GrayText = Color(0xFFAAAAAA)

data class SampleFileItem(
    val id: String,
    val name: String,
    val size: String,
    val sizeBytes: Long,
    val category: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AetherSyncApp() {
    val navController = rememberNavController()

    Surface(modifier = Modifier.fillMaxSize(), color = DarkBackground) {
        NavHost(navController = navController, startDestination = "main") {
            composable("main") { XenderMainScreen(navController) }
            composable("qr_code") { QRCodeDisplayScreen(navController) }
            composable("radar") { RadarDiscoveryScreen(navController) }
            composable("transfer") { TransferProgressScreen(navController) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XenderMainScreen(navController: NavHostController) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val categories = listOf("APPS", "PHOTOS", "MUSIC", "VIDEOS", "FILES", "HISTORY")

    val sampleFiles = remember {
        listOf(
            SampleFileItem("1", "AetherSync.apk", "18.5 MB", 19398656L, "APPS", Icons.Default.Android),
            SampleFileItem("2", "WhatsApp.apk", "42.1 MB", 44145049L, "APPS", Icons.Default.Android),
            SampleFileItem("3", "Photo_2026_01.jpg", "3.2 MB", 3355443L, "PHOTOS", Icons.Default.Image),
            SampleFileItem("4", "Vacation_Video.mp4", "150.4 MB", 157705830L, "VIDEOS", Icons.Default.Videocam),
            SampleFileItem("5", "Favorite_Song.mp3", "5.8 MB", 6081740L, "MUSIC", Icons.Default.MusicNote),
            SampleFileItem("6", "Project_Report.pdf", "1.2 MB", 1258291L, "FILES", Icons.Default.InsertDriveFile)
        )
    }

    val selectedItems = remember { mutableStateListOf<SampleFileItem>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = XenderGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Xender - AetherSync", color = LightText, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CardBackground),
                actions = {
                    IconButton(onClick = { navController.navigate("radar") }) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan QR", tint = XenderGreen)
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                color = CardBackground,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val totalSizeMb = selectedItems.sumOf { it.sizeBytes } / (1024.0 * 1024.0)
                    Column {
                        Text(
                            text = "Selected: ${selectedItems.size} items",
                            color = LightText,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = String.format("%.1f MB", totalSizeMb),
                            color = GrayText,
                            fontSize = 12.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { navController.navigate("radar") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                        ) {
                            Icon(Icons.Default.CallReceived, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("RECEIVE")
                        }

                        Button(
                            onClick = { navController.navigate("qr_code") },
                            colors = ButtonDefaults.buttonColors(containerColor = XenderGreen),
                            enabled = selectedItems.isNotEmpty() || true
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("SEND")
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(DarkBackground)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = CardBackground,
                contentColor = XenderGreen,
                edgePadding = 16.dp
            ) {
                categories.forEachIndexed { index, category ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = category,
                                color = if (selectedTabIndex == index) XenderGreen else GrayText,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            val currentCategory = categories[selectedTabIndex]
            val filteredFiles = sampleFiles.filter { it.category == currentCategory || currentCategory == "FILES" }

            if (currentCategory == "HISTORY") {
                HistoryTabContent(navController)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredFiles) { file ->
                        val isSelected = selectedItems.contains(file)
                        FileGridItem(
                            file = file,
                            isSelected = isSelected,
                            onToggleSelect = {
                                if (isSelected) selectedItems.remove(file) else selectedItems.add(file)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FileGridItem(
    file: SampleFileItem,
    isSelected: Boolean,
    onToggleSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackground)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) XenderGreen else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onToggleSelect() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = file.icon,
                contentDescription = null,
                tint = if (isSelected) XenderGreen else LightText,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = file.name,
                color = LightText,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = file.size,
                color = GrayText,
                fontSize = 10.sp
            )
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = XenderGreen,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp)
            )
        }
    }
}

@Composable
fun HistoryTabContent(navController: NavHostController) {
    val historyItems = remember {
        listOf(
            TransferSession("s1", "Photo_2026_01.jpg", 3355443L, "image/jpeg", progress = 1f, status = SessionStatus.COMPLETED),
            TransferSession("s2", "Vacation_Video.mp4", 157705830L, "video/mp4", progress = 0.6f, speed = 12.4, status = SessionStatus.TRANSFERRING)
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(historyItems) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBackground)
                    .clickable { navController.navigate("transfer") }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.InsertDriveFile, contentDescription = null, tint = XenderGreen)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.fileName, color = LightText, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (item.status == SessionStatus.COMPLETED) "Completed" else "Transferring ${ (item.progress * 100).toInt() }%",
                        color = if (item.status == SessionStatus.COMPLETED) XenderGreen else GrayText,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun QRCodeDisplayScreen(navController: NavHostController) {
    val ip = "192.168.43.1"
    val port = 8888
    val sessionId = "xender_session_99"

    val qrBitmap = remember {
        QRManager.generateConnectionQR(ip, port, sessionId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Share QR Code to Connect", color = LightText, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Receiver should scan this QR code using AetherSync", color = GrayText, fontSize = 14.sp, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(30.dp))

        Box(
            modifier = Modifier
                .size(260.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = qrBitmap.asImageBitmap(),
                contentDescription = "Connection QR Code",
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Hotspot: AetherSync_AP_99", color = LightText, fontWeight = FontWeight.Bold)
                Text("IP Address: $ip:$port", color = GrayText, fontSize = 13.sp)
                Text("Session ID: $sessionId", color = GrayText, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { navController.navigate("transfer") },
            colors = ButtonDefaults.buttonColors(containerColor = XenderGreen),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("OPEN TRANSFER SCREEN", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RadarDiscoveryScreen(navController: NavHostController) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Searching for Nearby Peers...", color = LightText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Ensure Wi-Fi or Hotspot is turned on", color = GrayText, fontSize = 14.sp)
        }

        Box(
            modifier = Modifier.size(280.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(scale)
                    .clip(CircleShape)
                    .background(XenderGreen.copy(alpha = 0.2f))
            )
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(XenderGreen.copy(alpha = 0.4f))
            )
            Icon(
                imageVector = Icons.Default.Radar,
                contentDescription = null,
                tint = LightText,
                modifier = Modifier.size(60.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { navController.navigate("qr_code") },
                colors = ButtonDefaults.buttonColors(containerColor = XenderGreen),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Default.QrCode, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("SCAN QR CODE")
            }

            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("CANCEL", color = LightText)
            }
        }
    }
}

@Composable
fun TransferProgressScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Transferring Files", color = LightText, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Connected to Peer Device", color = XenderGreen, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(30.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Vacation_Video.mp4", color = LightText, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { 0.65f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = XenderGreen,
                        trackColor = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("65% - 102.5 MB / 150.4 MB", color = GrayText, fontSize = 12.sp)
                        Text("14.2 MB/s", color = XenderGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("DISCONNECT & EXIT")
        }
    }
}
