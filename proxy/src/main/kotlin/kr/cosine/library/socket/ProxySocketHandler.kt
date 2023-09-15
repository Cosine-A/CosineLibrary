package kr.cosine.library.socket

import kotlinx.coroutines.*
import kr.cosine.library.config.ProxyConfig
import java.io.DataInputStream
import java.net.ServerSocket
import kotlin.coroutines.CoroutineContext

class ProxySocketHandler(
    private val proxyConfig: ProxyConfig
) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    private var job: Job? = null

    private var serverSocket: ServerSocket? = null

    fun start() {
        val newServerSocket = ServerSocket(proxyConfig.port)
        serverSocket = newServerSocket

        job = launch {
            while (isActive && !newServerSocket.isClosed) {
                val socket = newServerSocket.accept()

                DataInputStream(socket.getInputStream()).use { input ->
                    while (isActive && !socket.isClosed) {

                    }
                }
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
        serverSocket?.close()
        serverSocket = null
    }
}