package chat.commons.protocol

import chat.commons.routing.ReceiverPayload
import chat.commons.routing.ReceiverPayloadWithId
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class MessagePayloadSocket(val from: ReceiverPayload, val to: ReceiverPayload, val message: String, val sent: LocalDateTime)

@Serializable
data class AuthPayloadSocket(val receiver: ReceiverPayloadWithId, val lastMessage: Long = 0)

sealed class Protocol<T> {
    abstract var type : String
    abstract var payload: T

    @Serializable
    class ACK : Protocol<Long>() {
        override var type: String = "ACK"
        override var payload: Long = 0
    }

    @Serializable
    class AUTH : Protocol<AuthPayloadSocket>() {
        override var type: String = "AUTH"
        override lateinit var payload: AuthPayloadSocket
    }

    @Serializable
    class MESSAGE : Protocol<MessagePayloadSocket>() {
        override var type: String = "MESSAGE"
        override lateinit var payload: MessagePayloadSocket
    }
}

fun <T> Protocol<T>.isAck() = this.type == "ACK"
fun <T> Protocol<T>.isAuth() = this.type == "AUTH"
fun <T> Protocol<T>.isMessage() = this.type == "MESSAGE"

suspend fun <T> Protocol<T>.ack(block: suspend (Protocol.ACK) -> Unit) {
    if (this.isAck()) {
        val ack = this as Protocol.ACK
        block(ack)
    }
}

suspend fun <T> Protocol<T>.auth(block: suspend (Protocol.AUTH) -> Unit) {
    if (this.isAuth()) {
        val auth = this as Protocol.AUTH
        block(auth)
    }
}

suspend fun <T> Protocol<T>.message(block: suspend (Protocol.MESSAGE) -> Unit) {
    if (this.isMessage()) {
        val message = this as Protocol.MESSAGE
        block(message)
    }
}