package ua.nanit.limbo.server;

import ua.nanit.limbo.server.commands.CmdConn;
import ua.nanit.limbo.server.commands.CmdHelp;
import ua.nanit.limbo.server.commands.CmdMem;
import ua.nanit.limbo.server.commands.CmdStop;
import ua.nanit.limbo.server.commands.CmdVersion;

import java.util.*;

public final class ConsoleCommandHandler extends Thread implements CommandHandler<Command> {

    private final Map<String, Command> commands = new HashMap<>();

    public Command getCommand(String name) {
        return commands.get(name.toLowerCase(Locale.ROOT));
    }

    public void register(Command cmd, String... aliases) {
        for (String alias : aliases) {
            commands.put(alias.toLowerCase(Locale.ROOT), cmd);
        }
    }

    @Override
    public void register(Command command) {
        register(command, command.getClass().getSimpleName().replace("Cmd", "").toLowerCase(Locale.ROOT));
    }

    @Override
    public boolean executeCommand(String input) {
        Command handler = getCommand(input);

        if (handler != null) {
            try {
                handler.execute();
            } catch(Throwable t) {
                Log.error("Cannot execute command:", t);
            }
            return true;
        }

        Log.info("Unknown command. Type \"help\" to get commands list");
        return false;
    }

    @Override
    public Collection<Command> getCommands() {
        return Collections.unmodifiableCollection(commands.values());
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String command;

        while(true) {
            try {
                command = scanner.nextLine().trim();
            } catch(NoSuchElementException e) {
                break;
            }

            executeCommand(command);
        }
    }

    public ConsoleCommandHandler registerAll(LimboServer server) {
        register(new CmdHelp(server), "help");
        register(new CmdConn(server), "conn");
        register(new CmdMem(), "mem");
        register(new CmdStop(), "stop");
        register(new CmdVersion(), "version", "ver");
        return this;
    }
}
