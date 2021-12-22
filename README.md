# GainerBot

#### What is the GainerBot?
The GainerBot is a bot for Discord that intends to improve your Discord experience in silly ways. Interaction with it is mainly done through slash commands and *Patterns* that react to specific words.

#### Who is the GainerBot for?
The GainerBot started as a dumb idea for a private Discord server. But feel free to do whatever you want with it.

#### How do I use the GainerBot?
Using the GainerBot should be very simple. Just clone the repository from github and run `$ ./gradlew shadowJar` to compile the jar.  
Running the Bot just needs the jar-file and the *data* directory. Place them beside each other and store your bot-token in *data/token.txt*. Executing the jar-file now should start your bot.

#### How do I customize my GainerBot?
+ You can add sounds, copypastas and possibly even more just by copying files into the right *data* subdirectory and restarting your bot. Additional info can be found in the subdirectories.
+ All slash commands have to extend `BaseSlashCommand` and have to be registered via a `SlashCommandManager`.
+ All patterns have to extend `BasePattern` and have to be made active, by adding them to the list in `GainerBotPatterns`

#### What scope and permissions do I have to choose when adding the bot?
Scopes:
+ bot
+ applications.commands

For permissions, just choose what features your bot actually uses. The fewer the better.

#### I have more questions!
Feel free to contact me if you have any questions or remarks.
