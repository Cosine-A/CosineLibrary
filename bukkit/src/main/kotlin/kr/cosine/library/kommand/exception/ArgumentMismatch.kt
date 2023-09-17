package kr.cosine.library.kommand.exception

class ArgumentMismatch(val path: String) : Exception(path)