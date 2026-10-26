package com.indrivergacor.purwakarta

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

private val Ink = Color(0xFF101713)
private val Green = Color(0xFFB8F34A)
private val Panel = Color(0xFF1B2720)
private val Muted = Color(0xFFB6C2B8)

data class Zone(val name: String, val reason: String, val mapQuery: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GacorApp() }
    }
}

@Composable
fun GacorApp() {
    val context = LocalContext.current
    val zones = listOf(
        Zone("Stasiun Purwakarta", "Pantau jam kedatangan dan keberangkatan kereta.", "Stasiun Purwakarta"),
        Zone("Pasar Jumat / Pasar Rebo", "Area belanja; cek kepadatan dan titik jemput yang aman.", "Pasar Jumat Purwakarta"),
        Zone("Situ Buleud", "Area rekreasi dan aktivitas warga, terutama sore.", "Situ Buleud Purwakarta"),
        Zone("Sadang", "Kawasan perdagangan dan akses perjalanan; perhatikan arah lalu lintas.", "Sadang Purwakarta"),
        Zone("Cikampek–Jatiluhur (sekitar akses Purwakarta)", "Pantau permintaan perjalanan dan jarak jemput sebelum berpindah.", "Jatiluhur Purwakarta")
    )
    var selectedTab by remember { mutableStateOf(0) }
    var incomeText by remember { mutableStateOf("") }
    var tripsText by remember { mutableStateOf("") }
    var savedIncome by remember { mutableIntStateOf(0) }
    var savedTrips by remember { mutableIntStateOf(0) }
    var selectedZone by remember { mutableStateOf(zones.first()) }
    val rupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply { maximumFractionDigits = 0 }

    MaterialTheme(colorScheme = darkColorScheme(
        primary = Green, onPrimary = Ink, background = Ink, surface = Panel,
        onSurface = Color.White, secondary = Green
    )) {
        Scaffold(
            containerColor = Ink,
            bottomBar = {
                NavigationBar(containerColor = Panel, contentColor = Green) {
                    NavigationBarItem(selected = selectedTab == 0, onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Dashboard, null) }, label = { Text("Beranda") })
                    NavigationBarItem(selected = selectedTab == 1, onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.Place, null) }, label = { Text("Zona") })
                    NavigationBarItem(selected = selectedTab == 2, onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Default.Assessment, null) }, label = { Text("Catatan") })
                }
            }
        ) { padding ->
            when (selectedTab) {
                0 -> HomeScreen(Modifier.padding(padding), zones, onZones = { selectedTab = 1 })
                1 -> ZoneScreen(Modifier.padding(padding), zones, selectedZone, { selectedZone = it }) {
                    val uri = Uri.parse("geo:0,0?q=" + Uri.encode(it.mapQuery + ", Purwakarta, Jawa Barat"))
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
                else -> Column(Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("CATAT PENDAPATAN", color = Green, fontSize = 22.sp, fontWeight = FontWeight.Black)
                    Text("Isi hasil kerja hari ini. Data ini tersimpan selama aplikasi berjalan.", color = Muted)
                    OutlinedTextField(value = incomeText, onValueChange = { incomeText = it.filter(Char::isDigit) },
                        label = { Text("Pendapatan (Rp)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = tripsText, onValueChange = { tripsText = it.filter(Char::isDigit) },
                        label = { Text("Jumlah order selesai") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth())
                    Button(onClick = {
                        savedIncome += incomeText.toIntOrNull() ?: 0
                        savedTrips += tripsText.toIntOrNull() ?: 0
                        incomeText = ""; tripsText = ""
                    }, colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Ink),
                        modifier = Modifier.fillMaxWidth()) { Text("SIMPAN CATATAN", fontWeight = FontWeight.Bold) }
                    Card(colors = CardDefaults.cardColors(containerColor = Panel), modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("TOTAL TERCATAT", color = Muted)
                            Text(rupiah.format(savedIncome), color = Green, fontSize = 27.sp, fontWeight = FontWeight.Black)
                            Text("$savedTrips order selesai", color = Color.White)
                            TextButton(onClick = { savedIncome = 0; savedTrips = 0 }) { Text("Reset catatan") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier, zones: List<Zone>, onZones: () -> Unit) {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    val timeAdvice = when (hour) {
        in 5..8 -> "Pagi: pantau stasiun, pasar, dan area aktivitas kerja."
        in 9..11 -> "Menjelang siang: evaluasi order masuk; jangan berpindah terlalu jauh tanpa alasan."
        in 12..14 -> "Siang: cek area makan, pasar, dan pusat aktivitas."
        in 15..18 -> "Sore: pantau stasiun, pusat belanja, dan area rekreasi."
        in 19..22 -> "Malam: prioritaskan area terang, ramai, dan titik jemput aman."
        else -> "Jam sepi: pertimbangkan istirahat atau tunggu di lokasi aman."
    }
    LazyColumn(modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 22.dp, bottom = 24.dp)) {
        item {
            Text("INDrivER", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text("GACOR", color = Green, fontSize = 34.sp, fontWeight = FontWeight.Black)
            Text("ASISTEN DRIVER • PURWAKARTA", color = Muted, fontSize = 11.sp, letterSpacing = 1.5.sp)
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Green), shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(18.dp)) {
                    Text("SARAN WAKTU SAAT INI", color = Ink, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(timeAdvice, color = Ink, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                    Text("Saran umum berdasarkan jam perangkat, bukan data order langsung.", color = Ink.copy(alpha = .75f), fontSize = 12.sp)
                }
            }
        }
        item {
            Text("ZONA UNTUK DIPANTAU", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text("Pilihan lokasi awal di wilayah Purwakarta", color = Muted, fontSize = 12.sp)
        }
        items(zones.take(3)) { zone ->
            Card(colors = CardDefaults.cardColors(containerColor = Panel), modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Green)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(zone.name, color = Color.White, fontWeight = FontWeight.Bold)
                        Text(zone.reason, color = Muted, fontSize = 12.sp)
                    }
                }
            }
        }
        item {
            OutlinedButton(onClick = onZones, modifier = Modifier.fillMaxWidth()) { Text("LIHAT SEMUA ZONA") }
        }
        item {
            Text("Tips aman: patuhi aturan inDrive, jangan menunggu di badan jalan, dan cek biaya serta jarak jemput sebelum menerima perjalanan.",
                color = Muted, fontSize = 12.sp)
        }
    }
}

@Composable
fun ZoneScreen(modifier: Modifier, zones: List<Zone>, selected: Zone, onSelect: (Zone) -> Unit, onMap: (Zone) -> Unit) {
    Column(modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("ZONA PURWAKARTA", color = Green, fontSize = 24.sp, fontWeight = FontWeight.Black)
        Text("Pilih area untuk melihat lokasi di Google Maps. Zona ini adalah titik pantau awal, bukan jaminan order.", color = Muted, fontSize = 13.sp)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(zones) { zone ->
                Card(colors = CardDefaults.cardColors(containerColor = if (selected == zone) Color(0xFF2A3B2B) else Panel),
                    modifier = Modifier.fillMaxWidth(), onClick = { onSelect(zone) }) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, null, tint = Green)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(zone.name, color = Color.White, fontWeight = FontWeight.Bold)
                            Text(zone.reason, color = Muted, fontSize = 12.sp)
                        }
                        if (selected == zone) Icon(Icons.Default.CheckCircle, null, tint = Green)
                    }
                }
            }
        }
        Button(onClick = { onMap(selected) }, modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Ink)) {
            Icon(Icons.Default.Map, null); Spacer(Modifier.width(8.dp)); Text("BUKA DI GOOGLE MAPS")
        }
    }
}
