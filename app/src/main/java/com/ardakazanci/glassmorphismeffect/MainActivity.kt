package com.ardakazanci.glassmorphismeffect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ardakazanci.glassmorphismeffect.ui.theme.GlassmorphismEffectTheme
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GlassmorphismEffectTheme {
                CardBorderEffect()
            }
        }
    }
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun CardBorderEffect() {
    val hazeState = remember { HazeState() }
    val springState = remember { mutableStateOf(false) }

    val springScale: Float by animateFloatAsState(
        targetValue = if (springState.value) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val infiniteTransition = rememberInfiniteTransition(label = "SampleTransitionEffect")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "Sample Border Animation"
    )

    val density = LocalDensity.current
    val boxSize = 200.dp
    val boxSizePx = with(density) { boxSize.toPx() }
    val centerOffset = Offset(boxSizePx / 2, boxSizePx / 2)

    val gradientBrush = Brush.linearGradient(
        colors = listOf(Color(0xFF7790ff), Color(0xFFff787a), Color(0xFF1acd9a), Color(0xFFff8fc0)),
        start = Offset(0f, 0f).rotate(angle, centerOffset),
        end = Offset(boxSizePx, boxSizePx).rotate(angle, centerOffset)
    )

    Box(
        modifier = Modifier
            .background(Color(0xff030521))
            .fillMaxSize()
            .haze(hazeState)
            .clickable {
                springState.value = !springState.value
            }
    ) {
        val minRadius = with(density) { 100.dp.toPx() }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val circleCenters = listOf(
                Offset(size.width * 0.25f, size.height * 0.25f),
                Offset(size.width * 0.75f, size.height * 0.25f),
                Offset(size.width * 0.25f, size.height * 0.75f),
                Offset(size.width * 0.75f, size.height * 0.75f)
            )
            val circleColors = listOf(
                Color(0xFF7790ff),
                Color(0xFFff787a),
                Color(0xFF1acd9a),
                Color(0xFFff8fc0)
            )

            circleCenters.forEachIndexed { index, center ->
                drawCircle(
                    color = circleColors[index],
                    center = center,
                    radius = minRadius
                )
            }
        }

        val boxModifier = Modifier
            .hazeChild(
                state = hazeState,
                shape = RoundedCornerShape(50.dp),
                style = HazeMaterials.ultraThin()
            )
            .clip(RoundedCornerShape(50.dp))
            .border(
                width = 4.dp,
                brush = gradientBrush,
                shape = RoundedCornerShape(16.dp)
            )
            .background(Color.Transparent)
            .align(Alignment.Center)
            .size(width = 340.dp, height = 500.dp)
            .scale(springScale)

        Box(modifier = boxModifier) {
            Text(
                text = "Filled",
                color = Color.White,
                modifier = Modifier
                    .padding(16.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}

fun Offset.rotate(degrees: Float, pivot: Offset): Offset {
    val radians = Math.toRadians(degrees.toDouble())
    val cos = cos(radians)
    val sin = sin(radians)
    return Offset(
        x = (cos * (x - pivot.x) - sin * (y - pivot.y) + pivot.x).toFloat(),
        y = (sin * (x - pivot.x) + cos * (y - pivot.y) + pivot.y).toFloat()
    )
}