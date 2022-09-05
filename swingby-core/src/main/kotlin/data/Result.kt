package de.arindy.swingby.core.data

data class Result(
    val planet: Body,
    val probe: Body,
    val distance: Double,
) {
    override fun toString(): String {
        return "Result(\n" +
            "  planet=$planet,\n" +
            "  probe=$probe,\n" +
            "  distance=$distance\n)"
    }
}
