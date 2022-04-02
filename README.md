# TheAPI

## What is TheAPI
TheAPI is our completely unique and new project founded in 2019.
As the name implies, it is a developer API designed for developers.
TheAPI tries to rebuild everything it can, and not uselessly, into Async for the ability to make plugins with the best performance.
But as it is already known, nothing is perfect or flawless, that's why we have been working on the project continuously for years, redesigning different methods or even whole classes to make the performance the best.


## What does TheAPI include?
> We have been working on our own Configuration (Data & Config classes) for a long time, which includes 4 different types of data retrieval & storage - YAML, PROPERTIES, JSON and BYTE
> Including the ability to have comments in the YAML & PROPERTIES configuration type​

> Converting Objects to String and back to the original Object (Json class)

> Asynchronous scheduler

> Huge component api similiar to bungeecord's one with convertors​

> Custom GUI with actions (GUI & AnvilGUI classes)​

> Possibility to create your own async events - We have our own Listener & EventHandler classes​

### And lots of other different API classes
- StringUtils
- Animation
- PercentageList
- StreamUtils
- SpigotUpdateChecker
- Ref (Java reflections helper)
- ComponentAPI
- SortedAPI
- RankingAPI
- Scheduler (Tasker)
- PlaceholderAPI (Piece of our work)
- Config (Our own special configuration api)
- MemoryAPI
- DatabaseAPI
- SocketsAPI (Client, SocketServer classes & ClientReceiveMessageEvent theapi event)

## Our own async events
- ServerListPingEvent (Possibility to adjust max online players & number of online players and much more) * Only Bukkit/Spigot side
- ClientReceiveMessageEvent (SocketsAPI)


## What is our focus
We try to make it as easy as possible for plugin developers to work on plugins.
No more reflections, no more NMS and no more searching for differences between MC versions in the code!

##  Compatible softwares:
- CraftBukkit
- Spigot
- PaperSpigot (and all Paper's forks)
- BungeeCord
- WaterFall (and all WaterFall's forks)
- Velocity

## Requirements
- Bukkit/Spigot, Velocity or BungeeCord/WaterFall server software (Or implement code (shared section) to your own software)
- Server version 1.7.10 or newer (Or add your own NmsProvider for support older version/s)
- Java 1.8 or newer

## Download TheAPI
- Spigot (https://www.spigotmc.org/resources/72679/)
- Bukkit (https://dev.bukkit.org/projects/theapi)

## Gradle:
```java
dependencies {
    compileOnly files('libs/TheAPI.jar')
}
```

## Maven:
```java
        <dependency>
            <groupId>me.devtec.theapi</groupId>
            <artifactId>theapi</artifactId>
            <version>1.0</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/libs/TheAPI.jar</systemPath>
            <type>jar</type>
            <optional>true</optional>
        </dependency>
```

[![Spigot Downloads](https://img.shields.io/badge/dynamic/json.svg?url=https://api.spiget.org/v2/resources/72679&label=Spigot-Downloads&query=$.downloads&colorB=ee8a18&style=flat-square&maxAge=3600)](https://www.spigotmc.org/resources/72679/)
[![Spigot Rating](https://img.shields.io/badge/dynamic/json.svg?url=https://api.spiget.org/v2/resources/72679&label=Rating&query=$.rating.average&colorB=00AB66&style=flat-square&maxAge=3600)](https://www.spigotmc.org/resources/72679/)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/cc3d8b3c076848dc92cbbc3c074cab79)](https://www.codacy.com/gh/TheDevTec/TheAPI/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=TheDevTec/TheAPI&amp;utm_campaign=Badge_Grade)
[![](https://discordapp.com/api/guilds/579029317561090078/widget.png)](https://discord.gg/8YtfC234dA)
[![](https://bstats.org/signatures/bukkit/TheAPI.svg)](https://bstats.org/plugin/bukkit/TheAPI/10581)
