# `yapily-api-implementation-maven-plugin`

Automatically handles API server stub generation, for service implementations

# Usage

## Adding plugin to pom.xml

```xml

<plugin>
    <groupId>com.yapily</groupId>
    <artifactId>yapily-api-implementation-maven-plugin</artifactId>
    <configuration>
        <apiType>yapily-platform-consent-service</apiType>
        <apiVersion>0.0.4</apiVersion>
    </configuration>
</plugin>
```

### Generate stubbing

```shell
mvn yapily-api:generate
```
Fetches (cached) API, before generating the server stubbing using the OpenAPI spec
- `generate` automatically runs during the `generate-sources` phase (so you do not need to explicitly execute them to get started)
- Auto-generated artifacts are automatically .gitignored by the plugin

### Fetch Specification

```shell
mvn yapily-api:fetch
```

Fetches API from yapily-api bitbucket repository

### Clean
```shell
mvn yapily-api:clean
```
Cleans the auto-generated server stubbing
- Automatically runs as part of the `clean` phase

### Clean Specifications
```shell
mvn yapily-api:clean-spec
```
Cleans the downloaded specifications
- This was left out of the `clean` phase, to allow one to clean and then recompile, while offline

