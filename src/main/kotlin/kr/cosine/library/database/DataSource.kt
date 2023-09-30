package kr.cosine.library.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.bukkit.configuration.file.FileConfiguration
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class DataSource(
    private val config: FileConfiguration
) {

    private val lazyHikariDataSource = lazy {
        HikariDataSource(
            HikariConfig().apply {
                val host = config.getString("mysql.host")!!
                val port = config.getInt("mysql.port")
                val database = config.getString("mysql.database")!!
                this.jdbcUrl = "jdbc:mysql://$host:$port/$database"
                this.username = config.getString("mysql.user")!!
                this.password = config.getString("mysql.password")!!
                this.maximumPoolSize = config.getInt("mysql.max-pool-size")
                this.minimumIdle = config.getInt("mysql.min-pool-size")
            }
        )
    }
    private val lazyDatabase = lazy { Database.connect(hikariDataSource) }

    private val hikariDataSource get() = lazyHikariDataSource.value
    val database get() = lazyDatabase.value

    fun connect() {
        database
    }

    fun close() {
        if (lazyHikariDataSource.isInitialized()) {
            hikariDataSource.close()
        }
    }

    suspend fun <T> query(function: suspend (Transaction) -> T): T {
        return newSuspendedTransaction(Dispatchers.IO, database) {
            function(this)
        }
    }
}