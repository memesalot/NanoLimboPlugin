# NanoLimboPlugin

Run lightweight [NanoLimbo](https://github.com/Nan1t/NanoLimbo) limbo servers **as a Velocity plugin** — spin up multiple limbos directly on your proxy, no standalone jars or extra ports to manage by hand.

This is a port of [Nan1t's NanoLimbo](https://github.com/Nan1t/NanoLimbo) (v1.13.0) core into plugin form: the full upstream protocol stack, configuration format, and connection flow, driven from Velocity with multi-limbo support and proxy commands.

General features:
* Full protocol parity with upstream NanoLimbo — every version from 1.7.2 through 26.2.
* Multiple limbo servers from one plugin, each with its own `settings.yml`.
* Native **Velocity** integration: limbos are registered as backend servers, players can be forwarded to them, and `MODERN` / `LEGACY` / `BUNGEE_GUARD` info forwarding is supported per limbo.
* High performance — no threads per player, fixed thread pool, no useless data cached.
* Fully configurable (messages, boss bar, title, player list, ping, dimension).
* Lightweight (~4.5 MB shaded jar).

## Versions support

Symbol `X` means all minor versions.

- [x] 1.7.X
- [x] 1.8.X
- [x] 1.9.X
- [x] 1.10.X
- [x] 1.11.X
- [x] 1.12.X
- [x] 1.13.X
- [x] 1.14.X
- [x] 1.15.X
- [x] 1.16.X
- [x] 1.17.X
- [x] 1.18.X
- [x] 1.19.X
- [x] 1.20.X
- [x] 1.21.X &nbsp; *(incl. 1.21.2, 1.21.3, 1.21.4, 1.21.5, 1.21.6, 1.21.7, 1.21.8, 1.21.9, 1.21.10, 1.21.11)*
- [x] 26.1.X
- [x] 26.2

The server **doesn't** support snapshot versions.

## Installation

Required software: Velocity 3.3+ running on **Java 21**.

1. Download (or build) `velocity-x.y.z-all.jar` and drop it into Velocity's `plugins/` folder.
2. Start Velocity once — the plugin creates `plugins/nanolimbovelocity/config.yml` and a `settings.yml` for each configured limbo.
3. Edit the configs and restart.

### Plugin config (`config.yml`)

```yaml
limbos:
  first:                 # limbo name (also the registered Velocity server name)
    settingsFolder: first   # subfolder of plugins/nanolimbovelocity/ holding this limbo's settings.yml
messages:
  deserializer: LEGACY_AMPERSAND   # PLAIN | GSON | GSON_LEGACY | LEGACY_AMPERSAND | LEGACY_SECTION | MINIMESSAGE
  no-permission: '&cNot enough permission for this command'
  # ... other command messages
```

### Limbo config (`settings.yml`)

Each limbo uses the upstream NanoLimbo `settings.yml` format — see [Nan1t/NanoLimbo](https://github.com/Nan1t/NanoLimbo) for the full reference. Highlights:

* `bind` — ip/port the limbo listens on (players can also connect through Velocity as a backend server).
* `ping` — MOTD, version text and protocol shown in the server list.
* `dimension`, `gameMode`, `joinMessage`, `bossBar`, `title`, `playerList`, `headerAndFooter`, `brandName` — the limbo experience.
* `infoForwarding` — `NONE`, `LEGACY` (BungeeCord), `MODERN` (Velocity native, paste your proxy secret), or `BUNGEE_GUARD` (with tokens).
* `netty.transportType` — `NIO`, `EPOLL`, `IO_URING`, `KQUEUE` (auto-falls back to NIO).

## Commands

| Command | Description | Permission |
|---|---|---|
| `/limbohelp` | Show help | — |
| `/limboconn <limbo>` | Connection count on a limbo | `limbo.connection` |
| `/limbomem` | Memory usage stats | `limbo.memory` |
| `/limbostop <limbo>` | Stop a limbo | `limbo.stop` |
| `/limbostart <limbo>` | Start a stopped limbo | `limbo.start` |

## For developers

**Maven**:
```xml
<repositories>
   <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
   </repository>
</repositories>
<dependencies>
    <dependency>
       <groupId>com.github.bivashy.NanoLimboPlugin</groupId>
       <artifactId>api</artifactId>
       <version>2.0.0</version>
    </dependency>
</dependencies>
```
**Gradle**:
```groovy
repositories { maven { url 'https://jitpack.io' } }
dependencies { implementation 'com.github.bivashy.NanoLimboPlugin:api:2.0.0' }
```

### How to use the API?

```java
LimboConfig config = new LimboConfig(Paths.get("./")); // folder that contains settings.yml
config.load();

CommandHandler<Command> commandHandler = new ConsoleCommandHandler();
LimboServer server = new LimboServer(config, commandHandler);
server.start();

// When you are done
server.stop();
```

If you don't want console commands, pass your own `CommandHandler` implementation (e.g. one backed by your platform's command framework — see the Velocity module in this repo for an example using [Lamp](https://github.com/Revxrsal/Lamp)).

## Building

Required software: JDK 21.

```
./gradlew shadowJar
```

The plugin jar lands in `velocity/build/libs/`.

## Credits

* [Nan1t](https://github.com/Nan1t) — the original [NanoLimbo](https://github.com/Nan1t/NanoLimbo) this plugin ports.

### Contacts

If you have any questions or suggestions, join our [Discord server](https://discord.gg/4VGP3Gv)!
