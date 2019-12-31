Watson Overview
===============

# Malilib is required

Watson is a Minecraft mod that displays LogBlock and CoreProtect logs in 3-D.  It also has some features to make moderation tasks, such as observing chat and managing screenshots, a little easier.  The current features of the mod are:

* It displays individual edits as wireframe 3-D boxes.
* It groups edits of ore blocks into ore deposits, numbers each deposit, shows the numbers in 3-D space and provides commands to teleport to deposits and compute a stone:diamond ratio.
* It draws vectors between edits indicating the time sequence of edits.
* It draws text annotations in 3-D space. These can act as teleport targets.
* Edits and annotations can be saved to files and loaded at a later date.
* There's a simple built-in calculator for working out stone:diamond ratios.
* It uses colour to highlight parts of chat that match regular expressions. This can be used to draw attention to banned words. It can also be used to highlight the names of people, acting as a rudimentary friends list.
* It adds player names to screenshots automatically.
* It does a `/region info regionname` for you when you right click on a region with the wooden sword (rate limited to once every 10 seconds - the wooden sword will simply list the region name the other times).
* In order to shorten coordinate displays and make them easier to read, Watson also hides the LogBlock coords lines from chat and re-echoes them in a custom, brief format, where block IDs are numeric rather than words.  Re-echoed coordinates are assigned colours based on their physical proximity.  This makes separate ore deposits easy to distinguish in the coordinate listing.  Note that reformatting and recolouring of LogBlock query results can be controlled by the reformat_query_results and recolour_query_results [configuration settings](https://github.com/vacla/watson1132#configuration-file), respectively.


Downloads and Installation Instructions
---------------------------------------

See the [releases page for this project](https://github.com/vacla/watson1132/releases) for downloads and installation instructions.


Using Watson
------------

### Viewing Edits

Turn on the Watson display. On survival servers that use [the nerd.nu ModMode plugin](https://github.com/NerdNu/ModMode), the display is turned on and off automatically when switching in and out of ModMode:

    /w display on

The Watson display can be toggled by omitting the on|off parameter:

    /w display

Use LogBlock to get the coordinates.  As Watson sees coordinates listed in chat, it makes a record of the edit and draws a wireframe outline of the block where the edit occurred.

    /lb time 12h player playername block 56 coords
    /lb page 2
    /lb page 3
    /lb page 4

![Edits with default vector length.](https://raw.github.com/vacla/watson1132/master/wiki/images/screenshot1.png)

When listing edits in chat, Watson groups them together based on spatial proximity and echoes the co-ordinates using a different colour for each group.  This allows separate ore deposits to be readily distinguished, for the purpose of selecting which edit to teleport to with /lb tp.  (However, note that there is a separate /w tp feature for teleporting to ore deposits, discussed later.)

To teleport to an edit of interest:

    /lb tp 25

Perhaps, look at what happened immediately before that edit.  The Watson "/w pre" command displays the edits immediately before the most recently "selected" block. Just teleporting to an edit selects it for this purpose. Alternatively, when you check a block using the LogBlock toolblock (it defaults to bedrock, but on the nerd.nu servers it's coal ore), that also selects it.

    /w pre
    
By default, "/w pre" queries 45 edits from LogBlock.  You can explicitly override that number:

    /w pre 75
    
There's also a "/w post" command that queries LogBlock for the edits that happened immediately after the selected block:

    /w post
    /w post 60
    
The default numbers of edits for the "/w pre" and "/w post" queries are adjustable using the pre_count and post_count configuration setting, respectively.  If you increase those, you will also want to adjust the max_auto_pages setting to page through all of those results automatically.

Perhaps, look at the immediate vicinity of an edit:

    /lb area 3 player playername time 12h coords

Check individual blocks using the LogBlock tool block. Watson will draw this query result in 3-D, the same as with a "coords" query.

Possibly take some screenshots. The screenshot filename will include the name of the player whose last edit was selected.  Depending on the Watson settings, the screenshot may also be placed in a subdirectory of the Minecraft .minecraft/screenshots directory.  See the section on Screenshot Management for more information.

When you're done investigating, clear the currently stored edits. This also clears the player name (in screenshot filenames) and information about the coordinates, time and block type of the most recently selected edit.

    /w clear

If you forget any of the above commands:

    /w help


### The Cursor

The currently selected edit or position is shown as a magenta 3-D crosshair.  A position can be selected using the query tool of the logging plugin, irrespective of whether any results are returned.  If the currently selected position corresponds to a block edited by a player, then a dashed magenta line will be drawn to the edit that preceded it (if Watson has that edit in its memory).

The current selection is automatically changed when a new query result is received in chat, if you teleport to an ore deposit using "/w tp" or if you use the logging plugin's teleport-to-edit command (e.g. "/lb tp 1").

Watson can move the cursor through consecutive edits that it has seen in query results, using configurable keys (they default to UP and DOWN).  You can also teleport to the current cursor position using a configurable key (which defaults to MOUSE_LEFT).


### Viewing Ore Deposits

Watson groups adjacent destructions of ore blocks into ore deposits.  Here, "adjacent" includes blocks up to 1 block away along all three cardinal axes simultaneously.  Ore deposits are assigned numeric labels starting at 1 and increasing in time.  All diamonds are numbered first, then emeralds, then iron, gold, lapis, redstone, coal and finally quartz.  Thus, if the coordinates of 5 diamond deposits and 10 iron deposits have been retrieved from the LogBlock database, the diamond deposits will be numbered from 1 to 5, with 1 being the oldest diamond, and the iron deposits will be numbered from 6 to 15, with 6 being the oldest iron deposit.

![Ore deposits.](https://raw.github.com/vacla/watson1132/master/wiki/images/screenshot2.png)

Ore deposits are colour-coded according to ore type, with diamonds listed in light blue, emerald in light green, iron orange, gold yellow, lapis blue, redstone red, coal listed in dark grey and quartz in white.

To list all of the deposits:

    /w ore

Or if there are multiple pages (50 deposits to a page) you may need to specify a page number:

    /w ore 2

The "/w tp" command can teleport to the next deposit in the sequence (starting at one), the previous one, or the deposit with a specific number:

    /w tp
    /w tp next
    /w tp prev
    /w tp 17

The "/w tp" command is just a synonym for "/w tp next".  Teleporting to an ore deposit with "/w tp" selects that deposit, so that "/w pre" will show the edits leading up to it.

To automatically compute stone:diamond ratios for the current set of diamond deposits:

    /w ratio
    
Watson will compute one stone:diamond ratio for the time period that includes all diamond deposits listed by /w ore.  If there are segments of time where diamonds were mined particularly quickly, Watson will compute additional stone:diamond ratios for those smaller time segments too.

It is also possible to see what the current time is at the server, which can be useful information when looking at LogBlock time stamps:

    /w servertime

The numbers of deposits are drawn in 3-D and can be hidden, shown or toggled with the "/w label" command:

    /w label off
    /w label on
    /w label

Given the above commands for working with ore deposits, a basic x-ray checking procedure would be as follows:

1. List top miners: `/lb time 12h block 56 sum p`
1. List diamond edits for one particular miner: `/lb time 12h player fred block 56 coords`
1. Page through all coordinate results: `/lb next`
1. List ore deposits.  This will show their timestamps: `/w ore`
1. Check the stone:diamond ratio: `/w ratio`
1. Teleport to specific deposits for more detailed examination: `/w tp`
1. See what happened before the deposit in question was uncovered: `/w pre`


### Manipulating the Vector and Outline Displays

Watson draws vectors (arrows) from each edit to the next edit which is more recent, provided that the distance in space between the edits is greater than the minimum vector length.  The default minimum length is a configuration setting that defaults to 4.0.  To draw vectors between all edits:

    /w vector length 0

The above command sets the minimum vector length for the current session.  The minimum vector length can be permanently changed to that value using:

    /w config vector_length 0

To hide, show or toggle the vector display:

    /w vector off
    /w vector on
    /w vector
    
Watson remembers whether the vector display was on or off the last time Minecraft was run and uses that as the default state at startup.

To hide, show or toggle the outlines of blocks:

    /w outline off
    /w outline on
    /w outline


### Manipulating Annotations

Annotations are text associated with a particular location and displayed in 3-D space.

To create an annotation, first get a location, either with /lb coords query, or by simply using the LogBlock toolblock to mark a position.  When using the LogBlock toolblock, it is not necessary for the LogBlock database to contain any edits for that location.  The coordinates will be noted by Watson, regardless.

To add an annotation:

    /anno add This is the spot

To list all annotations:

    /anno list

To remove a single annotation, by number:

    /anno remove 1

Teleport to an annotation, by number:

    /anno tp 3

To hide, show or toggle the visibility of all annotations:

    /w anno off
    /w anno on
    /w anno

To remove all annotations:

    /anno clear


### Saving and Loading Edits from Files

Watson can save the current set of edits and annotations to a file in .minecraft/playeredits/.  Watson save files are in a self-explanatory text format that can be processed by UNIX text processing tools like `grep`.  If you don't specify a file name, Watson derives one from the current local time and the name of the player who performed the most recently selected edit.

To save a file (for Notch's edits the file might be Notch-2012-10-23-17.21.34):

    /w file save

To list all files:

    /w file list
    
Or alternatively:

    /w file list *
    
If there are multiple pages of files (50 to a page) then you may need to specify a page number:

    /w file list * 3

To list all files for players whose names begin with the specified text (case insensitive); the example below would list Notch's edit files, possibly among others:

    /w file list notc

To load the most recently saved file for a given player name (case insensitive):

    /w file load notch

Files can be deleted by specifying a pattern for the beginning of the file name:

    /w file delete not
    
You can delete all files with the asterisk:

    /w file delete *

You can also delete all files older than a specified date, in the form YYYY-MM-DD.  For example, to delete all files that were last modified before 2013:

    /w file expire 2013-01-01


### Filters

Filters are used to ignore query results that don't belong to specific players of interest.  Results for other players only appear in chat and are never added to the 3-D display.  By default, no filters are set, meaning that any log query result, will be added to the display.

To set a filter, use "/w filter add" followed by a list of player names:

    /w filter add Arnevdl29
    
This filter ignores all results except those describing edits made by Arnevdl29.  If you add a second filter:

    /w filter add Notch
    
then edits by both Arnevdl29 and Notch will be added to the display as they are encountered in chat.  Of course, it's not necessary to type two add commands.  You can just use:

    /w filter add Notch Arnevdl29

Setting a filter also sets the current player of interest.  So after "/w filter add Notch", subsequent screenshots will have the name Notch added to them (see Screenshot Management), even when no edits by Notch have been listed in chat.  This feature can be used to tag a screenshot with the name of any player, irrespective of whether that player has made any edits.  Note, however, that the next query result that passes the filter will change the currently selected player.
    
To list the current set of filters, use either of:

    /w filter list
    /w filter

To remove one or more filters, use "/w filter remove" with a list of player names:

    /w filter remove Notch

To remove all filters:

    /w filter clear

Also, note that clearing the Watson display with "/w clear" also clears the list of filters.


### Managing Edits By Specific Players

To hide only the edits (outlines, vectors and ore deposit labels) belonging to a specific player or list of player names:

    /w edits hide Notch

When edits are hidden, the corresponding ore deposits will be listed as strikeout text in the output of the "/w ore" command.

To show the edits again:

    /w edits show Notch
    
To completely remove all record of edits by specific players from the client's memory (note however that they will still exist as log entries on the server, as well as potentially in Watson save files):

    /w edits remove Notch Arnevdl29

There's also a "/w edits clear" command, which removes all edits by all players.  

To list the players who have made edits that are currently in Watson's memory, use either of:

    /w edits list
    /w edits
    
These commands list the number of edits by each player and say whether the edits are currently shown or hidden.

### In-Game GUI

Watson has an in-game Graphical User Interface (GUI) with controls for some of the settings that affect the Watson display.  By default, the key binding to show the in-game GUI is "L + C", the key can be changed there also.  You must be in-game (logged in to a server) to show this GUI.

![The Watson in-game GUI.](https://raw.github.com/vacla/watson1132/master/wiki/images/ingame.png)

![The Watson hotkeys GUI.](https://raw.github.com/vacla/watson1132/master/wiki/images/hotkey.png)

The various controls are described in the table below:

<table>
  <tr>
    <th>Control</th> <th>Type</th> <th>Purpose</th>
  </tr>
  <tr>
    <td>Watson Display: ON/OFF</td> <td>Toggle Button</td> <td>Clicking this button shows or hides the Watson display. When the watson display is hidden, all other controls are disabled, except the Clear Edits button.</td>
  </tr>
  <tr>
    <td>Show Vectors: ON/OFF</td> <td>Toggle Button</td> <td>Show or hide the vectors between edits, as if you typed "/w vector".</td>
  </tr>
  <tr>
    <td>Show Labels: ON/OFF</td> <td>Toggle Button</td> <td>Show or hide the ore deposit labels, as if you typed "/w label".</td>
  </tr>
  <tr>
    <td>Show Annotations: ON/OFF</td> <td>Toggle Button</td> <td>Show or hide annotations, as if you typed "/w anno".</td>
  </tr>
  <tr>
    <td>Clear Edits</td> <td>Push Button</td> <td>Forget all edits and filters, as if you typed "/w clear".</td>
  </tr>
  <tr>
    <td>Min Vector Length: &lt;number&gt;</td> <td>Slider</td> <td>Adjust the minimum length of a vector between consecutive edits for it to be visible. The setting is saved in the configuration file.  This is equivalent to "/w config vector_length &lt;value&gt;".</td>
  </tr>
  <tr>
    <td>Label Order: IMPORTANCE/TIMESTAMPS</td> <td>Toggle Button</td> <td>Switches between numbering ore deposits according to their time stamps or most important first (diamonds first). This button is equivalent to using the "/w config time_ordered_deposits" command.</td>
  </tr>
  <tr>
    <td>Show in-game options</td> <td>L + C</td> <td>Key binding to show the in-game GUI.</td>
  </tr>
  <tr>
    <td>Take a screenshot</td> <td>F12</td> <td>Take a Watson-styled screenshot with the player name in the filename.</td>
  </tr>
  <tr>
    <td>TP to next ore</td> <td>RIGHT</td> <td>Teleport to the next ore deposit (equivalent to "/w tp").</td>
  </tr>
  <tr>
    <td>TP to previous ore</td> <td>LEFT</td> <td>Teleport to the previous ore deposit (equivalent to "/w tp prev").</td>
  </tr>
  <tr>
    <td>Query edits before</td> <td>MOUSE_LEFT</td> <td>Query edits before the currently selected edit (equivalent to "/w pre").</td>
  </tr>
  <tr>
    <td>Query edits after</td> <td>MOUSE_RIGHT</td> <td>Query edits after the currently selected edit (equivalent to "/w post").</td>
  </tr>
  <tr>
    <td>Cursor to next edit</td> <td>UP</td> <td>Select the edit after the currently selected edit.</td>
  </tr>
  <tr>
    <td>Cursor to previous edit</td> <td>DOWN</td> <td>Select the edit before the currently selected edit.</td>
  </tr>
  <tr>
    <td>TP to cursor</td> <td>MOUSE_LEFT</td> <td>Teleport to the currently selected edit or position.</td>
  </tr>
</table>



### Built-In Calculator

Watson contains a simple calculator that understands +, -, *, / and parentheses ().  Currently, the calculator considers '-' to bind to any digits that immediately follow (making a negative number), so when subtracting, use spaces.  Example:

    /calc 800/(57 - 32)


### Highlighting Chat Content

Watson can highlight text that matches a specified [Java regular expression](http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html) using colour and formatting.  All currently defined highlights are applied to a line of chat in the order that they were defined.  Later highlights can override all or part of those that were defined earlier.

Let's say we'd like to make the "Unknown command." error message stand out a bit more by making it red:

    /hl add red ^Unknown\scommand.*

The above command will highlight the whole chat line if it matches the pattern.  Watson can also highlight selected parts of a chat line, using the "/hl select" command.  To use it, specify a pattern that matches the whole line and put parentheses around the parts of the line that should be highlighted.  For example, the following two commands will make player names in chat messages appear in orange italics with blue angle brackets around them:

    /hl select blue ^(<\w+>).*$
    /hl select /orange ^<(\w+)>.*$

The first command above highlights the name and the angle brackets in blue.  The second command reformats just the name.  This example also illustrates the fact that highlights are applied in the order that they were defined.

Valid colour names are: black, darkblue/navy, darkgreen/green, cyan, darkred/red, purple, orange/gold/brown, lightgrey/lightgray, darkgrey/darkgray/grey/gray, blue, lightgreen, lightblue, lightred/brightred/rose, pink/lightpurple/magenta, yellow and white.  The "/hl add" and "/hl select" commands also allow a style either instead of a colour, or preceding the colour.

<table>
  <tr>
    <th>Style</th> <th>Code</th> <th>Example</th>  <th>Meaning</th>
  </tr>
  <tr>
    <td>Bold</td> <td>+</td> <td><pre>/hl add + hello</pre></td>  <td>Highlight hello in bold.</td>
  </tr>
  <tr>
    <td>Italic</td> <td>/</td> <td><pre>/hl add /orange ^&lt;\w+&gt;</pre></td>  <td>Highlight the player name in global chat messages in orange italics.</td>
  </tr>
  <tr>
    <td>Underline</td> <td>_</td> <td><pre>/hl add _ the</pre></td>  <td>Underline "the".</td>
  </tr>
  <tr>
    <td>Strikethrough</td> <td>-</td> <td><pre>/hl add - redacted</pre></td>  <td>Strike through the word "redacted".</td>
  </tr>
  <tr>
    <td>Random</td> <td>?</td> <td><pre>/hl add ? magic</pre></td>  <td>Replace "magic" with random glyphs.</td>
  </tr>
</table>

To list the existing patterns if you need to remove any:

    /hl list

To remove a specific pattern by number:

    /hl remove 3

And if you forget any commands, try:

    /hl help


### Screenshot Management

Watson's screenshot features are now bound to a custom Watson Take Screenshot key, which defaults to F12.  Configure this by clicking Options... -> Controls... from the Minecraft menu and scrolling down to the Watson section of the keybindings.  If you desire, you can configure Watson's Take Screenshot key to F2 and disable the default Minecraft Take Screenshot key under Miscellaneous by pressing Esc.  The Watson screenshot facility is a strict superset of the default Minecraft functionality.

If you ban players for grief or xray, inevitably you will end up with a large number of screenshots that must be retained until the ban is appealed.  Watson includes features to make it easier to manage many Minecraft screenshots and to find the ones that pertain to a particular player.

When the name of the player is known (because Watson saw an "/lb coords" result for that player since the last "/w clear") the screenshot will be placed in the directory .minecraft/screenshots/&lt;playername&gt;/, and &lt;playername&gt; will be appended to the filename, e.g. ".minecraft/screenshots/Notch/2013-02-21_12.47.50-Notch.png".  Both of these behaviours can be turned on or off using the ss_player_directory and ss_player_suffix configuration settings, respectively.

When the player name is not known, then by default the screenshot just ends up in .minecraft/screenshots/.  But Watson can be configured to place the screenshot in a subdirectory based on the current date and time.  For example, "/w config ss_date_directory yyyy-MM-dd" would put the screenshot in a subdirectory based on the full numeric year, month and day, e.g. 2013-03-15.  Whereas "/w config ss_date_directory MMMM yyyy" would use the long name of the month, e.g. "March 2013".  There are many options and they are described in greater detail in the section on the Configuration File.



### CoreProtect Support

As with CoreProtect support is currently limited to viewing inspector and lookup results.  When used with CoreProtect, Watson does not currently support automatically calculating stone:diamond ratios ("/w ratio"), querying previous or subsequent edits ("/w pre" and "/w post") or automatically paging through results.

Note also that the parsing of lookup results currently ignores the returned world and assumes the current world instead.  This will be resolved under [Issue #23](https://github.com/totemo/watson/issues/23).  For now, the easy way to avoid this problem is to simply specify a radius, e.g.:

    /co l r:10


### Configuration

Watson's main configuration settings are stored in ".minecraft/config/watson.json".  They can be changed using the "/w config" command.  If a setting can be either "on" or "off", omitting a value for it in "/w config" will reverse the current value.  If the setting has a value that can't be toggled in this way, "/w config settingname" will show its current value. There is also a gui when you press the default hotkey "L + C".

Running "/w config help" will show help for all of the configuration settings.

<table>
  <tr>
    <th>Setting</th> <th>Values</th> <th>Default</th>  <th>Purpose</th> <th>Example</th>
  </tr>
  <tr>
    <td>watson</td> <td>on / off</td> <td>on</td> <td>Enable/disable all Watson functions.</td> <td>/w config watson off</td>
  </tr>
  <tr>
    <td>watson_prefix</td> <td>string</td> <td>w</td> <td>Set the prefix with which all Watson commands begin.</td> <td>/w config watson_prefix watson<br>/watson help<br>/watson config watson_prefix w</td>
  </tr>
  <tr>
    <td>debug</td> <td>on / off</td> <td>off</td> <td>Enable/disable all debug messages in the log file.</td> <td>/w config debug</td>
  </tr>
  <tr>
    <td>auto_page</td> <td>on / off</td> <td>off</td> <td>Enable/disable automatic paging through "/lb coords" results (up to max_auto_pages pages).</td> <td>/w config auto_page on</td>
  </tr>
  <tr>
    <td>max_auto_pages</td> <td>integer</td> <td>3</td> <td>The number of pages of "/lb coords" results to step through automatically.</td> <td>/w config max_auto_pages 4</td>
  </tr>
  <tr>
    <td>pre_count</td> <td>integer</td> <td>45</td> <td>The number of "/lb coords" results that will be returned by "/w pre", by default.</td> <td>/w config pre_count 60</td>
  </tr>
  <tr>
    <td>post_count</td> <td>integer</td> <td>45</td> <td>The number of "/lb coords" results that will be returned by "/w post", by default.</td> <td>/w config post_count 60</td>
  </tr>
  <tr>
    <td>region_info_timeout</td> <td>decimal number of seconds >= 1.0</td> <td>5.0</td> <td>Minimum elapsed time between automatic "/region info" commands when right clicking with the wooden sword.</td> <td>/w config region_info_timeout 3</td>
  </tr>
  <tr>
    <td>chat_timeout</td> <td>decimal number of seconds >= 0.0</td> <td>1.1</td> <td>Minimum elapsed time between automatically issued commands commands (e.g. /lb next) being sent to the server in the form of chat messages.</td> <td>/w config chat_timeout 1.0</td>
  </tr>
  <tr>
    <td>billboard_background</td> <td>ARGB colour as 8 hexadecinal digits</td> <td>A8000000</td> <td>The colour of the background of annotation and ore label billboards.</td> <td>/w config billboard_background 7f000000</td>
  </tr>
  <tr>
    <td>billboard_foreground</td> <td>ARGB colour as 8 hexadecinal digits</td> <td>7FFFFFFF</td> <td>The colour of the foreground of annotation and ore label billboards.</td> <td>/w config billboard_foreground 7fa0a0a0</td>
  </tr>
  <tr>
    <td>group_ores_in_creative</td> <td>on / off</td> <td>on</td> <td>If "on", edits are grouped into ore deposits even in creative mode.  If "off", that processing only happens in survival mode.  Currently defaulted to on until a reliable way to distinguish the server's gamemode from that of the player is determined.</td> <td>/w config group_ores_in_creative on</td>
  </tr>
  <tr>
    <td>teleport_command</td> <td>format string</td> <td>/tppos %g %d %g</td> <td>Specifies the formatting of the command used to teleport to specific coordinates in the implementation of "/w tp" and "/anno tp" commands.  Only %d (for integers) and %g (for decimal numbers) are supported as formatting specifiers.</td> <td>/w config teleport_command /tppos %d %d %d</td>
  </tr>
  <tr>
    <td>ss_player_directory</td> <td>on / off</td> <td>on</td> <td>When on, each screenshot is placed in a subdirectory: .minecraft/screenshots/&lt;player&gt;/, where &lt;player&gt; is the player who performed the most recently selected edit.  If there is no currently selected player, then the value of the ss_date_directory setting determines the name of the directory where the screenshot will be stored.</td> <td>/w config ss_player_directory off</td>
  </tr>
  <tr>
    <td>ss_player_suffix</td> <td>on / off</td> <td>on</td> <td>When on, each the name of the player who performed the most recently selected edit is appended as a suffix of each screenshot file name.</td> <td>/w config ss_player_suffix off</td>
  </tr>
  <tr>
    <td>ss_date_directory</td> <td>date format string</td> <td></td> <td>This setting determines the directory to store screenshots when ss_player_directory is off, or when no player is currently selected.  The setting is a format specifier for the Java <a href="http://docs.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html">SimpleDateFormat</a> class, interpreted in the user's locale (language settings), allowing considerable flexibility in the name of the output directory.  If set to the empty string (the default setting), then screenshots without a selected player will end up in .minecraft/screenshots/.  If a format is specified, then a subdirectory of .minecraft/screenshots/ is created to place each screenshot in, based on the time and date when the image was taken.  Recommended settings include "yyyy-MM-dd" (numeric year, month and day, e.g. 2013-03-15), "yyyy-MM" (year and month, e.g. 2013-03) and "MMMM yyyy" (month in long form and year, e.g. March 2013).  The format can be set to the empty string using the command "/w config ss_date_directory".</td> <td>/w config ss_date_directory yyyy-MM-dd</td>
  </tr>
  <tr>
    <td>reformat_query_results</td> <td>on / off</td> <td>on</td> <td>When on, query results are reformatted to a more compact form that uses numbers instead of material names (currently applies to LogBlock results only).</td> <td>/w config reformat_query_results off</td>
  </tr>
  <tr>
    <td>recolour_query_results</td> <td>on / off</td> <td>on</td> <td>(Note UK English spelling.) When on, query results are recoloured to indicate grouping of edits that are in close proximity to each other (currently applies to LogBlock results only).</td> <td>/w config recolour_query_results off</td>
  </tr>
  <tr>
    <td>time_ordered_deposits</td> <td>on / off</td> <td>off</td> <td>When off (the default) ore deposits are numbered in order of their significance to investigating xray (diamonds before iron, before coal).  When on, ore deposits are numbered in the order they were mined.</td> <td>/w config time_ordered_deposits on</td>
  </tr>
  <tr>
    <td>vector_length</td> <td>decimal</td> <td>4.0</td> <td>Specifies the minimum length (in blocks) of a vector for it to be visible.</td> <td>/w config vector_length 0</td>
  </tr>
</table>


Files
-----

* **.minecraft/config/watson.json** - The main configuration file.  Stores a variety of settings that persist between Minecraft sessions.
* **.minecraft/config/watson/** - Directory that has configuration files per server.
* **.minecraft/playeredits/** - Directory of save files containing records of edited blocks and annotations.


Compatibility
-------------

Watson is regularly tested for compatibility with:

* Fabric
* Optifine


Troubleshooting
---------------

<table>
  <tr>
    <th>Problem</th> <th>Resolution</th>
  </tr>  
  <tr>
    <td>The screenshot key does not add player names to saved screenshots.</td>
    <td>Watson defines it's own key binding for Watson-style screenshots (defaults to F12).  If you want this functionality to be bound to the F2 key, configure that under Options... -> Controls... in the Minecraft menu (scroll down to the Watson section of the key bindings) and de-configure the default Minecraft "Take Screenshot" key (press Esc to set it to NONE).</td>
  <tr>
    <td>I don't see anything.</td> <td>Make sure you put "coords" in your /lb query.  Watson needs coordinates to know where to draw things.</td>
  </tr>
  <tr>
    <td>I still don't see anything.</td> <td>Make sure you turn on the Watson display: /w display on</td>
  </tr>
  <tr>
    <td>I can't teleport with "/w tp".</td> <td>By default, Watson expects a /tppos command that accepts decimal numbers for coordinates, e.g. "/tppos -120.5 7 345.5".  Many teleport commands don't, however, or they have a different name.  If your teleport command requires integer coordinates, try "/w config teleport_command /tppos %d %d %d".  If you're using the the CraftBukkit /tp command, then you can use: "/w config teleport_command /tp %d %d %d".</td>
  </tr>
  <tr>
    <td>I see the edits, but ore deposits don't get numbered. /w ore doesn't work.</td> <td>An older version of Watson attempted to detect creative-mode servers and disable labelling of ore deposits (xray is pointless in creative mode).  However, what it actually detected was the user's game mode.  If you are using Watson in creative mode, and /w ore doesn't work, then you may need to turn on the group_ores_in_creative setting (recent Watson versions have this on by default).  The command is: /w config group_ores_in_creative on</td>
  </tr>
  <tr>
    <td>When I query a particular block it shows up as a smallish bright pink cube.</td>
    <td>Watson scrapes CoreProtect/LogBlock query results out of chat.  If it doesn't recognise the name of a block it just draws the pink cube as a reminder for me to add that name.  Let me know about it and I'll fix it.</td>
  </tr>
</table>


Contact Details
---------------

If Watson's not working for you or you want to suggest improvements, I'm happy to help.  You can contact me directly in the following ways:

* On gmail.com, just look at my github profile.

You can also raise bug reports or feature requests via GitHub, [here](https://github.com/vacla/watson1132/issues).