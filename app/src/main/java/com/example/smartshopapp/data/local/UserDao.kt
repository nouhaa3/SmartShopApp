package com.example.smartshopapp.data.local

import androidx.room.*

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    suspend fun getUser(uid: String): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserProfileEntity)

    @Query("DELETE FROM users")
    suspend fun clear()
}
