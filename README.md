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

## Steps

* Note: these steps automatically run during the natural project life-cycle (so you do not need to explicitly execute them to get started)

### generate

```shell
mvn yapily-api:generate
```

Fetches (cached) API, before generating the server stubbing using the OpenAPI spec

- Note: Auto-generated artifacts are automatically .gitignored by the plugin

### fetch

```shell
mvn yapily-api:fetch
```

Fetches API from yapily-api bitbucket repository

### Clean

```shell
mvn yapily-api:clean
```
 leans the downloaded specifications (not automatically cleaned during the `clean` phase, to allow offline cleaning/re-compilation)

