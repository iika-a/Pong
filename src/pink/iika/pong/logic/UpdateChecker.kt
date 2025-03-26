package pink.iika.pong.logic

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import org.json.JSONObject
import java.io.IOException

object UpdateChecker {
    fun isUpdateNeeded(currentVersion: String): Boolean {
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.github.com/repos/iika-a/pong/releases/latest"))
            .header("Accept", "application/vnd.github.v3+json")
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() != 200) {
            throw IOException("Unexpected response code: ${response.statusCode()}")
        }

        val latestVersion = JSONObject(response.body()).getString("tag_name")
        return latestVersion != currentVersion
    }
}
