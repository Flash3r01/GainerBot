# Schnitzel

## What the hell is this supposed to be?
Simple Answer: This is a little game, that can be created for a specific
user.


Schnitzel gives you the ability to create a small minigame for a user. You
can easily create multiple "Schnitzels" a user has to find, to get to a
reward. Schnitzels will be found by a defined phrase that the user has to
input.  
To aid the User in finding the Schnitzels, two commands exist:
- **status/stats**: Gives the user an overview for the found/remaining
                    Schnitzels.
- **help/hilfe**:   Sends the user a hint to a random undiscovered
                    Schnitzel. Has a 60s cooldown.


## Creating a SchnitzelHunt
Creating a SchnitzelHunt is as simple as creating a folder here,
with the name of the folder as the discord UserID of the User you want to
create the SchnitzelHunt for.

There are 2 files that shape the SchnitzelHunt:

***schnitzels.json***  
This file defines the Schnitzels that the User can find. Each Schnitzel
consists of: 
- **target**:           The phrase the user has to find
- **response**:         The response from the bot, if the target is sent.
    - Special characters:
    - \n - line break
    - \file:*filename* - specifies a file to be sent. (In this folder)
- **containsFiles**:    If the response contains files.
- **schnitzelHint**:    Represents the hint for the Schnitzel
    - **hint**:             The hint that gets sent for the Schnitzel.
    - **containsFiles**:    If the hint contains files.

template for *schnitzels.json*
```json
[
  {
    "target": "",
    "response": "",
    "containsFiles": false,
    "schnitzelHint": {
      "hint": "",
      "containsFiles": false
    }
  }
]
```

***secret.txt***  
The contents of this file get sent as the reward, if the User has found all
the Schnitzels.