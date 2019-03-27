# DxAdapter-lib
This library is not 100% ready yet. Use at own risk :)

# Usage:
in your **root** build.gradle:

    allprojects {
	    repositories {
		  ...
		  maven { url 'https://jitpack.io' }
	}

or in your **module's** build.gradle file:

	repositories {
	  ...
	  maven { url 'https://jitpack.io' }
	}


in your **module's** build.gradle also add:

  	dependencies {
    	implementation 'com.github.or-dvir:DxAdapter-lib:{latest release}'
	}
