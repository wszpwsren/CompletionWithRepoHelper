package pansong291.simplepinyin

/**
 * @author pansong291
 * @author tuchg
 * @date 2018/9/12
 * @date 2020/8/7
 */
object Pinyin {
    /**
     * 全部大写
     */
    const val UP_CASE = -1

    /**
     * 首字母大写
     */
    const val FIRST_UP_CASE = 0

    /**
     * 全部小写
     */
    const val LOW_CASE = 1

    /**
     * 将输入字符串转为拼音，以字符为单位插入分隔符，多个拼音只取其中一个
     *
     *
     * 例: "hello:中国！"  在separator为","时，输出： "H,E,L,L,O,:,Zhong,Guo,!"
     *
     * @param str       输入字符串
     * @param separator 分隔符
     * @return 中文转为拼音的字符串
     */
    fun toPinyin(str: String?, separator: String?): String? {
        return toPinyin(str, separator, FIRST_UP_CASE)
    }

    /**
     * 是否含有中文字
     *
     * @param str
     * @return
     */
    fun hasChinese(str: String): Boolean {
        for (c in str.toCharArray()) {
            if (PinyinData.MIN_VALUE <= c && c <= PinyinData.MAX_VALUE && getPinyinCode(c) > 0
                    || PinyinData.CHAR_12295 === c) {
                return true
            }
        }
        return false
    }

    /**
     * 将输入字符串转为拼音，以字符为单位插入分隔符，多个拼音只取其中一个
     *
     * @param str       输入字符串
     * @param separator 分隔符
     * @param caseType  大小写类型
     * @return 中文转为拼音的字符串
     */
    fun toPinyin(str: String?, separator: String?, caseType: Int): String? {
        if (str == null || str.length == 0) {
            return str
        }
        val resultPinyinStrBuf = StringBuilder()

        for (i in 0 until str.length) {
            resultPinyinStrBuf.append(toPinyin(str[i], caseType)[0])
            if (i != str.length - 1) {
                resultPinyinStrBuf.append(separator)
            }
        }
        return resultPinyinStrBuf.toString()
    }

    /**
     * 若输入字符是中文则转为拼音，若不是则返回该字符，支持多音字
     *
     * @param c 输入字符
     * @return 拼音字符串数组
     */
    fun toPinyin(c: Char): Array<String?> {
        return toPinyin(c, FIRST_UP_CASE)
    }

    /**
     * 若输入字符是中文则转为拼音，若不是则返回该字符，支持多音字
     *
     * @param c        输入字符
     * @param caseType 大小写类型
     * @return 拼音字符串数组
     */
    fun toPinyin(c: Char, caseType: Int): Array<String?> {
        var result = getPinyin(c, caseType)
        if (result == null) {
            var str: String = c.toString()
            when (caseType) {
                UP_CASE, FIRST_UP_CASE -> str = str.toUpperCase()
                LOW_CASE -> str = str.toLowerCase()
            }
            result = arrayOf(str)
        }
        return result
    }

    /**
     * 若输入字符是中文则转为拼音，若不是则返回null，支持多音字
     * 可用于判断该字符是否是中文汉字
     *
     * @param c 输入字符
     * @return 拼音字符串数组
     */
    fun getPinyin(c: Char): Array<String?>? {
        return getPinyin(c, FIRST_UP_CASE)
    }

    /**
     * 若输入字符是中文则转为拼音，若不是则返回null，支持多音字
     * 可用于判断该字符是否是中文汉字
     *
     * @param c        输入字符
     * @param caseType 大小写类型
     * @return 拼音字符串数组
     */
    fun getPinyin(c: Char, caseType: Int): Array<String?>? {
        var result: Array<String?>? = null
        val charIndex = getPinyinCode(c)
        if (charIndex < 0) {
            return null
        } else if (charIndex == 0) {
            result = arrayOf(PinyinData.PINYIN_12295)
        } else {
            val duoyin = getDuoyin(c)
            if (duoyin == null) {
                result = arrayOf(PinyinData.PINYIN_TABLE.get(charIndex))
            } else {
                result = arrayOfNulls(duoyin.size + 1)
                result[0] = PinyinData.PINYIN_TABLE.get(charIndex)
                System.arraycopy(duoyin, 0, result, 1, duoyin.size)
            }
        }
        for (i in result.indices) {
            when (caseType) {
                UP_CASE -> result[i] = result[i]?.toUpperCase()
                LOW_CASE -> result[i] = result[i]?.toLowerCase()
                FIRST_UP_CASE -> {
                }
            }
        }
        return result
    }

    /**
     * 删除小写字母
     *
     * @param firstUpCase 首字母大写的拼音
     * @return 拼音首字母
     */
    fun deleteLowerCase(firstUpCase: String): String {
        val sb = StringBuilder()
        var c: Char
        for (i in 0 until firstUpCase.length) {
            c = firstUpCase[i]
            if (c < 'a' || c > 'z') {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    private fun getPinyinCode(c: Char): Int {
        if (PinyinData.CHAR_12295 === c) {
            return 0
        } else if (c < PinyinData.MIN_VALUE || c > PinyinData.MAX_VALUE) {
            return -1
        }
        val offset: Int = c - PinyinData.MIN_VALUE
        return if (offset < PinyinData.PINYIN_CODE_1_OFFSET) {
            decodeIndex(PinyinCode1.PINYIN_CODE_PADDING, PinyinCode1.PINYIN_CODE, offset).toInt()
        } else if (offset < PinyinData.PINYIN_CODE_2_OFFSET) {
            decodeIndex(PinyinCode2.PINYIN_CODE_PADDING, PinyinCode2.PINYIN_CODE,
                    offset - PinyinData.PINYIN_CODE_1_OFFSET).toInt()
        } else {
            decodeIndex(PinyinCode3.PINYIN_CODE_PADDING, PinyinCode3.PINYIN_CODE,
                    offset - PinyinData.PINYIN_CODE_2_OFFSET).toInt()
        }
    }

    fun getDuoyin(c: Char): Array<String?>? {
        var duoyin: Array<String?>? = null
        var duoyinCode: ShortArray? = null
        val i: Int = DuoyinCode.getIndexOfDuoyinCharacter(c)
        if (i >= 0) {
            duoyinCode = DuoyinCode.decodeDuoyinIndex(i)
            duoyin = arrayOfNulls(duoyinCode.size)
            for (j in duoyinCode.indices) {
                duoyin[j] = PinyinData.PINYIN_TABLE.get(duoyinCode[j].toInt())
            }
        }
        return duoyin
    }

    private fun decodeIndex(paddings: ByteArray, indexes: ByteArray, offset: Int): Short {
        val index1 = offset / 8
        val index2 = offset % 8
        //低8位
        var realIndex: Short = (((indexes[offset]).toInt() and 0xff).toShort())
        //高1位，非0即1
        if ((paddings[index1].toInt() and PinyinData.BIT_MASKS.get(index2)) != 0) {
            realIndex = (realIndex.toInt() or PinyinData.PADDING_MASK).toShort()
        }
        return realIndex
    }
}