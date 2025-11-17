plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "KomponenteProjekatPrvi"
include("spec")
include("prvaImpl")
include("drugaImpl")
include("testApp")
include("trecaImpl")
include("calculations")