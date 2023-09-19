package kr.cosine.library

import kr.cosine.library.database.DataSource
import kr.cosine.library.extension.LogColor
import kr.cosine.library.extension.info
import kr.cosine.library.plugin.BukkitPlugin

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

        /*registerArgumentProvider(
            StringArgumentProvider(),
            IntegerArgumentProvider(),
            PlayerArgumentProvider()
        )
        registerCommand(
            TestCommand(this)
        )*/
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