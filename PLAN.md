# NanoLimboPlugin → Upstream Parity Plan (protocol 768–776 / 1.21.2–26.2)

**Decisions locked:** Java 21 · drop Bungee module · adopt upstream `settings.yml` format · full parity harness.
**Target platform:** Velocity. Upstream reference: `Nan1t/NanoLimbo@73b331d` (v1.13.0), cloned at `C:\Users\Nicolas\AppData\Local\Temp\opencode\NanoLimbo-upstream`.

## Phase 0 — Scaffolding
- Branch `feature/modern-versions` off `main`.
- Delete `bungee/` module; remove from `settings.gradle`; strip Bungee references from README.
- Root `build.gradle`: Java 8 → **21**; add **Lombok 1.18.46** (compileOnly + annotationProcessor); pin deps to upstream: netty **4.2.15.Final** (handler + natives, drop `netty-all`), adventure **5.1.1** (api, nbt, gson/legacy/plain/minimessage serializers), configurate **4.2.0**, gson **2.14.0**. Keep shadow plugin (upgrade if needed for Java 21).
- **Gate:** clean `gradlew build` of api+velocity on JDK 21.

## Phase 1 — Protocol layer (api)
Port verbatim from upstream (Lombok + all): `Version` (adds 768–776 + display names), `World`/`State` (all packet-ID mappings incl. new versions), `PacketLogin` (universal join-game), `PacketKnownPacks`, `PacketUpdateTags`, `PacketChunkWithLight`, play `PacketDisconnect`, `PacketLoginDisconnect`, updated `PacketPlayerInfo`, `PacketPlayerPositionAndLook`, `PacketDimensionRegistry`/`PacketSpawnPosition`, `PacketGameEvent`, `PacketRegistryData`, `PacketStatusResponse`, `ByteMessage` (version-aware NBT compound writing), `MetadataWriter`, `PacketSnapshot` (per-version supplier), `PacketUtils`, `NbtUtils`. Delete dead classes (`NbtMessage`, `NbtMessageUtil`, old `PacketEmptyChunk`/`PacketJoinGame` where superseded).
- **Gate:** compiles; old 767-era behavior mapped in `State` untouched.

## Phase 1b — Server config/command port (api)
Port upstream `configuration/` package (concrete `LimboConfig`, all `serializers/`), replacing `YamlLimboConfig`. Keep plugin's `Command`/`CommandHandler` abstractions; port upstream `CmdVersion`, keep `CmdHelp/Conn/Mem/Stop`.
- **Gate:** `LimboConfig` loads upstream `settings.yml` in a unit smoke test.

## Phase 2 — World/dimension system (api)
Port `DimensionType`, `Dimension`, `DimensionRegistry` (new), `NamespacedKey`; replace `.snbt` resources with upstream's 21 `.nbt` codecs + 11 `tags_*.nbt` files (binary copy).
- **Gate:** resources load via `DimensionRegistry` unit smoke test.

## Phase 3 — Connection & server core (api)
Port `ClientConnection` (new configuration-phase flow: KnownPacks → per-version RegistryData → UpdateTags → FinishConfiguration; play-phase chunks/game-event), `PacketHandler`, `PacketSnapshots`, `GameProfile`, `PlayerPublicKey`, pipeline (`PacketDecoder/Encoder`, `VarIntFrameDecoder/LengthEncoder`, `ChannelTrafficHandler`), `ClientChannelInitializer`, `Port`/`TransportType`, `LimboServer`, `Connections`. Keep the plugin's `Log` facade (JUL, no logback). Ship upstream `settings.yml` resource.
- **Gate:** standalone api harness can boot a limbo from a settings.yml.

## Phase 4 — Velocity module
- Replace `VelocityLimboServer`/module config parsing with upstream-format `LimboConfig` loaded from each limbo's `settingsFolder`; multi-limbo + Lamp commands + plugin `config.yml` (messages layer) stay as-is.
- Map forwarding defaults from Velocity where the plugin already did; update `@Plugin` version.
- **Gate:** `gradlew shadowJar` produces the velocity plugin jar.

## Phase 5 — Verification (delivery proof)
1. **Parity harness** (throwaway, temp dir): scripted offline-mode protocol client exercising status + login + configuration + play join per protocol **768, 769, 770, 771, 2, 773, 774, 775, 776 + 767 regression**, run against **upstream jar** and **our build** — compare packet sequences/IDs; fix-back until parity.
2. **Velocity E2E smoke:** real Velocity proxy (temp dir), plugin loads, limbo registers + starts, status ping through limbo port, `limboconn` works.
3. Full build green.
- **Gate:** parity report + smoke results shown before Phase 6.

## Phase 6 — Ship
README (version matrix incl. 1.21.2–1.21.11/26.1/26.2, new settings.yml docs, Velocity-only note), version bump, commit (push only on go-ahead).

## Risks & fallbacks
- **(a) netty 4.2.x + shaded natives inside Velocity plugin classloader** — if natives clash, fall back to NIO-only transport and disable native shading.
- **(b) adventure 5.x shaded in plugin jar vs Velocity's bundled adventure** — isolate via relocation if the smoke test trips.
Both have fallbacks that don't affect protocol parity.
