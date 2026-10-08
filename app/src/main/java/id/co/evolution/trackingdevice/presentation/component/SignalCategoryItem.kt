package id.co.evolution.trackingdevice.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.co.evolution.trackingdevice.domain.model.BleDeviceEntity
import id.co.evolution.trackingdevice.domain.model.SignalCategory
import id.co.evolution.trackingdevice.ui.theme.TrackingDeviceTheme

@Composable
fun SignalCategoryItem (category: SignalCategory){

    Row (
        modifier = Modifier.padding(4.dp)
      ){

        Box(modifier= Modifier.padding(top = 6.dp).size(8.dp).clip(CircleShape).background(category.color))

        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = category.label,
            color = category.color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun SignalCategoryItemPreview() {
    TrackingDeviceTheme {
        SignalCategoryItem(SignalCategory.LOST_SIGNAL)
    }
}