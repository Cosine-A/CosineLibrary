package kr.cosine.library

import kr.cosine.library.command.IntegerArgumentProvider
import kr.cosine.library.command.PlayerArgumentProvider
import kr.cosine.library.command.StringArgumentProvider
import kr.cosine.library.command.TestCommand
import kr.cosine.library.database.DataSource
import kr.cosine.library.extension.LogColor
import kr.cosine.library.extension.info
import kr.cosine.library.kommand.argument.ArgumentRegistry
import kr.cosine.library.plugin.BukkitPlugin
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder

class CosineLibrary : BukkitPlugin() {

    companion object {
        internal lateinit var plugin: CosineLibrary
            private set

        private var dataSource: DataSource? = null

        fun getDataSource(): DataSource = dataSource ?: throw NullPointerException("Database is not loaded.")
    }

    override fun onLoad() {
        plugin = this
    }

    override fun onStart() {
        setupDatabase()

        registerArgumentProvider(
            StringArgumentProvider(),
            IntegerArgumentProvider(),
            PlayerArgumentProvider()
        )
        registerCommand(
            TestCommand(this)
        )
    }

    override fun onStop() {
        dataSource?.close()
    }

    private fun setupDatabase() {
        if (config.getBoolean("mysql.enabled")) {
            val newDataSource = DataSource(config)
            newDataSource.connect()
            logger.info("Database is connected.", LogColor.GREEN)
            dataSource = newDataSource
        }
    }
}