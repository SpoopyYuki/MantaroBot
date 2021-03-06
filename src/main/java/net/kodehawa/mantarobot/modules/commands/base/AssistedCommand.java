package net.kodehawa.mantarobot.modules.commands.base;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.kodehawa.mantarobot.utils.commands.EmoteReference;

import java.util.concurrent.TimeUnit;

/**
 * "Assisted" version of the {@link Command} interface, providing some "common ground" for all Commands based on it.
 */
public interface AssistedCommand extends Command {
	default EmbedBuilder baseEmbed(GuildMessageReceivedEvent event, String name) {
		return baseEmbed(event, name, event.getJDA().getSelfUser().getEffectiveAvatarUrl());
	}

	default EmbedBuilder baseEmbed(GuildMessageReceivedEvent event, String name, String image) {
		return new EmbedBuilder()
			.setAuthor(name, null, image)
			.setColor(event.getMember().getColor())
			.setFooter("Requested by " + event.getMember().getEffectiveName(), event.getAuthor().getEffectiveAvatarUrl());
	}

	default void doTimes(int times, Runnable runnable) {
		for (int i = 0; i < times; i++) runnable.run();
	}

	default EmbedBuilder helpEmbed(GuildMessageReceivedEvent event, String name) {
		return baseEmbed(event, name).addField("Permission required", permission().toString(), true);
	}

	default void onError(GuildMessageReceivedEvent event){
		MessageEmbed helpEmbed = help(event);

		if (helpEmbed == null) {
			event.getChannel().sendMessage(EmoteReference.ERROR + "There's no extended help set for this command.").queue();
			return;
		}

		event.getChannel().sendMessage(EmoteReference.ERROR + "You executed this command incorrectly, help for it will be shown below.").queue(
				message -> message.delete().queueAfter(15, TimeUnit.SECONDS)
		);

		event.getChannel().sendMessage(help(event)).queue(
				message -> message.delete().queueAfter(70, TimeUnit.SECONDS)
		);
	}

	default void onHelp(GuildMessageReceivedEvent event) {
		MessageEmbed helpEmbed = help(event);

		if (helpEmbed == null) {
			event.getChannel().sendMessage(EmoteReference.ERROR + "There's no extended help set for this command.").queue();
			return;
		}

		event.getChannel().sendMessage(help(event)).queue();
	}
}
