# TheAPI

### Are you looking for **Shared** section of TheAPI
Shared section moved to "private" repo for easier compile in other programs
https://github.com/TheDevTec/TheAPI-Shared

## Our purpose
Are you tired of looking for different APIs on the internet to create in your projects? Neither do we! And this is why TheAPI is here.
We bring the most useful and most wanted features directly for developers. From custom configurations with comments feature to Object parsers to String and back. Also, TheAPI offers an extensive Sockets API that will make working with sockets a million times easier! And we have much more, check out the sample code in the Examples section.

## Requirements
- **Java 1.8** or newer
- (Used only in the Bukkit side) **Guava** & **Gson** libraries

## Built-in loaders:
- CraftBukkit, Spigot and all forks
- BungeeCord and all forks
- Velocity

## How to include the API with Maven:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
<dependencies>
    <dependency>
        <groupId>com.github.TheDevTec</groupId>
        <artifactId>TheAPI</artifactId>
        <version>13.4</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

## How to include the API with Gradle:

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}
dependencies {
    compileOnly "com.github.TheDevTec:TheAPI:13.4"
}
```


## Development builds
We also have development builds that bring various bug fixes or new features!
You can download the latest dev-build on our discord in the #ta-builds channel.
https://discord.gg/8YtfC234dA

## Examples
Looking for examples of usage in code?
On our github you can find a section called "Demo" where we test different features.
https://github.com/TheDevTec/TheAPI/tree/master/Demo/src/me/straikerinacz/theapi/demo

[![](https://badges.spiget.org/resources/downloads/Downloads-A940FB-72679.svg)](https://www.spigotmc.org/resources/theapi.72679/)
[![](https://badges.spiget.org/resources/rating/Rating-A940FB-72679.svg)](https://www.spigotmc.org/resources/theapi.72679/)
[![](https://img.shields.io/discord/579029317561090078?color=A940FB&label=Discord)]([https://discord.gg/8YtfC234dA](https://discord.gg/APwYKQRxby))
[![](https://bstats.org/signatures/bukkit/TheAPI.svg)](https://bstats.org/plugin/bukkit/TheAPI/10581)
