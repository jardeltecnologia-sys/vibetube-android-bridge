package br.com.vibetube.app.data.blogger.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BloggerHtmlParserTest {

    private val parser = BloggerHtmlParser()

    @Test
    fun `extrai ID de URL watch do YouTube`() {
        val html = """<p>Veja: <a href="https://www.youtube.com/watch?v=dQw4w9WgXcQ&t=10s">link</a></p>"""
        assertEquals("dQw4w9WgXcQ", parser.extractYouTubeId(html))
    }

    @Test
    fun `extrai ID de iframe embed`() {
        val html = """<iframe src="https://www.youtube.com/embed/AbCdEfGhIjK" width="640"></iframe>"""
        assertEquals("AbCdEfGhIjK", parser.extractYouTubeId(html))
    }

    @Test
    fun `extrai ID de youtu_be`() {
        val html = """Olha esse: https://youtu.be/xyz123ABCDE legal"""
        assertEquals("xyz123ABCDE", parser.extractYouTubeId(html))
    }

    @Test
    fun `extrai ID de Shorts`() {
        val html = """<a href="https://www.youtube.com/shorts/SHRT0001234">short</a>"""
        assertEquals("SHRT0001234", parser.extractYouTubeId(html))
    }

    @Test
    fun `extrai mp4 direto`() {
        val html = """<video><source src="https://cdn.exemplo.com/clip.mp4?v=1" type="video/mp4"/></video>"""
        val url = parser.extractDirectVideoUrl(html)
        assertNotNull(url)
        assertTrue(url!!.contains("clip.mp4"))
    }

    @Test
    fun `extrai webm`() {
        val html = """<source src="https://x.y/video.webm">"""
        assertEquals("https://x.y/video.webm", parser.extractDirectVideoUrl(html))
    }

    @Test
    fun `extrai m3u8 hls`() {
        val html = """<video src="https://stream.example.com/master.m3u8"></video>"""
        assertEquals("https://stream.example.com/master.m3u8", parser.extractDirectVideoUrl(html))
    }

    @Test
    fun `extrai primeira imagem do conteudo`() {
        val html = """
            <div>
                <p>texto</p>
                <img src="https://1.bp.blogspot.com/foo.jpg" alt="foo"/>
                <img src="https://1.bp.blogspot.com/bar.jpg"/>
            </div>
        """.trimIndent()
        assertEquals("https://1.bp.blogspot.com/foo.jpg", parser.extractFirstImageUrl(html))
    }

    @Test
    fun `cleanHtml remove tags e decodifica entidades`() {
        val html = """<p>Ol&aacute;, <b>mundo</b> &amp; tudo &#39;ok&#39;</p>"""
        val out = parser.cleanHtml(html, maxLen = 100)
        // Tags removidas, entidades decodificadas
        assertTrue("output: $out", !out.contains("<"))
        assertTrue("output: $out", out.contains("&"))
        assertTrue("output: $out", out.contains("'ok'"))
    }

    @Test
    fun `cleanHtml remove scripts inteiros`() {
        val html = """<p>texto</p><script>alert('xss')</script><p>fim</p>"""
        val out = parser.cleanHtml(html, maxLen = 200)
        assertTrue("output: $out", !out.contains("alert"))
        assertTrue("output: $out", out.contains("texto"))
        assertTrue("output: $out", out.contains("fim"))
    }

    @Test
    fun `cleanHtml respeita maxLen e usa elipse`() {
        val html = "a".repeat(500)
        val out = parser.cleanHtml(html, maxLen = 50)
        assertTrue("len=${out.length}", out.length <= 51)
        assertTrue(out.endsWith("…"))
    }

    @Test
    fun `cleanHtml com null retorna vazio`() {
        assertEquals("", parser.cleanHtml(null))
    }

    @Test
    fun `extractMedia prioriza YouTube sobre mp4`() {
        val html = """
            <iframe src="https://www.youtube.com/embed/yt00000abcd"></iframe>
            <video src="https://x.y/fallback.mp4"></video>
        """.trimIndent()
        val media = parser.extractMedia(html)
        assertEquals("yt00000abcd", media.youtubeId)
        assertEquals("https://www.youtube.com/embed/yt00000abcd", media.youtubeEmbedUrl)
    }

    @Test
    fun `youtubeThumbnail constroi URL hq`() {
        val url = parser.youtubeThumbnail("abc12345678")
        assertEquals("https://i.ytimg.com/vi/abc12345678/hqdefault.jpg", url)
    }

    @Test
    fun `youtubeThumbnail null se id null`() {
        assertNull(parser.youtubeThumbnail(null))
    }
}
