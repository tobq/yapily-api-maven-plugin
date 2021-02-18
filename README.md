# Usage

## Adding plugin to pom.xml
```xml
<plugin>
    <groupId>com.yapily</groupId>
    <artifactId>yapily-api-maven-plugin</artifactId>
    <configuration>
        <apiType>yapily-platform-security</apiType>
        <apiVersion>0.0.1</apiVersion>
    </configuration>
</plugin>
```

## Steps
* Note: these steps automatically run during the natural project build-cycle (so don't usually need to be explicitly executed)

### generate
```shell
mvn yapily-api generate
```
### fetch
```shell
mvn yapily-api fetch
```

### Clean
```shell
mvn yapily-api clean
```
- Cleans the downloaded specifications (not automatically cleaned during the `clean` phase, to allow offline cleaning/compilation)
