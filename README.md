# GainerBot

#### What is the GainerBot?
The GainerBot is a bot for Discord that intends to improve your Discord experience in silly ways. Interaction with it is mainly done through commands (Standard-Prefix: !!) and *Patterns* that react to specific words.

#### Who is the GainerBot for?
The GainerBot started as a dumb idea for a private Discord server. But feel free to do whatever you want with it.

#### How do I use the GainerBot?
Using the GainerBot should be very simple. Just clone the repository from github, navigate into the repository, create the file *data/token.txt*, fill it with your bot-token and execute the command `./gradlew run`. Done.

#### How do I customize my GainerBot?
+ You can add sounds, copypastas and possibly even more just by copying files into the right *data* subdirectory and restarting your bot. Additional info can be found in the subdirectories.
+ All commands have to extend `BaseCommand` and have to be made active, by adding them to the list in `GainerBotCommands`.
+ All patterns have to extend `BasePattern` and have to be made active, by adding them to the list in `GainerBotPatterns`