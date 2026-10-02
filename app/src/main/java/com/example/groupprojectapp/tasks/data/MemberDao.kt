package com.example.groupprojectapp.tasks.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {

    @Query("SELECT * FROM members ORDER BY name ASC")
    fun getAllMembers(): Flow<List<Member>>

    @Query("SELECT * FROM members")
    suspend fun getAllMembersOnce(): List<Member>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMember(member: Member): Long
}