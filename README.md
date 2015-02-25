ExpandableView
===========

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