package com.johnnyconsole.android.ims.session

class UserSession() {
    companion object {
        var username: String? = null
        var name: String? = null
        var access = -1

        fun construct(username: String, name: String, access: Int) {
            this.username = username
            this.name = name
            this.access = access
        }

        fun destroy() {
            username = null
            name = null
            access = -1
        }
    }
}
