# InstaTools

InstaTools is an application consisting of tools which help managing contents from Instagram.
It uses the Instagram Web API for:

1. Downloading bulk content from Instagram
2. Exporting direct messages in HTML, TXT and JSON file formats
3. Retrieving information about a user including their high-quality profile picture

### Modules

This project is written in pure Kotlin and contains 3 modules:

1. **CORE**: containing the core tools for interacting with the Instagram API.
2. **CLI**: an interactive command-line interface deployed as a JAR. 
   It requires you to manually extract Instagram cookies from your browser and put them in `cookies.txt`.
3. **JFX**: A JavaFX desktop application.

### Command-Line Interface

1. To download in desired qualities (using the parameter `--quality=?`):
    - `d`|`download`: direct links to posts or reels
    - `s`|`saved`: saved posts (+the ability to unsave them)
    - `p`|`posts`: posts of a profile
    - `t`|`tagged`: tagged posts of a profile
    - `r`|`story`: story of a profile
    - `h`|`highlight`: highlights of a profile
2. To export your direct messages: `m`|`messages`
3. To retrieve information about a user: `u`|`user`

This application is the successor of its Android version which was developed and published in 2022
on Google Play and Galaxy Store and was banned because of copyright infringement against the trademark of Instagram!

### License

```
Copyright © Mahdi Parastesh - All Rights Reserved.
```
