package com.example.domain.rbac

import com.example.domain.model.AccessLevel
import com.example.domain.model.FamilyDocument
import com.example.domain.model.FamilyEvent
import com.example.domain.model.FamilyMember
import com.example.domain.model.MedicalProfile
import com.example.domain.model.TransactionItem
import com.example.domain.model.UserRole

/**
 * Frontend RBAC preview evaluator mirroring planned server-side authorization rules.
 * Notice: In production (Phase 6), every authorization check will be enforced server-side
 * on the PHP 8.x API before returning records.
 */
object AccessControl {

    fun canViewMedicalProfile(viewer: FamilyMember, target: FamilyMember, profile: MedicalProfile?): Boolean {
        if (profile == null) return false
        if (viewer.id == target.id) return true
        // Parents can view medical records of child profiles they manage
        if (target.role == UserRole.CHILD_PROFILE && target.managedByParentIds.contains(viewer.id)) return true
        // Explicit record-level authorization (e.g., spouse or elder caregiver authorization)
        if (profile.authorizedViewerIds.contains(viewer.id)) return true
        // Medical records are private by default — even Super Admin respects explicit consent
        return false
    }

    fun canViewTransaction(viewer: FamilyMember, tx: TransactionItem): Boolean {
        return when (tx.accessLevel) {
            AccessLevel.FAMILY_SHARED -> true
            AccessLevel.ADMIN_ONLY -> viewer.role == UserRole.SUPER_ADMIN || tx.responsibleMemberId == viewer.id
            AccessLevel.PRIVATE_PERSONAL -> tx.responsibleMemberId == viewer.id
            AccessLevel.PARENT_MANAGED -> viewer.role == UserRole.SUPER_ADMIN || viewer.role == UserRole.PARENT
        }
    }

    fun canViewDocument(viewer: FamilyMember, doc: FamilyDocument): Boolean {
        if (doc.ownerMemberId == viewer.id) return true
        return when (doc.accessLevel) {
            AccessLevel.FAMILY_SHARED -> true
            AccessLevel.ADMIN_ONLY -> viewer.role == UserRole.SUPER_ADMIN || doc.authorizedViewerIds.contains(viewer.id)
            AccessLevel.PARENT_MANAGED -> viewer.role == UserRole.SUPER_ADMIN || viewer.role == UserRole.PARENT || doc.authorizedViewerIds.contains(viewer.id)
            AccessLevel.PRIVATE_PERSONAL -> doc.authorizedViewerIds.contains(viewer.id)
        }
    }

    fun canViewChildProfile(viewer: FamilyMember, child: FamilyMember): Boolean {
        if (viewer.id == child.id) return true
        if (viewer.role == UserRole.SUPER_ADMIN) return true
        if (child.managedByParentIds.contains(viewer.id)) return true
        return false
    }

    fun canViewEvent(viewer: FamilyMember, event: FamilyEvent): Boolean {
        if (event.assignedMemberIds.contains(viewer.id)) return true
        return when (event.accessLevel) {
            AccessLevel.FAMILY_SHARED -> true
            AccessLevel.PARENT_MANAGED -> viewer.role == UserRole.SUPER_ADMIN || viewer.role == UserRole.PARENT
            AccessLevel.ADMIN_ONLY -> viewer.role == UserRole.SUPER_ADMIN
            AccessLevel.PRIVATE_PERSONAL -> false
        }
    }

    fun canViewFinanceOverview(viewer: FamilyMember): Boolean {
        return viewer.role == UserRole.SUPER_ADMIN || viewer.role == UserRole.PARENT
    }
}
