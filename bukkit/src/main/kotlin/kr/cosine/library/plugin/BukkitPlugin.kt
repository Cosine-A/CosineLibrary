package kr.cosine.library.plugin

import kotlinx.coroutines.*
import kr.cosine.library.CosineLibrary
import kr.cosine.library.config.extension.yml
import kr.cosine.library.extension.LogColor
import kr.cosine.library.extension.info
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import kotlin.coroutines.CoroutineContext

abstract class BukkitPlugin : JavaPlugin(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = CoroutineName("${this::class.simpleName}CoroutineScope")

    val config = File(dataFolder, "config.yml").yml

    open fun onStart() {}

    open fun onStop() {}

    @Deprecated("Replaced by onStart. This method should never be used.", ReplaceWith("onStart()"))
    override fun onEnable() = runBlocking {
        loadConfig()
        onStart()
    }

    @Deprecated("Replaced by onStop. This method should never be used.", ReplaceWith("onStop()"))
    override fun onDisable() {
        onStop()
    }

    private fun loadConfig() {
        val inputStream = getResource("config.yml") ?: return
        if (!dataFolder.exists()) dataFolder.mkdirs()
        val file = File(dataFolder, "config.yml")
        if (!file.exists()) {
            file.bufferedWriter().use { writer ->
                val inputStreamReader = inputStream.reader()
                inputStreamReader.readLines().forEach { line ->
                    writer.appendLine(line)
                }
            }
        }
    }

    protected fun setupTable(vararg tables: Table) {
        tables.forEach { table ->
            val database = CosineLibrary.getDataSource().database
            transaction(database) {
                val tableName = table.tableName
                if (!Table(tableName).exists()) {
                    SchemaUtils.create(table)
                    logger.info("Table[${tableName}] is created.", LogColor.GREEN)
                }
            }
        }
    }
}