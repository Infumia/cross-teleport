plugins {
    java
}

allprojects { group = "tr.com.infumia" }

subprojects {
    apply<JavaPlugin>()
}

repositories {
    mavenCentral()
}
