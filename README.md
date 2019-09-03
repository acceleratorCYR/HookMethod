# HookMethod
## Required:  
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
- [packageName] [className] [funcName] #hook normal function or native function  
- [packageName] [className]  #hook construct function  

### Example
/data/local/tmp/monitor.conf:
```conf
com.kakao.talk com.kakao.talk.a.b.c funcA
com.kakao.talk com.kakao.talkb.c.s funcB
com.kakao.talk com.kakao.b.d
``` 


