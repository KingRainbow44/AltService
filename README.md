# Alt Service

*short for: alternate account service*

## What is this?

AltService is a server software which allows you to\
create and manage **Minecraft: Bedrock Edition** accounts programmatically.

## Features

**TODO: Add more features as they're added!**

# Building

## Generating Protocol Definitions

### Java
```shell
cd src/main/proto | protoc --java_out=../java/ *.proto
```

### TypeScript
```shell
cd src/main/proto | npx protoc --ts_out=../../../frontend/src/backend *.proto
```
