package chat.commons.protocol

import chat.commons.routing.ReceiverPayload
import chat.commons.routing.ReceiverPayloadWithId
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class MessagePayloadSocket(val from: ReceiverPayload, val to: ReceiverPayload, val message: String, val sent: LocalDateTime)

@Serializable
data class AuthPayloadSocket(val lastMessage: Long = 0)

@Serializable
sealed class Protocol {
    protected abstract var kind : String

    @Serializable
    class ACK : Protocol() {
        override var kind: String = "ACK"
        var payload: Long = 0
    }

    @Serializable
    class AUTH : Protocol() {
        override var kind: String = "AUTH"
        lateinit var payload: AuthPayloadSocket
    }

    @Serializable
    class MESSAGE : Protocol() {
        override var kind: String = "MESSAGE"
        lateinit var payload: MessagePayloadSocket
    }
}

fun Protocol.isAck() = this is Protocol.ACK
fun Protocol.isAuth() = this is Protocol.AUTH
fun Protocol.isMessage() = this is Protocol.MESSAGE

fun ack(block: Protocol.ACK.() -> Unit): Protocol {
    val ack = Protocol.ACK()
    ack.block()
    return ack
}

suspend fun Protocol.ack(block: suspend (Protocol.ACK) -> Unit) {
    if (this.isAck()) {
        val ack = this as Protocol.ACK
        block(ack)
    }
}

fun auth(block: Protocol.AUTH.() -> Unit): Protocol {
    val auth = Protocol.AUTH()
    auth.block()
    return auth
}

suspend fun Protocol.auth(block: suspend (Protocol.AUTH) -> Unit) {
    if (this.isAuth()) {
        val auth = this as Protocol.AUTH
        block(auth)
    }
}

fun message(block: Protocol.MESSAGE.() -> Unit): Protocol {
    val message = Protocol.MESSAGE()
    message.block()
    return message
}

suspend fun Protocol.message(block: suspend (Protocol.MESSAGE) -> Unit) {
    if (this.isMessage()) {
        val message = this as Protocol.MESSAGE
        block(message)
    }
}