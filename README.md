# `yapily-api-maven-plugin`

Automatically handles API server-stubbing/client-sdk generation, for service implementations

# Usage

## Adding plugin to pom.xml

```xml

<plugin>
    <groupId>com.yapily</groupId>
    <artifactId>yapily-api-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>generate-server</id>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <serverApi>
                    <type>yapily-platform-security</type>
                    <version>0.0.6</version>
                </serverApi>
            </configuration>
        </execution>
        <execution>
            <id>generate-client</id>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <clientApi>
                    <type>yapily-server-images</type>
                    <version>0.0.2</version>
                </clientApi>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Debug `<configuration/>` parameters

* You can use `<gitUrl/>` to override the default git repository to clone from
* You can also use `<localSpecPath/>` to skip cloning, and develop using a locally managed api

### Generate API server-stubbing/client-sdk's

```shell
mvn yapily-api:generate
```

Fetches (cached) API, before generating the server-stubbing/client-sdk using the OpenAPI spec

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

Cleans the auto-generated server-stubbing/client-sdk

- Automatically runs as part of the `clean` phase

### Clean Specifications

```shell
mvn yapily-api:clean-specs
```

Cleans the downloaded specifications

- This was left out of the `clean` phase, to allow one to clean and then recompile, while offline

