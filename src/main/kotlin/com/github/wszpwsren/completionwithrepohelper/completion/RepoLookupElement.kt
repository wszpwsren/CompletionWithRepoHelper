package com.github.wszpwsren.completionwithrepohelper.completion

import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiDocumentManager
import javax.swing.Icon

/**
 * @author: tuchg
 * @date: 2020/8/6 21:46
 * @description:
 */
class RepoLookupElement(
        // 原文本
        private val original: String?,
        private val value: String?,
        private val icon: Icon? = null
) : LookupElement() {

    private var lookupElement: LookupElement? = null

    /**
     * 据此进行文本匹配
     */
    override fun getLookupString(): String {
        return original!!
    }

    /**
     * 控制该项在补全列表最终显示效果
     */
    override fun renderElement(presentation: LookupElementPresentation) {
        // 若传入图片则按图片渲染
        icon?.let {
            presentation.icon = icon
        }
        // 复制原元素类型,包位置,icon等信息
        lookupElement?.renderElement(presentation)
        // 文本【WenBen】
        presentation.itemText = "${this.original}【${this.value}】"
    }

    /**
     * 借助原元素的编辑器文本插入能力, 如补全方法()调用等
     */
    override fun handleInsert(context: InsertionContext) {
        val editor = context.editor
        val foldingModel = editor.foldingModel
        foldingModel.runBatchFoldingOperation {
            val regions = foldingModel.allFoldRegions
            for (region in regions) {
                foldingModel.removeFoldRegion(region!!)
            }
        }
        val document = editor.document
        val startOffset = context.startOffset
        var selectionEndOffset = context.selectionEndOffset
        document.replaceString(startOffset, selectionEndOffset, value!!)
    }

}
