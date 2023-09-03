package chat.commons.routing

import kotlinx.serialization.Serializable


@Serializable
/**
 * Message payload for POST requests.
 * @param from is the sender of the message with their id
 * @param to is the receiver of the message
 * @param message is the message content
 * @see sent is the time the message was received (from the sender)
 */
data class MessagePayloadPOST(val from: ReceiverPayloadWithId, val to: ReceiverPayload, val message: String)


