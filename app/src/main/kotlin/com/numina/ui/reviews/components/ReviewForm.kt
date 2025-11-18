package com.numina.ui.reviews.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.numina.ui.components.NuminaTextField

@Composable
fun ReviewForm(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    title: String,
    onTitleChange: (String) -> Unit,
    content: String,
    onContentChange: (String) -> Unit,
    pros: List<String>,
    onProsChange: (List<String>) -> Unit,
    cons: List<String>,
    onConsChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPro by remember { mutableStateOf("") }
    var currentCon by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Rating
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Rating *",
                style = MaterialTheme.typography.titleSmall
            )
            InteractiveStarRating(
                rating = rating,
                onRatingChanged = onRatingChange
            )
        }

        // Title
        NuminaTextField(
            value = title,
            onValueChange = onTitleChange,
            label = "Title (optional)",
            singleLine = true
        )

        // Content
        OutlinedTextField(
            value = content,
            onValueChange = onContentChange,
            label = { Text("Review *") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            minLines = 5,
            maxLines = 10,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Pros
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Pros (optional)",
                style = MaterialTheme.typography.titleSmall
            )

            // Display existing pros
            pros.forEach { pro ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "• $pro",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { onProsChange(pros.filter { it != pro }) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove pro",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Add new pro
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = currentPro,
                    onValueChange = { currentPro = it },
                    label = { Text("Add a pro") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                IconButton(
                    onClick = {
                        if (currentPro.isNotBlank()) {
                            onProsChange(pros + currentPro.trim())
                            currentPro = ""
                        }
                    },
                    enabled = currentPro.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add pro"
                    )
                }
            }
        }

        // Cons
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Cons (optional)",
                style = MaterialTheme.typography.titleSmall
            )

            // Display existing cons
            cons.forEach { con ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "• $con",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { onConsChange(cons.filter { it != con }) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove con",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Add new con
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = currentCon,
                    onValueChange = { currentCon = it },
                    label = { Text("Add a con") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                IconButton(
                    onClick = {
                        if (currentCon.isNotBlank()) {
                            onConsChange(cons + currentCon.trim())
                            currentCon = ""
                        }
                    },
                    enabled = currentCon.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add con"
                    )
                }
            }
        }
    }
}
