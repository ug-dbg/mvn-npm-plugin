# Maven NPM plugin
A plugin to run npm (and Angular ng) commands from maven lifecycle.

## Why
Because npm is a pain in the ass to use from Maven.  
This plugin should work whether you are using an *nix or a Win platform.  
The npm/ng logs should be printed as correctly as possible in the maven logs.  

## Goals
- **exec**     : execute an npm command with argument from a working directory
- **exec-all** : execute all arguments as several *npm run* commands from a working directory
- **exec-ng**  : execute an Angular ng command with arguments from a working directory
- **version**  : Set the version attribute in a target package.json (or any json) file

## Parameters
- clean: 
  - **npm.workingDir**        : the working directory
  - **npm.distDir**           : the dist directory name to delete
  - **npm.nodeModulesDir**    : the node_modules folder name, default is 'node_modules'
  - **npm.packageLockFile**   : the package-lock file name, default is 'package-lock.json'
  - **npm.deleteNodeModules** : if true, delete the node_modules folder
  - **npm.deletePackageLock** : if true, delete the package-lock file
- exec/exec-all :
  - **npm.command**    : the npm command to run
  - **npm.args**       : the npm arguments
  - **npm.workingDir** : the working directory
  - **npm.home**       : the directory that contains the npm executable. If not set, assume present in system path.
- exec-ng :
  - **ng.command**    : the ng command to run
  - **ng.args**       : the ng arguments
  - **ng.workingDir** : the working directory
  - **ng.home**       : the directory that contains the ng executable. If not set, assume present in system path.
- version : 
  - **npm.workingDir**                   : the working directory, default is basedir
  - **npm.packageJsonFile**              : the package.json file name, default is "package.json"
  - **npm.packageJsonFileVersion**      : the version to set
  - **npm.packageJsonFileIndentFactor** : the output indentation factor, default is 1
  - **npm.packageJsonFileIndentChars**  : the output indentation characters, "SPACE" or "TAB", default is "SPACE"
- all goals : 
  - **log.level** : the default log level of the logger *(see java.util.logging.Level#parse)*

## Examples
Execute *npm install* in the *angular* sub-directory of the maven project : 
```bash
 ug-dbg@rpi ~/my-project $ mvn com.github.ug-dbg:mvn-npm-plugin:exec -Dnpm.command="install" -Dnpm.workingDir="angular"
```

Execute *npm run build -- --watch=true* from the package.json file in the *angular* sub-directory of the maven project : 
```bash
 ug-dbg@rpi ~/my-project $ mvn com.github.ug-dbg:mvn-npm-plugin:exec -Dnpm.command="run" -Dnpm.args="build -- --watch=true" -Dnpm.workingDir="angular"
```

Execute the *build:lib* then *build-dev* from the package.json file in the *angular* sub-directory of the maven project : 
```bash
 ug-dbg@rpi ~/my-project $ mvn com.github.ug-dbg:mvn-npm-plugin:exec-all -Dnpm.command="run" -Dnpm.args="build:lib,build:dev" -Dnpm.workingDir="angular"
```

Execute the *ng serve --proxy-config proxy.conf.dev.json* in the *angular* sub-directory of the maven project :
```bash
 ug-dbg@rpi ~/my-project $ mvn com.github.ug-dbg:mvn-npm-plugin:exec-ng -Dng.command="serve" -Dng.args="--proxy-config proxy.conf.dev.json" -Dnpm.workingDir="angular"
```

## Examples for pom configuration
```xml
<plugin>
    <groupId>com.github.ug-dbg</groupId>
    <artifactId>mvn-npm-plugin</artifactId>
    <version>1.5</version>
    <executions>
        <execution>
            <goals>
                <goal>exec-all</goal>
            </goals>
            <configuration>
                <logLevel>FINEST</logLevel>
                <workingDir>angular</workingDir>
                
                <!--All the args here will be run as successive 'npm run $args' commands-->
                <args>run build:lib --configuration=dev</args>
                <args>run start:dev -- --watch=true</args>
            </configuration>
        </execution>
    </executions>
</plugin>
```
