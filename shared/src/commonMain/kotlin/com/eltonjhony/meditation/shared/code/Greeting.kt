package com.eltonjhony.meditation.shared.code

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}