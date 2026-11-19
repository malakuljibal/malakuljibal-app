package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.AccessLevel
import com.example.domain.model.TransactionItem
import com.example.domain.model.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val familyId: String,
    val title: String,
    val category: String,
    val amount: Long,
    val type: String,
    val date: String,
    val paymentMethod: String,
    val responsibleMemberId: String,
    val responsibleMemberName: String,
    val description: String,
    val hasReceipt: Boolean,
    val accessLevel: String
) {
    fun toDomain(): TransactionItem {
        val parsedType = if (type.equals("INCOME", ignoreCase = true)) TransactionType.INCOME else TransactionType.EXPENSE
        val parsedAccessLevel = try {
            AccessLevel.valueOf(accessLevel)
        } catch (_: Exception) {
            AccessLevel.FAMILY_SHARED
        }
        return TransactionItem(
            id = id,
            title = title,
            category = category,
            amount = amount,
            type = parsedType,
            date = date,
            paymentMethod = paymentMethod,
            responsibleMemberId = responsibleMemberId,
            responsibleMemberName = responsibleMemberName,
            description = description,
            hasReceipt = hasReceipt,
            accessLevel = parsedAccessLevel
        )
    }

    companion object {
        fun fromDomain(item: TransactionItem, familyId: String): TransactionEntity {
            return TransactionEntity(
                id = item.id,
                familyId = familyId,
                title = item.title,
                category = item.category,
                amount = item.amount,
                type = item.type.name,
                date = item.date,
                paymentMethod = item.paymentMethod,
                responsibleMemberId = item.responsibleMemberId,
                responsibleMemberName = item.responsibleMemberName,
                description = item.description,
                hasReceipt = item.hasReceipt,
                accessLevel = item.accessLevel.name
            )
        }
    }
}
