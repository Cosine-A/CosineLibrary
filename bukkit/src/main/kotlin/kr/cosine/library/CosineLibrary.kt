package kr.cosine.library

import kr.cosine.library.database.DataSource
import kr.cosine.library.extension.LogColor
import kr.cosine.library.extension.info
import kr.cosine.library.plugin.BukkitPlugin

class CosineLibrary : BukkitPlugin() {

    companion object {
        private lateinit var dataSource: DataSource

        fun getDataSource(): DataSource = dataSource
    }

    override fun onStart() {
        setupDatabase()
    }

    override fun onStop() {
        dataSource.close()
    }

    private fun setupDatabase() {
        if (yml.getBoolean("mysql.enabled")) {
            val newDataSource = DataSource(yml)
            newDataSource.connect()
            logger.info("Database is connected.", LogColor.GREEN)
            dataSource = newDataSource
        }
    }
}