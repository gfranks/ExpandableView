ExpandableView
===========

ExpandableView is a custom layout that handles easy expanding and collapsing of content via use of a header and an optional footer that is highly configurable and works for both phone and tablet. ExpandableView is only available for API levels 15+.
See below for usage and examples. If you have any questions, issues, or want to contribute, please submit an issue or Pull Request, or you may contact me.

What It Looks Like:
------------------

See a short video of this control here:

[![Sample Video](http://img.youtube.com/vi/iQuti7TIHlM/0.jpg)](https://www.youtube.com/watch?v=iQuti7TIHlM)

### Screen Shots

![Examples](/resources/screenshot1.png?raw=true) ![Examples](/resources/screenshot2.png?raw=true)


How To Use It:
-------------

### Basic Example

```java

// See Sample for additional customization

/**
 * Initialize with separate layout files for your header, content, and footer
 */
 <com.github.gfranks.expandable.view.ExpandableView
     android:id="@+id/expandable_view"
     android:layout_width="match_parent"
     android:layout_height="wrap_content"
     app:headerLayout="@layout/layout_expandable_view_header"
     app:contentLayout="@layout/layout_expandable_view_content"
     app:footerLayout="@layout/layout_expandable_view_footer" />

/**
 * Initialize by manually adding your header, content, and footer
 */
 <com.github.gfranks.expandable.view.ExpandableView
     android:id="@+id/expandable_view"
     android:layout_width="match_parent"
     android:layout_height="wrap_content"
     android:layout_marginTop="10dp">

     <TextView
         android:layout_width="match_parent"
         android:layout_height="wrap_content"
         android:text="This is my header" />

     <TextView
         android:layout_width="match_parent"
         android:layout_height="wrap_content"
         android:text="This is my content" />

     <TextView
         android:layout_width="match_parent"
         android:layout_height="wrap_content"
         android:text="This is my footer" />

  </com.github.gfranks.expandable.view.ExpandableView>

/**
 * Initialize by manually adding your header, content, and footer and using a fragment as your content
 */
 <com.github.gfranks.expandable.view.ExpandableView
     android:id="@+id/expandable_view"
     android:layout_width="match_parent"
     android:layout_height="wrap_content"
     android:layout_marginTop="10dp">

     <TextView
         android:layout_width="match_parent"
         android:layout_height="wrap_content"
         android:text="This is my header" />

     <!-- Using a FrameLayout so you can use the fragment manager to replace this with a fragment
     NOTE: You can also apply a fragment for your header and footer

     <FrameLayout
         android:id="@+id/expandable_view_content"
         android:layout_width="match_parent"
         android:layout_height="wrap_content" />

     OR specify your fragment directly if NOT nested

     <fragment
         android:layout_width="match_parent"
         android:layout_height="wrap_content"
         android:name="com.path.to.fragment"
         android:tag="tag" />
     -->

     <TextView
         android:layout_width="match_parent"
         android:layout_height="wrap_content"
         android:text="This is my footer" />

 </com.github.gfranks.expandable.view.ExpandableView>

/**
 * Initialize with separate layout files for your header, content, and/or footer and specifying your other views manually
 */
 <com.github.gfranks.expandable.view.ExpandableView
     android:id="@+id/expandable_view"
     android:layout_width="match_parent"
     android:layout_height="wrap_content"
     android:layout_marginTop="10dp"
     app:contentLayout="@layout/layout_expandable_view_content">

     <TextView
         android:layout_width="match_parent"
         android:layout_height="wrap_content"
         android:text="This is my header" />

     <TextView
         android:layout_width="match_parent"
         android:layout_height="wrap_content"
         android:text="This is my footer" />

 </com.github.gfranks.expandable.view.ExpandableView>
```

Customization:
----------------
 * `isCollapsed` Boolean determining if ExpandableView should be collapsed by default
 * `collapseOnContentClick` Boolean determining if ExpandableView can be collapsed/expanded when the content view is clicked
 * `animationDuration` Int specifying your desired animation duration (default is 200ms)
 * `headerLayout` Layout resource you wish to use as your header view
 * `contentLayout` Layout resource you wish to use as your content view
 * `footerLayout` Layout resource you wish to use as your footer view (Optional)
 * `disableExpandCollapseOnClick` Boolean disabling collapse/expandsion when any view is clicked (should you wish to handle this on your own)
 * `addGradientOverlayWhenCollapsed` Boolean determining if you would like to add a gradient overlay over your content when collapsed (Be sure to specify a collapsedContentHeight greater than 0 or this will not be applied)
 * `gradientOverlayColor` Color of the gradient overlay used to overlay the content view when collapsed (Gradient goes from Color.TRANSPARENT to this color, defaults to Color.WHITE)
 * `collapsedContentHeight` Dimension used as the collapsed content height (Defaults to 0)

Callback Methods:
----------------

    /**
     * ExpandableViewListener
     */
    boolean canExpand(ExpandableView expandableView);
    boolean canCollapse(ExpandableView expandableView);
    void willExpand(ExpandableView expandableView);
    void willCollapse(ExpandableView expandableView);
    void didExpand(ExpandableView expandableView);
    void didCollapse(ExpandableView expandableView);
    void onHeightOffsetChanged(ExpandableView expandableView, float offset);

Installation:
------------

### Directly include source into your projects

- Simply copy the source/resource files from the library folder into your project.

### Use binary approach

- Follow these steps to include aar binary in your project:

    1: Copy com.github.gfranks.expandable.view-1.0.aar into your projects libs/ directory.

    2: Include the following either in your top level build.gradle file or your module specific one:
    ```
      repositories {
         flatDir {
             dirs 'libs'
         }
     }
    ```
    3: Under your dependencies for your main module's build.gradle file, you can reference that aar file like so:
    ```compile 'com.github.gfranks.expandable.view:com.github.gfranks.expandable.view-1.0@aar'```

License
-------
Copyright (c) 2015 Garrett Franks. All rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.