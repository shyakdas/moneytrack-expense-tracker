package ui.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.moneytrack.core.R
import org.junit.Rule
import org.junit.Test
import ui.components.card.budget.BudgetCard
import ui.components.card.budget.BudgetStatus
import ui.components.card.category.CategoryAmountType
import ui.components.card.category.CategoryCard
import ui.components.card.list.ListItemCard
import ui.components.card.list.ListItemVariant
import ui.components.card.transaction.TransactionCard
import ui.components.card.transaction.TransactionType
import ui.theme.MoneyTrackTheme

class CardsScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5
    )

    @Test
    fun cards_light_allVariants() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = false) {
                CardsContent()
            }
        }
    }

    @Test
    fun cards_dark_allVariants() {
        paparazzi.snapshot {
            MoneyTrackTheme(darkTheme = true) {
                CardsContent()
            }
        }
    }
}

@Composable
private fun CardsContent() {
    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        TransactionCard(
            icon = ImageVector.vectorResource(id = R.drawable.shopping_bag),
            title = "Shopping",
            subtitle = "Buy an Avocado...",
            amount = "- Rp 229.000",
            time = "03:30 PM",
            type = TransactionType.EXPENSE
        )

        CategoryCard(
            icon = Icons.Default.ShoppingBag,
            title = "Utilities",
            amount = "- $600",
            progress = 0.7f,
            type = CategoryAmountType.EXPENSE
        )

        BudgetCard(
            category = "Medical",
            remainingAmount = "$200",
            spentText = "$600 of $1000",
            progress = 0.6f,
            status = BudgetStatus.EXCEEDED
        )

        BudgetCard(
            category = "Medical",
            remainingAmount = "$0",
            spentText = "$1200 of $1000",
            progress = 1f,
            status = BudgetStatus.EXCEEDED
        )

        ListItemCard(
            title = "Title",
            description = "Description",
            rightText = "19.30",
            variant = ListItemVariant.DEFAULT
        )

        ListItemCard(
            title = "Title",
            description = null,
            variant = ListItemVariant.SELECT,
        )

        ListItemCard(
            title = "Title",
            description = "Description",
            variant = ListItemVariant.SWITCH,
        )
    }
}
