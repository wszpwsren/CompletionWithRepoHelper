package com.github.wszpwsren.completionwithrepohelper.completion.contributors

import com.github.tuchg.nonasciicodecompletionhelper.utils.countContainsSomeChar
import com.github.tuchg.nonasciicodecompletionhelper.utils.toPinyin
import com.github.wszpwsren.completionwithrepohelper.completion.ChineseLookupElement
import com.github.wszpwsren.completionwithrepohelper.completion.ChinesePrefixMatcher
import com.github.wszpwsren.completionwithrepohelper.completion.RepoLookupElement
import com.github.wszpwsren.completionwithrepohelper.config.PluginSettingsState
import com.github.wszpwsren.completionwithrepohelper.utils.Pinyin
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.text.StringUtil

/**
 * @author KolRigo
 * @date 2022-8-1
 */
open class CompletionWithRepoContributor : CompletionContributor() {

    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        val pluginSettingsState = PluginSettingsState.instance
        // feature:可暴力解决 bug:二次激活获取补全 但性能影响较大
        if (pluginSettingsState.enableForceCompletion) {
            parameters.withInvocationCount(2)
        }
        val prefix = result.prefixMatcher.prefix
        val resultSet = result
                .withPrefixMatcher(ChinesePrefixMatcher(result.prefixMatcher))
        var iterator = PluginSettingsState.instance.dictMap.iterator()
        while (iterator.hasNext()){
            var next = iterator.next()
            var key = next.key
            var value = next.value
            val chineseLookupElement = RepoLookupElement(key, value)
            resultSet.consume(chineseLookupElement)
        }
        // 先跳过当前 Contributors 获取包装后的 lookupElement而后进行修改装饰
        resultSet.runRemainingContributors(parameters) { r ->
            val element = r.lookupElement
            if (Pinyin.hasChinese(element.lookupString)) {
                // 从多音字列表提取命中次数最多的一个
                val closest = toPinyin(
                        element.lookupString,
                        Pinyin.FIRST_UP_CASE
                ).maxByOrNull { str -> countContainsSomeChar(str!!.toLowerCase(), prefix) }
                closest?.let {
                    //todo 完全匹配的优先级需提高
                    val priority = if (prefix.isNotEmpty()) StringUtil.difference(it, prefix) * 100.0 else 5.0
                    // 追加补全列表
                    renderElementHandle(element, it, priority, resultSet, r)
                }
            } else
                resultSet.passResult(r)
        }
        // 修复 输入单个字符本贡献器无响应
        resultSet.restartCompletionWhenNothingMatches()
    }

    open val renderElementHandle:
            (element: LookupElement, pinyin: String, priority: Double, rs: CompletionResultSet, r: CompletionResult) ->
            Unit = { element, pinyin, priority, rs, r ->
        val chineseLookupElement = ChineseLookupElement(element.lookupString, pinyin).copyFrom(element)
        val withPriority = PrioritizedLookupElement.withPriority(chineseLookupElement, priority)
        val wrap = CompletionResult.wrap(withPriority, r.prefixMatcher, r.sorter)
        rs.passResult(wrap!!)
    }
}
