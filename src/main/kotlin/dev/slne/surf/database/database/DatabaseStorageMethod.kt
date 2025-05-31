package dev.slne.surf.database.database

internal enum class DatabaseStorageMethod {

    LOCAL {
        override fun connect(provider: DatabaseProvider) {
            provider.connectLocal()
        }
    },

    EXTERNAL {
        override fun connect(provider: DatabaseProvider) {
            provider.connectExternal()
        }
    };

    abstract fun connect(provider: DatabaseProvider)

}