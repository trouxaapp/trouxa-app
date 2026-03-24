package com.trouxaapp.app.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase

/**
 * Room database for offline caching of remote data.
 * Used when migrating from hardcoded to JSON backend.
 */
@Entity(tableName = "cached_data")
data class CachedDataEntity(
    @PrimaryKey val key: String, // "data_json"
    val json: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Dao
interface CachedDataDao {
    @Query("SELECT * FROM cached_data WHERE `key` = :key")
    suspend fun get(key: String): CachedDataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CachedDataEntity)

    @Query("DELETE FROM cached_data WHERE `key` = :key")
    suspend fun delete(key: String)
}

@Database(entities = [CachedDataEntity::class], version = 1, exportSchema = false)
abstract class CacheDatabase : RoomDatabase() {
    abstract fun cachedDataDao(): CachedDataDao
}
