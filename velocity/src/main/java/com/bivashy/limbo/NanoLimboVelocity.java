package com.bivashy.limbo;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.bivashy.limbo.command.LampVelocityCommandHandler;
import com.bivashy.limbo.config.LimboConfig;
import com.bivashy.limbo.config.model.VelocityLimboServer;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerInfo;

import ua.nanit.limbo.NanoLimbo;
import ua.nanit.limbo.server.Log;
import ua.nanit.limbo.server.Command;
import ua.nanit.limbo.server.CommandHandler;
import ua.nanit.limbo.server.LimboServer;

@Plugin(id = "nanolimbovelocity", name = "NanoLimboVelocity", version = "2.0.0", authors = "bivashy, Nan1t")
public class NanoLimboVelocity {
    static {
        NanoLimbo.class.getName(); // For preventing shadow jar minimizing
    }

    private static NanoLimboVelocity instance;
    private final Map<String, LimboServer> servers = new HashMap<>();
    private final ProxyServer server;
    private final Path dataFolder;
    private final LimboConfig limboConfig;

    @Inject
    public NanoLimboVelocity(ProxyServer server, @DataDirectory Path dataFolder, org.slf4j.Logger logger) {
        instance = this;
        this.server = server;
        this.dataFolder = dataFolder;
        Log.setSink((level, msg, t) -> {
            switch (level) {
                case ERROR -> logger.error(msg, t);
                case WARNING -> logger.warn(msg, t);
                case DEBUG -> logger.debug(msg, t);
                default -> logger.info(msg, t);
            }
        });
        Log.info("NanoLimboVelocity %s initializing", "2.0.0");
        this.limboConfig = new LimboConfig(this);
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent e) {
        Log.info("Initializing %d limbo(s)...", servers().size());
        CommandHandler<Command> commandHandler = new LampVelocityCommandHandler(this).registerAll();
        for (VelocityLimboServer velocityLimboServer : servers()) {
            if (velocityLimboServer == null || velocityLimboServer.getLimboConfig().getAddress() == null) {
                Log.error("Skipping misconfigured limbo (check its settings.yml): %s", velocityLimboServer == null ? "?" : velocityLimboServer.getLimboName());
                continue;
            }
            LimboServer server = new LimboServer(velocityLimboServer.getLimboConfig(), commandHandler);

            ServerInfo serverInfo = new ServerInfo(velocityLimboServer.getLimboName(),
                    (InetSocketAddress) velocityLimboServer.getLimboConfig().getAddress());
            servers.put(serverInfo.getName(), server);
            this.server.registerServer(serverInfo);
            try {
                server.start();
                Log.info("Limbo '%s' registered and started on %s", serverInfo.getName(), serverInfo.getAddress());
            } catch(Exception ex) {
                Log.error("Failed to start limbo '%s'", ex, serverInfo.getName());
            }
        }
    }

    private List<VelocityLimboServer> servers() {
        return limboConfig.getServers() == null ? java.util.List.of() : limboConfig.getServers();
    }

    public ProxyServer getServer() {
        return server;
    }

    public Path getDataFolder() {
        return dataFolder;
    }

    public LimboConfig getLimboConfig() {
        return limboConfig;
    }

    public Map<String, LimboServer> getServers() {
        return servers;
    }

    public static NanoLimboVelocity getInstance() {
        return instance;
    }
}
