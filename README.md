# InstaTools Desktop

This is the desktop version of [*InstaTools*](https://github.com/fulcrum6378/instatools),
an application containing different tools which help people retrieve contents from Instagram.
It uses the web API of Instagram for:

1. Downloading bulk content from Instagram
2. Exporting direct messages in HTML, TXT and JSON file formats
3. Retrieving information about a user including their high-quality profile picture

### Modules

This project is written in pure Kotlin and contains these modules:

1. **CORE**: containing the core tools for interacting with the Instagram API.
2. **CLI**: an interactive command-line interface deployed as a JAR. 
   It requires you to manually extract Instagram cookies from your browser and put them in `cookies.txt`.
3. JFX: A GUI desktop application using JavaFX. (not yet developed)

## Command-Line Interface

1. To download in desired qualities (using the parameter `--quality=?`):
    - `d`|`download`: direct links to posts or reels
    - `s`|`saved`: saved posts (+the ability to unsave them)
    - `p`|`posts`: posts of a profile
    - `t`|`tagged`: tagged posts of a profile
    - `r`|`story`: story of a profile
    - `h`|`highlight`: highlights of a profile
2. To export your direct messages: `m`|`messages`
3. To retrieve information about a user: `u`|`user`

### License

```
Copyright © Mahdi Parastesh - All Rights Reserved.
```
