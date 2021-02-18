# `yapily-api-maven-plugin`

Automatically handles API server-stub generation

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
* Note: these steps automatically run during the natural project life-cycle (so you do not need to explicitly execute them to get started)

### generate
```shell
mvn yapily-api:generate
```
### fetch
```shell
mvn yapily-api:fetch
```

### Clean
```shell
mvn yapily-api:clean
```
- Cleans the downloaded specifications (not automatically cleaned during the `clean` phase, to allow offline cleaning/compilation)
- Auto-generated artifacts are automatically .gitignored by the plugin
