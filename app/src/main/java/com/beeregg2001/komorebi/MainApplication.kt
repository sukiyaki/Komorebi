package com.beeregg2001.komorebi

import android.app.Application
// import android.content.res.Configuration <- これを削除しました
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.beeregg2001.komorebi.data.api.interceptor.CloudflareAccessInterceptor
import com.beeregg2001.komorebi.data.api.interceptor.KonomiBasicAuthInterceptor
import com.beeregg2001.komorebi.data.worker.RecordSyncWorker
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider, ImageLoaderFactory {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var cloudflareAccessInterceptor: CloudflareAccessInterceptor

    @Inject
    lateinit var konomiBasicAuthInterceptor: KonomiBasicAuthInterceptor

    // Coil の画像取得にも接続先に応じた認証ヘッダーを付与する
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .okHttpClient {
                OkHttpClient.Builder()
                    .addInterceptor(cloudflareAccessInterceptor)
                    .addInterceptor(konomiBasicAuthInterceptor)
                    .build()
            }
            .build()
    }

    // ★ 最新の WorkManager に合わせてプロパティとしてオーバーライドします
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        // バックグラウンド同期スケジュールを登録
        RecordSyncWorker.schedule(this)
    }
}