pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "lmw2w_connect"
include(":app")

include(":leshan-lwm2m-core")
project(":leshan-lwm2m-core").projectDir = file("libs/leshan-local/leshan-lwm2m-core")

include(":leshan-lwm2m-client")
project(":leshan-lwm2m-client").projectDir = file("libs/leshan-local/leshan-lwm2m-client")

include(":leshan-tl-jc-client-coap")
project(":leshan-tl-jc-client-coap").projectDir = file("libs/leshan-local/leshan-tl-jc-client-coap")

include(":leshan-tl-jc-shared")
project(":leshan-tl-jc-shared").projectDir = file("libs/leshan-local/leshan-tl-jc-shared")
