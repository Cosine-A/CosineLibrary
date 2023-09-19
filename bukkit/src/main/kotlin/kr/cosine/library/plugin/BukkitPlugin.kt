package kr.cosine.library.plugin

import com.google.common.reflect.ClassPath
import kotlinx.coroutines.*
import kr.cosine.library.CosineLibrary
import kr.cosine.library.config.YamlConfiguration
import kr.cosine.library.extension.newInstance
import kr.cosine.library.extension.yml
import kr.cosine.library.extension.*
import kr.cosine.library.kommand.KommandExecutor
import kr.cosine.library.reflection.ClassRegistry
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.full.primaryConstructor

@Suppress("UnstableApiUsage")
abstract class BukkitPlugin : JavaPlugin(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = CoroutineName("${this::class.simpleName}CoroutineScope") + Dispatchers.Default

    lateinit var config: YamlConfiguration
        private set

    lateinit var classRegistry: ClassRegistry
        private set

    open fun onStart() {}

    open fun onStop() {}

    @Deprecated("Replaced by onStart. This method should never be used.", ReplaceWith("onStart()"))
    override fun onEnable() {
        createResourceFile("config.yml")

        config = File(dataFolder, "config.yml").yml
        classRegistry = ClassRegistry()

        onStart()
        loadClass()
        registerTable()
        registerListener()
        registerCommand()
    }

    private fun loadClass() {
        val classPath = ClassPath.from(super.getClassLoader()).getTopLevelClassesRecursive(this::class.java.packageName)
        classPath.forEach { classInfo ->
            val clazz = classInfo.load().kotlin
            if (clazz.simpleName!!.endsWith("Kt")) return@forEach
            classRegistry.add(clazz)
        }
    }

    private fun registerCommand() {
        classRegistry.getInheritedClasses(KommandExecutor::class).forEach { clazz ->
            val kommandExecutor = clazz.primaryConstructor.newInstance<KommandExecutor>(this)
            kommandExecutor.register()
        }
    }

    private fun registerListener() {
        classRegistry.getInheritedClasses(Listener::class).forEach { clazz ->
            val listener = clazz.primaryConstructor.newInstance<Listener>(this)
            server.pluginManager.registerEvents(listener, this)
        }
    }

    private fun registerTable() {
        val tableClasses = classRegistry.getInheritedClasses(Table::class)
        if (tableClasses.isNotEmpty() && !CosineLibrary.instance.config.getBoolean("mysql.enabled")) {
            logger.send("Database is not enabled.", LogColor.RED)
            logger.send("Change 'mysql.enabled' to true in CosineLibrary's config.yml.", LogColor.RED)
            return
        }
        tableClasses.forEach { clazz ->
            val table = clazz.objectInstance as Table
            val database = CosineLibrary.getDataSource().database
            transaction(database) {
                val tableName = table.tableName
                if (!Table(tableName).exists()) {
                    SchemaUtils.create(table)
                    logger.send("Table[${tableName}] is created.", LogColor.GREEN)
                }
            }
        }
    }

    @Deprecated("Replaced by onStop. This method should never be used.", ReplaceWith("onStop()"))
    override fun onDisable() {
        onStop()
    }

    fun createResourceFile(path: String) {
        val inputStream = getResource(path) ?: return
        if (!dataFolder.exists()) dataFolder.mkdirs()
        val file = File(dataFolder, path)
        if (!file.exists()) {
            file.bufferedWriter().use { writer ->
                inputStream.reader().readLines().forEach { line ->
                    writer.appendLine(line)
                }
            }
        }
    }
}