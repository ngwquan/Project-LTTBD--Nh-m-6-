package com.example.expensemanagement.data.repository
import com.example.expensemanagement.data.local.dao.UserDao
import com.example.expensemanagement.data.local.entity.UserEntity

class UserRepository(
    private val userDao: UserDao
) {

    suspend fun createUser(user: UserEntity) {
        userDao.insert(user)
    }

    suspend fun getUser(id: Long): UserEntity? {
        return userDao.getById(id)
    }

    suspend fun getAllUsers(): List<UserEntity> {
        return userDao.getAll()
    }

    suspend fun deleteUser(user: UserEntity) {
        userDao.delete(user)
    }
}