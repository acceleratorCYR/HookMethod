# HookMethod

## Describe  
This is used for android device to hook method in apps; It is based on Xposed.
## Required 
1. xposed  
2. phone has been rooted  

## Install
1. build and install  
2. create File **/data/local/tmp/monitor.conf**
3. **chmod 644 /data/local/tmp/monitor.conf** 
4. restart  


## Usage  
1. add packageName And FunctionName to monitor.conf
2. restart app
3. !no need to restart phone

### format of each line in monitor.conf  
each line in monitor.conf should be like:
- [packageName] [classFullPath] [MethodName] #hook normal Method or native method    
- [packageName] [classFullPath]  #hook construct Method

### Example of monitor.conf(it can contains multi lines):
/data/local/tmp/monitor.conf:
```conf
com.accelerator.test com.accelerator.test.Main onCreate
com.accelerator.test com.accelerator.test.Main
``` 


