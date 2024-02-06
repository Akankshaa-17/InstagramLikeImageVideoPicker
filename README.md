### MediaPickerInstagram (Reference Of Instagram)
Pick and capture the image, video or both.

MediaPickerInstagram is an android library that lets you choose single/multi pictures from gallery or camera.

[![](https://jitpack.io/v/Akankshaa-17/InstagramLikeImageVideoPicker.svg)](https://jitpack.io/#Akankshaa-17/InstagramLikeImageVideoPicker)


### Gradle Dependency
**Step 1.** Add the JitPack repository to your build file <br>
Add it in your root build.gradle (project level) at the end of repositories:
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
**Step 2.**
Add it in your settings.gradle if your pluginManagement and dependencyResolutionManagement is defined in settings.gradle file:
```
    pluginManagement {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
    
    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
            repositories {
                ...
                maven { url 'https://jitpack.io' }
        }
    }
```

**Step 3.** Add the dependency<br>
Add it in your dependencies section of build.gradle (app level):
```
	dependencies {
	        implementation 'com.github.Akankshaa-17:InstagramLikeImageVideoPicker:1.6'
	}
```

**Step 4.** Add these activities in your manifest file. in the application tag:
```
<activity android:name="ir.shahabazimi.instagrampicker.gallery.SelectActivity" />
<activity android:name="ir.shahabazimi.instagrampicker.gallery.MultiSelectActivity"/>
  <activity
            android:name="com.yalantis.ucrop.UCropActivity"
            android:screenOrientation="portrait"
            android:theme="@style/Theme.AppCompat.Light.NoActionBar"/>
```

### How to use it
For using this library just use the code below it is simple and straight forward.<br>

For single image/video selection use this:
```
            InstagramPicker
                .Builder(this)
                .setType(MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE)
                .setCropXRatio(1)
                .setCropYRatio(1)
                .setDefault16And9Ratio(false)
                .setSingleListener(object : SingleListener {
                override fun selectedPic(address: String) {
                    ...
                }
            }).build().show()
```

For multiple image/video selection use this:
```
            InstagramPicker
                .Builder(this)
                .setType(MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE)
                .setCropXRatio(1)
                .setCropYRatio(1)
                .setDefault16And9Ratio(true)
                .setNumberOfPictures(4)
                .setMultiSelect(true)
                .setMultiSelectListener(object : MultiListener{
                    override fun selectedPics(addresses: List<String>) {
                        ...
                    }
            }).build().show()
```

X ratio, Y ratio = these two integer variables determines the crop aspect ratio of the picture.<br>
MediaTypeEnum = types are defined in this enum to use any of the required types.<br>
number of pics = an integer variable that determines the number of pictures that user allowed to pick, a number between 2 and 1000<br>

<br><br>
**Reference Github Repo**<br> 
Special Thanks to <a href="https://github.com/ShahabGT/InstagramPicker" target="_blank">InstagramPicker</a>
<br><br>

### Developed By
* Akanksha Gaikwad
* Bhavesh Jethani

### License
```
Copyright 2023 Akanksha Gaikwad and Bhavesh Jethani

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
