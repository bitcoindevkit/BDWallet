
How to test with local REGTEST electrs server

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

4. Configure Android Studio ADB

   "Use existing manually managed server" on port 5038

5. From localhost command line stop and restart adb with port forwarding

   ```shell
   adb kill-server
   adb -L tcp:localhost:5038 reverse tcp:60401 tcp:60401
   adb -L tcp:localhost:5038 reverse --list
   ```

6. Open Android Studio "Build Variants" window (lower left)

7. Select Active Build Variant "localDebug"

8. Run or Debug "app"
