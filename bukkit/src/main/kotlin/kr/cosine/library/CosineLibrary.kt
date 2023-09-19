package kr.cosine.library

import kr.cosine.library.database.DataSource
import kr.cosine.library.extension.LogColor
import kr.cosine.library.extension.send
import kr.cosine.library.plugin.BukkitPlugin

class CosineLibrary : BukkitPlugin() {

    companion object {
        internal lateinit var instance: CosineLibrary
            private set

        private var dataSource: DataSource? = null

        fun getDataSource(): DataSource = dataSource ?: throw NullPointerException("Database is not loaded.")
    }

    override fun onLoad() {
        instance = this
    }

    override fun onStart() {
        setupDatabase()
    }

    override fun onStop() {
        dataSource?.close()
    }

    private fun setupDatabase() {
        if (config.getBoolean("mysql.enabled")) {
            val newDataSource = DataSource(config)
            newDataSource.connect()
            logger.send("Database is connected.", LogColor.GREEN)
            dataSource = newDataSource
        }
    }
}