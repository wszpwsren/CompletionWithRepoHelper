package com.github.wszpwsren.completionwithrepohelper.config

import com.intellij.openapi.wm.ToolWindow
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.JPanel

/**
 * @author wengyongcheng
 * @since 2020/3/1 10:30 下午
 */
class RepoWindow(toolWindow: ToolWindow) {
    private var hideButton: JButton? = null
    private var dialogButton: JButton? = null
    var content: JPanel? = null
        private set

    private fun init() {
        dialogButton = JButton("触发自定义dialog")
        dialogButton!!.addActionListener { e: ActionEvent? ->
            if (CustomDialogWrapper().showAndGet()) {
                // 监听弹框消失，相当于show 和 getExitCode方法的结合
                println("show and get")
            }
        }
        hideButton = JButton("取消")
        content = JPanel()
        content!!.add(dialogButton)
        content!!.add(hideButton)
    }

    init {
        init()
        hideButton!!.addActionListener { e: ActionEvent? -> toolWindow.hide(null) }
    }
}