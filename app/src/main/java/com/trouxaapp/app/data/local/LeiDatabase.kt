package com.trouxaapp.app.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

// ============================================================
// LEIS (Camara dos Deputados, live data only)
//
// Data philosophy: every field here is either (a) raw data from the
// Camara API, (b) a value derived from raw data by a documented rule
// (temperatura, tituloPopular), or (c) human-curated analysis that
// carries mandatory sources. Nothing is invented.
// ============================================================

@Entity(tableName = "leis")
data class LeiEntity(
    @PrimaryKey val plId: Int,
    val siglaTipo: String = "",
    val numero: Int = 0,
    val ano: Int = 0,
    val ementa: String = "",
    val ementaDetalhada: String = "",
    val keywords: String = "",
    val urlInteiroTeor: String = "",
    // Author's own justification text when the API provides it ("why this law exists")
    val justificativa: String = "",

    // Derived from the real ementa text (cleanup only, no new claims)
    val tituloPopular: String = "",
    // Keyword classification, used for filtering/organization only
    val tema: String = "",

    // All real authors as JSON: [{nome, partido, uf, foto, tipo, deputadoId, email}]
    val autoresJson: String = "",
    // First author denormalized for list cards
    val autorNome: String = "",
    val autorPartido: String = "",
    val autorUf: String = "",
    val autorFoto: String = "",
    val autorTipo: String = "",
    val autorEmail: String = "",

    // Tramitacao straight from the API, with the real fetch timestamp
    val dataApresentacao: String = "",
    val ultimoStatusData: String = "",
    val ultimoStatusDescricao: String = "",
    val ultimaSituacao: String = "",
    val despacho: String = "",
    val tramitacoesJson: String = "",
    val tramitacaoAtualizadaEm: Long = 0,

    // Objective activity score 0-100. Every point maps to a verifiable
    // fact listed in temperaturaMotivosJson (see Temperatura.kt).
    val temperatura: Int = 0,
    val temperaturaMotivosJson: String = "",

    // Human-curated analysis from the remote feed. Only rendered when
    // present AND carrying at least one source. Never auto-generated.
    val curadoriaResumo: String = "",
    val curadoriaProsJson: String = "",
    val curadoriaContrasJson: String = "",
    val curadoriaContexto: String = "",
    val curadoriaFontesJson: String = "",
    val curadoriaAtualizadaEm: Long = 0,

    val sincronizadoEm: Long = 0
)

@Dao
interface LeiDao {
    @Query("SELECT * FROM leis ORDER BY temperatura DESC, dataApresentacao DESC")
    fun observeByTemperatura(): Flow<List<LeiEntity>>

    @Query("SELECT * FROM leis ORDER BY dataApresentacao DESC, plId DESC")
    fun observeByRecentes(): Flow<List<LeiEntity>>

    @Query("SELECT * FROM leis WHERE plId = :id")
    suspend fun getById(id: Int): LeiEntity?

    @Query("SELECT * FROM leis WHERE plId = :id")
    fun observeById(id: Int): Flow<LeiEntity?>

    @Query("SELECT COUNT(*) FROM leis WHERE plId = :id")
    suspend fun exists(id: Int): Int

    @Query("SELECT COUNT(*) FROM leis")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM leis WHERE dataApresentacao >= :isoDate")
    suspend fun countApresentadasDesde(isoDate: String): Int

    @Query("SELECT MAX(plId) FROM leis")
    suspend fun maxId(): Int?

    // Every bill past the notification cursor. The event worker must look at
    // ALL of them, not just MAX(plId): several bills land between runs and a
    // cold newest must not bury a hot older one.
    @Query("SELECT * FROM leis WHERE plId > :id")
    suspend fun newerThan(id: Int): List<LeiEntity>

    @Query("SELECT plId FROM leis ORDER BY dataApresentacao DESC LIMIT :n")
    suspend fun idsMaisRecentes(n: Int): List<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lei: LeiEntity)

    @Query("""
        UPDATE leis SET
            ultimoStatusData = :statusData,
            ultimoStatusDescricao = :statusDescricao,
            ultimaSituacao = :situacao,
            despacho = :despacho,
            tramitacoesJson = :tramJson,
            tramitacaoAtualizadaEm = :ts,
            temperatura = :temperatura,
            temperaturaMotivosJson = :motivosJson
        WHERE plId = :id
    """)
    suspend fun updateTramitacao(
        id: Int,
        statusData: String,
        statusDescricao: String,
        situacao: String,
        despacho: String,
        tramJson: String,
        ts: Long,
        temperatura: Int,
        motivosJson: String
    )

    @Query("""
        UPDATE leis SET
            curadoriaResumo = :resumo,
            curadoriaProsJson = :prosJson,
            curadoriaContrasJson = :contrasJson,
            curadoriaContexto = :contexto,
            curadoriaFontesJson = :fontesJson,
            curadoriaAtualizadaEm = :ts
        WHERE plId = :id
    """)
    suspend fun updateCuradoria(
        id: Int,
        resumo: String,
        prosJson: String,
        contrasJson: String,
        contexto: String,
        fontesJson: String,
        ts: Long
    )

    // Cache hygiene: drop laws older than the sync window that never got curated,
    // so the feed does not accumulate stale uncurated entries forever.
    @Query("DELETE FROM leis WHERE dataApresentacao < :isoDate AND curadoriaResumo = ''")
    suspend fun purgeAntigasSemCuradoria(isoDate: String)
}

// ============================================================
// INDICADORES (Banco Central SGS cache)
// "data" is the official reference date of the value as published
// by the BCB. The UI must always render it next to the value.
// ============================================================

@Entity(tableName = "indicadores")
data class IndicadorEntity(
    @PrimaryKey val serie: Int,
    val nome: String,
    val valor: Double,
    val data: String,
    val atualizadoEm: Long
)

@Dao
interface IndicadorDao {
    @Query("SELECT * FROM indicadores WHERE serie = :serie")
    suspend fun getBySerie(serie: Int): IndicadorEntity?

    @Query("SELECT * FROM indicadores")
    suspend fun getAll(): List<IndicadorEntity>

    @Query("SELECT * FROM indicadores")
    fun observeAll(): Flow<List<IndicadorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(indicador: IndicadorEntity)
}

// ============================================================
// DEPUTADOS (Camara API cache)
// ============================================================

@Entity(tableName = "deputados")
data class DeputadoEntity(
    @PrimaryKey val id: Int,
    val nome: String,
    val siglaPartido: String,
    val siglaUf: String,
    val urlFoto: String,
    val email: String = "",
    val atualizadoEm: Long
)

@Dao
interface DeputadoDao {
    @Query("SELECT * FROM deputados WHERE siglaUf = :uf ORDER BY nome ASC")
    suspend fun getByUf(uf: String): List<DeputadoEntity>

    @Query("SELECT * FROM deputados WHERE id = :id")
    suspend fun getById(id: Int): DeputadoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(deputados: List<DeputadoEntity>)

    @Query("SELECT MAX(atualizadoEm) FROM deputados WHERE siglaUf = :uf")
    suspend fun getLastUpdateForUf(uf: String): Long?

    @Query("DELETE FROM deputados WHERE siglaUf = :uf")
    suspend fun deleteByUf(uf: String)
}

// ============================================================
// DESPESAS DE DEPUTADOS (CEAP, Camara API cache)
// ============================================================

@Entity(tableName = "despesas_deputado")
data class DespesaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deputadoId: Int,
    val ano: Int,
    val mes: Int,
    val tipoDespesa: String,
    val valor: Double,
    val fornecedor: String,
    val atualizadoEm: Long
)

@Dao
interface DespesaDao {
    @Query("SELECT * FROM despesas_deputado WHERE deputadoId = :depId ORDER BY ano DESC, mes DESC")
    suspend fun getByDeputado(depId: Int): List<DespesaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(despesas: List<DespesaEntity>)

    @Query("SELECT MAX(atualizadoEm) FROM despesas_deputado WHERE deputadoId = :depId")
    suspend fun getLastUpdate(depId: Int): Long?

    @Query("DELETE FROM despesas_deputado WHERE deputadoId = :depId")
    suspend fun deleteByDeputado(depId: Int)
}

// ============================================================
// FEED ITEMS (curated remote content)
// Schema v3: fonte, fonteUrl and dataReferencia are mandatory.
// FeedSyncWorker drops any item missing them, so everything in
// this table is renderable with a visible source.
// ============================================================

@Entity(tableName = "feed_items")
data class FeedItemEntity(
    @PrimaryKey val id: String,
    val tipo: String,           // "destaque" | "voce_sabia"
    val titulo: String,
    val texto: String,
    val fonte: String,
    val fonteUrl: String,
    val dataReferencia: String, // date the fact refers to, shown in the UI
    val ativo: Boolean = true,
    val atualizadoEm: Long
)

@Dao
interface FeedDao {
    @Query("SELECT * FROM feed_items WHERE ativo = 1")
    fun observeAtivos(): Flow<List<FeedItemEntity>>

    @Query("SELECT * FROM feed_items WHERE ativo = 1")
    suspend fun getAtivos(): List<FeedItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FeedItemEntity>)

    @Transaction
    suspend fun replaceAll(items: List<FeedItemEntity>) {
        deleteAll()
        insertAll(items)
    }

    @Query("DELETE FROM feed_items")
    suspend fun deleteAll()

    @Query("SELECT MAX(atualizadoEm) FROM feed_items")
    suspend fun getLastUpdate(): Long?
}

// ============================================================
// SYNC LOG (engine telemetry, shown in Settings)
// ============================================================

@Entity(tableName = "sync_log")
data class SyncLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val worker: String,         // "feed", "law", "indicador", "event"
    val timestamp: Long,
    val success: Boolean,
    val itemsChanged: Int = 0,
    val error: String = ""
)

@Dao
interface SyncLogDao {
    @Insert
    suspend fun insert(entry: SyncLogEntity)

    @Query("SELECT * FROM sync_log WHERE worker = :worker ORDER BY timestamp DESC LIMIT 1")
    suspend fun lastFor(worker: String): SyncLogEntity?

    @Query("SELECT * FROM sync_log WHERE worker = :worker AND success = 1 ORDER BY timestamp DESC LIMIT 1")
    suspend fun lastSuccessFor(worker: String): SyncLogEntity?

    @Query("SELECT * FROM sync_log ORDER BY timestamp DESC LIMIT :n")
    fun observeRecent(n: Int = 20): Flow<List<SyncLogEntity>>

    // One row per data source for the health panel: the latest entry of each
    // worker, instead of the last N raw rows (retries used to flood the list).
    @Query("""
        SELECT * FROM sync_log s
        WHERE timestamp = (SELECT MAX(timestamp) FROM sync_log WHERE worker = s.worker)
        GROUP BY worker
        ORDER BY worker
    """)
    fun observeLatestPerWorker(): Flow<List<SyncLogEntity>>

    @Query("DELETE FROM sync_log WHERE timestamp < :cutoff")
    suspend fun purgeOlderThan(cutoff: Long)
}

// ============================================================
// DATABASE
// ============================================================

@Database(
    entities = [
        LeiEntity::class,
        IndicadorEntity::class,
        DeputadoEntity::class,
        DespesaEntity::class,
        FeedItemEntity::class,
        SyncLogEntity::class
    ],
    version = 9,
    exportSchema = false
)
abstract class TrouxaDatabase : RoomDatabase() {
    abstract fun leiDao(): LeiDao
    abstract fun indicadorDao(): IndicadorDao
    abstract fun deputadoDao(): DeputadoDao
    abstract fun despesaDao(): DespesaDao
    abstract fun feedDao(): FeedDao
    abstract fun syncLogDao(): SyncLogDao
}
