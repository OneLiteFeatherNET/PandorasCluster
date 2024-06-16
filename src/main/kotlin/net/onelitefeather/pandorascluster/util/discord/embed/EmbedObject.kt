package net.onelitefeather.pandorascluster.util.discord.embed

import java.awt.Color

class EmbedObject {
    var title: String? = null
        private set
    var description: String? = null
        private set
    var url: String? = null
        private set
    var color: Color? = null
        private set

    var footer: Footer? = null
        private set
    var thumbnail: Image? = null
        private set
    var image: Image? = null
        private set
    var author: Author? = null
        private set
    private val fields: MutableList<EmbedField> = ArrayList()

    fun setTitle(title: String): EmbedObject {
        this.title = title
        return this
    }

    fun setDescription(description: String): EmbedObject {
        this.description = description
        return this
    }

    fun setUrl(url: String): EmbedObject {
        this.url = url
        return this
    }

    fun setColor(color: Color): EmbedObject {
        this.color = color
        return this
    }

    fun setFooter(text: String, icon: String): EmbedObject {
        this.footer = Footer(text, icon)
        return this
    }

    fun setAuthor(name: String, url: String?, authorIcon: String): EmbedObject {
        this.author = Author(name, url, authorIcon)
        return this
    }

    fun addField(embedField: EmbedField): EmbedObject {
        fields.add(embedField)
        return this
    }

    fun getFields(): List<EmbedField> {
        return fields
    }

    fun addField(name: String, value: String, inline: Boolean): EmbedObject {
        fields.add(EmbedField(name, value, inline))
        return this
    }

    fun setThumbnail(url: String): EmbedObject {
        this.thumbnail = Image(url)
        return this
    }

    fun setImage(url: String): EmbedObject {
        this.image = Image(url)
        return this
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is EmbedObject) return false

        if (title != o.title) return false
        if (description != o.description) return false
        if (url != o.url) return false
        return color == o.color
    }

    override fun hashCode(): Int {
        var result = if (title != null) title.hashCode() else 0
        result = 31 * result + (if (description != null) description.hashCode() else 0)
        result = 31 * result + (if (url != null) url.hashCode() else 0)
        result = 31 * result + (if (color != null) color.hashCode() else 0)
        return result
    }

    override fun toString(): String {
        return String.format(
            "%s{" +
                    "title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", url='" + url + '\'' +
                    ", color=" + color +
                    '}', this.javaClass.simpleName
        )
    }

    class Footer internal constructor(val text: String, val iconUrl: String)

    class Image(val url: String)

    class Author(val name: String, val url: String?, val authorIcon: String)

    class EmbedField(val name: String, val value: String, val isInline: Boolean)
}