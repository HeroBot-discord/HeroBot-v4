package fr.matthieu.herobot.services;

import com.sun.management.OperatingSystemMXBean;
import fr.matthieu.herobot.services.command.CommandParser;
import fr.matthieu.herobot.services.command.ParsingError;
import fr.matthieu.herobot.services.command.SimpleCommand;
import fr.matthieu.herobot.services.command.annotations.Command;
import fr.matthieu.herobot.services.command.annotations.FromMessage;
import fr.matthieu.herobot.services.command.annotations.Inject;
import fr.matthieu.herobot.services.command.annotations.Name;
import fr.matthieu.herobot.services.command.parsers.IntParser;
import fr.matthieu.herobot.services.command.parsers.LongParser;
import fr.matthieu.herobot.services.command.parsers.PercentParser;
import fr.matthieu.herobot.services.command.parsers.StringParser;
import fr.matthieu.herobot.services.command.parsers.discord.ChannelParser;
import fr.matthieu.herobot.services.command.parsers.discord.RoleParser;
import fr.matthieu.herobot.services.command.parsers.discord.UserParser;
import fr.matthieu.herobot.utilities.ServicesContainer;
import fr.matthieu.herobot.utilities.classes.MessageSanitizer;
import fr.matthieu.herobot.utilities.classes.Service;
import fr.matthieu.herobot.utilities.classes.ServicePriority;
import fr.matthieu.herobot.utilities.classes.plugin.Plugin;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import javax.annotation.Nullable;
import java.awt.*;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandsService extends Service {

    private final CommandParser parser = new CommandParser();
    private String prefix;
    private Map<String, SimpleCommand> commands = new HashMap<>();
    private HashMap<String, Bucket> bucket = new HashMap<>();
    private Bandwidth limit = Bandwidth.classic(7, Refill.intervally(2, Duration.ofMinutes(1)));
    private ExecutorService executors;

    public CommandsService(ServicesContainer container) {
        super(ServicePriority.LOW, true, container);
    }

    @Override
    public void initialize() throws Exception {
        parser.registerParser(IntParser.class);
        parser.registerParser(StringParser.class);
        parser.registerParser(ChannelParser.class);
        parser.registerParser(RoleParser.class);
        parser.registerParser(UserParser.class);
        parser.registerParser(PercentParser.class);
        parser.registerParser(LongParser.class);
    }

    @Override
    public void start() throws Exception {
        this.registerEventHandlers(this);
        this.prefix = ((ConfigurationManager) this.container.getService(ConfigurationManager.class)).getPrefix();
        this.registerCommandClass(this, this);
        executors = Executors.newFixedThreadPool(2);
    }

    public void registerCommandClass(Object object, Service service) {
        for (Method method : object.getClass().getMethods()) {
            Command commandAnnotation = method.getAnnotation(Command.class);
            if (commandAnnotation != null) {
                this.commands.put(commandAnnotation.name(), new SimpleCommand(method, object, commandAnnotation, service));
                logger.info("Registered {}", commandAnnotation.name());
            }
        }
    }

    @Override
    public void shutdown() throws Exception {

    }

    @Override
    public void kill() throws Exception {

    }

    @Command(name = "rate-limit", rateLimit = false)
    public void rateLimitInfo(@Inject Bucket bucket, @Inject Message message) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription(String.format("Here is some details about your rate-limit account on this guild. \r " +
                "Each rate-limited command uses one credit on your account, your account have a size of %s tokens, and get %s more tokens every minute if your count is under %s.", limit.getInitialTokens(), limit.getRefillTokens(), limit.getInitialTokens()))
                .setColor(new Color(55, 55, 55))
                .setTimestamp(Instant.now())
                .setAuthor("Rate-Limiting Info")
                .setFooter("Matthieu \u00A9 / 2018 - 2020")
                .addField("Account", String.format("%s/%s tokens", bucket.getAvailableTokens(), limit.getInitialTokens()), true)
                .addField("Refill", String.format("%s/%ss", limit.getRefillTokens(), limit.getRefillPeriodNanos() / 1e+9), true);

        if (bucket.getAvailableTokens() == 0)
            builder.addField("`\u274C` You are being rate-limited.", "You need to wait for us to add more tokens to your account. \r Please, do not spam commands.", false);

        message.getChannel().sendMessage(builder.build()).queue();
    }

    @Command(name = "rate-limit-consume-all")
    public void consumeAll(@Inject Bucket bucket, @Inject Message message) {
        bucket.tryConsumeAsMuchAsPossible();
        message.getChannel().sendMessage("`\u2705` Consumed all your rate-limit tokens.").queue();
    }

    @Command(name = "help")
    public void help(@Inject Message message, @Name(value = "Selected command") @FromMessage @Nullable String selectedCommand) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription("HeroBot is a Discord bot with a plugin system.")
                .setColor(new Color(55, 55, 55))
                .setTimestamp(Instant.now())
                .setFooter("Matthieu \u00A9 / 2018 - 2020");
        if (selectedCommand != null) {
            builder.setAuthor("HeroBot - Command help");
            if (this.commands.containsKey(selectedCommand)) {
                SimpleCommand command = this.commands.get(selectedCommand);
                Plugin plugin = null;
                if (command.service instanceof Plugin) {
                    plugin = ((Plugin) command.service);
                }
                builder
                        .addField("\0", String.format("**`%s`** \r %s", command.command.name(), command.command.description()), false)
                        .addField("Plugin", plugin != null ? plugin.getManifest().name : "HeroBot System", true)
                        .addField("Rate-limited", command.command.rateLimit() ? "Yes" : "No", true);

                if (plugin != null) {
                    builder.addField("Authors", String.join(" & ", plugin.getManifest().authors), true);
                }
            } else {
                builder.addField("\0", String.format("`\u274C` The command `%s` doesn't exist.", selectedCommand), true);
            }
        } else {
            builder.setAuthor("HeroBot - General help")
                    .addField("\u200B", String.format("\u2003 You can do `%shelp <command>` to get more details about a command", this.prefix), false);
            Map<Service, Set<SimpleCommand>> command = new HashMap<>();
            for (SimpleCommand comm : this.commands.values()) {
                command.putIfAbsent(comm.service, new HashSet<>());
                command.get(comm.service).add(comm);
            }
            for (Map.Entry<Service, Set<SimpleCommand>> group : command.entrySet()) {
                StringBuilder stringBuilder = new StringBuilder();
                for (SimpleCommand comm : group.getValue()) {
                    stringBuilder.append("`> ").append(comm.command.name()).append("`,");
                }
                String name = "HeroBot System";
                if (group.getKey() instanceof Plugin) {
                    name = ((Plugin) group.getKey()).getManifest().name;
                }
                String content = stringBuilder.toString();
                builder.addField(name, content.substring(0, content.length() - 1), false);
            }
        }
        message.getChannel().sendMessage(builder.build()).queue();
    }

    @Command(name = "system-info")
    public void systemInfo(TextChannel message) throws InterruptedException {
        OperatingSystemMXBean system = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        EmbedBuilder builder = new EmbedBuilder()
                .setDescription("Here is some details about the bot's statistics.")
                .setColor(new Color(55, 55, 55))
                .setTimestamp(Instant.now())
                .setFooter("Matthieu \u00A9 / 2018 - 2020");
        long nanoBefore = System.nanoTime();
        long cpuBefore = system.getProcessCpuTime();
        Thread.sleep(0x12c);
        long cpuAfter = system.getProcessCpuTime();
        long nanoAfter = System.nanoTime();
        long percent;
        if (nanoAfter > nanoBefore)
            percent = ((cpuAfter-cpuBefore)*100L)/
                    (nanoAfter-nanoBefore);
        else percent = 0;

        int total = message.getJDA().getTextChannels().size() + message.getJDA().getVoiceChannels().size();

        builder
                .addField("\0", "CPU Usage", false)
                .addField("Installed processors", String.valueOf(system.getAvailableProcessors()), true)
                .addField("CPU Load (Bot)", String.format("%s", Math.round(system.getProcessCpuLoad() * 100) / 100), true)
                .addField("CPU Load (System)", String.format("%s", Math.round(system.getSystemCpuLoad() * 100) / 100), true)
                .addField("% Cpu used (bot)", String.format("%s", Math.round(percent * 100) / 100), true)
                .addField("\0", "Memory Usage", false)
                .addField("Virtual Memory Size", String.format("%s MB",system.getCommittedVirtualMemorySize() / 1024 / 1024), true)
                .addField("Free virtual memory", String.format("%s MB",system.getFreePhysicalMemorySize() / 1024 / 1024), true)
                .addField("\0", "Os Informations", false)
                .addField("Os", String.format("%s / %s",system.getName(), system.getVersion()), true)
                .addField("Arch", system.getArch(), true)
                .addField("\0", "Bot informations", false)
                .addField("Guilds", String.format("%s Guilds",message.getJDA().getGuilds().size()), true)
                .addField("Members", String.format("%s Members",message.getJDA().getUsers().size()), true)
                .addField("Channels", String.format("%s ( %s Text, %s Categories, %s Voice )",
                        total,
                        message.getJDA().getTextChannels().size(),
                        message.getJDA().getCategories().size(),
                        message.getJDA().getVoiceChannels().size()
                        ), true);
        message.sendMessage(builder.build()).queue();
    }

    @SubscribeEvent()
    public void onMessage(GuildMessageReceivedEvent message) {
        this.executors.execute(() -> {
            if (!message.isWebhookMessage() && !message.getAuthor().isBot() && message.getMessage().getContentRaw().startsWith(this.prefix)) {

                String key = String.format("%s-%s", message.getAuthor().getId(), message.getMessage().getGuild().getId());
                Bucket userBucket;
                if (bucket.containsKey(key)) {
                    userBucket = bucket.get(key);
                } else {
                    userBucket = Bucket4j.builder().addLimit(limit).build();
                    bucket.put(key, userBucket);
                }
                String commandName = message.getMessage().getContentRaw().substring(this.prefix.length()).split(" ")[0];
                if (commands.containsKey(commandName)) {
                    SimpleCommand command = commands.get(commandName);
                    if (!command.command.rateLimit() || userBucket.tryConsume(command.command.price())) {
                        try {
                            Object[] commandParser = parser.parseCommandArgs(message.getMessage(), command, new Object[]{
                                    message,
                                    message.getMessage(),
                                    message.getChannel(),
                                    message.getAuthor(),
                                    message.getMember(),
                                    message.getMessage().getContentRaw(),
                                    message.getMessage().getGuild(),
                                    message.getMessage().getJDA(),
                                    userBucket
                            });
                            command.method.invoke(command.instance, commandParser);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (ParsingError error) {
                            message.getMessage().getChannel().sendMessage(error.getMessage()).queue();
                        }
                    } else {
                        message.getMessage().getChannel().sendMessageFormat("Please wait! HeroBot's commands are throttled. Do `%srate-limit` to get more info.", this.prefix).queue();
                    }
                }
            }
        });
    }

    public CommandParser getParser() {
        return parser;
    }
}
