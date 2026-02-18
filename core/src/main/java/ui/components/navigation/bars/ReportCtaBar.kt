package ui.components.navigation.bars

@Composable
fun ReportCtaBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                color = Violet20,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "See your financial report",
            style = MaterialTheme.typography.bodyLarge,
            color = Violet100
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.chevron_right),
            contentDescription = null,
            tint = Violet100
        )
    }
}

