package net.onelitefeather.pandorascluster.util.discord

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.onelitefeather.pandorascluster.util.discord.embed.EmbedObject
import net.onelitefeather.pandorascluster.util.propertyDiscordWebhookUrl
import java.awt.Color
import java.io.IOException
import java.net.URI
import java.nio.charset.Charset
import javax.net.ssl.HttpsURLConnection

class DiscordWebhook(val token: String, val tokenId: String) {
    var content: String? = null
    var username: String? = null
    var avatarUrl: String? = null
    var isTts: Boolean = false

    private var embeds: MutableList<EmbedObject> = ArrayList()

    fun getEmbeds(): List<EmbedObject> {
        return embeds
    }

    fun setEmbeds(embeds: MutableList<EmbedObject>) {
        this.embeds = embeds
    }

    @Throws(IOException::class)
    fun execute() {

        require(this.token.isNotEmpty() || this.tokenId.isNotEmpty()) { "The token or tokenId cannot be empty" }
        require(!(this.content == null && embeds.isEmpty())) { "Set content or add at least one EmbedObject" }

        val jsonObject = JsonObject()
        jsonObject.addProperty("content", this.content)
        jsonObject.addProperty("username", this.username)
        jsonObject.addProperty("avatar_url", this.avatarUrl)
        jsonObject.addProperty("tts", this.isTts)

        if (embeds.isNotEmpty()) {
            val embedObjects = JsonArray()

            for (embed in this.embeds) {
                val jsonEmbed = JsonObject()

                jsonEmbed.addProperty("title", embed.title)
                jsonEmbed.add("image", buildJsonImage(embed))
                jsonEmbed.add("thumbnail", buildJsonThumbnail(embed))
                jsonEmbed.addProperty("description", embed.description)
                jsonEmbed.addProperty("url", embed.url)

                val color = buildJsonColor(embed)
                jsonEmbed.addProperty("color", color.toString())
                jsonEmbed.add("footer", buildJsonFooter(embed))
                jsonEmbed.add("author", buildJsonAuthor(embed))

                val jsonFields = JsonArray()
                val fields: List<EmbedObject.EmbedField> = embed.getFields()
                for (field in fields) {
                    val jsonField = JsonObject()
                    jsonField.addProperty("name", field.name)
                    jsonField.addProperty("value", field.value)
                    jsonField.addProperty("inline", field.isInline)
                    jsonFields.add(jsonField)
                }

                jsonEmbed.add("fields", jsonFields)
                embedObjects.add(jsonEmbed)
            }

            jsonObject.add("embeds", embedObjects)

            val urlConnection = URI(propertyDiscordWebhookUrl.format(tokenId, token)).toURL()
            val connection = urlConnection.openConnection() as HttpsURLConnection
            connection.addRequestProperty("Content-Type", "application/json")
            connection.addRequestProperty("User-Agent", "JavaDiscordWebhook")
            connection.doOutput = true
            connection.requestMethod = "POST"

            val stream = connection.outputStream
            stream.write(jsonObject.toString().toByteArray(Charset.defaultCharset()))
            stream.flush()
            stream.close()

            connection.inputStream.close()
            connection.disconnect()
        }
    }

    private fun buildJsonColor(embed: EmbedObject): Int {
        val embedColor: Color = embed.color ?: Color.WHITE
        var rgb: Int = embedColor.red
        rgb = (rgb shl 8) + embedColor.green
        rgb = (rgb shl 8) + embedColor.blue
        return rgb
    }

    private fun buildJsonAuthor(embed: EmbedObject): JsonObject? {
        val embedAuthor: EmbedObject.Author = embed.author ?: return null

        val author = JsonObject()
        author.addProperty("name", embedAuthor.name)
        author.addProperty("url", embedAuthor.url)
        author.addProperty("icon_url", embedAuthor.authorIcon)
        return author
    }

    private fun buildJsonThumbnail(embed: EmbedObject): JsonObject? {
        val thumbnail: EmbedObject.Image = embed.thumbnail ?: return null

        val image = JsonObject()
        image.addProperty("url", thumbnail.url)
        return image
    }

    private fun buildJsonImage(embed: EmbedObject): JsonObject? {
        val embedImage:  EmbedObject.Image = embed.image ?: return null

        val image = JsonObject()
        image.addProperty("url", embedImage.url)
        return image
    }

    private fun buildJsonFooter(embed: EmbedObject): JsonObject? {
        val embedFooter: EmbedObject.Footer = embed.footer ?: return null

        val footer = JsonObject()
        footer.addProperty("text", embedFooter.text)
        footer.addProperty("icon_url", embedFooter.iconUrl)
        return footer
    }

    fun addEmbed(embedObject: EmbedObject) {
        embeds.add(embedObject)
    }
}