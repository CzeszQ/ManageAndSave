@file:OptIn(ExperimentalFoundationApi::class)

package pl.edu.ur.dc131419.manageandsave.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.ur.dc131419.manageandsave.data.local.db.entity.TransactionEntity
import java.text.DateFormat
import java.util.Date

@Composable
fun TransactionRow(
    tx: TransactionEntity,
    onClick: (TransactionEntity) -> Unit,
    onLongClick: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onClick(tx) },
                onLongClick = { onLongClick(tx) }
            )
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(tx.description)
            Text(DateFormat.getDateInstance().format(Date(tx.dateMillis)))
        }
        Spacer(Modifier.width(12.dp))
        Text(tx.amount.toString())
    }
    }
