package com.github.wszpwsren.completionwithrepohelper.config.window

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/**
 *
 * @author KolRigo
 * @date 2022/6/21 2054
 * @version
 * @update
 */
object NacosGetter {
    fun getConfig(serverAddr: String?): String? {
        var config = ""
        var build = Request.Builder()
                .url(serverAddr + "/nacos/v1/cs/configs?dataId=completion-repo.json&group=DEFAULT_GROUP")
                .get()
                .build()
        val okHttpClient = OkHttpClient()
        var newCall = okHttpClient.newCall(build)
        try {
            var response = newCall.execute()
            config = response.body!!.string()
        } catch (e: IOException) {
            println(e.message)
        }
        return config
    }
}