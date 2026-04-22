package com.example.expensemanagement.data.local.dao
import com.example.expensemanagement.data.local.entity.UserEntity
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import androidx.room.OnConflictStrategy

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE user_id = :id")
    fun getByIdFlow(id: Long): kotlinx.coroutines.flow.Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE user_id = :id")
    suspend fun getById(id: Long): UserEntity?

    @Query("SELECT * FROM users")
    suspend fun getAll(): List<UserEntity>

    @Delete
    suspend fun delete(user: UserEntity)
}