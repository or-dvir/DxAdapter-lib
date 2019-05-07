# DxAdapter
This is a `RecyclerView.Adapter` library with all sorts of built-in 
features. I made this library because I notices I am using the same 
`RecyclerView` features over and over again and I keep having to 
copy-paste the code and then modify it for the specific app I'm 
working on. Finally I have decided to make this generic library
so I could simply import it and have all those features ready to go.

Some things to note:
* This library was meant to be used with Kotlin. While it should
theoretically also work with Java apps, it was never tested for it.
* While efficiency is definitely on my mind, my priority for this library 
is simplicity and readability of code. So if it's important for you to
save those extra X milliseconds of runtime, there's probably some extra
code you'll need to write.
* This readme file is only for general information. For more in-depth 
explanations and other important usage notes, please see the documentation
and the sample app.

# Features
* Expandable items
* Click listeners
* Filter
* Selection of items
* Sticky Headers
* Dragging
* Swiping (with text/icon)
* Action Mode
* Scroll listeners

# Current Limitations
Please note that some of these might work, or work partially.
The point is these features are not yet fully tested and will be added
in future releases:
* Only supports vertical `LinearLayoutManager`
* No selection of cards.
* Only support 1 type of sticky header.

# Usage
in your build.gradle file:

	repositories {
	    ...
	    maven { url 'https://jitpack.io' }
	}
	
	dependencies {
        implementation 'com.github.or-dvir:DxAdapter-lib:{latest release}'
	}

This library **mostly** relies on the implementation of interfaces.
Note that while some features have matching interfaces for adapter and item,
others are only for adapter or only for item
(and some don't use interfaces at all!). This is due to the way 
certain feature are implemented by Google. Some examples:

1) Dragging and swiping are handled by the class `ItemTouchHelper.Callback` 
(`DxItemTouchCallback` in the library) and have nothing to do 
with the adapter class - therefore only item interfaces.
2) Filtering is done completely on the adapter class - therefore only 
adapter interface.
3) Scroll listeners have nothing to do with the adapter or the items,
and are handled by the `RecyclerView` itself (`DxRecyclerView` in the 
library) - so no interfaces needed at all.

## Your Adapter
Your adapter ***must*** extend `DXAdapter`, and implement the 
appropriate interfaces depending on what behavior you want:
`IAdapterExpandable`, `IAdapterFilterable`, `IAdapterSelectable`,
`IAdapterStickyHeader`.

## Your Items
In addition to the adapter interfaces, your items ***must also*** implement 
an appropriate interface: `IItemDraggable`, `IItemExpandable`, `IItemSelectable`,
`IItemSwipeable`. If you don't need any of those behaviors but still want
to use this library, have your items implement `IItemBase`.<br>
Note that for behaviors with matching interfaces (such as selectable or 
expandable) you **must implement the appropriate interface in *both* your 
adapter *and* your item** or those features will not work.

# FAQ
* **Whats with all the interfaces?! This is so much work!**<br>
There are several reasons for this:
  * Separation of code logic.
  * Easier to understand and expand upon when each feature is separate
  as opposed to all of them being cramped into 1 large class and mixed into
  each other.
  * Allowing for multi-type adapters where some items support a given
  feature and others don't. For example we can have a list with headers
  where the items are selectable and swipeable, but the headers are not.
  * By using interfaces your items are free to extend other classes.
  
* **Why are some interfaces empty? Whats the point?**<br>
This is so that the usage of features is consistent and preparing for 
adding future features and behaviors. (While I admit this is pointless 
for now, my OCD is making me :grinning:)