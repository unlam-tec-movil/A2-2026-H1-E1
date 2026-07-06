package ar.edu.unlam.mobile.scaffolding.ui.components.shared

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.AVATAR_SIZE
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_LARGE
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_MEDIUM
import ar.edu.unlam.mobile.scaffolding.ui.constant.dimension.Dimens.PADDING_SMALL

@Composable
fun ShimmerFeed() {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(PADDING_MEDIUM),
        verticalArrangement = Arrangement.spacedBy(PADDING_MEDIUM),
    ) {
        repeat(5) {
            ShimmerPostCard()
        }
    }
}

@Composable
private fun ShimmerPostCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(PADDING_LARGE),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(PADDING_MEDIUM)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ShimmerCircle(size = AVATAR_SIZE)
                Spacer(modifier = Modifier.width(PADDING_SMALL))
                ShimmerBox(width = 120.dp, height = 14.dp)
            }
            Spacer(modifier = Modifier.height(PADDING_MEDIUM))
            ShimmerBox(width = 280.dp, height = 12.dp)
            Spacer(modifier = Modifier.height(PADDING_SMALL))
            ShimmerBox(width = 200.dp, height = 12.dp)
            Spacer(modifier = Modifier.height(PADDING_MEDIUM))
            ShimmerBox(width = 80.dp, height = 10.dp)
            Spacer(modifier = Modifier.height(PADDING_SMALL))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                ShimmerBox(width = 40.dp, height = 10.dp)
                ShimmerBox(width = 40.dp, height = 10.dp)
            }
        }
    }
}

@Composable
private fun ShimmerCircle(size: androidx.compose.ui.unit.Dp) {
    val brush = shimmerBrush()
    Box(
        modifier =
            Modifier
                .size(size)
                .clip(CircleShape)
                .background(brush),
    )
}

@Composable
private fun ShimmerBox(
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp,
) {
    val brush = shimmerBrush()
    Box(
        modifier =
            Modifier
                .width(width)
                .height(height)
                .clip(RoundedCornerShape(4.dp))
                .background(brush),
    )
}

@Composable
private fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition()
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 600f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
    )
    return Brush.linearGradient(
        colors =
            listOf(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            ),
        start = Offset(translateAnim, 0f),
        end = Offset(translateAnim + 300f, 0f),
    )
}
