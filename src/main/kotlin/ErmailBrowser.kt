interface ErmailBrowser {
    fun read(maxReadEmailCount: Int = 1): Int
    fun login()
}