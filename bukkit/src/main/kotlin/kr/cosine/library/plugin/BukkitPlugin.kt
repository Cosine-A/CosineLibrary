package kr.cosine.library.plugin

import io.github.classgraph.ClassGraph
import kotlinx.coroutines.*
import kr.cosine.library.CosineLibrary
import kr.cosine.library.config.extension.yml
import kr.cosine.library.extension.*
import kr.cosine.library.kommand.KommandExecutor
import kr.cosine.library.kommand.argument.ArgumentProvider
import kr.cosine.library.kommand.argument.ArgumentRegistry
import kr.cosine.library.reflection.ClassNameRegistry
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

abstract class BukkitPlugin : JavaPlugin(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = CoroutineName("${this::class.simpleName}CoroutineScope") + Dispatchers.Default

    val config = File(dataFolder, "config.yml").yml

    private val classGraph =
        ClassGraph().enableClassInfo().enableAnnotationInfo().whitelistPackages(this::class.packageName)

    open fun onStart() {}

    open fun onStop() {}

    @Deprecated("Replaced by onStart. This method should never be used.", ReplaceWith("onStart()"))
    override fun onEnable() {
        loadConfig()
        onStart()
        ClassGraph().enableClassInfo().enableAnnotationInfo().whitelistPackages(this::class.packageName).scan().use { scanResult ->
            // 고쳐야됨
            scanResult.allClasses.forEach { classInfo ->
                println("classInfo: ${classInfo.simpleName}")
                ClassRegistry.add(classInfo)
            }
        }
        // ClassInfo use 끝나고 못씀
        ClassRegistry.getInheritedClasses(KommandExecutor::class).forEach { classInfo ->
            val clazz = classInfo.loadClass().kotlin
            val qualifiedName = clazz.qualifiedName!!
            if (ClassNameRegistry.contains(qualifiedName)) return@forEach
            val kommandExecutor = runCatching {
                clazz.primaryConstructor?.call(this@BukkitPlugin) as KommandExecutor
            }.getOrNull() ?: run {
                logger.info("${clazz.simpleName} class's primary constructor call failed.", LogColor.RED)
                return@forEach
            }
            ClassNameRegistry.add(qualifiedName)
            kommandExecutor.register()
        }
    }

    @Deprecated("Replaced by onStop. This method should never be used.", ReplaceWith("onStop()"))
    override fun onDisable() {
        onStop()
    }

    fun registerArgumentProvider(vararg argumentProviders: ArgumentProvider<*>) {
        argumentProviders.forEach { argumentProvider ->
            ArgumentRegistry.registerArgument(argumentProvider)
        }
    }

    fun registerCommand(vararg kommandExecutors: KommandExecutor) {
        kommandExecutors.forEach(KommandExecutor::register)
    }

    fun registerListener(vararg listeners: Listener) {
        listeners.forEach { listener ->
            server.pluginManager.registerEvents(listener, this)
        }
    }

    private fun loadConfig() {
        val inputStream = getResource("config.yml") ?: return
        if (!dataFolder.exists()) dataFolder.mkdirs()
        val file = File(dataFolder, "config.yml")
        if (!file.exists()) {
            file.bufferedWriter().use { writer ->
                inputStream.reader().readLines().forEach { line ->
                    writer.appendLine(line)
                }
                config.reload()
            }
        }
    }

    protected fun setupTable(vararg tables: Table) {
        if (!CosineLibrary.plugin.config.getBoolean("mysql.enabled")) {
            logger.info("Database is not enabled.", LogColor.RED)
            logger.info("Change 'mysql.enabled' to true in CosineLibrary's config.yml.", LogColor.RED)
            return
        }
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