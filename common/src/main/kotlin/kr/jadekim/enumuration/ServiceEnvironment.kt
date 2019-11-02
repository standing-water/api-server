package kr.jadekim.enumuration

enum class ServiceEnvironment {
    LOCAL,
    DEVELOPMENT,
    QA,
    STAGE,
    PRODUCTION;

    companion object {
        @JvmStatic
        fun of(name: String?): ServiceEnvironment? = when (name?.toLowerCase()) {
            "local" -> LOCAL
            "dev", "development" -> DEVELOPMENT
            "qa" -> QA
            "stage", "staging" -> STAGE
            "prd", "prod", "production", "real", "live" -> PRODUCTION
            else -> null
        }
    }
}