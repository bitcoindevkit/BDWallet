
### Install bdk-jni

1a. Clone [`bdk-jni` project](https://github.com/bitcoindevkit/bdk-jni)

2a. Follow README instruction to publish .aar files to local maven repository

OR

1b. Copy `bdk-jni-debug-0.2.0.aar` to `app/libs` folder in BDWallet project 

### Build

1. Build with gradle

   ```
   ./gradlew build
   ```

### Test

#### With local REGTEST electrs server

1. Install [docker desktop](https://www.docker.com/get-started)

1. Create aliases to start, stop, view logs and send cli commands to container

    ```shell
    alias rtstart='docker run -d --rm -p 127.0.0.1:18443-18444:18443-18444/tcp -p 127.0.0.1:60401:60401/tcp --name electrs bitcoindevkit/electrs'
    alias rtstop='docker kill electrs'
    alias rtlogs='docker container logs electrs'
    alias rtcli='docker exec -it electrs /root/bitcoin-cli -regtest -rpcuser=admin -rpcpassword=passw $@'
    ```

1. Use aliases to start container, view logs, run cli command, stop container

    ```shell
    rtstart  
    rtlogs  
    rtcli help    
    rtcli getwalletinfo    
    rtcli getnewaddress  
    rtstop
    ```
   
1. Use "AVD Manager" to lauch a virtual device (eg. "Pixel 3a API 30" or similar)  

1. From localhost command line setup adb with port forwarding, use same adb version as Android Studio

   ```shell
   ~/Android/Sdk/platform-tools/adb devices -l
   ~/Android/Sdk/platform-tools/adb -L tcp:localhost:5037 reverse tcp:60401 tcp:60401
   ~/Android/Sdk/platform-tools/adb -L tcp:localhost:5037 reverse --list
   ```

1. Open Android Studio "Build Variants" window (lower left)

1. Select Active Build Variant "localDebug"

1. Run or Debug "app"

1. Create a new wallet and create a deposit address

1. Send regtest coins to emulated wallet, and generate a block

   ```shell
   rtcli sendtoaddress <deposit address> 2.345
   rtcli getnewaddress
   rtcli generatetoaddress 1 <newaddress>
   ```