package pl.edu.ur.dc131419.manageandsave.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import pl.edu.ur.dc131419.manageandsave.data.local.db.entity.TransactionEntity

@Composable
fun TxActionsDialog(
    tx: TransactionEntity,
    onDismiss: () -> Unit,
    onEdit: (TransactionEntity) -> Unit,
    onDelete: (TransactionEntity) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Transakcja") },
        text = { Text(tx.description) },
        confirmButton = {
            TextButton(onClick = { onEdit(tx) }) { Text("Edytuj") }
        },
        dismissButton = {
            TextButton(onClick = { onDelete(tx) }) { Text("Usuń") }
        }
    )
}
