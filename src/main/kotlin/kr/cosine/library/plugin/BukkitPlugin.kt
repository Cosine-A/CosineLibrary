package kr.cosine.library.plugin

import com.google.common.reflect.ClassPath
import kotlinx.coroutines.*
import kr.cosine.library.CosineLibrary
import kr.cosine.library.extension.newInstance
import kr.cosine.library.extension.*
import kr.cosine.library.kommand.KommandExecutor
import kr.cosine.library.kommand.language.LanguageRegistry
import kr.cosine.library.registry.ClassRegistry
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

    lateinit var classRegistry: ClassRegistry
        private set

    lateinit var globalLanguageRegistry: LanguageRegistry
        private set

    lateinit var languageRegistry: LanguageRegistry
        private set

    open fun onStart() {}

    open fun onStop() {}

    @Deprecated("Replaced by onStart. This method should never be used.", ReplaceWith("onStart()"))
    override fun onEnable() {
        dataFolder.createFolder()
        saveDefaultConfig()

        classRegistry = ClassRegistry()
        val languageFolder = File(dataFolder, "language").createFolder()
        if (this is CosineLibrary) {
            val globalLanguageFolder = File(dataFolder, "global-language").createFolder()
            globalLanguageRegistry = LanguageRegistry(globalLanguageFolder)
            globalLanguageRegistry.load(this, true)
        }
        globalLanguageRegistry = CosineLibrary.instance.globalLanguageRegistry
        languageRegistry = LanguageRegistry(languageFolder)
        languageRegistry.load(this)

        onStart()
        loadClass()
        registerTable()
        registerListener()
        registerCommand()
    }

    private fun loadClass() {
        val classPath = ClassPath.from(super.getClassLoader()).getTopLevelClassesRecursive(this::class.java.packageName)
        classPath.forEach { classInfo ->
            val clazz = classInfo.load()
            val kClazz = clazz.kotlin
            if (kClazz.simpleName!!.endsWith("Kt") || clazz.isAnnotation) return@forEach
            classRegistry.add(kClazz)
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
}