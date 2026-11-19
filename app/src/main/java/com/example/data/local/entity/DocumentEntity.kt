package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.AccessLevel
import com.example.domain.model.DocumentCategory
import com.example.domain.model.DocumentStatus
import com.example.domain.model.FamilyDocument

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey val id: String,
    val familyId: String,
    val title: String,
    val category: String,
    val ownerMemberId: String,
    val ownerName: String,
    val issueDate: String,
    val expirationDate: String?,
    val daysUntilExpiry: Int?,
    val description: String,
    val tagsRaw: String, // comma-separated
    val uploadedBy: String,
    val uploadTimestamp: String,
    val fileFormat: String,
    val fileSizeLabel: String,
    val versionLabel: String,
    val status: String,
    val accessLevel: String
) {
    fun toDomain(): FamilyDocument {
        val cat = try {
            DocumentCategory.valueOf(category)
        } catch (_: Exception) {
            DocumentCategory.OTHER
        }
        val stat = try {
            DocumentStatus.valueOf(status)
        } catch (_: Exception) {
            DocumentStatus.ACTIVE
        }
        val acc = try {
            AccessLevel.valueOf(accessLevel)
        } catch (_: Exception) {
            AccessLevel.FAMILY_SHARED
        }
        val tagList = if (tagsRaw.isBlank()) emptyList() else tagsRaw.split(",").map { it.trim() }

        return FamilyDocument(
            id = id,
            title = title,
            category = cat,
            ownerMemberId = ownerMemberId,
            ownerName = ownerName,
            issueDate = issueDate,
            expirationDate = expirationDate,
            daysUntilExpiry = daysUntilExpiry,
            description = description,
            tags = tagList,
            uploadedBy = uploadedBy,
            uploadTimestamp = uploadTimestamp,
            fileFormat = fileFormat,
            fileSizeLabel = fileSizeLabel,
            versionLabel = versionLabel,
            status = stat,
            accessLevel = acc
        )
    }

    companion object {
        fun fromDomain(doc: FamilyDocument, familyId: String): DocumentEntity {
            return DocumentEntity(
                id = doc.id,
                familyId = familyId,
                title = doc.title,
                category = doc.category.name,
                ownerMemberId = doc.ownerMemberId,
                ownerName = doc.ownerName,
                issueDate = doc.issueDate,
                expirationDate = doc.expirationDate,
                daysUntilExpiry = doc.daysUntilExpiry,
                description = doc.description,
                tagsRaw = doc.tags.joinToString(","),
                uploadedBy = doc.uploadedBy,
                uploadTimestamp = doc.uploadTimestamp,
                fileFormat = doc.fileFormat,
                fileSizeLabel = doc.fileSizeLabel,
                versionLabel = doc.versionLabel,
                status = doc.status.name,
                accessLevel = doc.accessLevel.name
            )
        }
    }
}
