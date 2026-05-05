package br.com.vibetube.app.data.blogger.comments

import okhttp3.OkHttpClient
import org.junit.Assert.assertTrue
import org.junit.Test

class BloggerCommentsDataSourceTest {

    private val client = OkHttpClient()

    @Test
    fun `endpoint sem apiKey contem fetchBodies e maxResults`() {
        val ds = BloggerCommentsDataSource(client, apiKey = null)
        val url = ds.buildEndpoint("111", "222", 50)
        assertTrue(url.startsWith("https://www.googleapis.com/blogger/v3/blogs/111/posts/222/comments"))
        assertTrue(url.contains("fetchBodies=true"))
        assertTrue(url.contains("maxResults=50"))
        assertTrue(!url.contains("key="))
    }

    @Test
    fun `endpoint com apiKey inclui key`() {
        val ds = BloggerCommentsDataSource(client, apiKey = "AIzaTEST")
        val url = ds.buildEndpoint("111", "222", 25)
        assertTrue(url.contains("key=AIzaTEST"))
        assertTrue(url.contains("maxResults=25"))
    }

    @Test
    fun `endpoint usa blogId e postId fornecidos`() {
        val ds = BloggerCommentsDataSource(client)
        val url = ds.buildEndpoint("ABC", "XYZ", 10)
        assertTrue(url.contains("/blogs/ABC/posts/XYZ/comments"))
    }
}
