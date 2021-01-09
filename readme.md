# Vouchy - Role Vouchers done easily!

## What is this Bot??
This is a Bot i recently started to code. It came through my mind why there hasn't been any other bots that could "redeem" roles on servers through using code vouchers. For example for Giveaways etc.

This is why i invented Vouchy - A Role Voucher Bot.

## What does it do?
It is simple you see: You can create codes for other users to use, so that they could claim a Role that you desire.

For example, i would like *Monkeyman#1231* to be able to claim the ***Monke Role***. I would simply use Vouchy's Mechanics to generate a simple code and direct-message that code to *Monkeyman#1231*, so they can use it in a command to receive that Role! ***So easy!***

## How do i use it?
**Vouchy uses the prefix** `vy> `

It has around 7 commands in order to fulfill it's purpose, which are..

 **Usable for normal users:** [*everyone*]
 - > vy> help -> shows the help about the bot
 - > vy> about -> about the bot and developer
 - > vy> claim/redeem `<code>` -> claims the role from the given voucher code.


**Usable for staff members:** [*User with "Manage Roles" permissions*] 
- > vy> create @RoleMention -> create a voucher code that can be then claimed with vy> claim
- > vy> list -> lists all currently active/stored voucher codes. **these are limited to around 20 to display right now. See "plans/todo" section.**
- > vy> delete `<code>` -> deletes given voucher code.
- > vy> validate `<code>` -> checks if given voucher code is valid. Also returns who made the voucher code and for what role it is meant for.
- > vy> clearall -> just a debug command to clear all of your voucher codes if you prefer to.

## Anything important i need to make sure?

**Yes!** The bot needs the following permissions (will ask when needed):

 - Manage Roles (being able to give the Member the role after giving the correct voucher code.) [*Important for Bot function*]
 - Delete Messages (deleting Messages after command Usage, example usage below) [OPTIONAL, mostly just for privacy and safety]
 ![important](https://cdn.hopefuls.de/cH0U.gif)

**Also!! Make sure the Bot is not below any Role you want to create a voucher token for.**


## Demonstration Showcase

 Creating and using a voucher code![enter image description here](https://cdn.hopefuls.de/L6GK.gif)

Creating a voucher code and listing it inside the voucher list
![enter image description here](https://cdn.hopefuls.de/JNS4.gif)

Deleting a voucher code
![enter image description here](https://cdn.hopefuls.de/E6St.gif)
## Other important things

**The Bot has ratelimiting built into it**

 - any data changing commands every 5 seconds (every command except vy> help or vy> about)
 - Max violations: 5 (indicated by currRL on the ratelimit-warning message)
 - Reset after 1 Minute
 - if 5 violations exceeded, ratelimited for 5-10 Minutes.

**The Bot stores the following things for both functioning and debugging:**

 - **In-case of Errors**: sends the following data to my error logging server :) )
	 - Server ID (of cause)
	 - User ID (command executor)
	 - Command used (for example **vy> create @Mention**)
	 - Stack trace of error
	 - Error-Locator code (provide in Support/Bug Report)
	
- **Functionwise-Purpose**
	- Server ID, Role ID, User ID (on Command)
	- Will only be used to allow for claiming the roles. Can simply be removed with "vy> delete `<code>`".

## Plans/TODO

 - **Implement settings to be able to**
	 - Toggle privacy mode
	 - being able to change messages
	 - language
	 - ..etc
 - **More Voucher-Settings**
 - set multiple/max-use of Vouchers
 - set expiry
 - only be claimable by specific roles/users

## Support

 - [Discord Support Server](https://discord.hopefuls.de)



***__THANKS FOR USING VOUCHY__***
***Project-Informations***
- Uses MySQL to store data
- Coded in Java
