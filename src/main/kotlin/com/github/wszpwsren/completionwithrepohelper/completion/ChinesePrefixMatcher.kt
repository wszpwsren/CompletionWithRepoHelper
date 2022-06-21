package com.github.wszpwsren.completionwithrepohelper.completion

import com.github.tuchg.nonasciicodecompletionhelper.utils.countContainsSomeChar
import com.github.tuchg.nonasciicodecompletionhelper.utils.toPinyin
import com.github.wszpwsren.completionwithrepohelper.utils.Pinyin
import com.intellij.codeInsight.completion.PlainPrefixMatcher
import com.intellij.codeInsight.completion.PrefixMatcher

/**
 * 转换中文使之与IDE内提取标识符进行有效识别的关键类
 * @author: tuchg
 * @date: 2021/2/4 13:58
 */
class ChinesePrefixMatcher(prefixMatcher: PrefixMatcher) : PlainPrefixMatcher(prefixMatcher.prefix) {
    private var originalMatcher: PrefixMatcher? = prefixMatcher

    override fun prefixMatches(name: String): Boolean {
        return if (Pinyin.hasChinese(name)) {
            for (s in toPinyin(name, Pinyin.LOW_CASE)) {
                if (countContainsSomeChar(s, prefix) >= prefix.length) {
                    return true
                }
            }
            return false
        } else originalMatcher?.prefixMatches(name) == true
    }


    override fun cloneWithPrefix(prefix: String) =
            if (prefix == this.prefix) this else ChinesePrefixMatcher(originalMatcher!!.cloneWithPrefix(prefix))
}
