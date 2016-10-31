# Proguard configuration for Jackson 2.x (fasterxml package instead of codehaus package)

-keep class com.fasterxml.jackson.databind.ObjectMapper {
    public <methods>;
    protected <methods>;
}
-keep class com.fasterxml.jackson.databind.ObjectWriter {
    public ** writeValueAsString(**);
}

-keep class com.fasterxml.jackson.annotation.** { *; }
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry
-dontwarn com.fasterxml.jackson.databind.**