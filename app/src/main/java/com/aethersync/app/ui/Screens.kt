package com.aethersync.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHostController

// Theme Colors
val DarkBg = Color(0xFF08080C)
val AccentPurple = Color(0xFF8B5CF6)
val GlassWhite = Color(0x1AFFFFFF)

@Composable
fun AetherSyncApp() {
    val navController = rememberNavController()

    Surface(modifier = Modifier.fillMaxSize(), color = DarkBg) {
        NavHost(navController = navController, startDestination = "home") {
            composable("home") { HomeScreen(navController) }
            composable("send") { SendScreen(navController) }
            composable("receive") { ReceiveScreen(navController) }
            composable("transfer") { TransferScreen(navController) }
        }
    }
}

@Composable
fun HomeScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "AETHER\nSYNC",
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                lineHeight = 50.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(60.dp))

            // Send Button
            FuturisticButton(
                text = "SEND FILES",
                color = AccentPurple,
                onClick = { navController.navigate("send") }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Receive Button
            FuturisticButton(
                text = "RECEIVE FILES",
                color = Color.DarkGray,
                onClick = { navController.navigate("receive") }
            )
        }
    }
}

@Composable
fun FuturisticButton(text: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(280.dp)
            .height(70.dp)
            .clickable { onClick() }
            .background(
                Brush.linearGradient(listOf(color, color.copy(alpha = 0.7f))),
                shape = RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

@Composable
fun SendScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("SELECT FILES", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(30.dp))

        // Simple placeholders for categories
        val categories = listOf("Photos", "Videos", "Docs", "APKs", "Folders")
        categories.forEach { category ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(GlassWhite, RoundedCornerShape(15.dp))
                    .clickable { /* Trigger File Picker */ }
                    .padding(20.dp)
            ) {
                Text(category, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        FuturisticButton(text = "GENERATE QR", color = AccentPurple, onClick = { navController.navigate("transfer") })
    }
}

@Composable
fun ReceiveScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("SEARCHING...", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(40.dp))

            // Radar Animation Placeholder
            Box(
                modifier = Modifier.size(200.dp).background(AccentPurple.copy(alpha = 0.2f), RoundedCornerShape(100.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("📡", fontSize = 40.sp)
            }

            Spacer(modifier = Modifier.height(40.dp))
            FuturisticButton(text = "SCAN QR CODE", color = AccentPurple, onClick = { navController.navigate("transfer") })
        }
    }
}

@Composable
fun TransferScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("TRANSFERRING", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(30.dp))

            // Progress Bar
            Box(modifier = Modifier.fillMaxWidth().height(12.dp).background(Color.DarkGray, RoundedCornerShape(6.dp))) {
                Box(modifier = Modifier.fillMaxWidth(0.6f).height(12.dp).background(AccentPurple, RoundedCornerShape(6.dp)))
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text("60% - 1.2GB / 2.0GB", color = Color.Gray, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(40.dp))
            FuturisticButton(text = "CANCEL", color = Color.Red, onClick = { navController.popBackStack() })
        }
    }
}
