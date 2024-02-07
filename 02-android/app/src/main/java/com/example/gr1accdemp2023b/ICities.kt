package com.example.gr1accdemp2023b

class ICities(
    public var name: String?,
    public var state: String?,
    public var country: String?,
    public var capital: Boolean?,
    public var population: Long?,
    public var regions: List<String>?
) {
    override fun toString(): String {
        return "${name} - ${country}"
    }
}