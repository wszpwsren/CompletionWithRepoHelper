package com.github.wszpwsren.completionwithrepohelper.config.window

import com.github.wszpwsren.completionwithrepohelper.config.PluginSettingsState
import com.google.gson.Gson
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.ValidationInfo
import org.apache.commons.lang.StringUtils
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.ActionEvent
import javax.swing.*

/**
 * @author KolRigo
 * @date 2022/8/5
 */
class CustomDialogWrapper : DialogWrapper(true) {
    private var inputTextField: JTextField? = null
    private var okAction: CustomOKAction? = null
    private var exitAction: DialogWrapperExitAction? = null

    /**
     * 创建视图
     * @return
     */
    override fun createCenterPanel(): JComponent {
        val panel = JPanel()
        panel.layout = BorderLayout()
        val label = JLabel("输入仓库地址")
        label.preferredSize = Dimension(100, 100)
        panel.add(label, BorderLayout.CENTER)
        inputTextField = JTextField()
        panel.add(inputTextField, BorderLayout.NORTH)
        return panel
    }

    /**
     * 校验数据
     * @return 通过必须返回null，不通过返回一个 ValidationInfo 信息
     */
    override fun doValidate(): ValidationInfo? {
        val text = inputTextField!!.text
        return if (StringUtils.isNotBlank(text)) {
            val config = NacosGetter.getConfig(text)
            if (config == null) {
                return ValidationInfo("校验不通过")
            } else {
                PluginSettingsState.instance.dictMap = Gson().fromJson(config, HashMap::class.java) as HashMap<String?, String?>
                return null
            }
        } else {
            ValidationInfo("校验不通过")
        }
    }

    /**
     * 覆盖默认的ok/cancel按钮
     * @return
     */
    override fun createActions(): Array<Action> {
        exitAction = DialogWrapperExitAction("cancel", CANCEL_EXIT_CODE)
        okAction = CustomOKAction()
        // 设置默认的焦点按钮
        okAction!!.putValue(DEFAULT_ACTION, true)
        return arrayOf(okAction!!, exitAction!!)
    }

    /**
     * 自定义 ok Action
     */
    protected inner class CustomOKAction : DialogWrapperAction("ok") {
        override fun doAction(e: ActionEvent) {
            // 点击ok的时候进行数据校验
            val validationInfo = doValidate()
            if (validationInfo != null) {
                Messages.showMessageDialog(validationInfo.message, "校验不通过", Messages.getInformationIcon())
            } else {
                close(CANCEL_EXIT_CODE)
            }
        }
    }

    init {
        init()
        title = "自定义dialog"
    }
}