package com.beeregg2001.komorebi.data.api.interceptor

import com.beeregg2001.komorebi.data.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 設定された KonomiTV サーバーへのリクエストだけに Basic 認証ヘッダーを付与する。
 */
@Singleton
class KonomiBasicAuthInterceptor @Inject constructor(
    private val settingsRepository: SettingsRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val headers = runBlocking { settingsRepository.getKonomiBasicAuthHeaders() }
        if (headers.isEmpty()) return chain.proceed(request)

        val konomiBaseUrl = runBlocking {
            val ip = settingsRepository.konomiIp.first()
            val port = settingsRepository.konomiPort.first()
            if (ip.startsWith("http://") || ip.startsWith("https://")) {
                "$ip:$port"
            } else {
                "http://$ip:$port"
            }
        }.toHttpUrlOrNull()

        if (
            konomiBaseUrl == null ||
            request.url.host != konomiBaseUrl.host ||
            request.url.port != konomiBaseUrl.port
        ) {
            return chain.proceed(request)
        }

        val authenticatedRequest = request.newBuilder().apply {
            headers.forEach { (name, value) -> header(name, value) }
        }.build()
        return chain.proceed(authenticatedRequest)
    }
}
