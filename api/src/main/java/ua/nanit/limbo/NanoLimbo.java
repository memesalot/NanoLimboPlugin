/*
 * Copyright (C) 2020 Nan1t
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ua.nanit.limbo;

import org.checkerframework.checker.nullness.qual.NonNull;
import ua.nanit.limbo.configuration.LimboConfig;
import ua.nanit.limbo.server.ConsoleCommandHandler;
import ua.nanit.limbo.server.LimboServer;
import ua.nanit.limbo.server.Log;

import java.nio.file.Paths;

public final class NanoLimbo {

    public static void main(String[] args) {
        try {
            LimboConfig config = new LimboConfig(Paths.get("./"));
            config.load();

            ConsoleCommandHandler commandHandler = new ConsoleCommandHandler();
            LimboServer server = new LimboServer(config, commandHandler);
            commandHandler.registerAll(server);

            server.start();
            commandHandler.start();
        } catch (Exception e) {
            Log.error("Cannot start server: ", e);
        }
    }

    private NanoLimbo() {
        throw new @NonNull UnsupportedOperationException();
    }
}
