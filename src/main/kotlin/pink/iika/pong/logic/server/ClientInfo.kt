package pink.iika.pong.logic.server

import kotlinx.serialization.Serializable
import java.net.InetAddress

@Serializable
data class ClientInfo(@Serializable(with = InetAddressSerializer::class) val address: InetAddress, val port: Int)