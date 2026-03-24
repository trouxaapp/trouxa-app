package com.trouxaapp.app.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "analyzed_leis")
data class AnalyzedLeiEntity(
    @PrimaryKey val plId: Int,
    val siglaTipo: String = "",
    val numero: Int = 0,
    val ano: Int = 0,
    val ementa: String = "",
    // Template translation
    val tituloPopular: String = "",
    val resumo: String = "",
    val comoTeAfeta: String = "",
    val quemGanha: String = "",
    val quemPerde: String = "",
    val nivelTrouxa: Int = 0,
    val justificativaNivel: String = "",
    val acaoCidadao: String = "",
    // Author
    val autorNome: String = "",
    val autorPartido: String = "",
    val autorUf: String = "",
    val autorTwitter: String = "",
    val autorEmail: String = "",
    val mensagemSugerida: String = "",
    // Meta
    val analyzedAt: Long = 0,
    val status: String = "pending", // pending, analyzed, error
    val fonte: String = "template" // template, curadoria, fallback
)

@Dao
interface AnalyzedLeiDao {
    @Query("SELECT * FROM analyzed_leis WHERE status = 'analyzed' ORDER BY plId DESC")
    fun getAllAnalyzed(): Flow<List<AnalyzedLeiEntity>>

    @Query("SELECT * FROM analyzed_leis WHERE status = 'analyzed' ORDER BY nivelTrouxa DESC")
    fun getAllByGravity(): Flow<List<AnalyzedLeiEntity>>

    @Query("SELECT * FROM analyzed_leis WHERE plId = :id")
    suspend fun getById(id: Int): AnalyzedLeiEntity?

    @Query("SELECT * FROM analyzed_leis WHERE status = 'pending' LIMIT 5")
    suspend fun getPending(): List<AnalyzedLeiEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lei: AnalyzedLeiEntity)

    @Query("SELECT COUNT(*) FROM analyzed_leis WHERE plId = :id")
    suspend fun exists(id: Int): Int
}

@Database(entities = [AnalyzedLeiEntity::class, CachedDataEntity::class], version = 3, exportSchema = false)
abstract class TrouxaDatabase : RoomDatabase() {
    abstract fun analyzedLeiDao(): AnalyzedLeiDao
    abstract fun cachedDataDao(): CachedDataDao
}
