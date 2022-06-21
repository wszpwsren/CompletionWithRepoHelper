package com.github.wszpwsren.completionwithrepohelper.config

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.UI
import org.jetbrains.annotations.NotNull
import javax.swing.JComponent
import javax.swing.JPanel


/**
 * View层
 * @author: tuchg
 * @date: 10/10/2021 09:56
 */
class PluginSettingsView {
    private var myMainPanel: JPanel? = null
    private val myUserNameText = JBTextField()
    private val forceCompletionStatus = JBCheckBox("增强补全")
    private val repoUrlText = JBTextField()

    init {
        myMainPanel =
                FormBuilder.createFormBuilder()
                        .addLabeledComponent(JBLabel("Enter user name: "), myUserNameText, 1, false)
                        .addComponent(
                                UI.PanelFactory.panel(forceCompletionStatus)
                                        .withComment("效果等同于按两次补全快捷键，不建议开启，对性能影响较大")
                                        .createPanel(), 1
                        )
                        .addLabeledComponent(JBLabel("key your repository url: "), repoUrlText, 1, false)
                        .addComponentFillVertically(JPanel(), 0).panel
    }

    fun getPanel(): JPanel? {
        return myMainPanel
    }

    fun getPreferredFocusedComponent(): JComponent {
        return myUserNameText
    }

    @NotNull
    fun getUserNameText(): String? {
        return myUserNameText.text
    }

    @NotNull
    fun getRepoUrl(): String? {
        return repoUrlText.text
    }

    fun getForceCompletionStatus(): Boolean = forceCompletionStatus.isSelected

    fun setForceCompletionStatus(newStatus: Boolean) {
        forceCompletionStatus.isSelected = newStatus
    }


}