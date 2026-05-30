package com.example.hopper.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hopper.domain.model.CrowdBucket
import com.example.hopper.domain.model.ExitNode
import com.example.hopper.domain.model.ExitNodeCategory
import com.example.hopper.domain.model.Pandal
import com.example.hopper.ui.theme.CrowdGreen
import com.example.hopper.ui.theme.CrowdRed
import com.example.hopper.ui.theme.CrowdYellow
import com.example.hopper.ui.theme.MedicalRed
import com.example.hopper.ui.theme.MetroGreen
import com.example.hopper.ui.theme.PoliceBlue
import com.example.hopper.ui.theme.RailwayOrange

data class NearbyPandalInfo(
    val pandal: Pandal,
    val distanceMeters: Double,
    val crowdBucket: CrowdBucket?,
    val bearing: Float
)

data class NearbyExitInfo(
    val exitNode: ExitNode,
    val distanceMeters: Double,
    val bearing: Float
)
