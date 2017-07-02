# StoryTeller
To enjoy your favorite picture on your smartphone EVERYDAY as your wallpaper!

<img src="https://github.com/Sag0ld/StoryTeller/blob/master/Interface/Screenshot.png?raw=true" alt="Sreenshot" width="200px"/>

## Description
StoryTeller allows you to choose a wallpaper from a list of pictures of your photo archive with today's date. 

Every day (at midnight) the app will search in the user's chosen directory for a picture which has been taken with today's date. The application then sends you a notification resulting in two different behaviors:
1. A single match is found : the app will push a notification letting you know that you have the possibility of setting a new picture as your wallpaper. 
2. Multiple pictures are found : StoryTeller opens a special view allowing you to choose your favorite picture to set as your wallpaper. 

If no pictures are found for a given date, the app will reuse the same wallpaper as the day before.

## Tested on
- Oneplus 2 (Marshmallow API 23)

## Developped for 
- API 19 to 25

## Used Libraries
- [Glide V4](http://bumptech.github.io/glide/)

## Missing features
- Landscape display
- Support different screen resolutions

## Known Issues
This application is kind of laggy when attempting to remove it from the tasklist.
