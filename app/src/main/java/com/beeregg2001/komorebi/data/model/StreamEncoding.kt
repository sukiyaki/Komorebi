package com.beeregg2001.komorebi.data.model

/** KonomiTV に要求する映像エンコード方式。 */
data class StreamEncoding(
    val label: String,
    val value: String
) {
    companion object {
        const val DEFAULT_VALUE = "h264"
        val DEFAULT_ENCODINGS = listOf(
            StreamEncoding("H.264/AVC (標準)", "h264"),
            StreamEncoding("H.265/HEVC (通信節約モード)", "h265")
        )

        /**
         * 文字列からエンコード方式を取得する（利用可能なリストから検索）
         */
        fun fromValue(
            value: String,
            availableList: List<StreamEncoding> = DEFAULT_ENCODINGS
        ): StreamEncoding {
            return availableList.find { it.value.equals(value, ignoreCase = true) }
                ?: availableList.firstOrNull()
                ?: DEFAULT_ENCODINGS.first()
        }
    }
}
