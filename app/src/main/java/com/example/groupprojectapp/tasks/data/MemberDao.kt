package com.example.groupprojectapp.tasks.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * MODEL (data layer): the database queries for team members.
 * [getAllMembers] is a live Flow (used by the filter chips and the assignee
 * dropdown). [getAllMembersOnce] is a one-off read, used only to check
 * whether the table is empty before seeding.
 */

@Dao
interface MemberDao {

    @Query("SELECT * FROM members ORDER BY name ASC")
    fun getAllMembers(): Flow<List<Member>>

    @Query("SELECT * FROM members")
    suspend fun getAllMembersOnce(): List<Member>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMember(member: Member): Long
}