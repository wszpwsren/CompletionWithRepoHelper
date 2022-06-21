package com.github.wszpwsren.completionwithrepohelper.config

import com.intellij.openapi.components.ServiceManager.getService
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil.copyBean

/**
 * 持久保存用户配置 -Model
 * @author: tuchg
 * @date: 2020/11/20 14:01
 */
@State(name = "RCompletionHelperSettings", storages = [(Storage("pinyin_completion_helper.xml"))])
class PluginSettingsState : PersistentStateComponent<PluginSettingsState> {

    // 激活强力补全 用于暴力补全部分补全未显示的问题
    var enableForceCompletion: Boolean = false

    companion object {
        val instance: PluginSettingsState
            get() = getService(PluginSettingsState::class.java)
    }

    override fun getState() = this

    override fun loadState(state: PluginSettingsState) {
        copyBean(state, this)
    }
}