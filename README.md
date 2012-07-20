PatchIde
========

Plugin for Jetbrains IDEs such as Intellij IDEA, PHP Storm, RubyMine. Intended to be used in combo with [Color IDE plugin](httpshttps://github.com/dmalch/ColorIde).

When Color IDE is installed several UI issues appears. These issues can be fixed by only changing IDEA source code or files. This plugin fixes issues by patching IDE installation. All patches are contributed into IDEA source code and can appear in new versions by default.

##Issues

### 1. Broken Settings Page
before fix:

after fix:

Issue in jetbrains repository: http://youtrack.jetbrains.com/issue/IDEA-88520

### 2. Black tree nodes in several trees
before fix:

after fix:

Issue in jetbrains repository:http://youtrack.jetbrains.com/issue/IDEA-88526

### 3. Test nodes have green background after build 118.308 
before fix:

after fix:

Issue in jetbrains repository: not created yet