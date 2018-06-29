# Burst Mining Pool Telegram Bot
This project is a Spring-boot application, that hosts a telegram bot 
which can update burst-miners on their current pending payments of their mining pool.
The bot currently supports private and group chats. You can add multiple wallets
to the bot. The bot can monitor each wallet and send automatic notifications on pending
payment changes.

## Contributing
If you want to add a mining pool, please have a look at the wiki: 
[Adding support for your pool software](https://github.com/enoy19/burst-mining-pool-bot/wiki/Adding-support-for-your-pool-software)

## Hosting & Configuration
Wiki: [Hosting and Configuration](https://github.com/enoy19/burst-mining-pool-bot/wiki/Hosting-and-Configuration)

## Supported Mining Pools
 * CryptoGuru Pool Software
 
## Commands
### /add_wallet
Adds a wallet to the chat.\
Arguments:
 * Burst Address ([Format](https://burstwiki.org/wiki/RS_Address_Format))

### /remove_wallet
Removed a wallet of the chat.\
Arguments:
 * Burst Address 

### /notification
Activates pending payment change notifications for the given wallet.\
Arguments:
 * Burst Address
### /notification_threshold
Sets the notification threshold of a wallet. Notifications will only be sent,
when the overall pending payments exceed the threshold value.\
Arguments:
 * Burst Address
 * Threshold (double, must be positive.)
### /pending
Shows the current pending payments for each wallet.

### /share
Shows the current (historical) share for each wallet.

### /effective_capacity
Shows the current effective capacity in TB for each wallet.

### /confirmed_deadlines
Shows the current amount of confirmed deadlines for each wallet.

### /show_wallets
Shows wallets that were added to the chat.

### /donate
Shows donation information about developers and hosts.
