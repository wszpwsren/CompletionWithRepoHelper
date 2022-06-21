package com.github.wszpwsren.completionwithrepohelper.config

import com.alibaba.nacos.api.NacosFactory
import com.alibaba.nacos.api.exception.NacosException
import java.util.*

/**
 *
 * @author KolRigo
 * @date 2022/6/21 2054
 * @version
 * @update
 */
object NacosGetter {
    fun getConfig(serverAddr: String?): String? {
        return try {
            val properties = Properties()
            properties["serverAddr"] = serverAddr
            val configService = NacosFactory.createConfigService(properties)
            configService.getConfig("completion-repo","DEFAULT_GROUP", 3000)
        } catch (e: NacosException) {
            null
        }
    }
}